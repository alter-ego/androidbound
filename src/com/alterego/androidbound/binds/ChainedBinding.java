package com.alterego.androidbound.binds;

import java.util.List;

import com.alterego.androidbound.interfaces.IBinding;
import com.alterego.androidbound.interfaces.IBindingFactory;
import com.alterego.androidbound.zzzztoremove.ILogger;
import com.alterego.androidbound.zzzztoremove.reactive.Action;
import com.alterego.androidbound.zzzztoremove.reactive.IDisposable;
import com.alterego.androidbound.zzzztoremove.reactive.Observers;

public class ChainedBinding extends PropertyBinding {
	private boolean needChangesIfPossible;
	private IBindingFactory factory;
	private List<String> tokens;
	private IBinding currentBinding;
	private IDisposable currentBindingChanged;
	private String memberName;

	public ChainedBinding(Object source, String propertyName, List<String> tokens, boolean needChangesIfPossible, IBindingFactory factory, ILogger logger) {
		super(source, propertyName, needChangesIfPossible, logger);
		this.needChangesIfPossible = needChangesIfPossible;
		this.memberName = propertyName;
		this.tokens = tokens;
		this.factory = factory;
		updateChildBinding();
	}

	protected void updateChildBinding() {
		if (currentBinding != null) {
			currentBindingChanged.dispose();
			currentBinding.dispose();
			currentBindingChanged = null;
		}
		
		Object currentValue = getInfo().getValue(getSubject());
		if (currentValue == null)
			return;
		currentBinding = factory.create(currentValue, tokens, this.needChangesIfPossible);
		currentBindingChanged = currentBinding.getChanges().subscribe(Observers.fromAction(new Action<Object>() {
			public void invoke(Object obj) {
				notifyChange(obj);
			}
		}));
	}

	@Override
	public Class<?> getType() {
		return currentBinding == null ? Object.class : currentBinding.getType();
	}

	@Override
	protected void onBoundPropertyChanged() {
		updateChildBinding();
		notifyChange(getValue());
	}

	@Override
	public Object getValue() {
		return currentBinding == null ? IBinding.noValue : currentBinding.getValue();
	}

	@Override
	public void setValue(Object value) {
		if (currentBinding == null)
			getLogger().warning("Target property path is missing. Couldn't set value for " + memberName);
		else
			currentBinding.setValue(value);
	}

	@Override
	public void dispose() {
		super.dispose();
		if(currentBindingChanged != null) {
			currentBindingChanged.dispose();
			currentBindingChanged = null;
		}
		if (currentBinding != null) {
			currentBinding.dispose();
			currentBinding = null;
		}
	}
}
