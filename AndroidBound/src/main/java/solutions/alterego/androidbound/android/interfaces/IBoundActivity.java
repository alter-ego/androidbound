package solutions.alterego.androidbound.android.interfaces;

import android.view.View;

import solutions.alterego.androidbound.ViewModel;

public interface IBoundActivity {

    void setContentView(int layoutResID, ViewModel viewModel);

    View addViewModel(int layoutResID, ViewModel viewModel, String id);

    ViewModel getViewModel(String id);

    ViewModel getContentViewModel();
}
