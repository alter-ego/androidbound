package solutions.alterego.androidbound.binding;

import com.alterego.advancedandroidlogger.interfaces.IAndroidLogger;

import java.util.ArrayList;
import java.util.List;

import solutions.alterego.androidbound.binding.data.BindingRequest;
import solutions.alterego.androidbound.binding.data.BindingSpecification;
import solutions.alterego.androidbound.binding.interfaces.IBindingAssociationEngine;
import solutions.alterego.androidbound.binding.interfaces.IBinder;
import solutions.alterego.androidbound.factories.IBindingFactory;
import solutions.alterego.androidbound.parsers.IParser;

public class TextSpecificationBinder implements IBinder {

    private final IParser<List<BindingSpecification>> mParser;

    private final IBindingFactory mSourceFactory;

    private final IBindingFactory mTargetFactory;

    private IAndroidLogger mLogger;

    public TextSpecificationBinder(
            IParser<List<BindingSpecification>> parser,
            IBindingFactory sourceFactory,
            IBindingFactory targetFactory,
            IAndroidLogger logger) {

        mParser = parser;
        mSourceFactory = sourceFactory;
        mTargetFactory = targetFactory;
        setLogger(logger);
    }

    public List<IBindingAssociationEngine> bind(Object source, Object target, String bindingSpecifications) {
        final List<IBindingAssociationEngine> bindings = new ArrayList<IBindingAssociationEngine>();

        for (BindingSpecification specification : mParser.parse(bindingSpecifications)) {
            if (specification.getPath() != null && !specification.getPath().equals("")) {
                BindingRequest request = new BindingRequest();
                request.setSource(source);
                request.setTarget(target);
                request.setSpecification(specification);

                mLogger.debug("Creating full binding for " + source + " " + target);

                BindingAssociationEngine bindingAssociationEngine = new BindingAssociationEngine(request, mSourceFactory, mTargetFactory, mLogger);
                bindings.add(bindingAssociationEngine);
            } else {
                mLogger.debug("Cannot create binding for " + source + " " + target + ", path is null or empty!");
            }
        }
        return bindings;
    }

    public void setLogger(IAndroidLogger logger) {
        mLogger = logger.getLogger(this);
    }
}
