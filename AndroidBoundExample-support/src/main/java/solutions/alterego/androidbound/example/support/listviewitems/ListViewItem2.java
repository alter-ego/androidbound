package solutions.alterego.androidbound.example.support.listviewitems;

public class ListViewItem2 {

    private String mTitle;

    private String mImageUrl = "http://icons.iconarchive.com/icons/danleech/simple/128/android-icon.png";

    public ListViewItem2(String title) {
        mTitle = title;
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