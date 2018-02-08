package solutions.alterego.androidbound.binding;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import solutions.alterego.androidbound.NullLogger;
import solutions.alterego.androidbound.android.interfaces.IImageLoader;
import solutions.alterego.androidbound.android.interfaces.INeedsBoundView;
import solutions.alterego.androidbound.android.interfaces.INeedsImageLoader;
import solutions.alterego.androidbound.binding.interfaces.IBinder;
import solutions.alterego.androidbound.binding.interfaces.IBindingAssociationEngine;
import solutions.alterego.androidbound.converters.ValueConverterService;
import solutions.alterego.androidbound.converters.interfaces.IValueConverter;
import solutions.alterego.androidbound.factories.SourceBindingFactory;
import solutions.alterego.androidbound.factories.TargetBindingFactory;
import solutions.alterego.androidbound.interfaces.IDisposable;
import solutions.alterego.androidbound.interfaces.ILogger;
import solutions.alterego.androidbound.interfaces.INeedsLogger;
import solutions.alterego.androidbound.interfaces.IViewBindingEngine;
import solutions.alterego.androidbound.parsers.BindingSpecificationListParser;
import solutions.alterego.androidbound.parsers.BindingSpecificationParser;
import solutions.alterego.androidbound.resources.ResourceService;

public class ViewBindingEngine implements IViewBindingEngine {

    private ILogger mLogger = NullLogger.instance;

    private IImageLoader mImageLoader = IImageLoader.nullImageLoader;

    private boolean mDebugMode;

    private ValueConverterService mConverterService;

    private ResourceService mResourceService;

    private IBinder mBinder;

    protected Map<View, List<IBindingAssociationEngine>> mBoundViews = new ConcurrentHashMap<>();

    private Map<View, String> mLazyBoundViews = new ConcurrentHashMap<>();

    public ViewBindingEngine(ILogger logger) {
        setLogger(logger);
        mConverterService = new ValueConverterService(getLogger());
        mResourceService = new ResourceService(getLogger());

        SourceBindingFactory sourceFactory = new SourceBindingFactory(getLogger());
        TargetBindingFactory targetFactory = new TargetBindingFactory(getLogger());
        BindingSpecificationParser bindingParser = new BindingSpecificationParser(mConverterService, mResourceService, getLogger());
        BindingSpecificationListParser listParser = new BindingSpecificationListParser(bindingParser, getLogger());

        mBinder = new TextSpecificationBinder(listParser, sourceFactory, targetFactory, getLogger());
    }

    @Override
    public void registerConverter(IValueConverter converter) {
        mConverterService.registerConverter(converter);
    }

    @Override
    public IValueConverter findConverter(String name) {
        return mConverterService.findConverter(name);
    }

    @Override
    public void registerResource(String name, Object resource) {
        mResourceService.registerResource(name, resource);
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
            bindViewToSource(source, view, mLazyBoundViews.get(view));
        } else if (mLazyBoundViews.containsKey(view)) {
            bindViewToSource(source, view, mLazyBoundViews.get(view));
            mLazyBoundViews.remove(view);
        }
    }

    @Override
    public void registerLazyBindingsFor(View view, String bindingString) {
        mLazyBoundViews.put(view, bindingString);
        if (mBoundViews.containsKey(view)) {
            clearBindingForViewAndChildren(view);
        }
    }

    @Override
    public void bindViewToSource(Object source, View view, String bindingString) {
        if (bindingString != null && !bindingString.equals("")) {
            getLogger().verbose("bindViewToSource binding view = " + view + " to source = " + source);

            List<IBindingAssociationEngine> bindings = mBinder.bind(source, view, bindingString);
            registerBindingsFor(view, bindings);
        }

        if (view != null) {
            if (source instanceof INeedsBoundView) {
                ((INeedsBoundView) source).setBoundView(view);
            }
            if (view instanceof INeedsImageLoader) {
                ((INeedsImageLoader) view).setImageLoader(mImageLoader);
            }
            if (view instanceof INeedsLogger) {
                ((INeedsLogger) view).setLogger(getLogger());
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
    public List<IBindingAssociationEngine> getBindingsForView(View rootView) {
        return getBindingsForViewAndChildrenRecursive(rootView, new ArrayList<IBindingAssociationEngine>());
    }

    protected List<IBindingAssociationEngine> getBindingsForViewAndChildrenRecursive(View rootView, List<IBindingAssociationEngine> bindings) {

        if (mBoundViews.containsKey(rootView)) {
            bindings.addAll(mBoundViews.get(rootView));
        }

        if (!(rootView instanceof ViewGroup)) {
            return bindings;
        }

        ViewGroup vg = (ViewGroup) rootView;

        for (int i = 0; i < vg.getChildCount(); i++) {
            View view = vg.getChildAt(i);
            if (view instanceof AbsListView) {
                continue;
            }
            getBindingsForViewAndChildrenRecursive(view, bindings);
        }
        return bindings;
    }

    @Override
    public void clearBindingForViewAndChildren(View rootView) {
        clearBindingsForView(rootView);

        if (rootView == null || !(rootView instanceof ViewGroup)) {
            return;
        }

        ViewGroup viewGroup = (ViewGroup) rootView;
        for (int i = 0; i < viewGroup.getChildCount(); i++) {
            clearBindingForViewAndChildren(viewGroup.getChildAt(i));
        }
    }

    protected void clearBindingsForView(View view) {
        if (view == null) {
            return;
        }

        mLogger.verbose("clearBindingsFor view = " + view + ", current bound views size = " + mBoundViews.size());

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

        mLogger.verbose("clearBindingsFor finished for view = " + view + ", remaining bound views size = " + mBoundViews.size());

        if (isDebugMode()) {
            for (View remainingview : mBoundViews.keySet()) {
                if (remainingview.getContext() == view.getContext()) {
                    mLogger.verbose(
                            "clearBindingsFor found another remaining view with the same context as " + view + ", context = " + view.getContext()
                                    + ", found remaining view = " + remainingview);
                }
            }
        }
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
    public void disposeOf(Context ctx) {
        getLogger().verbose("disposing of context = " + ctx);

        for (View view : mBoundViews.keySet()) {
            if (view.getContext() == ctx) {
                clearBindingsForView(view); //it doesn't go deep because we're gonna get all of them anyway
            }
        }
    }

    @Override
    public void dispose() {
        clearAllBindings();

        mBinder = null;
        mConverterService = null;
        mResourceService = null;
        mImageLoader = IImageLoader.nullImageLoader;
        mLogger = NullLogger.instance;
    }

    public ILogger getLogger() {
        return mLogger;
    }

    public IImageLoader getImageLoader() {
        return mImageLoader;
    }

    public boolean isDebugMode() {
        return mDebugMode;
    }

    public IBinder getBinder() {
        return mBinder;
    }

    public void setLogger(ILogger logger) {
        mLogger = logger;
    }

    public void setImageLoader(IImageLoader imageLoader) {
        mImageLoader = imageLoader;
    }

    public void setDebugMode(boolean debugMode) {
        mDebugMode = debugMode;
    }
}
