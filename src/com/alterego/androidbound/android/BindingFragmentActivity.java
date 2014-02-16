package com.alterego.androidbound.android;

import com.alterego.advancedandroidlogger.implementations.NullAndroidLogger;
import com.alterego.advancedandroidlogger.interfaces.IAndroidLogger;
import com.alterego.androidbound.interfaces.IBindableView;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.view.View;

public abstract class BindingFragmentActivity extends FragmentActivity implements IBindableView {
	private Object dataContext;
	protected IAndroidLogger logger = NullAndroidLogger.instance;
	
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (this.getViewBinder() != null)
            getViewBinder().clearAllBindings();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (this.getViewBinder() != null)
            getViewBinder().clearAllBindings();
    }

    public Object getDataContext() {
        return dataContext;
    }

    public void setDataContext(Object value) {
        dataContext = value;
    }

    @Override
    public void setContentView(int layoutResID) {
        if (dataContext == null)
            throw new RuntimeException("setContentView called before setting dataContext.");

        if (this.getViewBinder() == null)
            throw new RuntimeException("getViewBinder must be not null at this point.");

        View view = getViewBinder().inflate(this, dataContext, layoutResID, null);
        setContentView(view);
    }

    public void setLogger(IAndroidLogger logger) {
        this.logger = logger.getLogger(this);
    }

    public void dispose() {
        if (this.getViewBinder() != null)
            getViewBinder().clearAllBindings();
    }
}
