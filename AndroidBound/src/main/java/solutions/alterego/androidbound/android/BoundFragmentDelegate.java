package solutions.alterego.androidbound.android;

import android.app.Activity;
import android.content.Intent;
import android.content.res.Configuration;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.lang.ref.WeakReference;
import java.util.HashMap;
import java.util.Map;

import lombok.Getter;
import lombok.experimental.Accessors;
import solutions.alterego.androidbound.NullLogger;
import solutions.alterego.androidbound.ViewModel;
import solutions.alterego.androidbound.android.interfaces.IActivityLifecycle;
import solutions.alterego.androidbound.android.interfaces.IBindableView;
import solutions.alterego.androidbound.android.interfaces.IBoundFragment;
import solutions.alterego.androidbound.android.interfaces.IFragmentLifecycle;
import solutions.alterego.androidbound.android.interfaces.INeedsConfigurationChange;
import solutions.alterego.androidbound.android.interfaces.INeedsNewIntent;
import solutions.alterego.androidbound.android.interfaces.INeedsOnActivityResult;
import solutions.alterego.androidbound.interfaces.IHasLogger;
import solutions.alterego.androidbound.interfaces.ILogger;
import solutions.alterego.androidbound.interfaces.INeedsLogger;
import solutions.alterego.androidbound.interfaces.IViewBinder;

@Accessors(prefix = "m")
public class BoundFragmentDelegate
        implements IActivityLifecycle, IFragmentLifecycle, IBoundFragment, INeedsOnActivityResult, INeedsNewIntent, INeedsConfigurationChange,
        INeedsLogger, IHasLogger {

    public static final String TAG_VIEWMODEL_MAIN = "androidbound_viewmodel_main";

    @Getter
    private Map<String, ViewModel> mViewModels;

    private ILogger mLogger = NullLogger.instance;

    private transient WeakReference<Activity> mBoundActivity;

    private boolean mShouldCallCreate = false;

    private Bundle mCreateBundle;

    private IViewBinder mViewBinder;

    public BoundFragmentDelegate(Fragment fragment) {
        this(fragment, null);
    }

    public BoundFragmentDelegate(Fragment fragment, IViewBinder viewBinder) {
        mBoundActivity = new WeakReference<>(fragment.getActivity());
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
        if (mBoundActivity == null) {
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

        viewModel.setParentActivity(getBoundActivity());
        viewModel.setLogger(getLogger());
        mViewModels.put(id, viewModel);

        View view = getViewBinder().inflate(getBoundActivity(), viewModel, layoutResID, parent, false);

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
                if (!viewModel.isCreated()) {
                    viewModel.onCreate(savedInstanceState);
                }
            }

            mShouldCallCreate = false;
        } else {
            mShouldCallCreate = true;
            mCreateBundle = savedInstanceState;
        }
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
                viewModel.onStart();
            }
        }
    }

    @Override
    public void onRestart() {
        //is not used in fragments
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
        if (getViewModels() != null) {
            for (ViewModel viewModel : getViewModels().values()) {
                viewModel.onSaveInstanceState(outState);
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
        Activity boundActivityRef = getBoundActivity();

        if (mBoundActivity != null
                && boundActivityRef != null
                && getViewBinder() != null
                && boundActivityRef.getWindow() != null
                && boundActivityRef.getWindow().getDecorView() != null) {
            getViewBinder().clearBindingForViewAndChildren(getBoundActivity().getWindow().getDecorView().getRootView());
        }

        if (getViewModels() != null) {
            for (ViewModel viewModel : getViewModels().values()) {
                viewModel.onDestroy();
            }
        }

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
}