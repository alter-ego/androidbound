package com.alterego.androidbound.factories;

import com.alterego.advancedandroidlogger.interfaces.IAndroidLogger;
import com.alterego.androidbound.binds.TargetPropertyBinding;
import com.alterego.androidbound.interfaces.IBinding;
import com.alterego.androidbound.zzzztoremove.reactive.IScheduler;

public class TargetBindingFactory extends SourceBindingFactory {
	private IScheduler scheduler;
	
	public TargetBindingFactory(IScheduler scheduler, IAndroidLogger logger) {
		super(logger);
		this.scheduler = scheduler;
	}

	@Override
	protected IBinding createLeaf(Object source, String property, boolean needChangesIfPossible) {
		IBinding result = new TargetPropertyBinding(source, property, needChangesIfPossible, scheduler, logger);
		return result;
	}
}
