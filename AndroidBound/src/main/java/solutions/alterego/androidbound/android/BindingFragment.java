package solutions.alterego.androidbound.android;

import android.app.Fragment;
import android.content.Intent;
import android.content.res.Configuration;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import solutions.alterego.androidbound.NullLogger;
import solutions.alterego.androidbound.ViewModel;
import solutions.alterego.androidbound.android.interfaces.IBoundFragment;
import solutions.alterego.androidbound.android.interfaces.IFragmentLifecycle;
import solutions.alterego.androidbound.android.interfaces.INeedsNewIntent;
import solutions.alterego.androidbound.interfaces.IHasLogger;
import solutions.alterego.androidbound.interfaces.ILogger;
import solutions.alterego.androidbound.interfaces.INeedsLogger;

public class BindingFragment extends Fragment implements IFragmentLifecycle, IBoundFragment, INeedsNewIntent, INeedsLogger, IHasLogger {

    private ILogger mLogger = NullLogger.instance;

    protected BoundFragmentDelegate mBoundFragmentDelegate;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mBoundFragmentDelegate = createBoundFragmentDelegate();
        mBoundFragmentDelegate.onCreate(savedInstanceState);
        mBoundFragmentDelegate.setLogger(mLogger);
    }

    protected BoundFragmentDelegate createBoundFragmentDelegate() {
        return new BoundFragmentDelegate(this);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return null;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState, int layoutResID, ViewModel viewModel) {
        super.onCreateView(inflater, container, savedInstanceState); //this actually always returns null, but this way it's more explicit
        View view = null;

        if (mBoundFragmentDelegate != null) {
            view = mBoundFragmentDelegate.onCreateView(inflater, container, savedInstanceState, layoutResID, viewModel);
        }

        return view;
    }

    @Override
    public void onStart() {
        super.onStart();

        if (mBoundFragmentDelegate != null) {
            mBoundFragmentDelegate.onStart();
        }
    }

    @Override
    public void onResume() {
        super.onResume();

        if (mBoundFragmentDelegate != null) {
            mBoundFragmentDelegate.onGotFocus();
        }
    }

    @Override
    public void onPause() {
        super.onPause();

        if (mBoundFragmentDelegate != null) {
            mBoundFragmentDelegate.onLostFocus();
        }
    }

    @Override
    public void onStop() {
        super.onStop();

        if (mBoundFragmentDelegate != null) {
            mBoundFragmentDelegate.onStop();
        }
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);

        if (mBoundFragmentDelegate != null) {
            mBoundFragmentDelegate.onSaveInstanceState(outState);
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();

        if (mBoundFragmentDelegate != null) {
            mBoundFragmentDelegate.onDestroy();
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();

        if (mBoundFragmentDelegate != null) {
            mBoundFragmentDelegate.onDestroyView();
        }
    }

    @Override
    public View addViewModel(int layoutResID, ViewModel viewModel, String id, @Nullable ViewGroup parent) {
        if (mBoundFragmentDelegate != null) {
            return mBoundFragmentDelegate.addViewModel(layoutResID, viewModel, id, parent);
        } else {
            return null;
        }
    }

    @Override
    public ViewModel getViewModel(String id) {
        return mBoundFragmentDelegate != null ? mBoundFragmentDelegate.getViewModel(id) : null;
    }

    @Override
    public ViewModel getContentViewModel() {
        return mBoundFragmentDelegate != null ? mBoundFragmentDelegate.getContentViewModel() : null;
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);

        if (mBoundFragmentDelegate != null) {
            mBoundFragmentDelegate.onConfigurationChanged(newConfig);
        }
    }

    @Override
    public void onNewIntent(Intent intent) {
        if (mBoundFragmentDelegate != null) {
            mBoundFragmentDelegate.onNewIntent(intent);
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (mBoundFragmentDelegate != null) {
            mBoundFragmentDelegate.onActivityResult(requestCode, resultCode, data);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if (mBoundFragmentDelegate != null) {
            mBoundFragmentDelegate.onRequestPermissionsResult(requestCode, permissions, grantResults);
        }
    }

    @Override
    public ILogger getLogger() {
        if (mBoundFragmentDelegate != null) {
            return mBoundFragmentDelegate.getLogger();
        } else {
            return NullLogger.instance;
        }
    }

    @Override
    public void setLogger(ILogger logger) {
        mLogger = logger;

        if (mBoundFragmentDelegate != null) {
            mBoundFragmentDelegate.setLogger(logger);
        }
    }

}