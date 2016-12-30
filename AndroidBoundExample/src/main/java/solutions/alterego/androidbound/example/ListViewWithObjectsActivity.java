package solutions.alterego.androidbound.example;

import com.alterego.advancedandroidlogger.implementations.DetailedAndroidLogger;
import com.alterego.advancedandroidlogger.implementations.NullAndroidLogger;
import com.alterego.advancedandroidlogger.interfaces.IAndroidLogger;
import com.codemonkeylabs.fpslibrary.TinyDancer;

import android.annotation.SuppressLint;
import android.os.Bundle;

import java.util.HashMap;
import java.util.Map;

import solutions.alterego.androidbound.ViewBinder;
import solutions.alterego.androidbound.android.BindingAppCompatActivity;
import solutions.alterego.androidbound.android.ui.BindableListView;
import solutions.alterego.androidbound.example.imageloader.UILImageLoader;
import solutions.alterego.androidbound.interfaces.IViewBinder;

public class ListViewWithObjectsActivity extends BindingAppCompatActivity {

    public static final String LOGGING_TAG = "TEST_APP";

    private static final IAndroidLogger.LoggingLevel LOGGING_LEVEL = IAndroidLogger.LoggingLevel.VERBOSE;

    private IViewBinder mViewBinder;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        IAndroidLogger logger = new DetailedAndroidLogger(LOGGING_TAG, LOGGING_LEVEL);
        TinyDancer.create().show(this);

        ViewBinder viewBinder = new ViewBinder(this, NullAndroidLogger.instance);
        viewBinder.setImageLoader(new UILImageLoader(this, null));
        setViewBinder(viewBinder);

        setContentView(R.layout.activity_listview, new ListViewWithObjectsActivityViewModel(this, logger));

        //populating map with object-layout relationships
        // we're not adding ListViewItem layout because that will use the default layout declared in XML
        @SuppressLint("WrongViewCast") BindableListView lv = (BindableListView) findViewById(R.id.listview);
        Map<Class<?>, Integer> objectTemplates = new HashMap<Class<?>, Integer>();
        objectTemplates.put(ListViewWithObjectsActivityViewModel.ListViewItem2.class, R.layout.activity_listview_listitem2);

        lv.setTemplatesForObjects(objectTemplates);
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
