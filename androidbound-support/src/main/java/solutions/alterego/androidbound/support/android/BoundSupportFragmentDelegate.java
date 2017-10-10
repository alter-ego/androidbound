package solutions.alterego.androidbound.support.android;

import android.app.Activity;
import android.content.Intent;
import android.content.res.Configuration;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.lang.ref.WeakReference;
import java.util.HashMap;
import java.util.Map;

import lombok.experimental.Accessors;
import solutions.alterego.androidbound.NullLogger;
import solutions.alterego.androidbound.ViewModel;
import solutions.alterego.androidbound.android.interfaces.IActivityFocus;
import solutions.alterego.androidbound.android.interfaces.IActivityLifecycle;
import solutions.alterego.androidbound.android.interfaces.IBindableView;
import solutions.alterego.androidbound.android.interfaces.IBoundFragment;
import solutions.alterego.androidbound.android.interfaces.IFragmentLifecycle;
import solutions.alterego.androidbound.android.interfaces.INeedsActivity;
import solutions.alterego.androidbound.android.interfaces.INeedsConfigurationChange;
import solutions.alterego.androidbound.android.interfaces.INeedsFragmentManager;
import solutions.alterego.androidbound.android.interfaces.INeedsNewIntent;
import solutions.alterego.androidbound.android.interfaces.INeedsOnActivityResult;
import solutions.alterego.androidbound.interfaces.IHasLogger;
import solutions.alterego.androidbound.interfaces.ILogger;
import solutions.alterego.androidbound.interfaces.INeedsLogger;
import solutions.alterego.androidbound.interfaces.IViewBinder;
import solutions.alterego.androidbound.support.android.interfaces.INeedsSupportFragmentManager;

@Accessors(prefix = "m")
public class BoundSupportFragmentDelegate
        implements IActivityLifecycle, IActivityFocus, IFragmentLifecycle, IBoundFragment, INeedsOnActivityResult, INeedsNewIntent, INeedsConfigurationChange,
        INeedsLogger, IHasLogger {

    public static final String TAG_VIEWMODEL_MAIN = "androidbound_viewmodel_main";

    private Map<String, ViewModel> mViewModels;

    private ILogger mLogger = NullLogger.instance;

    private transient View mBoundView;

    private transient WeakReference<Activity> mBoundActivity;

    private boolean mShouldCallCreate = false;

    private Bundle mCreateBundle;

    private IViewBinder mViewBinder;

    public BoundSupportFragmentDelegate(Fragment fragment) {
        this(fragment, null);
    }

    public BoundSupportFragmentDelegate(Fragment fragment, IViewBinder viewBinder) {
        mBoundActivity = new WeakReference<>((Activity) fragment.getActivity());
        mViewBinder = viewBinder;
    }

    private IViewBinder getViewBinder() {
        if (mViewBinder != null) {
            return mViewBinder;
        } else if (getBoundActivity() instanceof IBindableView) {
            return ((IBindableView) getBoundActivity()).getViewBinder();
        } else {
            throw new RuntimeException("There's no IViewBinder available!");
        }
    }

    private Activity getBoundActivity() {
        return mBoundActivity != null ? mBoundActivity.get() : null;
    }

    @Override
    public View addViewModel(int layoutResID, ViewModel viewModel, String id, @Nullable ViewGroup parent) {
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

        if (viewModel instanceof INeedsSupportFragmentManager && getBoundActivity() instanceof FragmentActivity) {
            ((INeedsSupportFragmentManager) viewModel).setFragmentManager(((FragmentActivity) getBoundActivity()).getSupportFragmentManager());
        }

        viewModel.setLogger(getLogger());
        mViewModels.put(id, viewModel);

        View view = getViewBinder().inflate(getBoundActivity(), viewModel, layoutResID, parent, false);

        if (id.equalsIgnoreCase(TAG_VIEWMODEL_MAIN)) {
            mBoundView = view;
        }

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

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState, int layoutResID, ViewModel viewModel) {
        if (mBoundActivity == null || getBoundActivity() == null) {
            throw new RuntimeException("Bound Activity is null!");
        }

        if (mShouldCallCreate) {
            onCreate(mCreateBundle);
        }

        return addViewModel(layoutResID, viewModel, TAG_VIEWMODEL_MAIN, container);
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
        //is not used in fragments
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
        if (getViewModels() != null) {
            for (ViewModel viewModel : getViewModels().values()) {
                if (viewModel instanceof IActivityLifecycle) {
                    ((IActivityLifecycle) viewModel).onSaveInstanceState(outState);
                }
            }

            //we reset this only if we have non-null VMs
            //otherwise it's possible that this was called after onDestroyView which saved the state in these, so we shouldn't destroy it
            //like for example in the ViewPager
            mShouldCallCreate = false;
            mCreateBundle = null;
        }
    }

    @Override
    public void onDestroy() {
        if (mBoundActivity != null) {
            mBoundActivity.clear();
            mBoundActivity = null;
        }

        mViewBinder = null;
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
    public void onDestroyView() {
        if (mBoundView != null) {
            getViewBinder().getViewBindingEngine().clearBindingForViewAndChildren(mBoundView);
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

        mBoundView = null;
        mViewModels = null;
        mShouldCallCreate = true; //this is here to balance it with onCreate not being called again when coming from background
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
                viewModel.setLogger(getLogger());
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
}