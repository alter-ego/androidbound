package solutions.alterego.androidbound.example.fragment;

import com.alterego.advancedandroidlogger.interfaces.IAndroidLogger;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import solutions.alterego.androidbound.ViewBinder;
import solutions.alterego.androidbound.android.BindingFragment;
import solutions.alterego.androidbound.android.BoundFragmentDelegate;
import solutions.alterego.androidbound.android.ui.BindableRecyclerView;
import solutions.alterego.androidbound.example.R;
import solutions.alterego.androidbound.example.fragment.viewmodel.FragmentViewModel;
import solutions.alterego.androidbound.example.imageloader.UILImageLoader;
import solutions.alterego.androidbound.example.util.AdvancedAndroidLoggerAdapter;
import solutions.alterego.androidbound.interfaces.IViewBinder;

import static solutions.alterego.androidbound.example.MainActivity.LOGGING_TAG;

public class TestFragment extends BindingFragment {

    private static final IAndroidLogger.LoggingLevel LOGGING_LEVEL = IAndroidLogger.LoggingLevel.VERBOSE;

    @Override
    protected BoundFragmentDelegate createBoundFragmentDelegate() {
        IViewBinder viewBinder = new ViewBinder(getActivity(), new AdvancedAndroidLoggerAdapter(LOGGING_TAG, LOGGING_LEVEL));
        viewBinder.setImageLoader(new UILImageLoader(getActivity(), null));
        return new BoundFragmentDelegate(this, viewBinder);
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
