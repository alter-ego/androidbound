package solutions.alterego.androidbound.android.interfaces;

import android.support.annotation.Nullable;
import android.view.View;
import android.view.ViewGroup;

import solutions.alterego.androidbound.ViewModel;

public interface IBoundFragment {

    View addViewModel(int layoutResID, ViewModel viewModel, String id, @Nullable ViewGroup parent);

    ViewModel getViewModel(String id);

    ViewModel getContentViewModel();
}
