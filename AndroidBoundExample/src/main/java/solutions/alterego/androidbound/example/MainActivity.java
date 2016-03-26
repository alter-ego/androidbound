package solutions.alterego.androidbound.example;

import com.alterego.advancedandroidlogger.implementations.DetailedAndroidLogger;
import com.alterego.advancedandroidlogger.interfaces.IAndroidLogger;

import android.graphics.Typeface;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;

import solutions.alterego.androidbound.ViewBinder;
import solutions.alterego.androidbound.ViewModel;


public class MainActivity extends AppCompatActivity {

    public static final String LOGGING_TAG = "TEST_APP";

    private static final IAndroidLogger.LoggingLevel LOGGING_LEVEL = IAndroidLogger.LoggingLevel.VERBOSE;

    private ViewBinder mViewBinder;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        IAndroidLogger logger = new DetailedAndroidLogger(LOGGING_TAG, LOGGING_LEVEL);
        mViewBinder = new ViewBinder(this, logger, null);
        mViewBinder.getFontManager().setDefaultFont(Typeface.createFromAsset(getAssets(), "Roboto-Regular.ttf"));
        mViewBinder.getFontManager().registerFont("light", Typeface.createFromAsset(getAssets(), "Roboto-Light.ttf"));
        mViewBinder.getFontManager().registerFont("italic", Typeface.createFromAsset(getAssets(), "Roboto-Italic.ttf"));
        mViewBinder.getFontManager().registerFont("bold", Typeface.createFromAsset(getAssets(), "Roboto-Bold.ttf"));

        ViewModel viewModel = new MainActivityViewModel(this, logger);

        View view = mViewBinder.inflate(this, viewModel, R.layout.activity_main, null);
        setContentView(view);

        setTitle("MainActivity");
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        //you should dispose of it only if it's not global (injected via Dagger, maybe)!
        if (mViewBinder != null) {
            mViewBinder.dispose();
        }
    }
}
