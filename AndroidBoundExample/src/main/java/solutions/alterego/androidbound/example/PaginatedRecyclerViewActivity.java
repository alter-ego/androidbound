package solutions.alterego.androidbound.example;

import com.alterego.advancedandroidlogger.interfaces.IAndroidLogger;
import com.codemonkeylabs.fpslibrary.TinyDancer;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.OrientationHelper;

import solutions.alterego.androidbound.android.support.BindingAppCompatActivity;
import solutions.alterego.androidbound.android.ui.BindableRecyclerView;
import solutions.alterego.androidbound.example.util.AdvancedAndroidLoggerAdapter;
import solutions.alterego.androidbound.example.viewmodels.PaginatedViewModel;
import solutions.alterego.androidbound.interfaces.ILogger;
import solutions.alterego.androidbound.interfaces.IViewBinder;

public class PaginatedRecyclerViewActivity extends BindingAppCompatActivity {

    private static final IAndroidLogger.LoggingLevel LOGGING_LEVEL = IAndroidLogger.LoggingLevel.VERBOSE;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        ILogger logger = new AdvancedAndroidLoggerAdapter(PaginatedRecyclerViewActivity.class.getSimpleName(), LOGGING_LEVEL);
        TinyDancer.create().show(this);

        setLogger(logger);

        setContentView(R.layout.activity_paginated_recyclerview, new PaginatedViewModel(this, logger));

        @SuppressLint("WrongViewCast") BindableRecyclerView paginatedRv = (BindableRecyclerView) findViewById(R.id.paginated_rv);
        paginatedRv.setNestedScrollingEnabled(false);
        LinearLayoutManager layoutManager = new LinearLayoutManager(this, OrientationHelper.VERTICAL, false);
        paginatedRv.setLayoutManager(layoutManager);

        paginatedRv = (BindableRecyclerView) findViewById(R.id.paginated_rv2);
        paginatedRv.setNestedScrollingEnabled(false);
        layoutManager = new LinearLayoutManager(this, OrientationHelper.VERTICAL, false);
        paginatedRv.setLayoutManager(layoutManager);
    }

    @Override
    public IViewBinder getViewBinder() {
        return ExampleApplication.getViewBinder();
    }

    @Override
    public void setViewBinder(IViewBinder viewBinder) {
    }
}
