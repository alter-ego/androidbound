package solutions.alterego.androidbound.android;

import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.content.Context;
import android.os.Build;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.LayoutInflater.Factory;
import android.view.LayoutInflater.Factory2;
import android.view.View;

import solutions.alterego.androidbound.android.interfaces.IBindableLayoutInflaterFactory;
import solutions.alterego.androidbound.android.interfaces.IBindableView;
import solutions.alterego.androidbound.android.ui.resources.BindingResources;
import solutions.alterego.androidbound.binding.interfaces.IBinder;
import solutions.alterego.androidbound.interfaces.IViewBinder;
import solutions.alterego.androidbound.viewresolvers.interfaces.IViewResolver;

public class BindableLayoutInflaterFactory implements IBindableLayoutInflaterFactory {

    private IBinder mBinder;

    private IViewResolver mViewResolver;

    private IViewBinder mViewBinder;

    public BindableLayoutInflaterFactory(IViewBinder viewBinder, IViewResolver resolver) {
        mViewBinder = viewBinder;
        mViewResolver = resolver;
    }

    @Override
    public LayoutInflater.Factory inflaterFor(final Object source) {
        return new InflaterFactoryBase(mViewBinder) {
            public View onCreateView(String name, Context context, AttributeSet attrs) {
                View view = mViewResolver.createView(name, context, attrs);
                if (view instanceof IBindableView) {
                    ((IBindableView) view).setViewBinder(mViewBinder);
                }
                if (view != null) {
                    if (source != null) {
                        bindView(view, context, attrs, source);
                    } else {
                        bindViewWithoutSource(view, attrs);
                    }
                }
                return view;
            }
        };
    }

    @Override
    public Factory inflaterFor(final Object source, final Factory factory) {
        return new InflaterFactoryBase(mViewBinder) {
            public View onCreateView(String name, Context context, AttributeSet attrs) {
                View view = mViewResolver.createView(name, context, attrs);
                if (view == null && factory != null) {
                    view = factory.onCreateView(name, context, attrs);
                }
                if (view instanceof IBindableView) {
                    ((IBindableView) view).setViewBinder(mViewBinder);
                }
                if (view != null) {
                    if (source != null) {
                        bindView(view, context, attrs, source);
                    } else {
                        bindViewWithoutSource(view, attrs);
                    }
                }
                return view;
            }
        };
    }

    @TargetApi(Build.VERSION_CODES.HONEYCOMB)
    @Override
    public Factory2 inflaterFor(final Object source, final Factory2 factory2) {
        return new InflaterFactory2Base(mViewBinder) {
            @Override
            public View onCreateView(View parent, String name, Context context, AttributeSet attrs) {
                View view = mViewResolver.createView(name, context, attrs);
                if (view == null && factory2 != null) {
                    view = factory2.onCreateView(name, context, attrs);
                }
                if (view instanceof IBindableView) {
                    ((IBindableView) view).setViewBinder(mViewBinder);
                }
                if (view != null) {
                    if (source != null) {
                        bindView(view, context, attrs, source);
                    } else {
                        bindViewWithoutSource(view, attrs);
                    }
                }
                return view;
            }

            public View onCreateView(String name, Context context, AttributeSet attrs) {
                View view = mViewResolver.createView(name, context, attrs);
                if (view == null && factory2 != null) {
                    view = factory2.onCreateView(name, context, attrs);
                }
                if (view instanceof IBindableView) {
                    ((IBindableView) view).setViewBinder(mViewBinder);
                }
                if (view != null) {
                    if (source != null) {
                        bindView(view, context, attrs, source);
                    } else {
                        bindViewWithoutSource(view, attrs);
                    }
                }
                return view;
            }
        };
    }

    private static abstract class InflaterFactoryBase implements LayoutInflater.Factory {

        private IViewBinder mBaseViewBinder;

        InflaterFactoryBase(IViewBinder viewBinder) {
            mBaseViewBinder = viewBinder;
        }

        void bindView(View view, Context context, AttributeSet attrs, Object source) {
            mBaseViewBinder.bindViewToSource(source, view, attrs.getAttributeValue(null, BindingResources.attr.BindingBase.binding));
        }

        void bindViewWithoutSource(View view, AttributeSet attrs) {
            String bindingString = attrs.getAttributeValue(null, BindingResources.attr.BindingBase.binding);

            if (bindingString != null && !bindingString.equals("")) {
                mBaseViewBinder.registerLazyBindingsFor(view, bindingString);
            }
        }
    }

    @SuppressLint("NewApi")
    private static abstract class InflaterFactory2Base implements LayoutInflater.Factory2 {

        private IViewBinder mBaseViewBinder;

        InflaterFactory2Base(IViewBinder viewBinder) {
            mBaseViewBinder = viewBinder;
        }

        void bindView(View view, Context context, AttributeSet attrs, Object source) {
            mBaseViewBinder.bindViewToSource(source, view, attrs.getAttributeValue(null, BindingResources.attr.BindingBase.binding));
        }

        void bindViewWithoutSource(View view, AttributeSet attrs) {
            String bindingString = attrs.getAttributeValue(null, BindingResources.attr.BindingBase.binding);

            if (bindingString != null && !bindingString.equals("")) {
                mBaseViewBinder.registerLazyBindingsFor(view, bindingString);
            }
        }
    }
}
