package com.alterego.androidbound.binds;

import com.alterego.advancedandroidlogger.implementations.NullAndroidLogger;
import com.alterego.advancedandroidlogger.interfaces.IAndroidLogger;
import com.alterego.androidbound.interfaces.IBinding;
import com.alterego.androidbound.interfaces.INeedsLogger;
import com.alterego.androidbound.zzzztoremove.reactive.IObservable;
import com.alterego.androidbound.zzzztoremove.reactive.ISubject;
import com.alterego.androidbound.zzzztoremove.reactive.Observables;
import com.alterego.androidbound.zzzztoremove.reactive.Subject;

public abstract class BindingBase implements IBinding, INeedsLogger {
	private static final IObservable<Object> noChanges = Observables.<Object>empty();
	private ISubject<Object> changes;
	private Object subject;
	private IAndroidLogger logger = NullAndroidLogger.instance;
	
	public BindingBase(Object subject, IAndroidLogger logger) {
		this.subject = subject;
		setLogger(logger);
	}
	
	public abstract Class<?> getType();

	public abstract Object getValue();

	public abstract void setValue(Object value);

	public IObservable<Object> getChanges() {
		if(this.changes == null) {
			return noChanges;
		}
		
		return this.changes;
	}
	
	public boolean hasChanges() {
		return this.changes != null;
	}
	
	protected void notifyChange(Object value) {
		if(this.changes == null) {
			return;
		}
		
		this.changes.onNext(value);
	}
	
	protected void setupChanges(boolean hasChanges) {
		if(hasChanges) {
			if(this.changes == null) {
				this.changes = new Subject<Object>();
			}
		} else {
			if(this.changes != null) {
				this.changes.dispose();
				this.changes = null;
			}
		}
	}
	
	protected Object getSubject() {
		return subject;
	}
	
	protected IAndroidLogger getLogger() {
		return logger;
	}
	
	public void setLogger(IAndroidLogger logger) {
		this.logger = logger.getLogger(this);
	}

	public void dispose() {
		if(this.changes != null) {
			this.changes.dispose();
		}
		
		this.changes = null;
	}
}
