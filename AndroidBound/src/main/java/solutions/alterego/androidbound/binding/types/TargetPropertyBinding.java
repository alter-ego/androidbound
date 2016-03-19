package solutions.alterego.androidbound.binding.types;

import com.alterego.advancedandroidlogger.interfaces.IAndroidLogger;

import rx.android.schedulers.AndroidSchedulers;

public class TargetPropertyBinding extends PropertyBinding {

    private UpdatingState currentState = UpdatingState.None;

    private String propertyName;

    public TargetPropertyBinding(Object subject, String propertyName, boolean needChangesIfPossible, IAndroidLogger logger) {
        super(subject, propertyName, needChangesIfPossible, logger);
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
            AndroidSchedulers.mainThread().createWorker().schedule(() -> TargetPropertyBinding.super.setValue(value));
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
