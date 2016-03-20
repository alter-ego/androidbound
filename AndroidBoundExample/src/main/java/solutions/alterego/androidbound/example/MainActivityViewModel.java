package solutions.alterego.androidbound.example;

import com.alterego.advancedandroidlogger.interfaces.IAndroidLogger;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;

import lombok.Getter;
import lombok.experimental.Accessors;
import solutions.alterego.androidbound.ViewModel;
import solutions.alterego.androidbound.interfaces.IDisposable;

@Accessors(prefix = "m")
public class MainActivityViewModel extends ViewModel implements IDisposable {

    @Getter
    private String mMainActivityTitle;

    @Getter
    private String mOpenBindableActivityText;

    public MainActivityViewModel(Activity activity, IAndroidLogger logger) {
        setLogger(logger);
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

}
