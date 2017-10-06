package solutions.alterego.androidbound.example.nestedrvs;


import com.alterego.advancedandroidlogger.interfaces.IAndroidLogger;
import com.codemonkeylabs.fpslibrary.TinyDancer;

import android.annotation.SuppressLint;
import android.graphics.Rect;
import android.os.Bundle;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;

import java.util.HashMap;
import java.util.Map;

import solutions.alterego.androidbound.android.support.BindingAppCompatActivity;
import solutions.alterego.androidbound.android.ui.BindableRecyclerView;
import solutions.alterego.androidbound.example.ExampleApplication;
import solutions.alterego.androidbound.example.PaginatedRecyclerViewActivity;
import solutions.alterego.androidbound.example.R;
import solutions.alterego.androidbound.example.nestedrvs.viewmodel.MainNestedViewModel;
import solutions.alterego.androidbound.example.nestedrvs.viewmodel.NestedViewModel;
import solutions.alterego.androidbound.example.util.AdvancedAndroidLoggerAdapter;
import solutions.alterego.androidbound.interfaces.ILogger;
import solutions.alterego.androidbound.interfaces.IViewBinder;

public class NestedRecyclerViewActivity extends BindingAppCompatActivity {

    private static final IAndroidLogger.LoggingLevel LOGGING_LEVEL = IAndroidLogger.LoggingLevel.VERBOSE;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        ILogger logger = new AdvancedAndroidLoggerAdapter(PaginatedRecyclerViewActivity.class.getSimpleName(), LOGGING_LEVEL);
        TinyDancer.create().show(this);

        setLogger(logger);

        setContentView(R.layout.activity_nested_recyclerview, new MainNestedViewModel());

        @SuppressLint("WrongViewCast") BindableRecyclerView mainRv = (BindableRecyclerView) findViewById(R.id.main_nested_rv);
        mainRv.setNestedScrollingEnabled(false);

        GridLayoutManager layoutManager = new GridLayoutManager(this, 2);
        mainRv.setLayoutManager(layoutManager);

        Map<Class<?>, Integer> map = new HashMap<Class<?>, Integer>();
        map.put(NestedViewModel.class, R.layout.nested_recycler_view);
        map.put(MainNestedViewModel.RecyclerViewItem.class, R.layout.activity_paginated_rv_item);

        mainRv.setTemplatesForObjects(map);

        final int categoryMargin = (int) (getResources().getDisplayMetrics().density * 16);
        mainRv.addItemDecoration(new RecyclerView.ItemDecoration() {
            @Override
            public void getItemOffsets(Rect outRect, View view, RecyclerView parent, RecyclerView.State state) {
                int pos = parent.getChildAdapterPosition(view);

                if (pos > 0) {

                    outRect.top = categoryMargin;

                    if (pos % 2 != 0) {
                        outRect.left = categoryMargin;
                        outRect.right = outRect.left / 2;
                    } else {
                        outRect.right = categoryMargin;
                        outRect.left = outRect.right / 2;
                    }

                    outRect.bottom = categoryMargin;
                } else {
                    outRect.top = categoryMargin / 2;
                    outRect.left = categoryMargin / 2;
                    outRect.right = categoryMargin / 2;
                }
            }
        });

        layoutManager.setSpanSizeLookup(new GridLayoutManager.SpanSizeLookup() {
            @Override
            public int getSpanSize(int position) {
                if (position == 0) {
                    return 2;
                }
                return 1;
            }
        });
    }

    @Override
    public IViewBinder getViewBinder() {
        return ExampleApplication.getViewBinder();
    }

    @Override
    public void setViewBinder(IViewBinder viewBinder) {
    }
}
