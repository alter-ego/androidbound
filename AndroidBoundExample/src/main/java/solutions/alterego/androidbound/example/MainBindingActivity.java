package solutions.alterego.androidbound.example;

import com.alterego.advancedandroidlogger.interfaces.IAndroidLogger;

import android.graphics.Typeface;
import android.os.Bundle;

import solutions.alterego.androidbound.ViewBinder;
import solutions.alterego.androidbound.android.BindingAppCompatActivity;
import solutions.alterego.androidbound.example.imageloader.UILImageLoader;
import solutions.alterego.androidbound.example.util.AdvancedAndroidLoggerAdapter;
import solutions.alterego.androidbound.example.util.CustomValueConverters;
import solutions.alterego.androidbound.example.viewmodels.MainBindingActivityViewModel;
import solutions.alterego.androidbound.interfaces.ILogger;
import solutions.alterego.androidbound.interfaces.IViewBinder;

public class MainBindingActivity extends BindingAppCompatActivity {

    public static final String LOGGING_TAG = "TEST_APP";

    private static final IAndroidLogger.LoggingLevel LOGGING_LEVEL = IAndroidLogger.LoggingLevel.VERBOSE;

    private IViewBinder mViewBinder;

    private CustomValueConverters mCustomValueConverters;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ILogger logger = new AdvancedAndroidLoggerAdapter(LOGGING_TAG, LOGGING_LEVEL);

        ViewBinder viewBinder = new ViewBinder(this, logger);
        viewBinder.getFontManager().setDefaultFont(Typeface.createFromAsset(getAssets(), "Roboto-Regular.ttf"));
        viewBinder.getFontManager().registerFont("light", Typeface.createFromAsset(getAssets(), "Roboto-Light.ttf"));
        viewBinder.getFontManager().registerFont("italic", Typeface.createFromAsset(getAssets(), "Roboto-Italic.ttf"));
        viewBinder.getFontManager().registerFont("bold", Typeface.createFromAsset(getAssets(), "Roboto-Bold.ttf"));
        mCustomValueConverters = new CustomValueConverters(this, viewBinder);
        viewBinder.setImageLoader(new UILImageLoader(this, null));

        setViewBinder(viewBinder);

        setContentView(R.layout.activity_bindable_main, new MainBindingActivityViewModel(this, logger));

        setTitle("MainBindingActivity");
    }

    @Override
    public IViewBinder getViewBinder() {
        return mViewBinder;
    }

    @Override
    public void setViewBinder(IViewBinder viewBinder) {
        mViewBinder = viewBinder;
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mCustomValueConverters = null;
        if (getViewBinder() != null) {
            getViewBinder().dispose();
        }
    }
}
