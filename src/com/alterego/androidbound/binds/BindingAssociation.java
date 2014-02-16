
package com.alterego.androidbound.binds;

import com.alterego.advancedandroidlogger.implementations.NullAndroidLogger;
import com.alterego.advancedandroidlogger.interfaces.IAndroidLogger;
import com.alterego.androidbound.interfaces.IBinding;
import com.alterego.androidbound.interfaces.IBindingAssociation;
import com.alterego.androidbound.interfaces.IBindingFactory;
import com.alterego.androidbound.zzzztoremove.reactive.Action;
import com.alterego.androidbound.zzzztoremove.reactive.IDisposable;
import com.alterego.androidbound.zzzztoremove.reactive.Observers;


import java.util.Locale;

public class BindingAssociation implements IBindingAssociation {
    private BindingMode mode;
    private Object dataContext;
    private BindingSpecification specification;
    private IBinding sourceBinding;
    private IBinding targetBinding;
    private IDisposable sourceSubscription;
    private IDisposable targetSubscription;
    private IAndroidLogger logger = NullAndroidLogger.instance;
    private IBindingFactory sourceFactory;

    private IBindingFactory targetFactory;

    public BindingAssociation(BindingRequest request, IBindingFactory sourceFactory, IBindingFactory targetFactory, IAndroidLogger logger) {
        this.mode = request.Specification.Mode;
        this.sourceFactory = sourceFactory;
        this.targetFactory = targetFactory;
        this.specification = request.Specification;

        setLogger(logger);
        createTargetBinding(request.Target);
        createSourceBinding(request.Source);

        if (needsTargetUpdate())
            updateSourceFromTarget(targetBinding.getValue());

        if (needsSourceUpdate())
            updateTargetFromSource(sourceBinding.getValue());
    }

    public Object getDataContext() {
        return dataContext;
    }

    public void setDataContext(Object value) {
        if (dataContext == value)
            return;
        dataContext = value;
        if (sourceBinding != null)
            sourceBinding.dispose();
        if (sourceSubscription != null)
            sourceSubscription.dispose();
        createSourceBinding(value);
        if (needsSourceUpdate())
            updateTargetFromSource(sourceBinding.getValue());
    }

    private void createSourceBinding(Object source) {
        boolean needsSubs = needsSourceSubscription();

        sourceBinding = sourceFactory.create(source, specification.Path, needsSubs);

        if (needsSubs)
            if (sourceBinding.hasChanges()) {
                //logger.debug("Binding " + specification.Path + " needs subscription.");
                sourceSubscription = sourceBinding.getChanges().subscribe(Observers.fromAction(new Action<Object>() {
                    public void invoke(Object obj) {
                        updateTargetFromSource(obj);
                    }
                }));
            } else {
                logger.warning("Binding " + specification.Path + " needs subscription, but changes were not available");
            }

        /* commented out because in contstructor, if createsource is called before createtarget, target binding used by updateTargetFromSource is always null
        if (needsSourceUpdate())
        	updateTargetFromSource(sourceBinding.getValue());
        */
    }

    private void createTargetBinding(Object target) {
        boolean needsSubs = needsTargetSubscription();

        targetBinding = (IBinding) targetFactory.create(target, specification.Target, needsSubs);

        if (needsSubs) {
            if (targetBinding.hasChanges()) {
                //logger.debug("Binding " + specification.Target + " needs subscription.");
                targetSubscription = targetBinding.getChanges().subscribe(Observers.fromAction(new Action<Object>() {
                    public void invoke(Object obj) {
                        updateSourceFromTarget(obj);
                    }
                }));
            } else {
                logger.warning("Binding " + specification.Target + " needs subscription, but changes were not available.");
            }
        }
        /* commented out because in contstructor, if createtarget is called before createsource, source binding used by updateSourceFromTarget is always null
        if (needsTargetUpdate())
        	updateSourceFromTarget(targetBinding.getValue());
        */
    }

    private boolean needsSourceSubscription() {
        switch (this.mode) {
            case Default:
            case OneWay:
            case TwoWay:
                return true;
            default:
                return false;
        }
    }

    private boolean needsTargetSubscription() {
        switch (this.mode) {
            case Default:
            case OneWayToSource:
            case TwoWay:
                return true;
            default:
                return false;
        }
    }

    private boolean needsSourceUpdate() {
        switch (this.mode) {
            case Default:
            case OneWayOneTime:
            case OneWay:
            case TwoWay:
                return true;
            default:
                return false;
        }
    }

    private boolean needsTargetUpdate() {
        switch (this.mode) {
            case TwoWay:
            case OneWayToSource:
            case OneWayToSourceOneTime:
                return true;
            default:
                return false;
        }
    }

    protected void updateTargetFromSource(Object obj) {
        Object result = null;
        try {
            if (obj != IBinding.noValue) {
                //				logger.verbose("Updating binding from source " + specification.Path + " to target "
                //						+ specification.Target);
                result = specification.Converter.convert(obj, targetBinding.getType(),
                        specification.ConverterParameter, Locale.getDefault());

            } else {
                logger.warning("Switching to fallback value for " + specification.Path);
                result = specification.FallbackValue;
            }
            targetBinding.setValue(result);
        } catch (Exception e) {
            logger.error("Error occurred while binding " + specification.Path + " to target " + specification.Target
                    + ": " + e.getMessage());
        }
    }

    protected void updateSourceFromTarget(Object obj) {
        try {
            Object result = specification.Converter.convertBack(obj, sourceBinding.getType(),
                    specification.ConverterParameter, Locale.getDefault());
            sourceBinding.setValue(result);
        } catch (Exception e) {
            logger.error("Error occurred while binding " + specification.Target + " to source " + specification.Path
                    + ": " + e.getMessage());
        }
    }

    public void setLogger(IAndroidLogger logger) {
        this.logger = logger.getLogger(this);
    }

    public void dispose() {
        if (sourceSubscription != null)
            sourceSubscription.dispose();
        if (targetSubscription != null)
            targetSubscription.dispose();
        if (sourceBinding != null)
            sourceBinding.dispose();
        if (targetBinding != null)
            targetBinding.dispose();
    }
}
