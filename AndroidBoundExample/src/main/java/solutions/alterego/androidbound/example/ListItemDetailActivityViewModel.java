package solutions.alterego.androidbound.example;

import com.alterego.advancedandroidlogger.interfaces.IAndroidLogger;

import android.app.Activity;
import android.os.Bundle;

import lombok.Getter;
import lombok.experimental.Accessors;
import solutions.alterego.androidbound.ViewModel;
import solutions.alterego.androidbound.interfaces.IDisposable;

@Accessors(prefix = "m")
public class ListItemDetailActivityViewModel extends ViewModel implements IDisposable {

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

}
