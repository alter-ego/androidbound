package solutions.alterego.androidbound.example.listviewitems;

import android.view.View;
import android.widget.Toast;

import lombok.Getter;
import lombok.experimental.Accessors;
import solutions.alterego.androidbound.android.interfaces.INeedsBoundView;
import solutions.alterego.androidbound.example.MainActivity;

@Accessors(prefix = "m")
public class ListViewItem implements INeedsBoundView {

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
