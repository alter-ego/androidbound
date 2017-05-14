package solutions.alterego.androidbound.example;

import android.app.Activity;
import android.content.Intent;
import android.view.View;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

import lombok.Getter;
import lombok.experimental.Accessors;
import solutions.alterego.androidbound.ViewModel;
import solutions.alterego.androidbound.android.interfaces.INeedsBoundView;
import solutions.alterego.androidbound.interfaces.ILogger;

@Accessors(prefix = "m")
public class RecyclerViewWithObjectsActivityViewModel extends ViewModel {

    private static final int listSize = 20;

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

    public static class ListViewItem implements INeedsBoundView {

        @Getter
        private String mTitle;

        @Getter
        private String mImageUrl = "https://www.google.co.uk/images/branding/googlelogo/1x/googlelogo_color_272x92dp.png";

        private View mBoundView;

        public ListViewItem(String title) {
            mTitle = title;
        }

        @Override
        public void setBoundView(View view) {
            mBoundView = view;
            android.util.Log.i(MainActivity.LOGGING_TAG, "setBoundView = " + view);
        }

        public void doImageClicked() {
            Toast.makeText(mBoundView.getContext(), " doImageClicked ", Toast.LENGTH_SHORT).show();
        }

        @Override
        public String toString() {
            return mTitle;
        }
    }

    public static class ListViewItem2 {

        @Getter
        private String mTitle;

        @Getter
        private String mImageUrl = "http://icons.iconarchive.com/icons/danleech/simple/128/android-icon.png";

        public ListViewItem2(String title) {
            mTitle = title;
        }

        @Override
        public String toString() {
            return mTitle;
        }
    }

    public void doOnItemClickListener(Object object) {
        Toast.makeText(getParentActivity(), " " + object, Toast.LENGTH_SHORT).show();
    }

    public void doOnItemClickListenerView(View view, Object object) {
        Toast.makeText(getParentActivity(), "ID " + view.getId() + " " + object, Toast.LENGTH_SHORT).show();
    }
}
