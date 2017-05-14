package solutions.alterego.androidbound.example;

import com.alterego.advancedandroidlogger.interfaces.IAndroidLogger;

import android.os.Bundle;

import solutions.alterego.androidbound.ViewBinder;
import solutions.alterego.androidbound.android.BindingAppCompatActivity;
import solutions.alterego.androidbound.example.imageloader.UILImageLoader;
import solutions.alterego.androidbound.example.util.AdvancedAndroidLoggerAdapter;
import solutions.alterego.androidbound.interfaces.ILogger;
import solutions.alterego.androidbound.interfaces.IViewBinder;


public class ListItemDetailActivity extends BindingAppCompatActivity {

    public static final String LOGGING_TAG = "TEST_APP";

    public static final String EXTRA_ITEM_TITLE = "EXTRA_ITEM_TITLE";

    public static final String EXTRA_ITEM_IMAGE_URL = "EXTRA_ITEM_IMAGE_URL";

    private static final IAndroidLogger.LoggingLevel LOGGING_LEVEL = IAndroidLogger.LoggingLevel.VERBOSE;

    private IViewBinder mViewBinder;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ILogger logger = new AdvancedAndroidLoggerAdapter(LOGGING_TAG, LOGGING_LEVEL);

        String title = getIntent().getStringExtra(EXTRA_ITEM_TITLE);
        String imageUrl = getIntent().getStringExtra(EXTRA_ITEM_IMAGE_URL);

        ViewBinder viewBinder = new ViewBinder(this, logger);
        viewBinder.setImageLoader(new UILImageLoader(this, null));
        setViewBinder(viewBinder);

        setContentView(R.layout.activity_item_detail, new ListItemDetailActivityViewModel(this, logger, title, imageUrl));

        setTitle("ListItemDetailActivity");
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
