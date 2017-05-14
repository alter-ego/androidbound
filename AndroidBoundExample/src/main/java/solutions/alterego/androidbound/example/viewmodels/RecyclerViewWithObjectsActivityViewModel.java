package solutions.alterego.androidbound.example.viewmodels;

import android.app.Activity;
import android.content.Intent;
import android.view.View;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import lombok.Getter;
import lombok.experimental.Accessors;
import solutions.alterego.androidbound.ViewModel;
import solutions.alterego.androidbound.example.ListItemDetailActivity;
import solutions.alterego.androidbound.example.MainActivity;
import solutions.alterego.androidbound.example.R;
import solutions.alterego.androidbound.example.listviewitems.ListViewItem;
import solutions.alterego.androidbound.example.listviewitems.ListViewItem2;
import solutions.alterego.androidbound.interfaces.ILogger;

@Accessors(prefix = "m")
public class RecyclerViewWithObjectsActivityViewModel extends ViewModel {

    private static final int listSize = 10;

    @Getter
    private String mListViewActivityTitle;

    @Getter
    private String mOpenMainActivityText = "Open main activity";

    @Getter
    private List<Object> mExampleListLinear = new ArrayList<Object>();

    @Getter
    private List<Object> mExampleListStaggered = new ArrayList<Object>();

    public RecyclerViewWithObjectsActivityViewModel(Activity activity, ILogger logger) {
        setLogger(logger);
        setParentActivity(activity);

        setListViewActivityTitle("RecyclerView with objects activity");

        for (int i = 0; i < listSize; i++) {
            if (i % 2 == 0) {
                mExampleListLinear.add(new ListViewItem(Integer.toString(i)));
            } else {
                mExampleListLinear.add(new ListViewItem2(Integer.toString(i)));
            }
        }

        mExampleListStaggered.addAll(mExampleListLinear);

        raisePropertyChanged("ExampleListLinear");
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

    public void doOnItemClickListener(Object object) {
        Toast.makeText(getParentActivity(), " " + object, Toast.LENGTH_SHORT).show();
    }

    public void doOnItemClickListenerView(View view, Object object) {
        Toast.makeText(getParentActivity(), "ID " + view.getId() + " " + object, Toast.LENGTH_SHORT).show();
    }
}
