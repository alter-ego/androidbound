package solutions.alterego.androidbound.example;

import com.alterego.advancedandroidlogger.interfaces.IAndroidLogger;
import com.codemonkeylabs.fpslibrary.TinyDancer;

import android.os.Bundle;

import solutions.alterego.androidbound.android.support.BindingAppCompatActivity;
import solutions.alterego.androidbound.example.util.AdvancedAndroidLoggerAdapter;
import solutions.alterego.androidbound.example.viewmodels.ListViewActivityViewModel;
import solutions.alterego.androidbound.interfaces.ILogger;
import solutions.alterego.androidbound.interfaces.IViewBinder;


public class ListViewActivity extends BindingAppCompatActivity {

    public static final String LOGGING_TAG = "TEST_APP";

    private static final IAndroidLogger.LoggingLevel LOGGING_LEVEL = IAndroidLogger.LoggingLevel.VERBOSE;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ILogger logger = new AdvancedAndroidLoggerAdapter(LOGGING_TAG, LOGGING_LEVEL);
        TinyDancer.create().show(this);

        setLogger(logger);

        setContentView(R.layout.activity_listview, new ListViewActivityViewModel(this, logger));

        setTitle("ListViewActivity");
    }

    @Override
    public IViewBinder getViewBinder() {
        return ExampleApplication.getViewBinder();
    }

    @Override
    public void setViewBinder(IViewBinder viewBinder) {
    }
}
