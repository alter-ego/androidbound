package solutions.alterego.androidbound.example.viewmodels;

import android.app.Activity;
import android.content.Intent;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

import lombok.Getter;
import lombok.experimental.Accessors;
import solutions.alterego.androidbound.android.AndroidViewModel;
import solutions.alterego.androidbound.example.ListItemDetailActivity;
import solutions.alterego.androidbound.example.MainActivity;
import solutions.alterego.androidbound.example.listviewitems.ListViewItem;
import solutions.alterego.androidbound.example.listviewitems.ListViewItem2;
import solutions.alterego.androidbound.interfaces.ILogger;

@Accessors(prefix = "m")
public class RecyclerViewActivityViewModel extends AndroidViewModel {

    private static final int listSize = 10;

    @Getter
    private String mListViewActivityTitle;

    @Getter
    private String mOpenMainActivityText = "Open main activity";

    @Getter
    private List<Object> mExampleListLinear = new ArrayList<Object>();

    public RecyclerViewActivityViewModel(Activity activity, ILogger logger) {
        setParentActivity(activity);

        setListViewActivityTitle("RecyclerView with objects activity");

        for (int i = 0; i < listSize; i++) {
            if (i % 2 == 0) {
                mExampleListLinear.add(new ListViewItem(Integer.toString(i)));
            } else {
                mExampleListLinear.add(new ListViewItem2(Integer.toString(i)));
            }
        }

        raisePropertyChanged("ExampleListLinear");
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

    public void doOnItemClickListener(Object object) {
        if (object instanceof ListViewItem) {
            Toast.makeText(getParentActivity(), "clicked ListViewItem = " + ((ListViewItem) object).getTitle(), Toast.LENGTH_SHORT).show();
            openDetail((ListViewItem) object);
        } else if (object instanceof ListViewItem2) {
            Toast.makeText(getParentActivity(), "clicked ListViewItem2 = " + ((ListViewItem2) object).getTitle(), Toast.LENGTH_SHORT).show();
        }
    }

}
