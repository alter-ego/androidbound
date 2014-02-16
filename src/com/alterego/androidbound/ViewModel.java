package com.alterego.androidbound;

import com.alterego.advancedandroidlogger.implementations.NullAndroidLogger;
import com.alterego.advancedandroidlogger.interfaces.IAndroidLogger;
import com.alterego.androidbound.interfaces.INeedsLogger;
import com.alterego.androidbound.interfaces.INotifyPropertyChanged;
import com.alterego.androidbound.zzzztoremove.reactive.IDisposable;
import com.alterego.androidbound.zzzztoremove.reactive.IObservable;
import com.alterego.androidbound.zzzztoremove.reactive.ISubject;
import com.alterego.androidbound.zzzztoremove.reactive.Subject;

public class ViewModel implements INeedsLogger, INotifyPropertyChanged, IDisposable {
	private ISubject<String> propertyChanges = new Subject<String>();
	protected IAndroidLogger logger = NullAndroidLogger.instance;

	protected void raisePropertyChanged(String property) {
		propertyChanges.onNext(property);
	}
	
	public IObservable<String> onPropertyChanged() {
		return propertyChanges;
	}

	public void dispose() {
		propertyChanges.dispose();
	}

	public void setLogger(IAndroidLogger logger) {
		this.logger = logger.getLogger(this);
	}
}
