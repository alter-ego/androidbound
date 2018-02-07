package solutions.alterego.androidbound.android;

import android.app.Activity;
import android.content.Intent;
import android.content.res.Configuration;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.view.View;

import java.lang.ref.WeakReference;
import java.util.HashMap;
import java.util.Map;

import solutions.alterego.androidbound.NullLogger;
import solutions.alterego.androidbound.ViewModel;
import solutions.alterego.androidbound.android.interfaces.IActivityFocus;
import solutions.alterego.androidbound.android.interfaces.IActivityLifecycle;
import solutions.alterego.androidbound.android.interfaces.IBindableView;
import solutions.alterego.androidbound.android.interfaces.IBoundActivity;
import solutions.alterego.androidbound.android.interfaces.INeedsActivity;
import solutions.alterego.androidbound.android.interfaces.INeedsConfigurationChange;
import solutions.alterego.androidbound.android.interfaces.INeedsFragmentManager;
import solutions.alterego.androidbound.android.interfaces.INeedsNewIntent;
import solutions.alterego.androidbound.android.interfaces.INeedsOnActivityResult;
import solutions.alterego.androidbound.android.interfaces.INeedsOnRequestPermissionResult;
import solutions.alterego.androidbound.interfaces.IHasLogger;
import solutions.alterego.androidbound.interfaces.ILogger;
import solutions.alterego.androidbound.interfaces.INeedsLogger;
import solutions.alterego.androidbound.interfaces.IViewBinder;

