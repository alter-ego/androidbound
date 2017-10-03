package solutions.alterego.androidbound.example.support.viewmodels;

import android.app.Activity;
import android.content.Intent;
import android.view.View;
import android.widget.Toast;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import solutions.alterego.androidbound.android.AndroidViewModel;
import solutions.alterego.androidbound.example.R;
import solutions.alterego.androidbound.example.support.ListItemDetailActivity;
import solutions.alterego.androidbound.example.support.MainActivity;
import solutions.alterego.androidbound.example.support.listviewitems.ListViewItem;
import solutions.alterego.androidbound.example.support.listviewitems.ListViewItem2;
import solutions.alterego.androidbound.interfaces.ILogger;

public class RecyclerViewWithObjectsActivityViewModel extends AndroidViewModel {

    private static final int listSize = 10;

    private String mListViewActivityTitle;

    private String mOpenMainActivityText = "Open main activity";

    private List<Object> mExampleListStaggered = new ArrayList<Object>();

    public RecyclerViewWithObjectsActivityViewModel(Activity activity, ILogger logger) {
        setLogger(logger);
        setParentActivity(new WeakReference<Activity>(activity));

        setListViewActivityTitle("RecyclerView with objects activity");

        for (int i = 0; i < listSize; i++) {
            if (i % 2 == 0) {
                mExampleListStaggered.add(new ListViewItem(Integer.toString(i)));
            } else {
                mExampleListStaggered.add(new ListViewItem2(Integer.toString(i)));
            }
        }

        raisePropertyChanged("ExampleListStaggered");
    }

    public Map<Class<?>, Integer> getStaggeredTemplatesForObjects() {
        Map<Class<?>, Integer> objectTemplates = new HashMap<Class<?>, Integer>();
        objectTemplates.put(ListViewItem.class, R.layout.activity_listview_listitem);
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

    private void openDetail(ListViewItem item) {
        Intent activityIntent = new Intent(getParentActivity(), ListItemDetailActivity.class);
        activityIntent.putExtra(ListItemDetailActivity.EXTRA_ITEM_TITLE, item.getTitle());
        activityIntent.putExtra(ListItemDetailActivity.EXTRA_ITEM_IMAGE_URL, item.getImageUrl());

        if (getParentActivity() != null) {
            getParentActivity().startActivity(activityIntent);
        }
    }

    public void doOnItemClickListenerView(View view, Object object) {
        Toast.makeText(getParentActivity(), "ID " + view.getId() + " " + object, Toast.LENGTH_SHORT).show();
    }

    public String getListViewActivityTitle() {
        return this.mListViewActivityTitle;
    }

    public String getOpenMainActivityText() {
        return this.mOpenMainActivityText;
    }

    public List<Object> getExampleListStaggered() {
        return this.mExampleListStaggered;
    }
}
