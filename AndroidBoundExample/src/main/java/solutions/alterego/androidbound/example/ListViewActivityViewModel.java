package solutions.alterego.androidbound.example;

import com.alterego.advancedandroidlogger.interfaces.IAndroidLogger;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

import lombok.Getter;
import lombok.experimental.Accessors;
import solutions.alterego.androidbound.ViewModel;
import solutions.alterego.androidbound.interfaces.IDisposable;

@Accessors(prefix = "m")
public class ListViewActivityViewModel extends ViewModel implements IDisposable {

    @Getter
    private String mListViewActivityTitle;

    @Getter
    private String mOpenMainActivityText;

    @Getter
    private List<ListViewItem> mExampleList;

    public ListViewActivityViewModel(Activity activity, IAndroidLogger logger) {
        setLogger(logger);
        setParentActivity(activity);

        setListViewActivityTitle("ListView activity");
    }

    public void setListViewActivityTitle(String title) {
        mListViewActivityTitle = title;
        raisePropertyChanged("ListViewActivityTitle");
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

    public boolean canOpenMainActivity() {
        return true;
    }

    public void doOpenMainActivity() {
        Intent activityIntent = new Intent(getParentActivity(), MainActivity.class);
        if (getParentActivity() != null) {
            getParentActivity().startActivity(activityIntent);
        }
    }

    public boolean canSelectListItem() {
        return true;
    }

    public void doSelectListItem() {
        Toast.makeText(getParentActivity(), "clicked list item!", Toast.LENGTH_SHORT).show();
    }

    public boolean canAddListItem() {
        if (mExampleList == null || mExampleList.size() < 3) {
            Toast.makeText(getParentActivity(), "added list item!", Toast.LENGTH_SHORT).show();
            return true;
        } else {
            Toast.makeText(getParentActivity(), "not adding any more!", Toast.LENGTH_SHORT).show();
            return false;
        }
    }

    public void doAddListItem() {
        if (mExampleList == null) {
            mExampleList = new ArrayList<ListViewItem>();
        }

        mExampleList.add(new ListViewItem(Integer.toString(mExampleList.size())));
        raisePropertyChanged("ExampleList");
    }

    public static class ListViewItem {
        @Getter
        private String mTitle;

        public ListViewItem(String title) {
            mTitle = title;
        }
    }

}
