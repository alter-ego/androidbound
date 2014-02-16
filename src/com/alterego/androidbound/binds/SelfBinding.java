package com.alterego.androidbound.binds;

import java.security.InvalidParameterException;

import com.alterego.androidbound.interfaces.INotifyPropertyChanged;
import com.alterego.androidbound.zzzztoremove.ILogger;
import com.alterego.androidbound.zzzztoremove.reactive.Action;
import com.alterego.androidbound.zzzztoremove.reactive.IDisposable;
import com.alterego.androidbound.zzzztoremove.reactive.Observables;
import com.alterego.androidbound.zzzztoremove.reactive.Observers;
import com.alterego.androidbound.zzzztoremove.reactive.Predicate;

public class SelfBinding extends BindingBase {
	private IDisposable subscription;
	
	public SelfBinding(Object subject, ILogger logger) {
		super(subject, logger);
		setupBinding(subject);
	}
	
	private void setupBinding(Object subject) {
		if(subject == null)
			return;

		if(getSubject() instanceof INotifyPropertyChanged) {
			this.setupChanges(true);
			getLogger().debug("Subject implements INotifyPropertyChanged. Subscribing...");
			Predicate<String> isMember = new Predicate<String>() { 
				public Boolean invoke(String member) {
					return member.equals("this");
				}
			};
			subscription = Observables.from((INotifyPropertyChanged)subject)
			.where(isMember)
			.subscribe(Observers.fromAction(new Action<String>() {
				public void invoke(String name) {
					onBoundPropertyChanged();
				}
			}));
		}
		else
			this.setupChanges(false);
	}

	protected void onBoundPropertyChanged() {
		notifyChange(getValue());
	}
	
	@Override
	public Class<?> getType() {
		return getSubject().getClass();
	}

	@Override
	public Object getValue() {
		return getSubject();
	}

	@Override
	public void setValue(Object value) {
		throw new InvalidParameterException("Cannot set the value of a SelfBinding");
	}

	@Override
	public void dispose() {
		if(subscription != null) {
			subscription.dispose();
			subscription = null;
		}
		super.dispose();
	}
}
