package solutions.alterego.androidbound.binds;

import com.alterego.advancedandroidlogger.interfaces.IAndroidLogger;

import java.security.InvalidParameterException;

import rx.Subscription;
import solutions.alterego.androidbound.interfaces.INotifyPropertyChanged;

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

            mSubscription = ((INotifyPropertyChanged) subject)
                    .onPropertyChanged()
                    .filter(member -> member.equals("this"))
                    .subscribe(s -> {
                        onBoundPropertyChanged();
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
