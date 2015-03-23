package solutions.alterego.androidbound.factories;

import com.alterego.advancedandroidlogger.interfaces.IAndroidLogger;

import rx.Scheduler;
import solutions.alterego.androidbound.binds.TargetPropertyBinding;
import solutions.alterego.androidbound.interfaces.IBinding;

public class TargetBindingFactory extends SourceBindingFactory {

    private Scheduler mScheduler;

    public TargetBindingFactory(Scheduler scheduler, IAndroidLogger logger) {
        super(logger);
        mScheduler = scheduler;
    }

    @Override
    protected IBinding createLeaf(Object source, String property, boolean needChangesIfPossible) {
        return new TargetPropertyBinding(source, property, needChangesIfPossible, mScheduler, mLogger);
    }
}
