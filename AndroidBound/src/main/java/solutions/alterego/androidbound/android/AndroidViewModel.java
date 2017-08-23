package solutions.alterego.androidbound.android;

import android.app.Activity;
import android.app.FragmentManager;
import android.os.Bundle;

import java.lang.ref.WeakReference;

import lombok.Getter;
import lombok.experimental.Accessors;
import solutions.alterego.androidbound.ViewModel;
import solutions.alterego.androidbound.android.interfaces.IActivityLifecycle;
import solutions.alterego.androidbound.android.interfaces.IHasActivity;
import solutions.alterego.androidbound.android.interfaces.IHasFragmentManager;
import solutions.alterego.androidbound.android.interfaces.INeedsActivity;
import solutions.alterego.androidbound.android.interfaces.INeedsFragmentManager;

@Accessors(prefix = "m")
public class AndroidViewModel extends ViewModel
        implements IActivityLifecycle, INeedsActivity, IHasActivity, INeedsFragmentManager, IHasFragmentManager {

    private transient WeakReference<Activity> mParentActivity;

    @Getter
    private boolean mCreated = false;

    @Getter
    private FragmentManager mFragmentManager;

    @Override
    public void setParentActivity(WeakReference<Activity> activityRef) {
        mParentActivity = activityRef;
    }

    @Override
    public Activity getParentActivity() {
        if (mParentActivity != null && mParentActivity.get() != null) {
            return mParentActivity.get();
        }

        return null;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        mCreated = true;
    }

    @Override
    public void onStart() {
        //do nothing, to be overridden
    }

    @Override
    public void onRestart() {
        //do nothing, to be overridden
    }

    @Override
    public void onResume() {
        //do nothing, to be overridden
    }

    @Override
    public void onPause() {
        //do nothing, to be overridden
    }

    @Override
    public void onStop() {
        //do nothing, to be overridden
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        //do nothing, to be overridden
    }

    @Override
    public void onDestroy() {
        if (!isDisposed()) {
            dispose();
        }

        mCreated = false;
    }

    @Override
    public void dispose() {
        super.dispose();

        if (mParentActivity != null && mParentActivity.get() != null) {
            mParentActivity.clear();
        }

        mFragmentManager = null;
    }

    @Override
    public void setFragmentManager(FragmentManager fragmentManager) {
        mFragmentManager = fragmentManager;
    }

}
