package solutions.alterego.androidbound.binding;

import com.alterego.advancedandroidlogger.implementations.NullAndroidLogger;
import com.alterego.advancedandroidlogger.interfaces.IAndroidLogger;

import java.util.Locale;

import rx.Subscription;
import rx.functions.Action1;
import solutions.alterego.androidbound.binding.data.BindingMode;
import solutions.alterego.androidbound.binding.data.BindingRequest;
import solutions.alterego.androidbound.binding.data.BindingSpecification;
import solutions.alterego.androidbound.binding.interfaces.IBinding;
import solutions.alterego.androidbound.binding.interfaces.IBindingAssociationEngine;
import solutions.alterego.androidbound.factories.IBindingFactory;

public class BindingAssociationEngine implements IBindingAssociationEngine {

    private BindingMode mMode;

    private Object mDataContext;

    private BindingSpecification mBindingSpecification;

    private IBinding mSourceBinding;

    private IBinding mTargetBinding;

    private Subscription mSourceSubscription;

    private Subscription mTargetSubscription;

    private IAndroidLogger mLogger = NullAndroidLogger.instance;

    private IBindingFactory mSourceFactory;

    private IBindingFactory mTargetFactory;

    public BindingAssociationEngine(BindingRequest request, IBindingFactory sourceFactory, IBindingFactory targetFactory, IAndroidLogger logger) {
        mMode = request.getSpecification().getMode();
        mSourceFactory = sourceFactory;
        mTargetFactory = targetFactory;
        mBindingSpecification = request.getSpecification();

        setLogger(logger);
        createTargetBinding(request.getTarget());
        createSourceBinding(request.getSource());

        if (needsTargetUpdate()) {
            updateSourceFromTarget(mTargetBinding.getValue());
        }

        if (needsSourceUpdate()) {
            updateTargetFromSource(mSourceBinding.getValue());
        }
    }

    public Object getDataContext() {
        return mDataContext;
    }

    public void setDataContext(Object value) {
        if (mDataContext == value) {
            return;
        }
        mDataContext = value;

        if (mSourceBinding != null) {
            mSourceBinding.dispose();
        }
        if (mSourceSubscription != null) {
            mSourceSubscription.unsubscribe();
        }

        createSourceBinding(value);
        if (needsSourceUpdate()) {
            updateTargetFromSource(mSourceBinding.getValue());
        }
    }

    private void createSourceBinding(Object source) {
        boolean needsSubs = needsSourceSubscription();

        mSourceBinding = mSourceFactory.create(source, mBindingSpecification.getPath(), needsSubs);

        if (needsSubs) {
            if (mSourceBinding.hasChanges()) {
                mSourceSubscription = mSourceBinding.getChanges().subscribe(new Action1<Object>() {
                    @Override
                    public void call(Object obj) {
                        updateTargetFromSource(obj);
                    }
                });
            } else {
                mLogger.warning("Binding " + mBindingSpecification.getPath() + " needs subscription, but changes were not available");
            }
        }
    }

    private void createTargetBinding(Object target) {
        boolean needsSubs = needsTargetSubscription();

        mTargetBinding = mTargetFactory.create(target, mBindingSpecification.getTarget(), needsSubs);

        if (needsSubs) {
            if (mTargetBinding.hasChanges()) {
                mTargetSubscription = mTargetBinding.getChanges().subscribe(new Action1<Object>() {
                    @Override
                    public void call(Object obj) {
                        updateSourceFromTarget(obj);
                    }
                });
            } else {
                mLogger.warning("Binding " + mBindingSpecification.getTarget() + " needs subscription, but changes were not available.");
            }
        }
    }

    private boolean needsSourceSubscription() {
        switch (mMode) {
            case Default:
            case OneWay:
            case TwoWay:
                return true;
            default:
                return false;
        }
    }

    private boolean needsTargetSubscription() {
        switch (mMode) {
            case Default:
            case OneWayToSource:
            case TwoWay:
                return true;
            default:
                return false;
        }
    }

    private boolean needsSourceUpdate() {
        switch (mMode) {
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
        switch (mMode) {
            case TwoWay:
            case OneWayToSource:
            case OneWayToSourceOneTime:
                return true;
            default:
                return false;
        }
    }

    protected void updateTargetFromSource(Object obj) {
        Object result;
        try {
            if (obj != IBinding.noValue) {
                result = mBindingSpecification
                        .getValueConverter()
                        .convert(obj, mTargetBinding.getType(), mBindingSpecification.getConverterParameter(), Locale.getDefault());

            } else {
                mLogger.warning("Switching to fallback value for " + mBindingSpecification.getPath());
                result = mBindingSpecification.getFallbackValue();
            }
            mTargetBinding.setValue(result);
        } catch (Exception e) {
            mLogger.error("Error occurred while binding " + mBindingSpecification.getPath() + " to target " + mBindingSpecification.getTarget()
                    + ": " + e.getMessage());
        }
    }

    protected void updateSourceFromTarget(Object obj) {
        try {
            Object result = mBindingSpecification
                    .getValueConverter()
                    .convertBack(obj, mSourceBinding.getType(), mBindingSpecification.getConverterParameter(), Locale.getDefault());
            mSourceBinding.setValue(result);
        } catch (Exception e) {
            mLogger.error("Error occurred while binding " + mBindingSpecification.getTarget() + " to source " + mBindingSpecification.getPath()
                    + ": " + e.getMessage());
        }
    }

    public void setLogger(IAndroidLogger logger) {
        mLogger = logger.getLogger(this);
    }

    public void dispose() {
        if (mSourceSubscription != null) {
            mSourceSubscription.unsubscribe();
        }
        if (mTargetSubscription != null) {
            mTargetSubscription.unsubscribe();
        }
        if (mSourceBinding != null) {
            mSourceBinding.dispose();
        }
        if (mTargetBinding != null) {
            mTargetBinding.dispose();
        }
    }
}
