package solutions.alterego.androidbound.example;

import com.alterego.advancedandroidlogger.interfaces.IAndroidLogger;

import android.app.Activity;
import android.content.Intent;

import lombok.Getter;
import lombok.experimental.Accessors;
import solutions.alterego.androidbound.ViewModel;
import solutions.alterego.androidbound.interfaces.IDisposable;

@Accessors(prefix = "m")
public class MainBindableActivityViewModel extends ViewModel implements IDisposable {

    @Getter
    private String mMainActivityTitle;

    @Getter
    private String mOpenNormalActivityText;

    public MainBindableActivityViewModel(Activity activity, IAndroidLogger logger) {
        setLogger(logger);
        setParentActivity(activity);

        setMainActivityTitle("Bindable Activity");
        setOpenNormalActivityText("Open Normal Activity");
    }

    public void setMainActivityTitle(String title) {
        mMainActivityTitle = title;
        raisePropertyChanged("MainActivityTitle");
    }

    public void setOpenNormalActivityText(String title) {
        mOpenNormalActivityText = title;
        raisePropertyChanged("OpenNormalActivityText");
    }

    @Override
    public void dispose() {
        //do nothing
    }

    public boolean canOpenNormalActivity() {
        return true;
    }

    public void doOpenNormalActivity() {
        Intent activityIntent = new Intent(getParentActivity(), MainActivity.class);
        if (getParentActivity() != null) {
            getParentActivity().startActivity(activityIntent);
        }
    }
}
