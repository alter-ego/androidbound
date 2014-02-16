package com.alterego.androidbound.binders;

import java.util.ArrayList;
import java.util.List;

import com.alterego.androidbound.binds.BindingAssociation;
import com.alterego.androidbound.binds.BindingRequest;
import com.alterego.androidbound.binds.BindingSpecification;
import com.alterego.androidbound.interfaces.IBinder;
import com.alterego.androidbound.interfaces.IBindingAssociation;
import com.alterego.androidbound.interfaces.IBindingFactory;
import com.alterego.androidbound.interfaces.IParser;
import com.alterego.androidbound.zzzztoremove.ILogger;

public class TextSpecificationBinder implements IBinder {
	private IParser<List<BindingSpecification>> parser;
	private ILogger logger;
	private IBindingFactory sourceFactory;
	private IBindingFactory targetFactory;

	public TextSpecificationBinder(IParser<List<BindingSpecification>> parser, IBindingFactory sourceFactory, IBindingFactory targetFactory, ILogger logger) {
		this.parser = parser;
		this.sourceFactory = sourceFactory;
		this.targetFactory = targetFactory;
		setLogger(logger);		
	}
	
	public List<IBindingAssociation> bind(Object source, Object target, String bindingSpecifications) {
		final List<IBindingAssociation> bindings = new ArrayList<IBindingAssociation>();
		for(BindingSpecification specification : parser.parse(bindingSpecifications)) {
			BindingRequest request = new BindingRequest();
			request.Source = source;
			request.Target = target;
			request.Specification = specification;
			logger.debug("Creating full binding for " + source + " " + target);
			BindingAssociation full = new BindingAssociation(request, sourceFactory, targetFactory, logger);
			bindings.add(full);
			//logger.verbose("Binding added");
		}
		return bindings;
	}

	public void setLogger(ILogger logger) {
		this.logger = logger.getLogger(this);
	}
}
