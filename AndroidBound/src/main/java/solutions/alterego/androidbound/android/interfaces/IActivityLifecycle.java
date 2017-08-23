package solutions.alterego.androidbound.android.interfaces;

import android.os.Bundle;

public interface IActivityLifecycle {

    void onCreate(Bundle savedInstanceState);

    boolean isCreated();

    void onStart();

    void onRestart();

    void onResume();

    void onPause();

    void onStop();

    void onSaveInstanceState(Bundle outState);

    void onDestroy();
}
