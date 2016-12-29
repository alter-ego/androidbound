package solutions.alterego.androidbound.example;

import com.alterego.advancedandroidlogger.interfaces.IAndroidLogger;

import android.app.Activity;

import lombok.Getter;
import lombok.experimental.Accessors;
import solutions.alterego.androidbound.ViewModel;

@Accessors(prefix = "m")
public class ListItemDetailActivityViewModel extends ViewModel {

    private static final int listSize = 250;

    @Getter
    private String mImageUrl;

    @Getter
    private String mTitle;

    public ListItemDetailActivityViewModel(Activity activity, IAndroidLogger logger, String title, String imageUrl) {
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
