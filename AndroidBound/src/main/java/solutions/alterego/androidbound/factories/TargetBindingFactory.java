package solutions.alterego.androidbound.factories;

import com.alterego.advancedandroidlogger.interfaces.IAndroidLogger;

import solutions.alterego.androidbound.binding.types.TargetPropertyBinding;
import solutions.alterego.androidbound.binding.interfaces.IBinding;

public class TargetBindingFactory extends SourceBindingFactory {

    public TargetBindingFactory(IAndroidLogger logger) {
        super(logger);
    }

    @Override
    protected IBinding createLeaf(Object source, String property, boolean needChangesIfPossible) {
        return new TargetPropertyBinding(source, property, needChangesIfPossible, mLogger);
    }
}
