package solutions.alterego.androidbound.android.interfaces;

import android.content.Intent;

public interface INeedsOnActivityResult {

    void onActivityResult(int requestCode, int resultCode, Intent data);

}
