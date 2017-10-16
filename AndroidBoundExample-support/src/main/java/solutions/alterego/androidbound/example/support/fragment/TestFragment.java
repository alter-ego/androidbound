package solutions.alterego.androidbound.example.support.fragment;

import com.alterego.advancedandroidlogger.interfaces.IAndroidLogger;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import solutions.alterego.androidbound.android.ui.BindableRecyclerView;
import solutions.alterego.androidbound.example.support.R;
import solutions.alterego.androidbound.example.support.fragment.viewmodel.FragmentViewModel;
import solutions.alterego.androidbound.example.support.imageloader.UILImageLoader;
import solutions.alterego.androidbound.example.support.util.AdvancedAndroidLoggerAdapter;
import solutions.alterego.androidbound.interfaces.IViewBinder;
import solutions.alterego.androidbound.support.SupportViewBinder;
import solutions.alterego.androidbound.support.android.BindingSupportFragment;
import solutions.alterego.androidbound.support.android.BoundSupportFragmentDelegate;

import static solutions.alterego.androidbound.example.support.MainActivity.LOGGING_TAG;

public class TestFragment extends BindingSupportFragment {

    private static final IAndroidLogger.LoggingLevel LOGGING_LEVEL = IAndroidLogger.LoggingLevel.VERBOSE;

    @Override
    protected BoundSupportFragmentDelegate getBoundFragmentDelegate() {
        IViewBinder viewBinder = new SupportViewBinder(getActivity(), new AdvancedAndroidLoggerAdapter(LOGGING_TAG, LOGGING_LEVEL));
        viewBinder.setImageLoader(new UILImageLoader(getActivity(), null));
        return new BoundSupportFragmentDelegate(this, viewBinder);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return super.onCreateView(inflater, container, savedInstanceState, R.layout.fragment_test, new FragmentViewModel());
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        BindableRecyclerView bindableRecyclerView = (BindableRecyclerView) view.findViewById(R.id.recyclerview_linear);
        bindableRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
    }
}
