package solutions.alterego.androidbound.binding.types;

import java.util.List;

import rx.Subscription;
import rx.functions.Action1;
import solutions.alterego.androidbound.binding.interfaces.IBinding;
import solutions.alterego.androidbound.factories.IBindingFactory;
import solutions.alterego.androidbound.interfaces.ILogger;

public class ChainedBinding extends PropertyBinding {

    private boolean mNeedChangesIfPossible;

    private IBindingFactory mBindingFactory;

    private List<String> mTokens;

    private IBinding mCurrentBinding;

    private Subscription mCurrentBindingChanged;

    private String mMemberName;

    public ChainedBinding(Object source, String propertyName, List<String> tokens, boolean needChangesIfPossible, IBindingFactory factory,
            ILogger logger) {
        super(source, propertyName, needChangesIfPossible, logger);
        mNeedChangesIfPossible = needChangesIfPossible;
        mMemberName = propertyName;
        mTokens = tokens;
        mBindingFactory = factory;
        updateChildBinding();
    }

    protected void updateChildBinding() {
        if (mCurrentBinding != null) {
            mCurrentBindingChanged.unsubscribe();
            mCurrentBinding.dispose();
            mCurrentBindingChanged = null;
        }

        Object currentValue = getInfo().getValue(getSubject());
        if (currentValue == null) {
            return;
        }

        mCurrentBinding = mBindingFactory.create(currentValue, mTokens, this.mNeedChangesIfPossible);
        mCurrentBindingChanged = mCurrentBinding.getChanges().subscribe(new Action1<Object>() {
            @Override
            public void call(Object obj) {
                notifyChange(obj);
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
            getLogger().warning("Target property path is missing. Couldn't set value for " + mMemberName);
        } else {
            mCurrentBinding.setValue(value);
        }
    }

    @Override
    public void addValue(Object object) {
        mCurrentBinding.addValue(object);
    }

    @Override
    public void dispose() {
        super.dispose();
        if (mCurrentBindingChanged != null) {
            mCurrentBindingChanged.unsubscribe();
            mCurrentBindingChanged = null;
        }
        if (mCurrentBinding != null) {
            mCurrentBinding.dispose();
            mCurrentBinding = null;
        }
    }
}
