
package com.alterego.androidbound.android;

import java.util.List;

import android.annotation.TargetApi;
import android.content.Context;
import android.os.Build;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.LayoutInflater.Factory;
import android.view.LayoutInflater.Factory2;
import android.view.View;

import com.alterego.androidbound.BindingResources;
import com.alterego.androidbound.android.interfaces.IBindableLayoutInflaterFactory;
import com.alterego.androidbound.interfaces.IBindableView;
import com.alterego.androidbound.interfaces.IBinder;
import com.alterego.androidbound.interfaces.IBindingAssociation;
import com.alterego.androidbound.interfaces.IViewBinder;
import com.alterego.androidbound.interfaces.IViewResolver;

public class BindableLayoutInflaterFactory implements IBindableLayoutInflaterFactory {
    private IBinder binder;
    private IViewResolver resolver;
    private IViewBinder viewBinder;

    private static abstract class InflaterFactoryBase implements LayoutInflater.Factory {
        public InflaterFactoryBase(IViewBinder viewBinder, IBinder binder) {
            this.binder = binder;
            this.viewBinder = viewBinder;
        }

        protected void bindView(View view, Context context, AttributeSet attrs, Object source) {
            String bindingString = attrs.getAttributeValue(null,
                    BindingResources.attr.BindingBase.binding);

            if (bindingString != null && !bindingString.equals("")) {
                List<IBindingAssociation> bindings = binder.bind(source, view, bindingString);
                this.viewBinder.registerBindingsFor(view, bindings);
                //storeBindings(view, bindings);
            }
            /*
            TypedArray array = context.obtainStyledAttributes(attrs, R.styleable.binding);
            int styles = array.getIndexCount();
            for(int i = 0; i < styles; ++i) {
            	int id = array.getIndex(i);
            	if(id == R.styleable.binding_binding) {
            		String bindingString = array.getString(id);
            		List<IBindingAssociation> bindings = binder.bind(source, view, bindingString);
            		storeBindings(view, bindings);
            	}				
            }
            array.recycle();
            */
        }

        /*
        @SuppressWarnings("unchecked")
        protected void storeBindings(View view, List<IBindingAssociation> bindings) {
        	//Object tag = view.getTag(R.id.bindingTagUnique);
        	Object tag = view.getTag(BindingResources.id.bindingTagUnique);
        	if(tag != null)
        		for(IBindingAssociation binding : (List<IBindingAssociation>)tag)
        			binding.dispose();

        	//view.setTag(R.id.bindingTagUnique, bindings);
        	view.setTag(BindingResources.id.bindingTagUnique, bindings);
        }
        */

        private IBinder binder;
        private IViewBinder viewBinder;
    }

    public BindableLayoutInflaterFactory(IBinder binder, IViewBinder viewBinder,
            IViewResolver resolver) {
        this.binder = binder;
        this.viewBinder = viewBinder;
        this.resolver = resolver;
    }

    @Override
    public LayoutInflater.Factory inflaterFor(final Object source) {
        return new InflaterFactoryBase(this.viewBinder, this.binder) {
            public View onCreateView(String name, Context context, AttributeSet attrs) {
                View view = resolver.createView(name, context, attrs);
                if (view instanceof IBindableView) {
                    ((IBindableView) view).setViewBinder(viewBinder);
                }
                if (view != null)
                    bindView(view, context, attrs, source);
                return view;
            }
        };
    }

    @Override
    public Factory inflaterFor(final Object source, final Factory factory) {
        return new InflaterFactoryBase(this.viewBinder, this.binder) {
            public View onCreateView(String name, Context context, AttributeSet attrs) {
                View view = resolver.createView(name, context, attrs);
                if (view == null && factory != null) {
                    view = factory.onCreateView(name, context, attrs);
                }
                if (view instanceof IBindableView) {
                    ((IBindableView) view).setViewBinder(viewBinder);
                }
                if (view != null)
                    bindView(view, context, attrs, source);
                return view;
            }
        };
    }

    @TargetApi(Build.VERSION_CODES.HONEYCOMB)
    @Override
    public Factory inflaterFor(final Object source, final Factory2 factory2) {
        return new InflaterFactoryBase(this.viewBinder, this.binder) {
            public View onCreateView(String name, Context context, AttributeSet attrs) {
                View view = resolver.createView(name, context, attrs);
                if (view == null && factory2 != null) {
                    view = factory2.onCreateView(name, context, attrs);
                }
                if (view instanceof IBindableView) {
                    ((IBindableView) view).setViewBinder(viewBinder);
                }
                if (view != null)
                    bindView(view, context, attrs, source);
                return view;
            }
        };
    }
}
