
package com.alterego.androidbound.binds;

import com.alterego.advancedandroidlogger.interfaces.IAndroidLogger;
import com.alterego.androidbound.zzzztoremove.reactive.IScheduler;

public class TargetPropertyBinding extends PropertyBinding {
    private enum UpdatingState {
        None, UpdatingSource, UpdatingTarget
    }

    private UpdatingState currentState = UpdatingState.None;
    private String propertyName;
    private IScheduler scheduler;

    public TargetPropertyBinding(Object subject, String propertyName, boolean needChangesIfPossible, IScheduler scheduler, IAndroidLogger logger) {
        super(subject, propertyName, needChangesIfPossible, logger);
        this.propertyName = propertyName;
        this.scheduler = scheduler;
    }

    @Override
    public void setValue(final Object value) {
        if (currentState != UpdatingState.None)
            return;

        getLogger().verbose("Receiving set state for type" + (value != null ? value.getClass() : "<null>"));
        try {
            currentState = UpdatingState.UpdatingTarget;
            scheduler.schedule(new Runnable() {
                public void run() {
                    TargetPropertyBinding.super.setValue(value);
                }
            });
        } finally {
            currentState = UpdatingState.None;
        }
    }

    @Override
    protected void onBoundPropertyChanged() {
        if (currentState != UpdatingState.None)
            return;

        getLogger().verbose("Raising change notification for " + propertyName);
        try {
            currentState = UpdatingState.UpdatingSource;
            super.onBoundPropertyChanged();
        } finally {
            currentState = UpdatingState.None;
        }
    }
}
