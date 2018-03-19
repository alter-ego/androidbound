package solutions.alterego.androidbound.factories;

import java.util.Arrays;
import java.util.List;

import solutions.alterego.androidbound.binding.interfaces.IBinding;
import solutions.alterego.androidbound.binding.types.ChainedBinding;
import solutions.alterego.androidbound.binding.types.CommandBinding;
import solutions.alterego.androidbound.binding.types.PropertyBinding;
import solutions.alterego.androidbound.binding.types.SelfBinding;
import solutions.alterego.androidbound.interfaces.ILogger;

public class SourceBindingFactory implements IBindingFactory {

    protected final boolean mDebugMode;

    protected ILogger mLogger;

    public SourceBindingFactory(ILogger logger, boolean debugMode) {
        mDebugMode = debugMode;
        setLogger(logger);
    }

    public IBinding create(Object source, String combinedPath, boolean needChangesIfPossible) {
        String[] tokens = combinedPath.split("\\.");
        return create(source, Arrays.asList(tokens), needChangesIfPossible);
    }

    public IBinding create(Object source, List<String> pathTokens, boolean needChangesIfPossible) {
        if (pathTokens == null || pathTokens.size() == 0) {
            throw new UnsupportedOperationException("Empty token list passed to binding factory");
        }

        String current = pathTokens.get(0);
        if (pathTokens.size() == 1) {
            return createLeaf(source, current, needChangesIfPossible);
        }
        return createChained(source, current, pathTokens.subList(1, pathTokens.size()), needChangesIfPossible);
    }

    protected IBinding createLeaf(Object source, String property, boolean needChangesIfPossible) {
        if (property.equals("this")) {
            return new SelfBinding(source, mLogger);
        } else if (CommandBinding.isCommand(source, property)) {
            return new CommandBinding(source, property, mLogger, mDebugMode);
        } else {
            return new PropertyBinding(source, property, needChangesIfPossible, mLogger, mDebugMode);
        }
    }

    protected IBinding createChained(Object source, String property, List<String> remainingTokens, boolean needChangesIfPossible) {
        return new ChainedBinding(source, property, remainingTokens, needChangesIfPossible, this, mLogger, mDebugMode);
    }

    public void setLogger(ILogger logger) {
        mLogger = logger.getLogger(this);
    }
}
