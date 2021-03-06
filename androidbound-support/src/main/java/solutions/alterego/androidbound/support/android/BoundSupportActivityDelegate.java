package solutions.alterego.androidbound.support.android;

import android.app.Activity;
import android.support.v4.app.FragmentActivity;
import android.view.View;

import java.util.HashMap;

import solutions.alterego.androidbound.ViewModel;
import solutions.alterego.androidbound.android.BoundActivityDelegate;
import solutions.alterego.androidbound.android.interfaces.INeedsActivity;
import solutions.alterego.androidbound.interfaces.INeedsLogger;
import solutions.alterego.androidbound.interfaces.IViewBinder;
import solutions.alterego.androidbound.support.android.interfaces.INeedsSupportFragmentManager;

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
            throw new RuntimeException("Bound Activity is null! ViewModel = " + viewModel);
        }

        if (viewModel == null) {
            throw new RuntimeException("ViewModel is null! Activity = " + getBoundActivity());
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

        if (viewModel instanceof INeedsLogger) {
            ((INeedsLogger) viewModel).setLogger(getLogger());
        }

        mViewModels.put(id, viewModel);

        View view = getViewBinder().inflate(getBoundActivity(), viewModel, layoutResID, null);

        if (isShouldCallCreate()) {
            viewmodelOnCreate(viewModel, getCreateBundle());
        }

        return view;
    }

}