package solutions.alterego.androidbound.android.interfaces;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import solutions.alterego.androidbound.ViewModel;

public interface IFragmentLifecycle {

    @Nullable
    View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState, int layoutResID, ViewModel viewModel);

    void onDestroyView();
}
