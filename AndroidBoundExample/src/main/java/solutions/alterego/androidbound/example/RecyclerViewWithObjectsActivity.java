package solutions.alterego.androidbound.example;

import com.alterego.advancedandroidlogger.implementations.DetailedAndroidLogger;
import com.alterego.advancedandroidlogger.implementations.NullAndroidLogger;
import com.alterego.advancedandroidlogger.interfaces.IAndroidLogger;
import com.codemonkeylabs.fpslibrary.TinyDancer;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.OrientationHelper;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;

import java.util.HashMap;
import java.util.Map;

import solutions.alterego.androidbound.ViewBinder;
import solutions.alterego.androidbound.android.BindingAppCompatActivity;
import solutions.alterego.androidbound.android.ui.BindableRecyclerView;
import solutions.alterego.androidbound.interfaces.IViewBinder;

public class RecyclerViewWithObjectsActivity extends BindingAppCompatActivity {

    public static final String LOGGING_TAG = "TEST_APP";

    private static final IAndroidLogger.LoggingLevel LOGGING_LEVEL = IAndroidLogger.LoggingLevel.VERBOSE;

    private IViewBinder mViewBinder;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        IAndroidLogger logger = new DetailedAndroidLogger(LOGGING_TAG, LOGGING_LEVEL);
        TinyDancer.create().show(this);

        ViewBinder viewBinder = new ViewBinder(this, NullAndroidLogger.instance, null);
        setViewBinder(viewBinder);
        setViewModel(new RecyclerViewWithObjectsActivityViewModel(this, logger));

        setContentView(R.layout.activity_recyclerview);

        //populating map with object-layout relationships
        Map<Class<?>, Integer> objectTemplates = new HashMap<Class<?>, Integer>();
        objectTemplates.put(RecyclerViewWithObjectsActivityViewModel.ListViewItem.class, R.layout.activity_listview_listitem);
        objectTemplates.put(RecyclerViewWithObjectsActivityViewModel.ListViewItem2.class, R.layout.activity_listview_listitem2);

        @SuppressLint("WrongViewCast") BindableRecyclerView recyclerViewLinear = (BindableRecyclerView) findViewById(R.id.recyclerview_linear);
        recyclerViewLinear.setNestedScrollingEnabled(false);
        LinearLayoutManager layoutManager = new LinearLayoutManager(this, OrientationHelper.VERTICAL, false);
        recyclerViewLinear.setLayoutManager(layoutManager);
        recyclerViewLinear.setTemplatesForObjects(objectTemplates);

        @SuppressLint("WrongViewCast") BindableRecyclerView recyclerViewStaggered = (BindableRecyclerView) findViewById(R.id.recyclerview_staggered);
        recyclerViewStaggered.setNestedScrollingEnabled(false);
        StaggeredGridLayoutManager staggeredGridLayoutManager = new StaggeredGridLayoutManager(2, OrientationHelper.VERTICAL);
        recyclerViewStaggered.setLayoutManager(staggeredGridLayoutManager);
        recyclerViewStaggered.setTemplatesForObjects(objectTemplates);

        //to test that the normal recycler view still works without any problems
        RecyclerView recyclerViewNormal = (RecyclerView) findViewById(R.id.recyclerview_normal);
        recyclerViewNormal.setNestedScrollingEnabled(false);
        LinearLayoutManager layoutManagerLinearNormal = new LinearLayoutManager(this, OrientationHelper.VERTICAL, false);
        recyclerViewNormal.setLayoutManager(layoutManagerLinearNormal);
        recyclerViewNormal.setAdapter(new RecyclerViewNormalAdapter(this));
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
