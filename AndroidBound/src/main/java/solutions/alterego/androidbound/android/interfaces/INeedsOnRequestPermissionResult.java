package solutions.alterego.androidbound.android.interfaces;

import android.support.annotation.NonNull;

public interface INeedsOnRequestPermissionResult {

    void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults);

}
