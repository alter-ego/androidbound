package solutions.alterego.androidbound.android;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;

import java.util.ArrayList;
import java.util.List;

import lombok.Getter;
import lombok.experimental.Accessors;
import solutions.alterego.androidbound.ViewModel;
import solutions.alterego.androidbound.android.interfaces.IBindableView;
import solutions.alterego.androidbound.android.interfaces.IBoundActivity;
import solutions.alterego.androidbound.android.interfaces.IActivityLifecycle;

@Accessors(prefix = "m")
public class BoundActivityDelegate implements IActivityLifecycle, IBoundActivity {

    @Getter
    private List<ViewModel> mViewModels;

    private Activity mBoundActivity;

    public BoundActivityDelegate(Activity activity) {
        mBoundActivity = activity;
    }

    @Override
    public void setContentView(int layoutResID, ViewModel viewModel) {
        if (mBoundActivity == null) {
            throw new RuntimeException("Bound Activity is null!");
        }

        mBoundActivity.setContentView(addViewModel(layoutResID, viewModel));
    }

    @Override
    public View addViewModel(int layoutResID, ViewModel viewModel) {
        if (mBoundActivity == null) {
            throw new RuntimeException("Bound Activity is null!");
        }

        if (!(mBoundActivity instanceof IBindableView)) {
            throw new RuntimeException("Activity must extend IBindableView!");
        }

        if (viewModel == null) {
            throw new RuntimeException("viewModel is null!");
        }

        if (((IBindableView) mBoundActivity).getViewBinder() == null) {
            throw new RuntimeException("getViewBinder must not be null!");
        }

        if (mViewModels == null) {
            mViewModels = new ArrayList<>();
        }

        if (mViewModels.contains(viewModel)) {
            throw new RuntimeException("cannot add same instance of viewModel twice!");
        }

        viewModel.setParentActivity(mBoundActivity);
        mViewModels.add(viewModel);

        return ((IBindableView) mBoundActivity).getViewBinder().inflate(mBoundActivity, viewModel, layoutResID, null);
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        if (getViewModels() != null) {
            for (ViewModel viewModel : getViewModels()) {
                viewModel.onCreate(savedInstanceState);
            }
        }
    }

    @Override
    public void onResume() {
        if (getViewModels() != null) {
            for (ViewModel viewModel : getViewModels()) {
                viewModel.onResume();
            }
        }
    }

    @Override
    public void onPause() {
        if (getViewModels() != null) {
            for (ViewModel viewModel : getViewModels()) {
                viewModel.onPause();
            }
        }
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        if (getViewModels() != null) {
            for (ViewModel viewModel : getViewModels()) {
                viewModel.onSaveInstanceState(outState);
            }
        }
    }

    @Override
    public void onDestroy() {
        if (mBoundActivity != null
                && mBoundActivity instanceof IBindableView
                && ((IBindableView) mBoundActivity).getViewBinder() != null
                && mBoundActivity.getWindow() != null
                && mBoundActivity.getWindow().getDecorView() != null) {
            ((IBindableView) mBoundActivity).getViewBinder().clearBindingForViewAndChildren(mBoundActivity.getWindow().getDecorView().getRootView());
        }

        if (getViewModels() != null) {
            for (ViewModel viewModel : getViewModels()) {
                viewModel.onDestroy();
            }
        }

        mViewModels = null;
        mBoundActivity = null;
    }

}