package solutions.alterego.androidbound.example.support.listviewitems;

import android.view.View;
import android.widget.Toast;

import solutions.alterego.androidbound.android.interfaces.INeedsBoundView;
import solutions.alterego.androidbound.example.support.MainActivity;

public class ListViewItem implements INeedsBoundView {

    private String mTitle;

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

    public String getTitle() {
        return this.mTitle;
    }

    public String getImageUrl() {
        return this.mImageUrl;
    }
}
