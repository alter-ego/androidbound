package com.alterego.androidbound;

import com.alterego.androidbound.android.BindableLayoutInflaterFactory;
import com.alterego.androidbound.android.converters.BooleanToVisibilityConverter;
import com.alterego.androidbound.android.converters.FontConverter;
import com.alterego.androidbound.android.interfaces.IBindableLayoutInflaterFactory;
import com.alterego.androidbound.android.interfaces.IFontManager;
import com.alterego.androidbound.binders.TextSpecificationBinder;
import com.alterego.androidbound.factories.SourceBindingFactory;
import com.alterego.androidbound.factories.TargetBindingFactory;
import com.alterego.androidbound.interfaces.IBinder;
import com.alterego.androidbound.interfaces.IBindingAssociation;
import com.alterego.androidbound.interfaces.IValueConverter;
import com.alterego.androidbound.interfaces.IViewBinder;
import com.alterego.androidbound.interfaces.IViewResolver;
import com.alterego.androidbound.parsers.BindingSpecificationListParser;
import com.alterego.androidbound.parsers.BindingSpecificationParser;
import com.alterego.androidbound.services.ResourceService;
import com.alterego.androidbound.services.ValueConverterService;
import com.alterego.androidbound.zzzztoremove.reactive.IScheduler;

import com.alterego.advancedandroidlogger.implementations.NullAndroidLogger;
import com.alterego.advancedandroidlogger.interfaces.IAndroidLogger;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.LayoutInflater.Factory;
import android.view.LayoutInflater.Factory2;
import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;

@Accessors(prefix="m")
public class ViewBinder implements IViewBinder {
    private static List<IBindingAssociation> emptyBindings = Arrays.asList(new IBindingAssociation[0]);
    @Getter @Setter private IAndroidLogger mLogger = NullAndroidLogger.instance;

    private ValueConverterService converters;
    private ResourceService resources;
    private IBindableLayoutInflaterFactory inflaterFactory;
    private ChainedViewResolver viewResolver;
    private Map<View, List<IBindingAssociation>> boundViews = new HashMap<View, List<IBindingAssociation>>();
    @Getter @Setter private IFontManager mFontManager;

    public ViewBinder(IScheduler notificationScheduler, IAndroidLogger logger) {
        setLogger(logger);
        converters = new ValueConverterService(logger);
        resources = new ResourceService(logger);

        SourceBindingFactory sourceFactory = new SourceBindingFactory(logger);
        TargetBindingFactory targetFactory = new TargetBindingFactory(notificationScheduler, logger);

        BindingSpecificationParser bindingParser = new BindingSpecificationParser(converters, resources, logger);
        BindingSpecificationListParser listParser = new BindingSpecificationListParser( bindingParser, logger);
        IBinder binder = new TextSpecificationBinder(listParser, sourceFactory, targetFactory, logger);


        viewResolver = new ChainedViewResolver(new ViewResolver(logger));
        inflaterFactory = new BindableLayoutInflaterFactory(binder, this, viewResolver);
        setFontManager(new FontManager(getLogger()));

        registerDefaultConverters();
    }
    
    private void registerDefaultConverters() {
    	registerConverter(BooleanToVisibilityConverter.getConverterName(), new BooleanToVisibilityConverter());
    	registerConverter(FontConverter.getConverterName(), new FontConverter(getFontManager(), getLogger()));
    }

    @Override
    public void registerConverter(String name, IValueConverter converter) {
        converters.registerConverter(name, converter);
    }

    @Override
    public void registerResource(String name, Object resource) {
        resources.registerResource(name, resource);
    }

    @Override
    public void registerViewResolver(IViewResolver resolver) {
    	this.viewResolver.addResolverFront(resolver);
    }

    @Override
    public void unregisterViewResolver(IViewResolver resolver) {
    	this.viewResolver.removeesolver(resolver);
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
        if (!boundViews.containsKey(view)) {
            return;
        }

        List<IBindingAssociation> bindings = boundViews.get(view);

        for (IBindingAssociation binding : bindings) {
            binding.dispose();
        }

        bindings.clear();
        boundViews.remove(view);
    }

