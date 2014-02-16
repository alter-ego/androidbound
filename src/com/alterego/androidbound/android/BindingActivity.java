package com.alterego.androidbound.android;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;

import com.alterego.androidbound.interfaces.IBindableView;
import com.alterego.androidbound.interfaces.IViewBinder;
import com.alterego.androidbound.zzzztoremove.ILogger;
import com.alterego.androidbound.zzzztoremove.NullLogger;

public abstract class BindingActivity extends Activity implements IBindableView {
	private Object dataContext;
	protected ILogger logger = NullLogger.instance;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		if(this.getViewBinder() != null)
		    getViewBinder().clearAllBindings();
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
	    if(this.getViewBinder() != null)
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
		if(dataContext == null)
			throw new RuntimeException("setContentView called before setting dataContext.");
		
		if(this.getViewBinder() == null) 
		    throw new RuntimeException("getViewBinder must be not null at this point.");

		View view = getViewBinder().inflate(this, dataContext, layoutResID, null);
		setContentView(view);
	}

	public void setLogger(ILogger logger) {
		this.logger = logger.getLogger(this);
	}

	public void dispose() {
	    if(this.getViewBinder() != null)
	        getViewBinder().clearAllBindings();
	}
}