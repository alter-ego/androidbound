package solutions.alterego.androidbound.example;

import com.alterego.advancedandroidlogger.interfaces.IAndroidLogger;

import android.graphics.Typeface;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;

import solutions.alterego.androidbound.ViewBinder;
import solutions.alterego.androidbound.ViewModel;
import solutions.alterego.androidbound.example.imageloader.UILImageLoader;
import solutions.alterego.androidbound.example.util.AdvancedAndroidLoggerAdapter;
import solutions.alterego.androidbound.example.viewmodels.MainActivityViewModel;
import solutions.alterego.androidbound.interfaces.ILogger;


public class MainActivity extends AppCompatActivity {

    public static final String LOGGING_TAG = "TEST_APP";

    private static final IAndroidLogger.LoggingLevel LOGGING_LEVEL = IAndroidLogger.LoggingLevel.VERBOSE;

    private ViewBinder mViewBinder;

    private ViewModel mViewModel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ILogger logger = new AdvancedAndroidLoggerAdapter(LOGGING_TAG, LOGGING_LEVEL);
        mViewBinder = new ViewBinder(this, logger);

        mViewBinder.getFontManager().setDefaultFont(Typeface.createFromAsset(getAssets(), "Roboto-Bold.ttf"));
        mViewBinder.getFontManager().registerFont("light", Typeface.createFromAsset(getAssets(), "Roboto-Light.ttf"));
        mViewBinder.getFontManager().registerFont("italic", Typeface.createFromAsset(getAssets(), "Roboto-Italic.ttf"));
        mViewBinder.getFontManager().registerFont("bold", Typeface.createFromAsset(getAssets(), "Roboto-Bold.ttf"));
        mViewBinder.setImageLoader(new UILImageLoader(this, null));

        mViewModel = new MainActivityViewModel(this, logger);
        mViewModel.onCreate(savedInstanceState);

        View view = mViewBinder.inflate(this, mViewModel, R.layout.activity_main, null);
        setContentView(view);

        setTitle("MainActivity");
    }

    @Override
    protected void onResume() {
        super.onResume();
        mViewModel.onResume();
    }

    @Override
    protected void onPause() {
        super.onPause();
        mViewModel.onPause();
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        mViewModel.onSaveInstanceState(outState);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mViewModel.dispose();

        //you shouldn't actually dispose of the view binder, it should be global (injected via Dagger maybe)!
        //this is just for demonstration purposes
        if (mViewBinder != null) {
            mViewBinder.dispose();
        }
    }
}
