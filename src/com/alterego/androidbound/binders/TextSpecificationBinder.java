package com.alterego.androidbound.binders;

import com.alterego.advancedandroidlogger.interfaces.IAndroidLogger;
import com.alterego.androidbound.binds.BindingAssociation;
import com.alterego.androidbound.binds.BindingRequest;
import com.alterego.androidbound.binds.BindingSpecification;
import com.alterego.androidbound.interfaces.IBinder;
import com.alterego.androidbound.interfaces.IBindingAssociation;
import com.alterego.androidbound.interfaces.IBindingFactory;
import com.alterego.androidbound.interfaces.IParser;

import java.util.ArrayList;
import java.util.List;

public class TextSpecificationBinder implements IBinder {

    private final IParser<List<BindingSpecification>> mParser;

    private IAndroidLogger mLogger;

    private final IBindingFactory mSourceFactory;

    private final IBindingFactory mTargetFactory;

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

    public List<IBindingAssociation> bind(Object source, Object target, String bindingSpecifications) {
        final List<IBindingAssociation> bindings = new ArrayList<IBindingAssociation>();

        for (BindingSpecification specification : mParser.parse(bindingSpecifications)) {

            BindingRequest request = new BindingRequest();
            request.setSource(source);
            request.setTarget(target);
            request.setSpecification(specification);

            mLogger.debug("Creating full binding for " + source + " " + target);

            BindingAssociation bindingAssociation = new BindingAssociation(request, mSourceFactory, mTargetFactory, mLogger);
            bindings.add(bindingAssociation);
        }
        return bindings;
    }

    public void setLogger(IAndroidLogger logger) {
        mLogger = logger.getLogger(this);
    }
}
