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
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;
import solutions.alterego.androidbound.android.BindableLayoutInflaterFactory;
import solutions.alterego.androidbound.android.FontManager;
import solutions.alterego.androidbound.android.converters.BooleanToVisibilityConverter;
import solutions.alterego.androidbound.android.converters.FontConverter;
import solutions.alterego.androidbound.android.interfaces.IBindableLayoutInflaterFactory;
import solutions.alterego.androidbound.android.interfaces.IFontManager;
import solutions.alterego.androidbound.android.interfaces.IImageLoader;
import solutions.alterego.androidbound.android.interfaces.INeedsBoundView;
import solutions.alterego.androidbound.android.interfaces.INeedsImageLoader;
import solutions.alterego.androidbound.binding.TextSpecificationBinder;
import solutions.alterego.androidbound.binding.interfaces.IBindingAssociationEngine;
import solutions.alterego.androidbound.converters.ValueConverterService;
import solutions.alterego.androidbound.converters.interfaces.IValueConverter;
import solutions.alterego.androidbound.factories.SourceBindingFactory;
import solutions.alterego.androidbound.factories.TargetBindingFactory;
import solutions.alterego.androidbound.interfaces.IDisposable;
import solutions.alterego.androidbound.interfaces.ILogger;
import solutions.alterego.androidbound.interfaces.IViewBinder;
import solutions.alterego.androidbound.parsers.BindingSpecificationListParser;
import solutions.alterego.androidbound.parsers.BindingSpecificationParser;
import solutions.alterego.androidbound.resources.ResourceService;
import solutions.alterego.androidbound.viewresolvers.ChainedViewResolver;
import solutions.alterego.androidbound.viewresolvers.ViewResolver;
import solutions.alterego.androidbound.viewresolvers.interfaces.IViewResolver;

@Accessors(prefix = "m")
public class ViewBinder implements IViewBinder {

    @Getter
    @Setter
    private static ILogger mLogger = NullLogger.instance;

    WeakReference<Context> mContext;

    private ValueConverterService mConverterService;

    private ResourceService mResourceService;

    private IBindableLayoutInflaterFactory mInflaterFactory;

    private ChainedViewResolver mViewResolver;

    private TextSpecificationBinder mBinder;

    private Map<View, List<IBindingAssociationEngine>> mBoundViews = new HashMap<View, List<IBindingAssociationEngine>>();

    private Map<View, String> mLazyBoundViews = new HashMap<>();

    @Getter
    @Setter
    private IFontManager mFontManager;

    private IImageLoader mImageLoader = IImageLoader.nullImageLoader;

    public ViewBinder(Context ctx, ILogger logger) {
        setLogger(logger);
        setContext(ctx);
        mConverterService = new ValueConverterService(getLogger());
        mResourceService = new ResourceService(getLogger());

        SourceBindingFactory sourceFactory = new SourceBindingFactory(getLogger());
        TargetBindingFactory targetFactory = new TargetBindingFactory(getLogger());
        BindingSpecificationParser bindingParser = new BindingSpecificationParser(mConverterService, mResourceService, getLogger());
        BindingSpecificationListParser listParser = new BindingSpecificationListParser(bindingParser, getLogger());

        mBinder = new TextSpecificationBinder(listParser, sourceFactory, targetFactory, getLogger());

        mViewResolver = new ChainedViewResolver(new ViewResolver(getLogger()));
        mInflaterFactory = new BindableLayoutInflaterFactory(this, mViewResolver);
        setFontManager(new FontManager(getLogger()));

        registerDefaultConverters();
    }

    private void setContext(Context ctx) {
        if (mContext != null) {
            mContext.clear();
        }

        if (ctx != null) {
            mContext = new WeakReference<>(ctx);
        }
    }

    private Context getContext() {
        if (mContext != null) {
            return mContext.get();
        }

        return null;
    }

