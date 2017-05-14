package solutions.alterego.androidbound.example;

import org.joda.time.DateTime;

import android.app.Activity;
import android.content.Intent;
import android.widget.Toast;

import lombok.Getter;
import lombok.experimental.Accessors;
import solutions.alterego.androidbound.ViewModel;
import solutions.alterego.androidbound.interfaces.ILogger;

@Accessors(prefix = "m")
public class MainBindingActivityViewModel extends ViewModel {

    @Getter
    private String mMainActivityTitle;

    @Getter
    private String mOpenNormalActivityText;

    @Getter
    private boolean mTextViewVisible = false;

    public MainBindingActivityViewModel(Activity activity, ILogger logger) {
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

    public boolean canOpenNormalActivity() {
        return true;
    }

    public void doOpenNormalActivity() {
        Intent activityIntent = new Intent(getParentActivity(), MainActivity.class);
        if (getParentActivity() != null) {
            getParentActivity().startActivity(activityIntent);
        }
    }

    public boolean canRelativeLayoutClick() {
        return true;
    }

    public void doRelativeLayoutClick() {
        Toast.makeText(getParentActivity(), "clicked relative layout!", Toast.LENGTH_SHORT).show();
    }

    public boolean canToggleTextViewVisibility() {
        return true;
    }

    public void doToggleTextViewVisibility() {
        setTextViewVisible(!isTextViewVisible());
    }

    public void setTextViewVisible(boolean visible) {
        mTextViewVisible = visible;
        raisePropertyChanged("TextViewVisible");
    }

    public String getImageViewSourceUrl() {
        return "https://www.google.com/images/branding/googlelogo/1x/googlelogo_color_272x92dp.png";
    }

    public DateTime getCurrentDate() {
        return DateTime.now();
    }
}
