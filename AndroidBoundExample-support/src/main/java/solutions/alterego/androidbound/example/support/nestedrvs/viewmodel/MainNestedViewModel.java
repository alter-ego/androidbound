package solutions.alterego.androidbound.example.support.nestedrvs.viewmodel;


import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import solutions.alterego.androidbound.ViewModel;
import solutions.alterego.androidbound.example.support.R;

public class MainNestedViewModel extends ViewModel {

    private List<Object> mDataItems;

    public MainNestedViewModel() {
        mDataItems = new ArrayList<Object>();
        mDataItems.add(new NestedViewModel());
        for (int i = 0; i < 50; i++) {
            mDataItems.add(new RecyclerViewItem("item " + i, "http://icons.iconarchive.com/icons/danleech/simple/128/android-icon.png"));
        }
    }

    public Map<Class<?>, Integer> getObjectTemplates() {
        Map<Class<?>, Integer> map = new HashMap<Class<?>, Integer>();
        map.put(NestedViewModel.class, R.layout.nested_recycler_view);
        map.put(RecyclerViewItem.class, R.layout.activity_paginated_rv_item);
        return map;
    }

    public List<Object> getDataItems() {
        return mDataItems;
    }

}
