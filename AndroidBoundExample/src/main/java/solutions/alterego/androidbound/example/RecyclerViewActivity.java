package solutions.alterego.androidbound.example;

import com.alterego.advancedandroidlogger.interfaces.IAndroidLogger;
import com.codemonkeylabs.fpslibrary.TinyDancer;

import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.OrientationHelper;
import android.support.v7.widget.RecyclerView;

import solutions.alterego.androidbound.android.BindingAppCompatActivity;
import solutions.alterego.androidbound.example.util.AdvancedAndroidLoggerAdapter;
import solutions.alterego.androidbound.example.viewmodels.RecyclerViewActivityViewModel;
import solutions.alterego.androidbound.interfaces.ILogger;
import solutions.alterego.androidbound.interfaces.IViewBinder;

public class RecyclerViewActivity extends BindingAppCompatActivity {

    public static final String LOGGING_TAG = "TEST_APP";

    private static final IAndroidLogger.LoggingLevel LOGGING_LEVEL = IAndroidLogger.LoggingLevel.VERBOSE;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ILogger logger = new AdvancedAndroidLoggerAdapter(LOGGING_TAG, LOGGING_LEVEL);
        TinyDancer.create().show(this);

        setLogger(logger);

        setContentView(R.layout.activity_recyclerview, new RecyclerViewActivityViewModel(this, logger));

        //to test that the normal recycler view still works without any problems
        RecyclerView recyclerViewNormal = (RecyclerView) findViewById(R.id.recyclerview_normal);
        recyclerViewNormal.setNestedScrollingEnabled(false);
        LinearLayoutManager layoutManagerLinearNormal = new LinearLayoutManager(this, OrientationHelper.VERTICAL, false);
        recyclerViewNormal.setLayoutManager(layoutManagerLinearNormal);
        recyclerViewNormal.setAdapter(new RecyclerViewNormalAdapter(this));
    }

    @Override
    public IViewBinder getViewBinder() {
        return ExampleApplication.getViewBinder();
    }

    @Override
    public void setViewBinder(IViewBinder viewBinder) {
    }
}
