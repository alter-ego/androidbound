package solutions.alterego.androidbound;

import android.annotation.TargetApi;
import android.content.Context;
import android.os.Build;
import android.view.LayoutInflater;
import android.view.LayoutInflater.Factory;
import android.view.LayoutInflater.Factory2;
import android.view.View;
import android.view.ViewGroup;

import java.lang.ref.WeakReference;
import java.lang.reflect.Field;

import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;
import solutions.alterego.androidbound.android.BindableLayoutInflaterFactory;
import solutions.alterego.androidbound.android.FontManager;
import solutions.alterego.androidbound.android.NullBindableLayoutInflaterFactory;
import solutions.alterego.androidbound.android.converters.BooleanToVisibilityConverter;
import solutions.alterego.androidbound.android.converters.FontConverter;
import solutions.alterego.androidbound.android.interfaces.IBindableLayoutInflaterFactory;
import solutions.alterego.androidbound.android.interfaces.IFontManager;
import solutions.alterego.androidbound.android.interfaces.IImageLoader;
import solutions.alterego.androidbound.android.viewresolvers.NullViewResolver;
import solutions.alterego.androidbound.binding.NullViewBindingEngine;
import solutions.alterego.androidbound.binding.ViewBindingEngine;
import solutions.alterego.androidbound.converters.interfaces.IValueConverter;
import solutions.alterego.androidbound.interfaces.ILogger;
import solutions.alterego.androidbound.interfaces.IViewBinder;
import solutions.alterego.androidbound.interfaces.IViewBindingEngine;
import solutions.alterego.androidbound.android.viewresolvers.ChainedViewResolver;
import solutions.alterego.androidbound.android.viewresolvers.ViewResolver;
import solutions.alterego.androidbound.android.interfaces.IViewResolver;

@Accessors(prefix = "m")
public class ViewBinder implements IViewBinder {

    @Getter
    @Setter
    private ILogger mLogger = NullLogger.instance;

    private WeakReference<Context> mContext;

    private IBindableLayoutInflaterFactory mInflaterFactory = NullBindableLayoutInflaterFactory.instance;

    private IViewResolver mViewResolver = NullViewResolver.instance;

    @Getter
    private IFontManager mFontManager;

    @Getter
    private boolean mDebugMode;

    @Getter
    private IViewBindingEngine mViewBindingEngine = NullViewBindingEngine.instance;

    /**
     * For APIs >= 11 && < 21, there was a framework bug that prevented a LayoutInflater's
     * Factory2 from being merged properly if set after a cloneInContext from a LayoutInflater
     * that already had a Factory2 registered. We work around that bug here. If we can't we
     * log an error.
     */
    @Getter
    private boolean mCheckedField = false;

    @Getter
    private Field mLayoutInflaterFactory2Field = null;

    public ViewBinder(Context ctx) {
        setContext(ctx);
        init();
    }

    public ViewBinder(Context ctx, ILogger logger) {
        setLogger(logger);
        setContext(ctx);
        init();
    }

    private void init() {
        mViewBindingEngine = new ViewBindingEngine(getLogger());

        mViewResolver = new ChainedViewResolver(new ViewResolver(getLogger()));
        mInflaterFactory = new BindableLayoutInflaterFactory(this, mViewResolver);
        setFontManager(new FontManager(getLogger()));

        registerDefaultConverters();
    }

    @Override
    public void setDebugMode(boolean debugMode) {
        mDebugMode = debugMode;
        mViewBindingEngine.setDebugMode(debugMode);
    }

    @Override
    public void setContext(Context ctx) {
        if (getContext() != ctx) {
            getLogger().verbose("old context = " + mContext + ", new context = " + ctx);

            if (mContext != null) {
                mContext.clear();
            }

            if (ctx != null) {
                mContext = new WeakReference<>(ctx);
            }
        }
    }

    @Override
    public Context getContext() {
        if (mContext != null) {
            return mContext.get();
        }

        return null;
    }

    private void registerDefaultConverters() {
        registerConverter(BooleanToVisibilityConverter.getConverterName(), new BooleanToVisibilityConverter());
    }

    @Override
    public void registerConverter(String name, IValueConverter converter) {
        mViewBindingEngine.registerConverter(name, converter);
    }

    @Override
    public void registerResource(String name, Object resource) {
        mViewBindingEngine.registerResource(name, resource);
    }

    @Override
    public void registerViewResolver(IViewResolver resolver) {
        if (mViewResolver instanceof ChainedViewResolver) {
            ((ChainedViewResolver) mViewResolver).addResolverToFront(resolver);
        }
    }

