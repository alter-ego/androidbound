package solutions.alterego.androidbound.android;

import android.annotation.TargetApi;
import android.content.Context;
import android.os.Build;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.LayoutInflater.Factory;
import android.view.LayoutInflater.Factory2;
import android.view.View;

import java.util.List;

import solutions.alterego.androidbound.BindingResources;
import solutions.alterego.androidbound.android.interfaces.IBindableLayoutInflaterFactory;
import solutions.alterego.androidbound.interfaces.IBindableView;
import solutions.alterego.androidbound.interfaces.IBinder;
import solutions.alterego.androidbound.interfaces.IBindingAssociation;
import solutions.alterego.androidbound.interfaces.IViewBinder;
import solutions.alterego.androidbound.interfaces.IViewResolver;

public class BindableLayoutInflaterFactory implements IBindableLayoutInflaterFactory {

    private IBinder mBinder;

    private IViewResolver mViewResolver;

    private IViewBinder mViewBinder;

    public BindableLayoutInflaterFactory(IBinder binder, IViewBinder viewBinder, IViewResolver resolver) {
        mBinder = binder;
        mViewBinder = viewBinder;
        mViewResolver = resolver;
    }

    @Override
    public LayoutInflater.Factory inflaterFor(final Object source) {
        return new InflaterFactoryBase(mViewBinder, mBinder) {
            public View onCreateView(String name, Context context, AttributeSet attrs) {
                View view = mViewResolver.createView(name, context, attrs);
                if (view instanceof IBindableView) {
                    ((IBindableView) view).setViewBinder(mViewBinder);
                }
                if (view != null) {
                    bindView(view, context, attrs, source);
                }
                return view;
            }
        };
    }

    @Override
    public Factory inflaterFor(final Object source, final Factory factory) {
        return new InflaterFactoryBase(mViewBinder, mBinder) {
            public View onCreateView(String name, Context context, AttributeSet attrs) {
                View view = mViewResolver.createView(name, context, attrs);
                if (view == null && factory != null) {
                    view = factory.onCreateView(name, context, attrs);
                }
                if (view instanceof IBindableView) {
                    ((IBindableView) view).setViewBinder(mViewBinder);
                }
                if (view != null) {
                    bindView(view, context, attrs, source);
                }
                return view;
            }
        };
    }

    @TargetApi(Build.VERSION_CODES.HONEYCOMB)
    @Override
    public Factory inflaterFor(final Object source, final Factory2 factory2) {
        return new InflaterFactoryBase(mViewBinder, mBinder) {
            public View onCreateView(String name, Context context, AttributeSet attrs) {
                View view = mViewResolver.createView(name, context, attrs);
                if (view == null && factory2 != null) {
                    view = factory2.onCreateView(name, context, attrs);
                }
                if (view instanceof IBindableView) {
                    ((IBindableView) view).setViewBinder(mViewBinder);
                }
                if (view != null) {
                    bindView(view, context, attrs, source);
                }
                return view;
            }
        };
    }

    private static abstract class InflaterFactoryBase implements LayoutInflater.Factory {

        private IBinder mBaseBinder;

        private IViewBinder mBaseViewBinder;

        public InflaterFactoryBase(IViewBinder viewBinder, IBinder binder) {
            mBaseBinder = binder;
            mBaseViewBinder = viewBinder;
        }

        protected void bindView(View view, Context context, AttributeSet attrs, Object source) {
            String bindingString = attrs.getAttributeValue(null,
                    BindingResources.attr.BindingBase.binding);

            if (bindingString != null && !bindingString.equals("")) {
                List<IBindingAssociation> bindings = mBaseBinder.bind(source, view, bindingString);
                mBaseViewBinder.registerBindingsFor(view, bindings);
            }
        }

    }
}
