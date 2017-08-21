package solutions.alterego.androidbound.binding;

import java.util.Locale;

import io.reactivex.disposables.Disposable;
import io.reactivex.disposables.Disposables;
import io.reactivex.functions.Consumer;
import io.reactivex.schedulers.Schedulers;
import solutions.alterego.androidbound.NullLogger;
import solutions.alterego.androidbound.binding.data.BindingMode;
import solutions.alterego.androidbound.binding.data.BindingRequest;
import solutions.alterego.androidbound.binding.data.BindingSpecification;
import solutions.alterego.androidbound.binding.interfaces.IBinding;
import solutions.alterego.androidbound.binding.interfaces.IBindingAssociationEngine;
import solutions.alterego.androidbound.factories.IBindingFactory;
import solutions.alterego.androidbound.interfaces.ILogger;
import solutions.alterego.androidbound.utils.Exceptional;

public class BindingAssociationEngine implements IBindingAssociationEngine {

    private BindingMode mMode;

    private Object mDataContext;

    private BindingSpecification mBindingSpecification;

    private IBinding mSourceBinding;

    private IBinding mTargetBinding;

    private Disposable mSourceDisposable;

    private Disposable mTargetDisposable;

    private Disposable mSourceAccumulateDisposable = Disposables.disposed();

    private Disposable mTargetAccumulateDisposable = Disposables.disposed();

    private ILogger mLogger = NullLogger.instance;

    private IBindingFactory mSourceFactory;

    private IBindingFactory mTargetFactory;

