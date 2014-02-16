
package com.alterego.androidbound.binds;

import com.alterego.advancedandroidlogger.interfaces.IAndroidLogger;
import com.alterego.androidbound.helpers.Reflector;
import com.alterego.androidbound.helpers.Reflector.PropertyInfo;
import com.alterego.androidbound.interfaces.IBinding;
import com.alterego.androidbound.interfaces.INotifyPropertyChanged;
import com.alterego.androidbound.zzzztoremove.reactive.Action;
import com.alterego.androidbound.zzzztoremove.reactive.IDisposable;
import com.alterego.androidbound.zzzztoremove.reactive.Observables;
import com.alterego.androidbound.zzzztoremove.reactive.Observers;
import com.alterego.androidbound.zzzztoremove.reactive.Predicate;

public class PropertyBinding extends BindingBase {
    private IDisposable memberSubscription;
    private PropertyInfo info;

    public PropertyBinding(Object subject, String propertyName, boolean needChangesIfPossible, IAndroidLogger logger) {
        super(subject, logger);

        this.info = Reflector.getProperty(subject.getClass(), propertyName); //new PropertyInfo(subject.getClass(), propertyName);

        setupBinding(subject, this.info.name, needChangesIfPossible);
    }

    private void setupBinding(Object subject, final String propertyName, boolean needChangesIfPossible) {
        if (subject == null) {
            return;
        }

        if (needChangesIfPossible && (getSubject() instanceof INotifyPropertyChanged)) {
            this.setupChanges(true);
            getLogger().debug(propertyName + " implements INotifyPropertyChanged. Subscribing...");

            Predicate<String> isMember = new Predicate<String>() {
                public Boolean invoke(String member) {
                    return member.equals(propertyName);
                }
            };

            memberSubscription = Observables.from((INotifyPropertyChanged) subject)
                    .where(isMember)
                    .subscribe(Observers.fromAction(new Action<String>() {
                        public void invoke(String name) {
                            onBoundPropertyChanged();
                        }
                    }));
        }
        else {
            this.setupChanges(false);
        }
    }

    protected PropertyInfo getInfo() {
        return info;
    }

    protected void onBoundPropertyChanged() {
        notifyChange(getValue());
    }

    @Override
    public Class<?> getType() {
        return info.type;
    }

    @Override
    public Object getValue() {
        if (info.canRead)
            return info.getValue(getSubject());

        getLogger().warning("Cannot get value for property " + this.info.name + ": property is non-existent");
        return IBinding.noValue;
    }

    @Override
    public void setValue(Object value) {
        if (info.canWrite) {
            info.setValue(getSubject(), value);
        } else {
            if (info.canRead) {
                getLogger().warning("Cannot set value for property " + this.info.name + ": propery is read-only");
            } else {
                getLogger().warning("Cannot set value for property " + this.info.name + ": propery is non-existent");
            }
        }
    }

    @Override
    public void dispose() {
        if (memberSubscription != null) {
            memberSubscription.dispose();
            memberSubscription = null;
        }
        super.dispose();
    }
}
