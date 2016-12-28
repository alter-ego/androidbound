package solutions.alterego.androidbound.android.interfaces;

import android.os.Bundle;

public interface IActivityLifecycle {

    void onCreate(Bundle savedInstanceState);

    void onStart();

    void onRestart();

    void onResume();

    void onPause();

    void onStop();

    void onSaveInstanceState(Bundle outState);

    void onDestroy();
}
