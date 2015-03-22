package solutions.alterego.androidbound.binds;

import com.alterego.advancedandroidlogger.interfaces.IAndroidLogger;

import rx.Subscription;
import rx.functions.Action1;
import rx.functions.Func1;
import solutions.alterego.androidbound.helpers.Reflector;
import solutions.alterego.androidbound.helpers.reflector.PropertyInfo;
import solutions.alterego.androidbound.interfaces.IBinding;
import solutions.alterego.androidbound.interfaces.INotifyPropertyChanged;
import solutions.alterego.androidbound.zzzztoremove.reactive.Observables;

public class PropertyBinding extends BindingBase {

    private Subscription mMemberSubscription;

    private PropertyInfo mPropertyInfo;

    public PropertyBinding(Object subject, String propertyName, boolean needChangesIfPossible, IAndroidLogger logger) {
        super(subject, logger);

        mPropertyInfo = Reflector.getProperty(subject.getClass(), propertyName);
        setupBinding(subject, mPropertyInfo.getPropertyName(), needChangesIfPossible);
    }

    private void setupBinding(Object subject, final String propertyName, boolean needChangesIfPossible) {
        if (subject == null) {
            return;
        }

        if (needChangesIfPossible && (getSubject() instanceof INotifyPropertyChanged)) {
            setupChanges(true);
            getLogger().debug(propertyName + " implements INotifyPropertyChanged. Subscribing...");

            mMemberSubscription = Observables.from((INotifyPropertyChanged) subject)
                    .filter(new Func1<String, Boolean>() {
                        @Override
                        public Boolean call(String member) {
                            return member.equals(propertyName);
                        }
                    })
                    .subscribe(new Action1<String>() {
                        @Override
                        public void call(String s) {
                            onBoundPropertyChanged();
                        }
                    });
        } else {
            setupChanges(false);
        }
    }

    protected PropertyInfo getInfo() {
        return mPropertyInfo;
    }

    protected void onBoundPropertyChanged() {
        notifyChange(getValue());
    }

    @Override
    public Class<?> getType() {
        return mPropertyInfo.getPropertyType();
    }

    @Override
    public Object getValue() {
        if (mPropertyInfo.isCanRead()) {
            return mPropertyInfo.getValue(getSubject());
        }

        getLogger().warning("Cannot get value for property " + mPropertyInfo.getPropertyName() + ": property is non-existent");
        return IBinding.noValue;
    }

    @Override
    public void setValue(Object value) {
        if (mPropertyInfo.isCanWrite()) {
            mPropertyInfo.setValue(getSubject(), value);
        } else {
            if (mPropertyInfo.isCanRead()) {
                getLogger().warning("Cannot set value for property " + mPropertyInfo.getPropertyName() + ": propery is read-only");
            } else {
                getLogger().warning("Cannot set value for property " + mPropertyInfo.getPropertyName() + ": propery is non-existent");
            }
        }
    }

    @Override
    public void dispose() {
        if (mMemberSubscription != null) {
            mMemberSubscription.unsubscribe();
            mMemberSubscription = null;
        }
        super.dispose();
    }
}
