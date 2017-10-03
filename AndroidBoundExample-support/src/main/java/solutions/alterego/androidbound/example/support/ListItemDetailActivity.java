package solutions.alterego.androidbound.example.support;

import com.alterego.advancedandroidlogger.interfaces.IAndroidLogger;

import android.os.Bundle;

import solutions.alterego.androidbound.example.R;
import solutions.alterego.androidbound.example.support.util.AdvancedAndroidLoggerAdapter;
import solutions.alterego.androidbound.example.support.viewmodels.ListItemDetailActivityViewModel;
import solutions.alterego.androidbound.interfaces.ILogger;
import solutions.alterego.androidbound.interfaces.IViewBinder;
import solutions.alterego.androidbound.support.android.BindingAppCompatActivity;


public class ListItemDetailActivity extends BindingAppCompatActivity {

    public static final String LOGGING_TAG = "TEST_APP";

    public static final String EXTRA_ITEM_TITLE = "EXTRA_ITEM_TITLE";

    public static final String EXTRA_ITEM_IMAGE_URL = "EXTRA_ITEM_IMAGE_URL";

    private static final IAndroidLogger.LoggingLevel LOGGING_LEVEL = IAndroidLogger.LoggingLevel.VERBOSE;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ILogger logger = new AdvancedAndroidLoggerAdapter(LOGGING_TAG, LOGGING_LEVEL);

        String title = getIntent().getStringExtra(EXTRA_ITEM_TITLE);
        String imageUrl = getIntent().getStringExtra(EXTRA_ITEM_IMAGE_URL);

        setLogger(logger);

        setContentView(R.layout.activity_item_detail, new ListItemDetailActivityViewModel(this, logger, title, imageUrl));

        setTitle("ListItemDetailActivity");
    }

    @Override
    public IViewBinder getViewBinder() {
        return ExampleApplication.getViewBinder();
    }

    @Override
    public void setViewBinder(IViewBinder viewBinder) {
    }

}
