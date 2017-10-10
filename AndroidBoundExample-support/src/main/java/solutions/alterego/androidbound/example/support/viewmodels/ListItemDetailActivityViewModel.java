package solutions.alterego.androidbound.example.support.viewmodels;

import android.app.Activity;

import solutions.alterego.androidbound.android.AndroidViewModel;
import solutions.alterego.androidbound.interfaces.ILogger;

public class ListItemDetailActivityViewModel extends AndroidViewModel {

    private static final int listSize = 250;

    private String mImageUrl;

    private String mTitle;

    public ListItemDetailActivityViewModel(Activity activity, ILogger logger, String title, String imageUrl) {
        setLogger(logger);
        setParentActivity(activity);

        setTitle(title);
        setImageUrl(imageUrl);
    }

    public void setTitle(String title) {
        mTitle = title;
        raisePropertyChanged("Title");
    }

    public void setImageUrl(String imageUrl) {
        mImageUrl = imageUrl;
        raisePropertyChanged("ImageUrl");
    }

    public String getImageUrl() {
        return this.mImageUrl;
    }

    public String getTitle() {
        return this.mTitle;
    }
}
