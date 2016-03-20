package solutions.alterego.androidbound.android;

import com.alterego.advancedandroidlogger.implementations.NullAndroidLogger;
import com.alterego.advancedandroidlogger.interfaces.IAndroidLogger;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;

import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;
import solutions.alterego.androidbound.ViewModel;
import solutions.alterego.androidbound.android.interfaces.IBindableView;

@Accessors(prefix = "m")
public abstract class BindingActivity extends Activity implements IBindableView {

    @Setter
    protected IAndroidLogger mLogger = NullAndroidLogger.instance;

    @Getter
    @Setter
    private ViewModel mViewModel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getViewBinder() != null) {
            getViewBinder().clearAllBindings();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (getViewBinder() != null) {
            getViewBinder().clearAllBindings();
        }
        if (getViewModel() != null) {
            getViewModel().dispose();
            setViewModel(null);
        }
    }

    @Override
    public void setContentView(int layoutResID) {
        if (mViewModel == null) {
            throw new RuntimeException("call setViewModel(Object) before calling setContentView!");
        }

        if (getViewBinder() == null) {
            throw new RuntimeException("getViewBinder must not be null!");
        }

        View view = getViewBinder().inflate(this, mViewModel, layoutResID, null);
        setContentView(view);
    }
}