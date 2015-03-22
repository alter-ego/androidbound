package solutions.alterego.androidbound.binds;

import com.alterego.advancedandroidlogger.interfaces.IAndroidLogger;
import solutions.alterego.androidbound.interfaces.INotifyPropertyChanged;
import solutions.alterego.androidbound.zzzztoremove.reactive.Action;
import solutions.alterego.androidbound.zzzztoremove.reactive.IDisposable;
import solutions.alterego.androidbound.zzzztoremove.reactive.Observables;
import solutions.alterego.androidbound.zzzztoremove.reactive.Observers;
import solutions.alterego.androidbound.zzzztoremove.reactive.Predicate;

import java.security.InvalidParameterException;

public class SelfBinding extends BindingBase {

    private IDisposable mSubscription;

    public SelfBinding(Object subject, IAndroidLogger logger) {
        super(subject, logger);
        setupBinding(subject);
    }

    private void setupBinding(Object subject) {
        if (subject == null) {
            return;
        }

        if (getSubject() instanceof INotifyPropertyChanged) {
            this.setupChanges(true);
            getLogger().debug("Subject implements INotifyPropertyChanged. Subscribing...");
            Predicate<String> isMember = new Predicate<String>() {
                public Boolean invoke(String member) {
                    return member.equals("this");
                }
            };

            mSubscription = Observables.from((INotifyPropertyChanged) subject)
                    .where(isMember)
                    .subscribe(Observers.fromAction(new Action<String>() {
                        public void invoke(String name) {
                            onBoundPropertyChanged();
                        }
                    }));
        } else {
            this.setupChanges(false);
        }
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
        if (mSubscription != null) {
            mSubscription.dispose();
            mSubscription = null;
        }
        super.dispose();
    }
}
