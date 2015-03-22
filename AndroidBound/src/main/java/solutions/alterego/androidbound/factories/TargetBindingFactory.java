package solutions.alterego.androidbound.factories;

import com.alterego.advancedandroidlogger.interfaces.IAndroidLogger;
import solutions.alterego.androidbound.binds.TargetPropertyBinding;
import solutions.alterego.androidbound.interfaces.IBinding;
import solutions.alterego.androidbound.zzzztoremove.reactive.IScheduler;

public class TargetBindingFactory extends SourceBindingFactory {

    private IScheduler mScheduler;

    public TargetBindingFactory(IScheduler scheduler, IAndroidLogger logger) {
        super(logger);
        mScheduler = scheduler;
    }

    @Override
    protected IBinding createLeaf(Object source, String property, boolean needChangesIfPossible) {
        return new TargetPropertyBinding(source, property, needChangesIfPossible, mScheduler, mLogger);
    }
}
