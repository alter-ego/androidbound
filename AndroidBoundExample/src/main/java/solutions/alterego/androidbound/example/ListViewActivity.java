package solutions.alterego.androidbound.example;

import com.alterego.advancedandroidlogger.interfaces.IAndroidLogger;
import com.codemonkeylabs.fpslibrary.TinyDancer;

import android.os.Bundle;

import solutions.alterego.androidbound.NullLogger;
import solutions.alterego.androidbound.ViewBinder;
import solutions.alterego.androidbound.android.BindingAppCompatActivity;
import solutions.alterego.androidbound.example.imageloader.UILImageLoader;
import solutions.alterego.androidbound.interfaces.ILogger;
import solutions.alterego.androidbound.interfaces.IViewBinder;


public class ListViewActivity extends BindingAppCompatActivity {

    public static final String LOGGING_TAG = "TEST_APP";

    private static final IAndroidLogger.LoggingLevel LOGGING_LEVEL = IAndroidLogger.LoggingLevel.VERBOSE;

    private IViewBinder mViewBinder;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ILogger logger = new AdvancedAndroidLoggerAdapter(LOGGING_TAG, LOGGING_LEVEL);
        TinyDancer.create().show(this);

        ViewBinder viewBinder = new ViewBinder(this, NullLogger.instance);
        viewBinder.setImageLoader(new UILImageLoader(this, null));
        setViewBinder(viewBinder);

        setContentView(R.layout.activity_listview, new ListViewActivityViewModel(this, logger));

        setTitle("ListViewActivity");
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
        if (getViewBinder() != null) {
            getViewBinder().dispose();
        }
    }
}
