package solutions.alterego.androidbound.example;

import com.alterego.advancedandroidlogger.interfaces.IAndroidLogger;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.widget.Toast;

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

    @Getter
    private String mEditTextText = "empty edit text";

    @Getter
    private String mTextViewBoundToEditText = "empty";

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



    public int getButtonBackgroundColor() {
        return Color.rgb(255, 0, 50);
    }

    public boolean canTextViewClick() {
        return true;
    }

    public void doTextViewClick() {
        Toast.makeText(getParentActivity(), "clicked text view!", Toast.LENGTH_SHORT).show();
    }

    public boolean canTextViewLongClick() {
        return true;
    }

    public void doTextViewLongClick() {
        Toast.makeText(getParentActivity(), "long clicked text view!", Toast.LENGTH_SHORT).show();
    }

    public void setEditTextText(String text) {
        mLogger.info("text = " + text);
        mTextViewBoundToEditText = text;
        raisePropertyChanged("TextViewBoundToEditText");
    }

    public int getEditTextColor() {
        return Color.rgb(255, 0, 255);
    }

    public int getImageViewResource() {
        return R.mipmap.ic_launcher;
    }

    public boolean canImageViewClick() {
        return true;
    }

    public void doImageViewClick() {
        Toast.makeText(getParentActivity(), "clicked ImageView!", Toast.LENGTH_SHORT).show();
    }

    public boolean canImageViewLongClick() {
        return true;
    }

    public void doImageViewLongClick() {
        Toast.makeText(getParentActivity(), "long clicked ImageView!", Toast.LENGTH_SHORT).show();
    }

}