    private void registerDefaultConverters() {
        registerConverter(BooleanToVisibilityConverter.getConverterName(), new BooleanToVisibilityConverter());
        registerConverter(FontConverter.getConverterName(), new FontConverter(getFontManager(), getLogger()));
    }

    @Override
    public void registerConverter(String name, IValueConverter converter) {
        mConverterService.registerConverter(name, converter);
    }

    @Override
    public void registerResource(String name, Object resource) {
        mResourceService.registerResource(name, resource);
    }

    @Override
    public void registerViewResolver(IViewResolver resolver) {
        mViewResolver.addResolverToFront(resolver);
    }

    @Override
    public void unregisterViewResolver(IViewResolver resolver) {
        mViewResolver.removeResolver(resolver);
    }

    @Override
    public void registerLazyBindingsFor(View view, String bindingString) {
        mLazyBoundViews.put(view, bindingString);
        if (mBoundViews.containsKey(view)) {
            clearBindingForViewAndChildren(view);
        }
    }

    @Override
    public void lazyBindView(View view, Object source) {
        if (source == null) {
            mLogger.error("ViewModel source cannot be null!");
            return;
        }

        checkAndBindView(view, source);
    }

    private void checkAndBindView(View view, Object source) {
        mLogger.verbose("checking bindings for view = " + view + " and source = " + source);

        if (view instanceof ViewGroup) {
            for (int childIndex = 0; childIndex < ((ViewGroup) view).getChildCount(); childIndex++) {
                checkAndBindView(((ViewGroup) view).getChildAt(childIndex), source);
            }
        } else if (mLazyBoundViews.containsKey(view)) {
            bindViewToSource(source, view, mLazyBoundViews.get(view));
            mLazyBoundViews.remove(view);
        }
    }

    @Override
    public void bindViewToSource(Object source, View view, String bindingString) {
        if (bindingString != null && !bindingString.equals("")) {
            mLogger.verbose("bindViewToSource binding view = " + view + " to source = " + source);

            List<IBindingAssociationEngine> bindings = mBinder.bind(source, view, bindingString);
            registerBindingsFor(view, bindings);
        }

        if (view != null && source instanceof INeedsBoundView) {
            ((INeedsBoundView) source).setBoundView(view);
        }

        if (view != null && view instanceof INeedsImageLoader) {
            ((INeedsImageLoader) view).setImageLoader(mImageLoader);
        }
    }

    @Override
    public void clearBindingForViewAndChildren(View rootView) {
        clearBindingsFor(rootView);

        if (rootView == null || !(rootView instanceof ViewGroup)) {
            return;
        }

        ViewGroup viewGroup = (ViewGroup) rootView;
        for (int i = 0; i < viewGroup.getChildCount(); i++) {
            clearBindingForViewAndChildren(viewGroup.getChildAt(i));
        }
    }

    @Override
    public void clearBindingsFor(View view) {
        if (view == null) {
            return;
        }

        mLogger.verbose("clearBindingsFor view = " + view);

        if (mLazyBoundViews.containsKey(view)) {
            mLazyBoundViews.remove(view);
        }

        if (!mBoundViews.containsKey(view)) {
            return;
        }

        List<IBindingAssociationEngine> bindings = mBoundViews.get(view);

        for (IBindingAssociationEngine binding : bindings) {
            binding.dispose();
        }

        if (view instanceof IDisposable) {
            ((IDisposable) view).dispose();
        }

        bindings.clear();
        mBoundViews.remove(view);
    }

    @Override
    public void clearAllBindings() {
        for (List<IBindingAssociationEngine> bindings : mBoundViews.values()) {
            for (IBindingAssociationEngine binding : bindings) {
                binding.dispose();
            }
            bindings.clear();
        }
        mBoundViews.clear();
        mLazyBoundViews.clear();
    }

