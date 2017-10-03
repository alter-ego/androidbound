package solutions.alterego.androidbound.example.fragment;

import android.app.Activity;
import android.os.Bundle;
import android.support.annotation.Nullable;

import solutions.alterego.androidbound.example.R;

public class TestFragmentActivity extends Activity {

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_fragment_test);
        if (savedInstanceState == null) {
            getFragmentManager().beginTransaction().replace(R.id.content, new TestFragment()).commit();
        }
    }
}