    public BindingAssociationEngine(BindingRequest request, IBindingFactory sourceFactory,
            IBindingFactory targetFactory, ILogger logger) {
        mMode = request.getSpecification().getMode();
        mSourceFactory = sourceFactory;
        mTargetFactory = targetFactory;
        mBindingSpecification = request.getSpecification();

        setLogger(logger);
        createTargetBinding(request.getTarget());
        createSourceBinding(request.getSource());

        createRemoveBinding(request.getSource());
        createAccumulateSourceBinding(request.getSource());
        createAccumulateTargetBinding(request.getTarget());

        if (needsTargetUpdate()) {
            updateSourceFromTarget(mTargetBinding.getValue());
        }

        if (needsSourceUpdate()) {
            updateTargetFromSource(mSourceBinding.getValue());
        }

        if (needsTargetAccumulate()) {
            accumulateItems(mSourceBinding.getValue());
        }

        if (needsTargetRemove()) {
            removeItems(mSourceBinding.getValue());
        }

        if (needsSourceAccumulate()) {
            accumulateItemsToSource(mTargetBinding.getValue());
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
        if (mSourceDisposable != null) {
            mSourceDisposable.dispose();
        }
        if (mSourceAccumulateDisposable != null) {
            mSourceAccumulateDisposable.dispose();
        }

        createAccumulateSourceBinding(value);
        createSourceBinding(value);
        if (needsSourceUpdate()) {
            updateTargetFromSource(mSourceBinding.getValue());
        }

        if (needsTargetAccumulate()) {
            accumulateItems(mSourceBinding.getValue());
        }
    }

    private void createSourceBinding(Object source) {
        boolean needsSubs = needsSourceDisposable();

        mSourceBinding = mSourceFactory.create(source, mBindingSpecification.getPath(), needsSubs);

        if (needsSubs) {
            if (mSourceBinding.hasChanges()) {
                mSourceDisposable = mSourceBinding.getChanges()
                        .subscribeOn(Schedulers.computation())
                        .subscribe(new Consumer<Exceptional<Object>>() {
                            @Override
                            public void accept(Exceptional<Object> value) throws Exception {
                                updateTargetFromSource(value);
                            }
                        });
            } else {
                mLogger.warning("Binding " + mBindingSpecification.getPath()
                        + " needs Disposable, but changes were not available");
            }
        }
    }

    private void createRemoveBinding(Object target) {
        boolean needsSubs = needsTargetRemove();
        mTargetBinding = mTargetFactory.create(target, mBindingSpecification.getTarget(), needsSubs);
        if (needsSubs) {
            if (mTargetBinding.hasChanges()) {
                mTargetDisposable = mTargetBinding.getChanges()
                        .subscribeOn(Schedulers.computation())
                        .subscribe(new Consumer<Exceptional<Object>>() {
                            @Override
                            public void accept(Exceptional<Object> value) throws Exception {
                                removeItems(value);
                            }
                        });
            } else {
                mLogger.warning("Binding " + mBindingSpecification.getTarget()
                        + " needs Disposable, but changes were not available.");
            }
        }
    }

    private void createTargetBinding(Object target) {
        boolean needsSubs = needsTargetDisposable();

        mTargetBinding = mTargetFactory.create(target, mBindingSpecification.getTarget(), needsSubs);

        if (needsSubs) {
            if (mTargetBinding.hasChanges()) {
                mTargetDisposable = mTargetBinding.getChanges()
                        .subscribeOn(Schedulers.computation())
                        .subscribe(new Consumer<Exceptional<Object>>() {
                            @Override
                            public void accept(Exceptional<Object> value) throws Exception {
                                updateSourceFromTarget(value);
                            }
                        });
            } else {
                mLogger.warning("Binding " + mBindingSpecification.getTarget()
                        + " needs Disposable, but changes were not available.");
            }
        }
    }


    private void createAccumulateSourceBinding(Object source) {
        boolean needsSubs = needsTargetAccumulate();

        mSourceBinding = mSourceFactory.create(source, mBindingSpecification.getPath(), needsSubs);

        if (needsSubs) {
            if (mSourceBinding.hasChanges()) {
                mSourceAccumulateDisposable = mSourceBinding.getChanges()
                        .subscribeOn(Schedulers.computation())
                        .subscribe(new Consumer<Exceptional<Object>>() {
                            @Override
                            public void accept(Exceptional<Object> value) throws Exception {
                                accumulateItems(value);
                            }
                        });
            } else {
                mLogger.warning("Binding " + mBindingSpecification.getPath()
                        + " needs Disposable, but changes were not available");
            }
        }
    }

    private void createAccumulateTargetBinding(Object target) {
        boolean needsSubs = needsSourceAccumulate();
        mTargetBinding = mTargetFactory.create(target, mBindingSpecification.getTarget(), needsSubs);
        if (needsSubs) {
            if (mTargetBinding.hasChanges()) {
                mTargetAccumulateDisposable = mTargetBinding.getChanges()
                        .subscribeOn(Schedulers.computation())
                        .subscribe(new Consumer<Exceptional<Object>>() {
                            @Override
                            public void accept(Exceptional<Object> value) throws Exception {
                                accumulateItemsToSource(value);
                            }
                        });
            } else {
                mLogger.warning("Binding " + mBindingSpecification.getTarget()
                        + " needs Disposable, but changes were not available.");
            }
        }
    }

    private boolean needsSourceDisposable() {
        switch (mMode) {
            case Default:
            case OneWay:
            case TwoWay:
                return true;
            default:
                return false;
        }
    }

    private boolean needsTargetDisposable() {
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

    public boolean needsTargetAccumulate() {
        return mMode == BindingMode.Accumulate || mMode == BindingMode.AccumulateTwoWay;
    }

    private boolean needsTargetRemove() {
        return mMode == BindingMode.RemoveSource;
    }


    public boolean needsSourceAccumulate() {
        return mMode == BindingMode.AccumulateToSource || mMode == BindingMode.AccumulateTwoWay;
    }

    protected void updateTargetFromSource(Object obj) {
        Object result;
        try {
            if (obj != IBinding.noValue) {
                result = mBindingSpecification
                        .getValueConverter()
                        .convert(unwrap(obj), mTargetBinding.getType(), mBindingSpecification.getConverterParameter(),
                                Locale.getDefault());

            } else {
                mLogger.warning("Switching to fallback value for " + mBindingSpecification.getPath());
                result = mBindingSpecification.getFallbackValue();
            }
            mTargetBinding.setValue(result);
        } catch (Exception e) {
            mLogger.error("Error occurred while binding " + mBindingSpecification.getPath() + " to target "
                    + mBindingSpecification.getTarget()
                    + ": " + e.getMessage());
        }
    }

    protected void updateSourceFromTarget(Object obj) {
        try {
            Object result = mBindingSpecification
                    .getValueConverter()
                    .convertBack(unwrap(obj), mSourceBinding.getType(), mBindingSpecification.getConverterParameter(),
                            Locale.getDefault());

            mSourceBinding.setValue(result);
        } catch (Exception e) {
            mLogger.error("Error occurred while binding " + mBindingSpecification.getTarget() + " to source "
                    + mBindingSpecification.getPath()
                    + ": " + e.getMessage());
        }
    }

    private Object unwrap(Object obj) {
        if (obj instanceof Exceptional) {
            return ((Exceptional) obj).value();
        }
        return obj;
    }

    private void removeItems(Object obj) {
        Object result;
        try {
            if (obj != IBinding.noValue) {
                result = mBindingSpecification
                        .getValueConverter()
                        .convert(unwrap(obj), mSourceBinding.getType(), mBindingSpecification.getConverterParameter(),
                                Locale.getDefault());

            } else {
                mLogger.warning("Switching to fallback value for " + mBindingSpecification.getPath());
                result = mBindingSpecification.getFallbackValue();
            }
            mTargetBinding.removeValue(result);
        } catch (Exception e) {
            mLogger.error("Error occurred while binding " + mBindingSpecification.getPath() + " to target "
                    + mBindingSpecification.getTarget()
                    + ": " + e.getMessage());
        }
    }

    private void accumulateItems(Object obj) {
        Object result;
        try {
            if (obj != IBinding.noValue) {
                result = mBindingSpecification
                        .getValueConverter()
                        .convert(unwrap(obj), mSourceBinding.getType(), mBindingSpecification.getConverterParameter(),
                                Locale.getDefault());

            } else {
                mLogger.warning("Switching to fallback value for " + mBindingSpecification.getPath());
                result = mBindingSpecification.getFallbackValue();
            }
            mTargetBinding.addValue(result);
        } catch (Exception e) {
            mLogger.error("Error occurred while binding " + mBindingSpecification.getPath() + " to target "
                    + mBindingSpecification.getTarget()
                    + ": " + e.getMessage());
        }
    }

    private void accumulateItemsToSource(Object obj) {
        try {
            Object result = mBindingSpecification
                    .getValueConverter()
                    .convertBack(unwrap(obj), mSourceBinding.getType(), mBindingSpecification.getConverterParameter(),
                            Locale.getDefault());
            mSourceBinding.addValue(result);
        } catch (Exception e) {
            mLogger.error("Error occurred while binding " + mBindingSpecification.getTarget() + " to source "
                    + mBindingSpecification.getPath()
                    + ": " + e.getMessage());
        }
    }

    public void setLogger(ILogger logger) {
        mLogger = logger.getLogger(this);
    }

    public void dispose() {
        if (mSourceDisposable != null) {
            mSourceDisposable.dispose();
        }
        if (mTargetDisposable != null) {
            mTargetDisposable.dispose();
        }
        if (mSourceBinding != null) {
            mSourceBinding.dispose();
        }
        if (mTargetBinding != null) {
            mTargetBinding.dispose();
        }

        mTargetAccumulateDisposable.dispose();
        mSourceAccumulateDisposable.dispose();
    }
}