    @Override
    public View inflate(Context context, Object source, int layoutResID, ViewGroup viewGroup) {
        LayoutInflater inflater = LayoutInflater.from(context).cloneInContext(context);

        if (android.os.Build.VERSION.SDK_INT >= 11 && context instanceof Factory2) {
            setFactory2(context, source, inflater);
        } else if (context instanceof Factory) {
            inflater.setFactory(mInflaterFactory.inflaterFor(source, (Factory) context));
        } else {
            inflater.setFactory(mInflaterFactory.inflaterFor(source));
        }

        return inflater.inflate(layoutResID, viewGroup);
    }

    @Override
    public View inflate(Context context, Object source, int layoutResID, ViewGroup viewGroup, IViewResolver resolver) {

        mViewResolver.addResolverToFront(resolver);
        View view = this.inflate(context, source, layoutResID, viewGroup);
        mViewResolver.removeResolver(resolver);

        return view;
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

    /**
     * For APIs >= 11 && < 21, there was a framework bug that prevented a LayoutInflater's
     * Factory2 from being merged properly if set after a cloneInContext from a LayoutInflater
     * that already had a Factory2 registered. We work around that bug here. If we can't we
     * log an error.
     */
    private boolean sCheckedField;

    private Field sLayoutInflaterFactory2Field;

    //code from android.support.v4.view.LayoutInflaterCompatHC
    private void forceSetFactory2(LayoutInflater inflater, LayoutInflater.Factory2 factory) {
        if (!sCheckedField) {
            try {
                sLayoutInflaterFactory2Field = LayoutInflater.class.getDeclaredField("mFactory2");
                sLayoutInflaterFactory2Field.setAccessible(true);
            } catch (NoSuchFieldException e) {
                mLogger.error("forceSetFactory2 Could not find field 'mFactory2' on class " + LayoutInflater.class.getName()
                        + "; inflation may have unexpected results." + e.getMessage());
            }
            sCheckedField = true;
        }
        if (sLayoutInflaterFactory2Field != null) {
            try {
                sLayoutInflaterFactory2Field.set(inflater, factory);
            } catch (IllegalAccessException e) {
                mLogger.error(
                        "forceSetFactory2 could not set the Factory2 on LayoutInflater " + inflater + "; inflation may have unexpected results." + e
                                .getMessage());
            }
        }
    }

    @Override
    public void registerBindingsFor(View view, List<IBindingAssociationEngine> bindings) {
        if (view == null || bindings == null) {
            return;
        }

        if (mBoundViews.containsKey(view)) {
            mBoundViews.get(view).addAll(bindings);
        } else {
            mBoundViews.put(view, bindings);
        }
    }

    @Override
    public List<IBindingAssociationEngine> getBindingsFor(View view) {
        if (mBoundViews.containsKey(view)) {
            return mBoundViews.get(view);
        }

        return new ArrayList<IBindingAssociationEngine>();
    }

    @Override
    public List<IBindingAssociationEngine> getBindingsForViewAndChildren(View rootView) {
        return getBindingsForViewAndChildrenRecursive(rootView, new ArrayList<IBindingAssociationEngine>());
    }

    private List<IBindingAssociationEngine> getBindingsForViewAndChildrenRecursive(View rootView, List<IBindingAssociationEngine> bindings) {

        if (mBoundViews.containsKey(rootView)) {
            bindings.addAll(mBoundViews.get(rootView));
        }

        if (!(rootView instanceof ViewGroup)) {
            return bindings;
        }

        ViewGroup vg = (ViewGroup) rootView;

        for (int i = 0; i < vg.getChildCount(); i++) {
            getBindingsForViewAndChildrenRecursive(vg.getChildAt(i), bindings);
        }
        return bindings;
    }

    @Override
    public void dispose() {
        clearAllBindings();

        if (mContext != null) {
            mContext.clear();
        }

        mConverterService = null;
        mResourceService = null;
        mInflaterFactory = null;
        mViewResolver = null;
        mFontManager = null;
        mImageLoader = IImageLoader.nullImageLoader;
    }

    @Override
    public void setImageLoader(IImageLoader imageLoader) {
        mImageLoader = imageLoader;
    }
}
