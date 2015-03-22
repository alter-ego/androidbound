package solutions.alterego.androidbound;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.LayoutInflater.Factory;
import android.view.LayoutInflater.Factory2;
import android.view.View;
import android.view.ViewGroup;

import com.alterego.advancedandroidlogger.implementations.NullAndroidLogger;
import com.alterego.advancedandroidlogger.interfaces.IAndroidLogger;
import solutions.alterego.androidbound.android.BindableLayoutInflaterFactory;
import solutions.alterego.androidbound.android.converters.BooleanToVisibilityConverter;
import solutions.alterego.androidbound.android.converters.FontConverter;
import solutions.alterego.androidbound.android.interfaces.IBindableLayoutInflaterFactory;
import solutions.alterego.androidbound.android.interfaces.IFontManager;
import solutions.alterego.androidbound.binders.TextSpecificationBinder;
import solutions.alterego.androidbound.factories.SourceBindingFactory;
import solutions.alterego.androidbound.factories.TargetBindingFactory;
import solutions.alterego.androidbound.interfaces.IBinder;
import solutions.alterego.androidbound.interfaces.IBindingAssociation;
import solutions.alterego.androidbound.interfaces.IValueConverter;
import solutions.alterego.androidbound.interfaces.IViewBinder;
import solutions.alterego.androidbound.interfaces.IViewResolver;
import solutions.alterego.androidbound.parsers.BindingSpecificationListParser;
import solutions.alterego.androidbound.parsers.BindingSpecificationParser;
import solutions.alterego.androidbound.services.ResourceService;
import solutions.alterego.androidbound.services.ValueConverterService;
import solutions.alterego.androidbound.zzzztoremove.reactive.IScheduler;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;

@Accessors(prefix="m")
public class ViewBinder implements IViewBinder {

	@Getter @Setter Context mContext;
	@Getter @Setter private static IAndroidLogger mLogger = NullAndroidLogger.instance;
    private ValueConverterService mConverterService;
    private ResourceService mResourceService;
    private IBindableLayoutInflaterFactory mInflaterFactory;
    private ChainedViewResolver mViewResolver;
    private Map<View, List<IBindingAssociation>> mBoundViews = new HashMap<View, List<IBindingAssociation>>();
    @Getter @Setter private IFontManager mFontManager;

    public ViewBinder(Context ctx, IScheduler notificationScheduler, IAndroidLogger logger) {
        this(ctx, notificationScheduler, logger, null);
    }
    
    public ViewBinder(Context ctx, IScheduler notificationScheduler, IAndroidLogger logger, ImageLoaderConfiguration imageLoaderConfiguration) {
    	setLogger(logger);
    	setContext(ctx);
        mConverterService = new ValueConverterService(getLogger());
        mResourceService = new ResourceService(getLogger());

        SourceBindingFactory sourceFactory = new SourceBindingFactory(getLogger());
        TargetBindingFactory targetFactory = new TargetBindingFactory(notificationScheduler, getLogger());
        BindingSpecificationParser bindingParser = new BindingSpecificationParser(mConverterService, mResourceService, getLogger());
        BindingSpecificationListParser listParser = new BindingSpecificationListParser(bindingParser, getLogger());
        
        IBinder binder = new TextSpecificationBinder(listParser, sourceFactory, targetFactory, getLogger());

        mViewResolver = new ChainedViewResolver(new ViewResolver(getLogger()));
        mInflaterFactory = new BindableLayoutInflaterFactory(binder, this, mViewResolver);
        setFontManager(new FontManager(getLogger()));

        registerDefaultConverters();

        if (imageLoaderConfiguration==null) {
            ImageLoader.getInstance().init(getDefaultImageLoaderConfig(ctx));
        } else {
            ImageLoader.getInstance().init(imageLoaderConfiguration);
        }

    }

    private ImageLoaderConfiguration getDefaultImageLoaderConfig (Context ctx) {
        DisplayImageOptions defaultOptions = new DisplayImageOptions.Builder()
                .cacheInMemory(true)
                .cacheOnDisc(true)
                .build();
        ImageLoaderConfiguration config = new ImageLoaderConfiguration.Builder(ctx)
                .defaultDisplayImageOptions(defaultOptions)
                .build();
        return config;
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
    public void clearBindingForViewAndChildren(View rootView) {
        this.clearBindingsFor(rootView);

        if (!(rootView instanceof ViewGroup)) {
            return;
        }

        ViewGroup viewGroup = (ViewGroup) rootView;
        for (int i = 0; i < viewGroup.getChildCount(); i++) {
            this.clearBindingForViewAndChildren(viewGroup.getChildAt(i));
        }
    }

    @Override
    public void clearBindingsFor(View view) {
        if (!mBoundViews.containsKey(view)) {
            return;
        }

        List<IBindingAssociation> bindings = mBoundViews.get(view);

        for (IBindingAssociation binding : bindings) {
            binding.dispose();
        }

        bindings.clear();
        mBoundViews.remove(view);
    }

    @Override
    public void clearAllBindings() {
        for (List<IBindingAssociation> bindings : mBoundViews.values()) {
            for (IBindingAssociation binding : bindings) {
                binding.dispose();
            }
            bindings.clear();
        }
        mBoundViews.clear();
    }

    @Override
    public View inflate(Context context, Object source, int layoutResID, ViewGroup viewGroup) {
        if (source == null) {
            mLogger.error("ViewModel source cannot be null!");
            return null;
        }

        LayoutInflater inflater = LayoutInflater.from(context).cloneInContext(context);
      //TODO remove double casts???
        if (((Object) context) instanceof Factory) {
            inflater.setFactory(mInflaterFactory.inflaterFor(source, (Factory) (Object) context));
        } else if (((Object) context) instanceof Factory2) {
            inflater.setFactory(mInflaterFactory.inflaterFor(source, (Factory2) (Object) context));
        } else {
            inflater.setFactory(mInflaterFactory.inflaterFor(source));
        }

        return inflater.inflate(layoutResID, viewGroup);
    }

	@Override
	public View inflate(Context context, Object source, int layoutResID, ViewGroup viewGroup, IViewResolver resolver) {
		LayoutInflater inflater = LayoutInflater.from(context).cloneInContext(context);

		mViewResolver.addResolverToFront(resolver);
		//TODO remove double casts???
        if (((Object) context) instanceof Factory) {
            inflater.setFactory(mInflaterFactory.inflaterFor(source, (Factory) (Object) context));
        } else if (((Object) context) instanceof Factory2) {
            inflater.setFactory(mInflaterFactory.inflaterFor(source, (Factory2) (Object) context));
        } else {
            inflater.setFactory(mInflaterFactory.inflaterFor(source));
        }

        View view = inflater.inflate(layoutResID, viewGroup);
        mViewResolver.removeResolver(resolver);

        return view;
	}


    @Override
    public void registerBindingsFor(View view, List<IBindingAssociation> bindings) {
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
    public List<IBindingAssociation> getBindingsFor(View view) {
        if (mBoundViews.containsKey(view)) {
            return mBoundViews.get(view);
        }

        return new ArrayList<IBindingAssociation> ();
    }

    @Override
    public List<IBindingAssociation> getBindingsForViewAndChildren(View rootView) {
        return this.getBindingsForViewAndChildrenRecursive(rootView, new ArrayList<IBindingAssociation>());
    }

    private List<IBindingAssociation> getBindingsForViewAndChildrenRecursive(View rootView, List<IBindingAssociation> bindings) {

        if (this.mBoundViews.containsKey(rootView)) {
            bindings.addAll(this.mBoundViews.get(rootView));
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

}
