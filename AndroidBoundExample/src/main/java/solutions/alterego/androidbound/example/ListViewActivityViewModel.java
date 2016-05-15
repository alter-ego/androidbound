package solutions.alterego.androidbound.example;

import com.alterego.advancedandroidlogger.interfaces.IAndroidLogger;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

import lombok.Getter;
import lombok.experimental.Accessors;
import solutions.alterego.androidbound.ViewModel;
import solutions.alterego.androidbound.interfaces.IDisposable;

@Accessors(prefix = "m")
public class ListViewActivityViewModel extends ViewModel implements IDisposable {

    private static final int listSize = 250;

    @Getter
    private String mListViewActivityTitle;

    @Getter
    private String mOpenMainActivityText;

    @Getter
    private List<ListViewItem> mExampleList = new ArrayList<ListViewItem>();

    public ListViewActivityViewModel(Activity activity, IAndroidLogger logger) {
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

    @Override
    public void dispose() {
        super.dispose();
        //do nothing
    }

    @Override
    public void onCreate(Bundle outState) {
        //do nothing
    }

    @Override
    public void onResume() {
        //do nothing
    }

    @Override
    public void onPause() {
        //do nothing
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        //do nothing
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

    public static class ListViewItem {
        @Getter
        private String mTitle;

        @Getter
        private String mImageUrl = "https://www.google.co.uk/images/branding/googlelogo/1x/googlelogo_color_272x92dp.png";

        public ListViewItem(String title) {
            mTitle = title;
        }
    }

}
