package solutions.alterego.androidbound.android.interfaces;

import android.app.Activity;

import java.lang.ref.WeakReference;

public interface INeedsActivity {

    void setParentActivity(WeakReference<Activity> activityRef);
}
