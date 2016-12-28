package solutions.alterego.androidbound.android;

import com.alterego.advancedandroidlogger.implementations.NullAndroidLogger;
import com.alterego.advancedandroidlogger.interfaces.IAndroidLogger;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;

import lombok.Setter;
import lombok.experimental.Accessors;
import solutions.alterego.androidbound.ViewModel;
import solutions.alterego.androidbound.android.interfaces.IBindableView;
import solutions.alterego.androidbound.android.interfaces.IBoundActivity;

@Accessors(prefix = "m")
public abstract class BindingAppCompatActivity extends AppCompatActivity implements IBindableView, IBoundActivity {

    @Setter
    protected IAndroidLogger mLogger = NullAndroidLogger.instance;

    private BoundActivityDelegate mBoundActivityDelegate;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mBoundActivityDelegate = new BoundActivityDelegate(this);
        mBoundActivityDelegate.onCreate(savedInstanceState);
    }

    @Override
    protected void onResume() {
        super.onResume();

        if (mBoundActivityDelegate != null) {
            mBoundActivityDelegate.onResume();
        }
    }

    @Override
    protected void onPause() {
        super.onPause();

        if (mBoundActivityDelegate != null) {
            mBoundActivityDelegate.onPause();
        }
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);

        if (mBoundActivityDelegate != null) {
            mBoundActivityDelegate.onSaveInstanceState(outState);
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (mBoundActivityDelegate != null) {
            mBoundActivityDelegate.onDestroy();
        }
    }

    @Override
    @Deprecated //should use setContentView with ViewModel or addViewModel
    public void setContentView(int layoutResID) {
        super.setContentView(layoutResID);
    }

    @Override
    public void setContentView(int layoutResID, ViewModel viewModel) {
        if (mBoundActivityDelegate != null) {
            mBoundActivityDelegate.setContentView(layoutResID, viewModel);
        }
    }

    @Override
    public View addViewModel(int layoutResID, ViewModel viewModel) {
        if (mBoundActivityDelegate != null) {
            return mBoundActivityDelegate.addViewModel(layoutResID, viewModel);
        } else {
            return null;
        }
    }
}