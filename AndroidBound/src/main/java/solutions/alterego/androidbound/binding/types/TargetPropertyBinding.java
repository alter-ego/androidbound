package solutions.alterego.androidbound.binding.types;

import io.reactivex.android.schedulers.AndroidSchedulers;
import solutions.alterego.androidbound.interfaces.ILogger;

public class TargetPropertyBinding extends PropertyBinding {

    private UpdatingState currentState = UpdatingState.None;

    private String propertyName;

    public TargetPropertyBinding(Object subject, String propertyName, boolean needChangesIfPossible, ILogger logger, boolean debugMode) {
        super(subject, propertyName, needChangesIfPossible, logger, debugMode);
        this.propertyName = propertyName;
    }

    @Override
    public void setValue(final Object value) {
        if (currentState != UpdatingState.None) {
            return;
        }

        getLogger().verbose("Receiving set state for type" + (value != null ? value.getClass() : "<null>"));
        try {
            currentState = UpdatingState.UpdatingTarget;
            AndroidSchedulers.mainThread().createWorker().schedule(new Runnable() {
                @Override
                public void run() {
                    TargetPropertyBinding.super.setValue(value);
                }
            });
        } finally {
            currentState = UpdatingState.None;
        }
    }

    @Override
    public void addValue(final Object value) {
        if (currentState != UpdatingState.None) {
            return;
        }

        getLogger().verbose("Receiving set state for type" + (value != null ? value.getClass() : "<null>"));
        try {
            currentState = UpdatingState.UpdatingTarget;
            AndroidSchedulers.mainThread().createWorker().schedule(new Runnable() {
                @Override
                public void run() {
                    TargetPropertyBinding.super.addValue(value);
                }
            });
        } finally {
            currentState = UpdatingState.None;
        }
    }

    @Override
    public void removeValue(final Object value) {
        if (currentState != UpdatingState.None) {
            return;
        }

        getLogger().verbose("Receiving set state for type" + (value != null ? value.getClass() : "<null>"));
        try {
            currentState = UpdatingState.UpdatingTarget;
            AndroidSchedulers.mainThread().createWorker().schedule(new Runnable() {
                @Override
                public void run() {
                    TargetPropertyBinding.super.removeValue(value);
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
