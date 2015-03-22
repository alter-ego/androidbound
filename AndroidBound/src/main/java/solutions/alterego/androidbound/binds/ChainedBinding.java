package solutions.alterego.androidbound.binds;

import com.alterego.advancedandroidlogger.interfaces.IAndroidLogger;
import solutions.alterego.androidbound.interfaces.IBinding;
import solutions.alterego.androidbound.interfaces.IBindingFactory;
import solutions.alterego.androidbound.zzzztoremove.reactive.Action;
import solutions.alterego.androidbound.zzzztoremove.reactive.IDisposable;
import solutions.alterego.androidbound.zzzztoremove.reactive.Observers;

import java.util.List;

public class ChainedBinding extends PropertyBinding {

    private boolean mNeedChangesIfPossible;

    private IBindingFactory mBindingFactory;

    private List<String> mTokens;

    private IBinding mCurrentBinding;

    private IDisposable mCurrentBindingChanged;

    private String mMemberName;

    public ChainedBinding(Object source, String propertyName, List<String> tokens, boolean needChangesIfPossible, IBindingFactory factory,
            IAndroidLogger logger) {
        super(source, propertyName, needChangesIfPossible, logger);
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
        mCurrentBindingChanged = mCurrentBinding.getChanges().subscribe(Observers.fromAction(new Action<Object>() {
            public void invoke(Object obj) {
                notifyChange(obj);
            }
        }));
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
        return mCurrentBinding == null ? IBinding.noValue : mCurrentBinding.getValue();
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
