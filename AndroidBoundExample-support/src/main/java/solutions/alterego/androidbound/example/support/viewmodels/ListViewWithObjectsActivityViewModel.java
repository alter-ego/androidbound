package solutions.alterego.androidbound.example.support.viewmodels;

import android.app.Activity;
import android.content.Intent;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import solutions.alterego.androidbound.android.AndroidViewModel;
import solutions.alterego.androidbound.example.support.ListItemDetailActivity;
import solutions.alterego.androidbound.example.support.MainActivity;
import solutions.alterego.androidbound.example.support.R;
import solutions.alterego.androidbound.example.support.listviewitems.ListViewItem;
import solutions.alterego.androidbound.example.support.listviewitems.ListViewItem2;
import solutions.alterego.androidbound.interfaces.ILogger;

public class ListViewWithObjectsActivityViewModel extends AndroidViewModel {

    private static final int listSize = 250;

    private String mListViewActivityTitle;

    private String mOpenMainActivityText = "Open main activity";

    private List<Object> mExampleList = new ArrayList<Object>();

    public ListViewWithObjectsActivityViewModel(Activity activity, ILogger logger) {
        setParentActivity(activity);

        setListViewActivityTitle("ListView with objects activity");

        for (int i = 0; i < listSize; i++) {
            if (i % 2 == 0) {
                mExampleList.add(new ListViewItem(Integer.toString(i)));
            } else {
                mExampleList.add(new ListViewItem2(Integer.toString(i)));
            }
        }
        raisePropertyChanged("ExampleList");
    }

    //populating map with object-layout relationships
    // we're not adding ListViewItem layout because that will use the default layout declared in XML
    public Map<Class<?>, Integer> getLVTemplatesForObjects() {
        Map<Class<?>, Integer> objectTemplates = new HashMap<Class<?>, Integer>();
        objectTemplates.put(ListViewItem2.class, R.layout.activity_listview_listitem2);
        return objectTemplates;
    }

    public void setListViewActivityTitle(String title) {
        mListViewActivityTitle = title;
        raisePropertyChanged("ListViewActivityTitle");
    }

    public boolean canOpenMainActivity() {
        return true;
    }

    public void doOpenMainActivity() {
        Intent activityIntent = new Intent(getParentActivity(), MainActivity.class);
        if (getParentActivity() != null) {
            getParentActivity().startActivity(activityIntent);
        }
    }

    public boolean canSelectListItem(Object item) {
        return true;
    }

    public void doSelectListItem(Object item) {
        if (item instanceof ListViewItem) {
            Toast.makeText(getParentActivity(), "clicked ListViewItem = " + ((ListViewItem) item).getTitle(), Toast.LENGTH_SHORT).show();
            openDetail((ListViewItem) item);
        } else if (item instanceof ListViewItem2) {
            Toast.makeText(getParentActivity(), "clicked ListViewItem2 = " + ((ListViewItem2) item).getTitle(), Toast.LENGTH_SHORT).show();
        }
    }

    private void openDetail(ListViewItem item) {
        Intent activityIntent = new Intent(getParentActivity(), ListItemDetailActivity.class);
        activityIntent.putExtra(ListItemDetailActivity.EXTRA_ITEM_TITLE, item.getTitle());
        activityIntent.putExtra(ListItemDetailActivity.EXTRA_ITEM_IMAGE_URL, item.getImageUrl());

        if (getParentActivity() != null) {
            getParentActivity().startActivity(activityIntent);
        }
    }

    public String getListViewActivityTitle() {
        return this.mListViewActivityTitle;
    }

    public String getOpenMainActivityText() {
        return this.mOpenMainActivityText;
    }

    public List<Object> getExampleList() {
        return this.mExampleList;
    }
}
