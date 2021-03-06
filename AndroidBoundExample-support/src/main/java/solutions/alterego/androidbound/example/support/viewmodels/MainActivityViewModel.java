package solutions.alterego.androidbound.example.support.viewmodels;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;

import solutions.alterego.androidbound.android.AndroidViewModel;
import solutions.alterego.androidbound.example.support.MainBindingActivity;
import solutions.alterego.androidbound.example.support.PaginatedRecyclerViewActivity;
import solutions.alterego.androidbound.example.support.RecyclerViewActivity;
import solutions.alterego.androidbound.example.support.RecyclerViewWithObjectsActivity;
import solutions.alterego.androidbound.example.support.fragment.TestFragmentActivity;
import solutions.alterego.androidbound.example.support.nestedrvs.NestedRecyclerViewActivity;
import solutions.alterego.androidbound.interfaces.ILogger;

public class MainActivityViewModel extends AndroidViewModel {

    private String mMainActivityTitle;

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

    public void doOpenPaginatedRecyclerViewActivity() {
        getParentActivity().startActivity(new Intent(getParentActivity(), PaginatedRecyclerViewActivity.class));
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

    public boolean canOpenRecyclerViewActivity() {
        return true;
    }

    public void doOpenRecyclerViewActivity() {
        Intent activityIntent = new Intent(getParentActivity(), RecyclerViewActivity.class);
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

    public void  doOpenNestedRecyclerViewActivity() {
        Intent activityIntent = new Intent(getParentActivity(), NestedRecyclerViewActivity.class);
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

    public String getMainActivityTitle() {
        return this.mMainActivityTitle;
    }

    public String getOpenBindableActivityText() {
        return this.mOpenBindableActivityText;
    }
}