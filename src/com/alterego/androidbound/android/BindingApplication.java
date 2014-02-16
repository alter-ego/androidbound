package com.alterego.androidbound.android;

import com.alterego.androidbound.ViewBinder;
import com.alterego.androidbound.interfaces.IViewBinder;
import com.alterego.androidbound.zzzztoremove.AndroidLogger;
import com.alterego.androidbound.zzzztoremove.ILogger;
import com.alterego.androidbound.zzzztoremove.reactive.IScheduler;
import android.app.Application;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.os.Handler;

public class BindingApplication extends Application {
	protected ILogger logger;
	protected IScheduler notificationScheduler;
	protected IViewBinder viewBinder;

	@Override
	public void onCreate() {
		super.onCreate();

		logger = new AndroidLogger(getApplicationName());
		notificationScheduler = new HandlerScheduler(new Handler());
		viewBinder = new ViewBinder(notificationScheduler, logger);
	}
	
	public IScheduler getScheduler() {
		return notificationScheduler;
	}
	
	public IViewBinder getViewBinder() {
		return viewBinder;
	}

	public String getApplicationName() {
		PackageManager pm = getApplicationContext().getPackageManager();
		ApplicationInfo ai;
		try {
		    ai = pm.getApplicationInfo( this.getPackageName(), 0);
		} catch (final NameNotFoundException e) {
		    ai = null;
		}
		return (String) (ai != null ? pm.getApplicationLabel(ai) : "(unknown)");		
	}
	
	public ILogger getLogger() {
		return logger;
	}
}
