package solutions.alterego.androidbound.example;

import android.app.Activity;
import android.content.Intent;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

import lombok.Getter;
import lombok.experimental.Accessors;
import solutions.alterego.androidbound.ViewModel;
import solutions.alterego.androidbound.example.listviewitems.ListViewItem;
import solutions.alterego.androidbound.interfaces.ILogger;

@Accessors(prefix = "m")
public class ListViewActivityViewModel extends ViewModel {

    private static final int listSize = 250;

    @Getter
    private String mListViewActivityTitle;

    @Getter
    private String mOpenMainActivityText = "Open main activity";

    @Getter
    private List<ListViewItem> mExampleList = new ArrayList<ListViewItem>();

    public ListViewActivityViewModel(Activity activity, ILogger logger) {
        setLogger(logger);
        setParentActivity(activity);

        setListViewActivityTitle("ListView activity");

        for (int i = 0; i < listSize; i++) {
            mExampleList.add(new ListViewItem(Integer.toString(i)));
        }
        raisePropertyChanged("ExampleList");
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

    public boolean canSelectListItem(ListViewItem item) {
        return true;
    }

    public void doSelectListItem(ListViewItem item) {
        Toast.makeText(getParentActivity(), "clicked list item = " + item.getTitle(), Toast.LENGTH_SHORT).show();

        Intent activityIntent = new Intent(getParentActivity(), ListItemDetailActivity.class);
        activityIntent.putExtra(ListItemDetailActivity.EXTRA_ITEM_TITLE, item.getTitle());
        activityIntent.putExtra(ListItemDetailActivity.EXTRA_ITEM_IMAGE_URL, item.getImageUrl());

        if (getParentActivity() != null) {
            getParentActivity().startActivity(activityIntent);
        }
    }

}
