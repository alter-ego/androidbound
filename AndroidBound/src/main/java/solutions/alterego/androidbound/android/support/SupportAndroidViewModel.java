package solutions.alterego.androidbound.android.support;

import android.app.Activity;
import android.os.Bundle;

import java.lang.ref.WeakReference;

import lombok.Getter;
import lombok.experimental.Accessors;
import solutions.alterego.androidbound.ViewModel;
import solutions.alterego.androidbound.android.interfaces.IActivityFocus;
import solutions.alterego.androidbound.android.interfaces.IActivityLifecycle;
import solutions.alterego.androidbound.android.interfaces.IHasActivity;
import solutions.alterego.androidbound.android.interfaces.INeedsActivity;

@Accessors(prefix = "m")
public class SupportAndroidViewModel extends ViewModel
        implements IActivityLifecycle, IActivityFocus, INeedsActivity, IHasActivity, INeedsSupportFragmentManager,
        IHasSupportFragmentManager {

    private transient WeakReference<Activity> mParentActivity;

    @Getter
    private boolean mCreated = false;

    @Getter
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
}
