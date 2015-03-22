package solutions.alterego.androidbound.binds;

import com.alterego.advancedandroidlogger.interfaces.IAndroidLogger;

import java.security.InvalidParameterException;

import rx.Subscription;
import rx.functions.Action1;
import rx.functions.Func1;
import solutions.alterego.androidbound.interfaces.INotifyPropertyChanged;
import solutions.alterego.androidbound.zzzztoremove.reactive.Observables;
import solutions.alterego.androidbound.zzzztoremove.reactive.Predicate;

public class SelfBinding extends BindingBase {

    private Subscription mSubscription;

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

            mSubscription = Observables
                    .from((INotifyPropertyChanged) subject)
                    .filter(new Func1<String, Boolean>() {
                        @Override
                        public Boolean call(String member) {
                            return member.equals("this");
                        }
                    })
                    .subscribe(new Action1<String>() {
                        @Override
                        public void call(String s) {
                            onBoundPropertyChanged();
                        }
                    });
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
            mSubscription.unsubscribe();
            mSubscription = null;
        }
        super.dispose();
    }
}
