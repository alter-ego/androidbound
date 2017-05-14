package solutions.alterego.androidbound.example.viewmodels;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.widget.Toast;

import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;
import solutions.alterego.androidbound.ViewModel;
import solutions.alterego.androidbound.example.ListViewActivity;
import solutions.alterego.androidbound.example.ListViewWithObjectsActivity;
import solutions.alterego.androidbound.example.MainBindingActivity;
import solutions.alterego.androidbound.example.PaginatedRecyclerViewActivity;
import solutions.alterego.androidbound.example.R;
import solutions.alterego.androidbound.example.RecyclerViewWithObjectsActivity;
import solutions.alterego.androidbound.example.fragment.TestFragmentActivity;
import solutions.alterego.androidbound.interfaces.ILogger;

@Accessors(prefix = "m")
public class MainActivityViewModel extends ViewModel {

    @Getter
    private String mMainActivityTitle;

    @Getter
    private String mOpenBindableActivityText;

    @Getter
    private String mEditTextText = "empty edit text";

    @Getter
    private String mTextViewBoundToEditText = "empty";

    @Getter
    @Setter
    private String mBoundEditTextText = mEditTextText;

    public MainActivityViewModel(Activity activity, ILogger logger) {
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

    public void doOpenPaginatedRecyclerViewActivity() {
        getParentActivity().startActivity(new Intent(getParentActivity(), PaginatedRecyclerViewActivity.class));
    }

    @Override
    public void onCreate(Bundle outState) {
        //do nothing
        raisePropertyChanged("BoundEditTextText");
    }

//    @Override
//    public void onStart() {
//        //do nothing
//    }
//
//    @Override
//    public void onRestart() {
//        //do nothing
//    }

    @Override
    public void onResume() {
        //do nothing
    }

    @Override
    public void onPause() {
        //do nothing
    }

//    @Override
//    public void onStop() {
//        //do nothing
//    }

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

    public boolean canClearEditTextText() {
        return true;
    }

    public void doClearEditTextText() {
        setBoundEditTextText("");
        raisePropertyChanged("BoundEditTextText");
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

    public boolean canOpenRecyclerViewWithObjectsActivity() {
        return true;
    }

    public void doOpenRecyclerViewWithObjectsActivity() {
        Intent activityIntent = new Intent(getParentActivity(), RecyclerViewWithObjectsActivity.class);
        if (getParentActivity() != null) {
            getParentActivity().startActivity(activityIntent);
        }
    }

    public void doOpenFragmentActivity() {
        if (getParentActivity() != null) {
            Intent intent = new Intent(getParentActivity(), TestFragmentActivity.class);
            getParentActivity().startActivity(intent);
        }

    }

    public String getOpenActivityButtonContentDescription() {
        return "opens main activity";
    }
}