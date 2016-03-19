package solutions.alterego.androidbound.android;

import com.alterego.advancedandroidlogger.implementations.NullAndroidLogger;
import com.alterego.advancedandroidlogger.interfaces.IAndroidLogger;

import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.view.View;

import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;
import solutions.alterego.androidbound.interfaces.IBindableView;

@Accessors(prefix = "m")
public abstract class BindingActionBarActivity extends ActionBarActivity implements IBindableView {

    @Setter
    protected IAndroidLogger mLogger = NullAndroidLogger.instance;

    @Getter
    @Setter
    private Object mBoundData;

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
    }

    @Override
    public void setContentView(int layoutResID) {
        if (mBoundData == null) {
            throw new RuntimeException("call setBoundData(Object) before calling setContentView!");
        }

        if (getViewBinder() == null) {
            throw new RuntimeException("getViewBinder must not be null!");
        }

        View view = getViewBinder().inflate(this, mBoundData, layoutResID, null);
        setContentView(view);
    }

    public void dispose() {
        if (getViewBinder() != null) {
            getViewBinder().clearAllBindings();
        }
    }
}