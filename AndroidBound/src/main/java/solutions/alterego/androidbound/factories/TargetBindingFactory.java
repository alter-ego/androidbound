package solutions.alterego.androidbound.factories;

import solutions.alterego.androidbound.binding.interfaces.IBinding;
import solutions.alterego.androidbound.binding.types.TargetPropertyBinding;
import solutions.alterego.androidbound.interfaces.ILogger;

public class TargetBindingFactory extends SourceBindingFactory {

    public TargetBindingFactory(ILogger logger, boolean debugMode) {
        super(logger, debugMode);
    }

    @Override
    protected IBinding createLeaf(Object source, String property, boolean needChangesIfPossible) {
        return new TargetPropertyBinding(source, property, needChangesIfPossible, mLogger, mDebugMode);
    }
}
