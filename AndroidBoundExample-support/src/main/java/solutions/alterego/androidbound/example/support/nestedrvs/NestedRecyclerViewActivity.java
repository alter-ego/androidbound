package solutions.alterego.androidbound.example.support.nestedrvs;


import com.alterego.advancedandroidlogger.interfaces.IAndroidLogger;
import com.codemonkeylabs.fpslibrary.TinyDancer;

import android.os.Bundle;

import solutions.alterego.androidbound.example.support.ExampleApplication;
import solutions.alterego.androidbound.example.support.PaginatedRecyclerViewActivity;
import solutions.alterego.androidbound.example.support.R;
import solutions.alterego.androidbound.example.support.nestedrvs.viewmodel.MainNestedViewModel;
import solutions.alterego.androidbound.example.support.util.AdvancedAndroidLoggerAdapter;
import solutions.alterego.androidbound.interfaces.ILogger;
import solutions.alterego.androidbound.interfaces.IViewBinder;
import solutions.alterego.androidbound.support.android.BindingAppCompatActivity;
import solutions.alterego.androidbound.support.android.ui.BindableRecyclerView;

public class NestedRecyclerViewActivity extends BindingAppCompatActivity {

    private static final IAndroidLogger.LoggingLevel LOGGING_LEVEL = IAndroidLogger.LoggingLevel.VERBOSE;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        ILogger logger = new AdvancedAndroidLoggerAdapter(PaginatedRecyclerViewActivity.class.getSimpleName(), LOGGING_LEVEL);
        TinyDancer.create().show(this);

        setLogger(logger);
        setContentView(R.layout.activity_nested_recyclerview, new MainNestedViewModel());

        BindableRecyclerView mainRv = findViewById(R.id.main_nested_rv);
        mainRv.setNestedScrollingEnabled(false);
    }

    @Override
    public IViewBinder getViewBinder() {
        return ExampleApplication.getViewBinder();
    }

    @Override
    public void setViewBinder(IViewBinder viewBinder) {
    }
}
