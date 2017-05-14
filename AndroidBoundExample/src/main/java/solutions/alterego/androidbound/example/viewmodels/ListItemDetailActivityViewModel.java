package solutions.alterego.androidbound.example.viewmodels;

import android.app.Activity;

import lombok.Getter;
import lombok.experimental.Accessors;
import solutions.alterego.androidbound.ViewModel;
import solutions.alterego.androidbound.interfaces.ILogger;

@Accessors(prefix = "m")
public class ListItemDetailActivityViewModel extends ViewModel {

    private static final int listSize = 250;

    @Getter
    private String mImageUrl;

    @Getter
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

}
