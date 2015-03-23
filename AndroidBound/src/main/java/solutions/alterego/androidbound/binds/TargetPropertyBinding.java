package solutions.alterego.androidbound.binds;

import com.alterego.advancedandroidlogger.interfaces.IAndroidLogger;

import rx.Scheduler;
import rx.functions.Action0;

public class TargetPropertyBinding extends PropertyBinding {

    private UpdatingState currentState = UpdatingState.None;

    private String propertyName;

    private Scheduler scheduler;

    public TargetPropertyBinding(Object subject, String propertyName, boolean needChangesIfPossible, Scheduler scheduler, IAndroidLogger logger) {
        super(subject, propertyName, needChangesIfPossible, logger);
        this.propertyName = propertyName;
        this.scheduler = scheduler;
    }

    @Override
    public void setValue(final Object value) {
        if (currentState != UpdatingState.None) {
            return;
        }

        getLogger().verbose("Receiving set state for type" + (value != null ? value.getClass() : "<null>"));
        try {
            currentState = UpdatingState.UpdatingTarget;
            scheduler.createWorker().schedule(new Action0() {
                @Override
                public void call() {
                    TargetPropertyBinding.super.setValue(value);
                }
            });
        } finally {
            currentState = UpdatingState.None;
        }
    }

    @Override
    protected void onBoundPropertyChanged() {
        if (currentState != UpdatingState.None) {
            return;
        }

        getLogger().verbose("Raising change notification for " + propertyName);
        try {
            currentState = UpdatingState.UpdatingSource;
            super.onBoundPropertyChanged();
        } finally {
            currentState = UpdatingState.None;
        }
    }

    private enum UpdatingState {
        None, UpdatingSource, UpdatingTarget
    }
}
