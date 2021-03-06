package solutions.alterego.androidbound.binding.types;

import java.util.List;

import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Consumer;
import solutions.alterego.androidbound.binding.interfaces.IBinding;
import solutions.alterego.androidbound.factories.IBindingFactory;
import solutions.alterego.androidbound.interfaces.ILogger;
import solutions.alterego.androidbound.utils.Exceptional;

public class ChainedBinding extends PropertyBinding {

    private boolean mNeedChangesIfPossible;

    private IBindingFactory mBindingFactory;

    private List<String> mTokens;

    private IBinding mCurrentBinding;

    private Disposable mCurrentBindingChanged;

    private String mMemberName;

    public ChainedBinding(Object source, String propertyName, List<String> tokens, boolean needChangesIfPossible, IBindingFactory factory,
            ILogger logger, boolean debugMode) {
        super(source, propertyName, needChangesIfPossible, logger, debugMode);
        mNeedChangesIfPossible = needChangesIfPossible;
        mMemberName = propertyName;
        mTokens = tokens;
        mBindingFactory = factory;
        updateChildBinding();
    }

    protected void updateChildBinding() {
        if (mCurrentBinding != null) {
            mCurrentBindingChanged.dispose();
            mCurrentBinding.dispose();
            mCurrentBindingChanged = null;
        }

        Object currentValue = getInfo().getValue(getSubject());
        if (currentValue == null) {
            return;
        }

        mCurrentBinding = mBindingFactory.create(currentValue, mTokens, this.mNeedChangesIfPossible);
        mCurrentBindingChanged = mCurrentBinding.getChanges()
                .subscribe(new Consumer<Exceptional<Object>>() {
                    @Override
                    public void accept(Exceptional<Object> value) throws Exception {
                        notifyChange(value);
                    }
                });
    }

    @Override
    public Class<?> getType() {
        return mCurrentBinding == null ? Object.class : mCurrentBinding.getType();
    }

    @Override
    protected void onBoundPropertyChanged() {
        updateChildBinding();
        notifyChange(getValue());
    }

    @Override
    public Object getValue() {
        return mCurrentBinding == null ? noValue : mCurrentBinding.getValue();
    }

    @Override
    public void setValue(Object value) {
        if (mCurrentBinding == null) {
            String msg = "Target property path is missing. Couldn't set value for " + mMemberName;
            getLogger().warning(msg);
            if (mDebugMode) {
                throw new RuntimeException(msg);
            }
        } else {
            mCurrentBinding.setValue(value);
        }
    }

    @Override
    public void addValue(Object object) {
        if (mCurrentBinding == null) {
            String msg = "Target property path is missing. Couldn't set value for " + mMemberName;
            getLogger().warning(msg);
            if (mDebugMode) {
                throw new RuntimeException(msg);
            }
        } else {
            mCurrentBinding.setValue(object);
        }
    }

    @Override
    public void dispose() {
        super.dispose();
        if (mCurrentBindingChanged != null) {
            mCurrentBindingChanged.dispose();
            mCurrentBindingChanged = null;
        }
        if (mCurrentBinding != null) {
            mCurrentBinding.dispose();
            mCurrentBinding = null;
        }
    }
}
