package solutions.alterego.androidbound.support.android;

import android.app.Activity;
import android.os.Bundle;
import android.support.v4.app.FragmentManager;

import java.lang.ref.WeakReference;

import solutions.alterego.androidbound.ViewModel;
import solutions.alterego.androidbound.android.interfaces.IActivityFocus;
import solutions.alterego.androidbound.android.interfaces.IActivityLifecycle;
import solutions.alterego.androidbound.android.interfaces.IHasActivity;
import solutions.alterego.androidbound.android.interfaces.INeedsActivity;
import solutions.alterego.androidbound.support.android.interfaces.IHasSupportFragmentManager;
import solutions.alterego.androidbound.support.android.interfaces.INeedsSupportFragmentManager;

public class SupportAndroidViewModel extends ViewModel
        implements IActivityLifecycle, IActivityFocus, INeedsActivity, IHasActivity, INeedsSupportFragmentManager, IHasSupportFragmentManager {

    private transient WeakReference<Activity> mParentActivity;

    private boolean mCreated = false;

    private android.support.v4.app.FragmentManager mFragmentManager;

    @Override
    public void setParentActivity(Activity activity) {
        if (activity != null) {
            mParentActivity = new WeakReference<>(activity);
        }
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
    public void setFragmentManager(android.support.v4.app.FragmentManager fragmentManager) {
        mFragmentManager = fragmentManager;
    }

    @Override
    public void onGotFocus() {
        //do nothing, to be overridden
    }

    @Override
    public void onLostFocus() {
        //do nothing, to be overridden
    }

    public boolean isCreated() {
        return this.mCreated;
    }

    public FragmentManager getFragmentManager() {
        return this.mFragmentManager;
    }
}
