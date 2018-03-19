package solutions.alterego.androidbound.example.listviewitems;

import lombok.Getter;
import lombok.experimental.Accessors;

@Accessors(prefix = "m")
public class ListViewItem2 {

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

    public void doImageClicked() {
        //do nothing
    }
}