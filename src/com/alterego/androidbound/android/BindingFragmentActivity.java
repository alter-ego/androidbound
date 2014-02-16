package com.alterego.androidbound.android;

import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;

import com.alterego.advancedandroidlogger.implementations.NullAndroidLogger;
import com.alterego.advancedandroidlogger.interfaces.IAndroidLogger;
import com.alterego.androidbound.interfaces.IBindableView;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.view.View;

@Accessors(prefix="m")
public abstract class BindingFragmentActivity extends FragmentActivity implements IBindableView {
	@Getter @Setter private Object mBoundData;
	@Setter protected IAndroidLogger mLogger = NullAndroidLogger.instance;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		if(getViewBinder() != null)
			getViewBinder().clearAllBindings();
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
		if(getViewBinder() != null)
			getViewBinder().clearAllBindings();
	}

	@Override
	public void setContentView(int layoutResID) {
		if(mBoundData == null)
			throw new RuntimeException("call setBoundData(Object) before calling setContentView!");

		if(getViewBinder() == null) 
			throw new RuntimeException("getViewBinder must not be null!");

		View view = getViewBinder().inflate(this, mBoundData, layoutResID, null);
		setContentView(view);
	}

	public void dispose() {
		if(getViewBinder() != null)
			getViewBinder().clearAllBindings();
	}
}
