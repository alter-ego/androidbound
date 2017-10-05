package solutions.alterego.androidbound.android.support;

import android.app.Activity;
import android.support.v4.app.FragmentActivity;
import android.view.View;

import java.util.HashMap;

import lombok.experimental.Accessors;
import solutions.alterego.androidbound.ViewModel;
import solutions.alterego.androidbound.android.BoundActivityDelegate;
import solutions.alterego.androidbound.android.interfaces.INeedsActivity;
import solutions.alterego.androidbound.interfaces.IViewBinder;

@Accessors(prefix = "m")
public class BoundSupportActivityDelegate extends BoundActivityDelegate {

    public BoundSupportActivityDelegate(Activity activity) {
        this(activity, null);
    }

    public BoundSupportActivityDelegate(Activity activity, IViewBinder viewBinder) {
        super(activity, viewBinder);
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

        if (viewModel instanceof INeedsSupportFragmentManager && getBoundActivity() instanceof FragmentActivity) {
            ((INeedsSupportFragmentManager) viewModel).setFragmentManager(((FragmentActivity) getBoundActivity()).getSupportFragmentManager());
        }

        viewModel.setLogger(getLogger());
        mViewModels.put(id, viewModel);

        View view = getViewBinder().inflate(getBoundActivity(), viewModel, layoutResID, null);

        if (isShouldCallCreate()) {
            onCreate(getCreateBundle());
        }

        return view;
    }

}