public class BoundActivityDelegate
        implements IActivityLifecycle, IActivityFocus, IBoundActivity, INeedsOnActivityResult, INeedsOnRequestPermissionResult, INeedsNewIntent,
        INeedsConfigurationChange, INeedsLogger, IHasLogger {

    public static final String TAG_VIEWMODEL_MAIN = "androidbound_viewmodel_main";

    protected Map<String, ViewModel> mViewModels;

    private ILogger mLogger = null;

    private transient WeakReference<Activity> mBoundActivity;

    private boolean mShouldCallCreate = false;

    private Bundle mCreateBundle;

    protected IViewBinder mViewBinder;

    public BoundActivityDelegate(Activity activity) {
        this(activity, null);
    }

    public BoundActivityDelegate(Activity activity, IViewBinder viewBinder) {
        mBoundActivity = new WeakReference<>(activity);
        mViewBinder = viewBinder;
    }

    protected IViewBinder getViewBinder() {
        if (mViewBinder != null) {
            return mViewBinder;
        } else if (getBoundActivity() instanceof IBindableView) {
            return ((IBindableView) getBoundActivity()).getViewBinder();
        } else {
            throw new RuntimeException("There's no IViewBinder available!");
        }
    }

    protected Activity getBoundActivity() {
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
        if (getBoundActivity() == null) {
            throw new RuntimeException("Bound Activity is null!");
        }

        if (viewModel == null) {
            throw new RuntimeException("viewModel is null!");
        }

        if (mViewModels == null) {
            mViewModels = new HashMap<>();
        }

        if (mViewModels.values().contains(viewModel)) {
            throw new RuntimeException("cannot add same instance of viewModel twice!");
        }

        if (viewModel instanceof INeedsActivity) {
            ((INeedsActivity) viewModel).setParentActivity(getBoundActivity());
        }

        if (viewModel instanceof INeedsFragmentManager) {
            ((INeedsFragmentManager) viewModel).setFragmentManager(getBoundActivity().getFragmentManager());
        }

        if (viewModel instanceof INeedsLogger) {
            ((INeedsLogger) viewModel).setLogger(getLogger());
        }

        mViewModels.put(id, viewModel);

        View view = getViewBinder().inflate(getBoundActivity(), viewModel, layoutResID, null);

        if (isShouldCallCreate()) {
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
                if (viewModel instanceof IActivityLifecycle && !((IActivityLifecycle) viewModel).isCreated()) {
                    ((IActivityLifecycle) viewModel).onCreate(savedInstanceState);
                }
            }

            mShouldCallCreate = false;
        } else {
            mShouldCallCreate = true;
            mCreateBundle = savedInstanceState;
        }

        if (getBoundActivity() != null && getBoundActivity().getIntent() != null) {
            onNewIntent(getBoundActivity().getIntent()); //we call this manually so that you don't have to check in ViewModel.onCreate()
        }
    }

    @Override
    public boolean isCreated() {
        return true;
    }

    @Override
    public void onStart() {
        if (getViewModels() != null) {
            for (ViewModel viewModel : getViewModels().values()) {
                if (viewModel instanceof IActivityLifecycle) {
                    ((IActivityLifecycle) viewModel).onStart();
                }
            }
        }
    }

    @Override
    public void onRestart() {
        if (getViewModels() != null) {
            for (ViewModel viewModel : getViewModels().values()) {
                if (viewModel instanceof IActivityLifecycle) {
                    ((IActivityLifecycle) viewModel).onRestart();
                }
            }
        }
    }

    @Override
    public void onStop() {
        if (getViewModels() != null) {
            for (ViewModel viewModel : getViewModels().values()) {
                if (viewModel instanceof IActivityLifecycle) {
                    ((IActivityLifecycle) viewModel).onStop();
                }
            }
        }
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        mShouldCallCreate = false;
        mCreateBundle = null;

        if (getViewModels() != null) {
            for (ViewModel viewModel : getViewModels().values()) {
                if (viewModel instanceof IActivityLifecycle) {
                    ((IActivityLifecycle) viewModel).onSaveInstanceState(outState);
                }
            }
        }
    }

    @Override
    public void onDestroy() {
        Activity boundActivityRef = getBoundActivity();

        if (mBoundActivity != null
                && boundActivityRef != null
                && boundActivityRef instanceof IBindableView
                && boundActivityRef.getWindow() != null
                && boundActivityRef.getWindow().getDecorView() != null) {
            getViewBinder().getViewBindingEngine().clearBindingForViewAndChildren(getBoundActivity().getWindow().getDecorView());
            getViewBinder().disposeOf(mBoundActivity.get());
        }

        if (getViewModels() != null) {
            for (ViewModel viewModel : getViewModels().values()) {
                if (viewModel instanceof IActivityLifecycle) {
                    ((IActivityLifecycle) viewModel).onDestroy();
                } else {
                    viewModel.dispose();
                }
            }
        }

        mViewModels = null;
        mBoundActivity.clear();
        mBoundActivity = null;
        mViewBinder = null;
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
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (getViewModels() != null) {
            for (ViewModel viewModel : getViewModels().values()) {
                if (viewModel instanceof INeedsOnRequestPermissionResult) {
                    ((INeedsOnRequestPermissionResult) viewModel).onRequestPermissionsResult(requestCode, permissions, grantResults);
                }
            }
        }
    }

    @Override
    public ILogger getLogger() {
        return mLogger != null ? mLogger : NullLogger.instance;
    }

    @Override
    public void setLogger(ILogger logger) {
        mLogger = logger;

        if (getViewModels() != null) {
            for (ViewModel viewModel : getViewModels().values()) {
                if (viewModel instanceof INeedsLogger) {
                    ((INeedsLogger) viewModel).setLogger(getLogger());
                }
            }
        }
    }

    @Override
    public void onGotFocus() {
        if (getViewModels() != null) {
            for (ViewModel viewModel : getViewModels().values()) {
                if (viewModel instanceof IActivityFocus) {
                    ((IActivityFocus) viewModel).onGotFocus();
                }
            }
        }
    }

    @Override
    public void onLostFocus() {
        if (getViewModels() != null) {
            for (ViewModel viewModel : getViewModels().values()) {
                if (viewModel instanceof IActivityFocus) {
                    ((IActivityFocus) viewModel).onLostFocus();
                }
            }
        }
    }

    public Map<String, ViewModel> getViewModels() {
        return this.mViewModels;
    }

    public boolean isShouldCallCreate() {
        return this.mShouldCallCreate;
    }

    public Bundle getCreateBundle() {
        return this.mCreateBundle;
    }
}