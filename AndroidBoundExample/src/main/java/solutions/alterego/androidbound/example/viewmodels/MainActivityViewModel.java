package solutions.alterego.androidbound.example.viewmodels;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;

import lombok.Getter;
import lombok.experimental.Accessors;
import solutions.alterego.androidbound.android.AndroidViewModel;
import solutions.alterego.androidbound.example.ListViewActivity;
import solutions.alterego.androidbound.example.ListViewWithObjectsActivity;
import solutions.alterego.androidbound.example.MainBindingActivity;
import solutions.alterego.androidbound.interfaces.ILogger;

@Accessors(prefix = "m")
public class MainActivityViewModel extends AndroidViewModel {

    @Getter
    private String mMainActivityTitle;

    @Getter
    private String mOpenBindableActivityText;

    public MainActivityViewModel(Activity activity, ILogger logger) {
        setParentActivity(activity);

        setMainActivityTitle("Main Activity");
        setOpenBindableActivityText("Open Bindable Activity");
    }

    public void setMainActivityTitle(String title) {
        mMainActivityTitle = title;
        raisePropertyChanged("MainActivityTitle");
    }

    public void setOpenBindableActivityText(String title) {
        mOpenBindableActivityText = title;
        raisePropertyChanged("OpenBindableActivityText");
    }

    public boolean canOpenBindableActivity() {
        return true;
    }

    public void doOpenBindableActivity() {
        Intent activityIntent = new Intent(getParentActivity(), MainBindingActivity.class);
        if (getParentActivity() != null) {
            getParentActivity().startActivity(activityIntent);
        }
    }

    public int getMainActivityTitleColor() {
        return Color.rgb(0, 255, 0);
    }

    public int getMainActivityBackgroundColor() {
        return Color.rgb(200, 250, 250);
    }

    public int getButtonBackgroundColor() {
        return Color.rgb(255, 0, 50);
    }

    public boolean canOpenListViewActivity() {
        return true;
    }

    public void doOpenListViewActivity() {
        Intent activityIntent = new Intent(getParentActivity(), ListViewActivity.class);
        if (getParentActivity() != null) {
            getParentActivity().startActivity(activityIntent);
        }
    }

    public boolean canOpenListViewWithObjectsActivity() {
        return true;
    }

    public void doOpenListViewWithObjectsActivity() {
        Intent activityIntent = new Intent(getParentActivity(), ListViewWithObjectsActivity.class);
        if (getParentActivity() != null) {
            getParentActivity().startActivity(activityIntent);
        }
    }

    public String getOpenActivityButtonContentDescription() {
        return "opens main activity";
    }
}