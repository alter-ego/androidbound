package solutions.alterego.androidbound.example.viewmodels;

import android.app.Activity;
import android.content.Intent;
import android.widget.Toast;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import lombok.Getter;
import lombok.experimental.Accessors;
import solutions.alterego.androidbound.android.AndroidViewModel;
import solutions.alterego.androidbound.example.ListItemDetailActivity;
import solutions.alterego.androidbound.example.MainActivity;
import solutions.alterego.androidbound.example.R;
import solutions.alterego.androidbound.example.listviewitems.ListViewItem;
import solutions.alterego.androidbound.example.listviewitems.ListViewItem2;
import solutions.alterego.androidbound.interfaces.ILogger;

@Accessors(prefix = "m")
public class ListViewWithObjectsActivityViewModel extends AndroidViewModel {

    private static final int listSize = 250;

    @Getter
    private String mListViewActivityTitle;

    @Getter
    private String mOpenMainActivityText = "Open main activity";

    @Getter
    private List<Object> mExampleList = new ArrayList<Object>();

    public ListViewWithObjectsActivityViewModel(Activity activity, ILogger logger) {
        setLogger(logger);
        setParentActivity(new WeakReference<Activity>(activity));

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

}