    @Override
    public void unregisterViewResolver(IViewResolver resolver) {
        if (mViewResolver instanceof ChainedViewResolver) {
            ((ChainedViewResolver) mViewResolver).removeResolver(resolver);
        }
    }

    @Override
    public void setFontManager(IFontManager fontManager) {
        mFontManager = fontManager;
        registerConverter(FontConverter.getConverterName(), new FontConverter(getFontManager(), getLogger()));
    }

    @Override
    public void setImageLoader(IImageLoader imageLoader) {
        mViewBindingEngine.setImageLoader(imageLoader);
    }

    @Override
    public View inflate(Context context, Object source, int layoutResID, ViewGroup viewGroup) {
        return inflate(context, source, layoutResID, viewGroup, viewGroup != null);
    }

    @Override
    public View inflate(Context context, Object source, int layoutResID, ViewGroup viewGroup, IViewResolver resolver) {
        View view = null;

        if (mViewResolver instanceof ChainedViewResolver) {
            ((ChainedViewResolver) mViewResolver).addResolverToFront(resolver);
            view = inflate(context, source, layoutResID, viewGroup);
            ((ChainedViewResolver) mViewResolver).removeResolver(resolver);
        }

        return view;
    }

    @Override
    public void disposeOf(Context context) {
        mViewBindingEngine.disposeOf(context);
    }

    @Override
    public View inflate(Context context, Object source, int layoutResID, ViewGroup viewGroup, boolean attachToRoot) {
        LayoutInflater inflater = LayoutInflater.from(context).cloneInContext(context);

        if (android.os.Build.VERSION.SDK_INT >= 11 && context instanceof Factory2) {
            setFactory2(context, source, inflater);
        } else if (context instanceof Factory) {
            inflater.setFactory(mInflaterFactory.inflaterFor(source, (Factory) context));
        } else {
            inflater.setFactory(mInflaterFactory.inflaterFor(source));
        }

        return inflater.inflate(layoutResID, viewGroup, attachToRoot);
    }

    @TargetApi(Build.VERSION_CODES.HONEYCOMB)
    private void setFactory2(Context context, Object source, LayoutInflater inflater) {
        LayoutInflater.Factory2 factory2 = mInflaterFactory.inflaterFor(source, (Factory2) context);
        inflater.setFactory2(factory2);

        //code from android.support.v4.view.LayoutInflaterCompatHC
        if (android.os.Build.VERSION.SDK_INT < 21) {
            final LayoutInflater.Factory f = inflater.getFactory();
            if (f instanceof LayoutInflater.Factory2) {
                // The merged factory is now set to getFactory(), but not getFactory2() (pre-v21).
                // We will now try and force set the merged factory to mFactory2
                forceSetFactory2(inflater, (LayoutInflater.Factory2) f);
            } else {
                // Else, we will force set the original wrapped Factory2
                forceSetFactory2(inflater, factory2);
            }
        }
    }

    //code from android.support.v4.view.LayoutInflaterCompatHC
    private void forceSetFactory2(LayoutInflater inflater, LayoutInflater.Factory2 factory) {
        if (!mCheckedField) {
            try {
                mLayoutInflaterFactory2Field = LayoutInflater.class.getDeclaredField("mFactory2");
                mLayoutInflaterFactory2Field.setAccessible(true);
            } catch (NoSuchFieldException e) {
                mLogger.error("forceSetFactory2 Could not find field 'mFactory2' on class " + LayoutInflater.class.getName()
                        + "; inflation may have unexpected results." + e.getMessage());
            }
            mCheckedField = true;
        }
        if (mLayoutInflaterFactory2Field != null) {
            try {
                mLayoutInflaterFactory2Field.set(inflater, factory);
            } catch (IllegalAccessException e) {
                mLogger.error(
                        "forceSetFactory2 could not set the Factory2 on LayoutInflater " + inflater + "; inflation may have unexpected results." + e
                                .getMessage());
            }
        }
    }

    @Override
    public void dispose() {
        if (mContext != null) {
            mContext.clear();
        }

        mViewBindingEngine.dispose();
        mViewBindingEngine = NullViewBindingEngine.instance;

        mViewResolver.dispose();
        mViewResolver = NullViewResolver.instance;

        mInflaterFactory = NullBindableLayoutInflaterFactory.instance;
        mLogger = NullLogger.instance;
        mFontManager = null;
    }
}
