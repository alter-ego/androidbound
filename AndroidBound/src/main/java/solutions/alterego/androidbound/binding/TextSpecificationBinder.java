package solutions.alterego.androidbound.binding;

import android.view.View;

import java.util.ArrayList;
import java.util.List;

import solutions.alterego.androidbound.binding.data.BindingRequest;
import solutions.alterego.androidbound.binding.data.BindingSpecification;
import solutions.alterego.androidbound.binding.interfaces.IBinder;
import solutions.alterego.androidbound.binding.interfaces.IBindingAssociationEngine;
import solutions.alterego.androidbound.factories.IBindingFactory;
import solutions.alterego.androidbound.interfaces.ILogger;
import solutions.alterego.androidbound.parsers.IParser;

public class TextSpecificationBinder implements IBinder {

    private final IParser<List<BindingSpecification>> mParser;

    private final IBindingFactory mSourceFactory;

    private final IBindingFactory mTargetFactory;

    private final boolean mDebugMode;

    private ILogger mLogger;

    public TextSpecificationBinder(
            IParser<List<BindingSpecification>> parser,
            IBindingFactory sourceFactory,
            IBindingFactory targetFactory,
            ILogger logger,
            boolean debugMode) {

        mParser = parser;
        mSourceFactory = sourceFactory;
        mTargetFactory = targetFactory;
        mDebugMode = debugMode;
        setLogger(logger);
    }

    public List<IBindingAssociationEngine> bind(Object source, View target, String bindingSpecifications) {
        final List<IBindingAssociationEngine> bindings = new ArrayList<IBindingAssociationEngine>();

        for (BindingSpecification specification : mParser.parse(bindingSpecifications)) {
            if (specification.getSource() != null && !specification.getSource().equals("")) {
                BindingRequest request = new BindingRequest();
                request.setSource(source);
                request.setTarget(target);
                request.setSpecification(specification);

                mLogger.debug("Creating full binding for " + source + " " + target);

                BindingAssociationEngine bindingAssociationEngine = new BindingAssociationEngine(request, mSourceFactory, mTargetFactory, mLogger, mDebugMode);
                bindings.add(bindingAssociationEngine);
            } else {
                mLogger.debug("Cannot create binding for " + source + " " + target + ", path is null or empty!");
            }
        }
        return bindings;
    }

    public void setLogger(ILogger logger) {
        mLogger = logger.getLogger(this);
    }
}
