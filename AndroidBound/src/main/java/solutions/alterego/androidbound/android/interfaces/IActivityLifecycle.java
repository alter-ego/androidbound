package solutions.alterego.androidbound.android.interfaces;

import android.os.Bundle;

public interface IActivityLifecycle {

    void onCreate(Bundle savedInstanceState);

    void onResume();

    void onPause();

    void onSaveInstanceState(Bundle outState);

    void onDestroy();
}
