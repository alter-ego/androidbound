package com.alterego.androidbound.factories;

import com.alterego.advancedandroidlogger.interfaces.IAndroidLogger;
import com.alterego.androidbound.binds.TargetPropertyBinding;
import com.alterego.androidbound.interfaces.IBinding;
import com.alterego.androidbound.zzzztoremove.reactive.IScheduler;

public class TargetBindingFactory extends SourceBindingFactory {

    private IScheduler mScheduler;

    public TargetBindingFactory(IScheduler scheduler, IAndroidLogger logger) {
        super(logger);
        mScheduler = scheduler;
    }

    @Override
    protected IBinding createLeaf(Object source, String property, boolean needChangesIfPossible) {
        return new TargetPropertyBinding(source, property, needChangesIfPossible, mScheduler, mLogger);
    }
}