    @Override
    public void clearAllBindings() {
        for (List<IBindingAssociation> bindings : boundViews.values()) {
            for (IBindingAssociation binding : bindings) {
                binding.dispose();
            }
            bindings.clear();
        }
        boundViews.clear();
    }

    @Override
    public View inflate(Context context, Object source, int layoutResID, ViewGroup viewGroup) {
        if (source == null) {
            mLogger.error("ViewModel is null -_-'");
            return null;
        }

        LayoutInflater inflater = LayoutInflater.from(context).cloneInContext(context);

        if (((Object) context) instanceof Factory) {
            inflater.setFactory(inflaterFactory.inflaterFor(source, (Factory) (Object) context));
        } else if (((Object) context) instanceof Factory2) {
            inflater.setFactory(inflaterFactory.inflaterFor(source, (Factory2) (Object) context));
        } else {
            inflater.setFactory(inflaterFactory.inflaterFor(source));
        }

        return inflater.inflate(layoutResID, viewGroup);
    }

	@Override
	public View inflate(Context context, Object source, int layoutResID, ViewGroup viewGroup, IViewResolver additionalResolver) {
		LayoutInflater inflater = LayoutInflater.from(context).cloneInContext(context);

		this.viewResolver.addResolverFront(additionalResolver);

        if (((Object) context) instanceof Factory) {
            inflater.setFactory(inflaterFactory.inflaterFor(source, (Factory) (Object) context));
        } else if (((Object) context) instanceof Factory2) {
            inflater.setFactory(inflaterFactory.inflaterFor(source, (Factory2) (Object) context));
        } else {
            inflater.setFactory(inflaterFactory.inflaterFor(source));
        }

        View view = inflater.inflate(layoutResID, viewGroup);

        this.viewResolver.removeesolver(additionalResolver);

        return view;
	}


    @Override
    public void registerBindingsFor(View view, List<IBindingAssociation> bindings) {
        if (view == null || bindings == null) {
            return;
        }

        if (boundViews.containsKey(view)) {
            boundViews.get(view).addAll(bindings);
        } else {
            boundViews.put(view, bindings);
        }
    }

    @Override
    public List<IBindingAssociation> getBindingsFor(View view) {
        if (boundViews.containsKey(view)) {
            return boundViews.get(view);
        }

        return emptyBindings;
    }

    @Override
    public List<IBindingAssociation> getBindingsForViewAndChildren(View rootView) {
        return this.getBindingsForViewAndChildrenRecursive(rootView,
                new ArrayList<IBindingAssociation>());
    }

    private List<IBindingAssociation> getBindingsForViewAndChildrenRecursive(View rootView,
            List<IBindingAssociation> bindings) {

        if (this.boundViews.containsKey(rootView)) {
            bindings.addAll(this.boundViews.get(rootView));
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

    public int size() {
        return boundViews.size();
    }
    
    private static class ChainedViewResolver implements IViewResolver {
    	private List<IViewResolver> baseResolvers;

    	public ChainedViewResolver() {
    		this.baseResolvers = new ArrayList<IViewResolver>();
    	}
    	public ChainedViewResolver(IViewResolver...initialViewResolvers) {
    		this();
    		if(initialViewResolvers == null) {
    			return;
    		}

    		for(IViewResolver r: initialViewResolvers) {
    			this.baseResolvers.add(r);
    		}
    	}

		@Override
		public void setLogger(IAndroidLogger logger) {
		}

		@Override
		public View createView(String name, Context context, AttributeSet attrs) {
			for(IViewResolver resolver: this.baseResolvers) {
				View retval = resolver.createView(name, context, attrs);
				if(retval != null) {
					return retval;
				}
			}

			return null;
		}

		public void addResolverFront(IViewResolver resolver) {
			this.baseResolvers.add(0, resolver);
		}

		public void addResolverBack(IViewResolver resolver) {
			this.baseResolvers.add(resolver);
		}

		public void removeesolver(IViewResolver resolver) {
			this.baseResolvers.remove(resolver);
		}
    }
}
