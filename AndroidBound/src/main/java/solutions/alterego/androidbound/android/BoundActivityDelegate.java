package solutions.alterego.androidbound.android;

import com.alterego.advancedandroidlogger.interfaces.IAndroidLogger;

import android.app.Activity;
import android.content.Intent;
import android.content.res.Configuration;
import android.os.Bundle;
import android.view.View;

import java.lang.ref.WeakReference;
import java.util.HashMap;
import java.util.Map;

import lombok.Getter;
import lombok.experimental.Accessors;
import solutions.alterego.androidbound.ViewBinder;
import solutions.alterego.androidbound.ViewModel;
import solutions.alterego.androidbound.android.interfaces.IActivityLifecycle;
import solutions.alterego.androidbound.android.interfaces.IBindableView;
import solutions.alterego.androidbound.android.interfaces.IBoundActivity;
import solutions.alterego.androidbound.android.interfaces.INeedsConfigurationChange;
import solutions.alterego.androidbound.android.interfaces.INeedsNewIntent;
import solutions.alterego.androidbound.android.interfaces.INeedsOnActivityResult;
import solutions.alterego.androidbound.interfaces.IHasLogger;
import solutions.alterego.androidbound.interfaces.INeedsLogger;

@Accessors(prefix = "m")
public class BoundActivityDelegate implements IActivityLifecycle, IBoundActivity, INeedsOnActivityResult, INeedsNewIntent, INeedsConfigurationChange,
        INeedsLogger, IHasLogger {

    public static final String TAG_VIEWMODEL_MAIN = "androidbound_viewmodel_main";

    @Getter
    private Map<String, ViewModel> mViewModels;

    private IAndroidLogger mLogger = null;

    private transient WeakReference<Activity> mBoundActivity;

    private boolean mShouldCallCreate = false;

    private Bundle mCreateBundle;

    public BoundActivityDelegate(Activity activity) {
        mBoundActivity = new WeakReference<>(activity);
    }

    private Activity getBoundActivity() {
        return mBoundActivity != null ? mBoundActivity.get() : null;
    }

    @Override
    public void setContentView(int layoutResID, ViewModel viewModel) {
        if (mBoundActivity == null || getBoundActivity() == null) {
            throw new RuntimeException("Bound Activity is null!");
        }

        getBoundActivity().setContentView(addViewModel(layoutResID, viewModel, TAG_VIEWMODEL_MAIN));
    }

    @Override
    public View addViewModel(int layoutResID, ViewModel viewModel, String id) {
        if (mBoundActivity == null) {
            throw new RuntimeException("Bound Activity is null!");
        }

        if (!(getBoundActivity() instanceof IBindableView)) {
            throw new RuntimeException("Activity must implement IBindableView!");
        }

        if (viewModel == null) {
            throw new RuntimeException("viewModel is null!");
        }

        if (((IBindableView) getBoundActivity()).getViewBinder() == null) {
            throw new RuntimeException("getViewBinder must not be null!");
        }

        if (mViewModels == null) {
            mViewModels = new HashMap<>();
        }

        if (mViewModels.values().contains(viewModel)) {
            throw new RuntimeException("cannot add same instance of viewModel twice!");
        }

        viewModel.setParentActivity(getBoundActivity());
        viewModel.setLogger(getLogger());
        mViewModels.put(id, viewModel);

        View view = ((IBindableView) getBoundActivity()).getViewBinder().inflate(getBoundActivity(), viewModel, layoutResID, null);

        if (mShouldCallCreate) {
            onCreate(mCreateBundle);
        }

        return view;
    }

    @Override
    public ViewModel getViewModel(String id) {
        if (mViewModels != null) {
            return mViewModels.get(id);
        } else {
            return null;
        }
    }

    @Override
    public ViewModel getContentViewModel() {
        return getViewModel(TAG_VIEWMODEL_MAIN);
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        if (getViewModels() != null) {
            for (ViewModel viewModel : getViewModels().values()) {
                viewModel.onCreate(savedInstanceState);
            }
        } else {
            mShouldCallCreate = true;
            mCreateBundle = savedInstanceState;
        }
    }

    @Override
    public void onStart() {
        if (getViewModels() != null) {
            for (ViewModel viewModel : getViewModels().values()) {
                viewModel.onStart();
            }
        }
    }

    @Override
    public void onRestart() {
        if (getViewModels() != null) {
            for (ViewModel viewModel : getViewModels().values()) {
                viewModel.onRestart();
            }
        }
    }

    @Override
    public void onResume() {
        if (getViewModels() != null) {
            for (ViewModel viewModel : getViewModels().values()) {
                viewModel.onResume();
            }
        }
    }

    @Override
    public void onPause() {
        if (getViewModels() != null) {
            for (ViewModel viewModel : getViewModels().values()) {
                viewModel.onPause();
            }
        }
    }

    @Override
    public void onStop() {
        if (getViewModels() != null) {
            for (ViewModel viewModel : getViewModels().values()) {
                viewModel.onStop();
            }
        }
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        mShouldCallCreate = false;
        mCreateBundle = null;

        if (getViewModels() != null) {
            for (ViewModel viewModel : getViewModels().values()) {
                viewModel.onSaveInstanceState(outState);
            }
        }
    }

    @Override
    public void onDestroy() {
        Activity boundActivityRef = getBoundActivity();

        if (mBoundActivity != null
                && boundActivityRef != null
                && boundActivityRef instanceof IBindableView
                && ((IBindableView) boundActivityRef).getViewBinder() != null
                && boundActivityRef.getWindow() != null
                && boundActivityRef.getWindow().getDecorView() != null) {
            ((IBindableView) boundActivityRef).getViewBinder()
                    .clearBindingForViewAndChildren(getBoundActivity().getWindow().getDecorView().getRootView());
        }

        if (getViewModels() != null) {
            for (ViewModel viewModel : getViewModels().values()) {
                viewModel.onDestroy();
            }
        }

        mViewModels = null;
        mBoundActivity.clear();
        mBoundActivity = null;
        mLogger = null;
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        if (getViewModels() != null) {
            for (ViewModel viewModel : getViewModels().values()) {
                if (viewModel instanceof INeedsConfigurationChange) {
                    ((INeedsConfigurationChange) viewModel).onConfigurationChanged(newConfig);
                }
            }
        }
    }

    @Override
    public void onNewIntent(Intent newIntent) {
        if (getViewModels() != null) {
            for (ViewModel viewModel : getViewModels().values()) {
                if (viewModel instanceof INeedsNewIntent) {
                    ((INeedsNewIntent) viewModel).onNewIntent(newIntent);
                }
            }
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (getViewModels() != null) {
            for (ViewModel viewModel : getViewModels().values()) {
                if (viewModel instanceof INeedsOnActivityResult) {
                    ((INeedsOnActivityResult) viewModel).onActivityResult(requestCode, resultCode, data);
                }
            }
        }
    }

    @Override
    public IAndroidLogger getLogger() {
        return mLogger != null ? mLogger : ViewBinder.getLogger();
    }

    @Override
    public void setLogger(IAndroidLogger logger) {
        mLogger = logger;

        if (getViewModels() != null) {
            for (ViewModel viewModel : getViewModels().values()) {
                viewModel.setLogger(getLogger());
            }
        }
    }
}