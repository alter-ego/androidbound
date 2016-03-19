package solutions.alterego.androidbound.example;

import com.alterego.advancedandroidlogger.implementations.DetailedAndroidLogger;
import com.alterego.advancedandroidlogger.interfaces.IAndroidLogger;

import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.view.View;

import solutions.alterego.androidbound.ViewBinder;
import solutions.alterego.androidbound.ViewModel;


public class MainActivity extends ActionBarActivity {

    public static final String LOGGING_TAG = "TEST_APP";

    private static final IAndroidLogger.LoggingLevel LOGGING_LEVEL = IAndroidLogger.LoggingLevel.VERBOSE;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        IAndroidLogger logger = new DetailedAndroidLogger(LOGGING_TAG, LOGGING_LEVEL);
        ViewBinder viewBinder = new ViewBinder(this, logger, null);
        ViewModel viewModel = new MainActivityViewModel(logger);

        View view = viewBinder.inflate(this, viewModel, R.layout.activity_main, null);
        setContentView(view);
    }

}
