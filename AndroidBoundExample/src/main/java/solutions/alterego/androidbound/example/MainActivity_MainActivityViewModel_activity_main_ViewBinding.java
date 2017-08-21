package solutions.alterego.androidbound.example;

import android.support.annotation.UiThread;
import android.view.View;

import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import solutions.alterego.androidbound.NullLogger;
import solutions.alterego.androidbound.android.ViewUtils;
import solutions.alterego.androidbound.android.interfaces.IFontManager;
import solutions.alterego.androidbound.android.interfaces.IImageLoader;
import solutions.alterego.androidbound.android.interfaces.INeedsBoundView;
import solutions.alterego.androidbound.android.interfaces.INeedsImageLoader;
import solutions.alterego.androidbound.android.ui.BindableButton;
import solutions.alterego.androidbound.binding.interfaces.IBinder;
import solutions.alterego.androidbound.binding.interfaces.IBindingAssociationEngine;
import solutions.alterego.androidbound.example.viewmodels.MainActivityViewModel;
import solutions.alterego.androidbound.interfaces.IDisposable;
import solutions.alterego.androidbound.interfaces.ILogger;
import solutions.alterego.androidbound.interfaces.INeedsLogger;
import solutions.alterego.androidbound.interfaces.IViewBinder;

//class name: {caller}_{source}_{layout}_ViewBinding
public class MainActivity_MainActivityViewModel_activity_main_ViewBinding implements IDisposable {

    private ILogger mLogger;

    private MainActivityViewModel mViewModel;

    private IFontManager mFontManager;

    private View mView;

    private IBinder mBinder;

    private IImageLoader mImageLoader;

    private Map<View, List<IBindingAssociationEngine>> mBoundViews = new ConcurrentHashMap<View, List<IBindingAssociationEngine>>();

    //comes from generated XML that has all supported classed with IDs replaced through ViewResolver
    private BindableButton open_bindable_activity_button; //maps through ViewResolver? Button -> BindableButton; member name is view id

    private static String open_bindable_activity_button_bindingString = "{Click @- OpenBindableActivity};\n"
            + "                    {Text @= OpenBindableActivityText };\n"
            + "                    {Typeface @- ToFont(this, &apos;missing&apos;)};\n"
            + "                    {BackgroundColor @= ButtonBackgroundColor};\n"
            + "                    {TextColor @= MainActivityTitleColor};\n"
            + "                    {ContentDescription @= OpenActivityButtonContentDescription}";

    //this is gonna get checked later by the ViewBinder to see if it needs to be viewbound runtime
    //list of view ids bound compile-time checked by ViewBindingEngine.bindViewToSource
    public static final List<String> getCompileTimeBoundIds() {
        return Arrays.asList("open_bindable_activity_button");
    }

    //called from View view = mViewBinder.inflate(this, mViewModel, R.layout.activity_main, null);
    //setContentView(view);
    //this constructor call is added/written to the calling activity/fragment

    //then the viewbinder checks if the runtime binding id is present in the mBoundIds; if not, it is bound runtime
    @UiThread
    public MainActivity_MainActivityViewModel_activity_main_ViewBinding(ILogger logger, IViewBinder viewBinder, MainActivityViewModel source,
            View target) {
        mLogger = logger;
        mViewModel = source;
        mView = target;
        mFontManager = viewBinder.getFontManager();
//        mBinder = viewBinder.getViewBindingEngine().getBinder();
//        mImageLoader = viewBinder.getViewBindingEngine().getImageLoader();

        //TODO setup converters
//        mToFontValueConverter = viewBinder.findConverter("ToFont"); ?? do we set them up or just use them for setting values?

        open_bindable_activity_button = ViewUtils
                .findRequiredViewAsType(target, R.id.open_bindable_activity_button, "field 'open_bindable_activity_button'", BindableButton.class);

        //setup fonts?
        open_bindable_activity_button.setTypeface(mFontManager.getFont("missing"));

        //doesn't get written if the bindingString is null or empty!
        bindViewToSource(mViewModel, open_bindable_activity_button, open_bindable_activity_button_bindingString);

        //use BindingAssociationEngine data?
        //TODO call oneWayOneTimeBindings(ToTarget, ToSource)
        //TODO setup oneWayBindings(ToTarget, ToSource) w/subscriptions
        //TODO setup twoWayBindings() w/subscriptions
        //?
        //log things?

        setINeedsBoundViewReferences();
        setINeedsImageLoader();
        setINeedsLogger();
    }

    public void setINeedsBoundViewReferences() { //read from viewBinder.getViewBindingEngine().bindViewToSource
        //not needed for open_bindable_activity_button
    }

    public void setINeedsImageLoader() { //read from viewBinder.getViewBindingEngine().bindViewToSource
        //not needed for open_bindable_activity_button
    }

    public void setINeedsLogger() { //read from viewBinder.getViewBindingEngine().bindViewToSource
        //not needed for open_bindable_activity_button
    }

    @UiThread
    public void bindViewToSource(Object source, View view, String bindingString) {
        if (bindingString != null && !bindingString.equals("")) {
            mLogger.verbose("bindViewToSource binding view = " + view + " to source = " + source);

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
                ((INeedsLogger) view).setLogger(mLogger);
            }
        }
    }

    private void registerBindingsFor(View view, List<IBindingAssociationEngine> bindings) {
        if (view == null || bindings == null) {
            return;
        }

        if (mBoundViews.containsKey(view)) {
            mBoundViews.get(view).addAll(bindings);
        } else {
            mBoundViews.put(view, bindings);
        }
    }

    private void clearBindingsForView(View view) {
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
    }

    @Override
    public void dispose() {
        //TODO
        //annull/dispose all the bindings?
        clearBindingsForView(open_bindable_activity_button);

        //annull all the view references
        open_bindable_activity_button = null;

        //annull references
        mViewModel = null; //we just annull the reference, it will be disposed of by the activity
        mView = null;
        mLogger = NullLogger.instance;
        mBinder = null;
        mImageLoader = IImageLoader.NullImageLoader.nullImageLoader;
    }
}
