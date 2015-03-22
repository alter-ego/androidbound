package solutions.alterego.androidbound.binds;

import com.alterego.advancedandroidlogger.interfaces.IAndroidLogger;

import solutions.alterego.androidbound.helpers.Reflector;
import solutions.alterego.androidbound.helpers.reflector.PropertyInfo;
import solutions.alterego.androidbound.interfaces.IBinding;
import solutions.alterego.androidbound.interfaces.INotifyPropertyChanged;
import solutions.alterego.androidbound.zzzztoremove.reactive.Action;
import solutions.alterego.androidbound.zzzztoremove.reactive.IDisposable;
import solutions.alterego.androidbound.zzzztoremove.reactive.Observables;
import solutions.alterego.androidbound.zzzztoremove.reactive.Observers;
import solutions.alterego.androidbound.zzzztoremove.reactive.Predicate;

public class PropertyBinding extends BindingBase {

    private IDisposable mMemberSubscription;

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

            Predicate<String> isMember = new Predicate<String>() {
                public Boolean invoke(String member) {
                    return member.equals(propertyName);
                }
            };

            mMemberSubscription = Observables.from((INotifyPropertyChanged) subject)
                    .where(isMember)
                    .subscribe(Observers.fromAction(new Action<String>() {
                        public void invoke(String name) {
                            onBoundPropertyChanged();
                        }
                    }));
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
            mMemberSubscription.dispose();
            mMemberSubscription = null;
        }
        super.dispose();
    }
}
