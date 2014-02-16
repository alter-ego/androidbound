package com.alterego.androidbound.services;

import java.util.HashMap;
import java.util.Map;

import com.alterego.advancedandroidlogger.interfaces.IAndroidLogger;
import com.alterego.androidbound.interfaces.INeedsLogger;
import com.alterego.androidbound.interfaces.IResourceProvider;
import com.alterego.androidbound.interfaces.IResourceRegistry;

public class ResourceService implements IResourceProvider, IResourceRegistry, INeedsLogger {
	Map<String, Object> resources = new HashMap<String, Object>();
	private IAndroidLogger logger;
	
	public ResourceService(IAndroidLogger logger) {
		setLogger(logger);
	}
	
	public void registerResource(String name, Object resource) {
		logger.verbose("Registering resource " + name);
		resources.put(name, resource);
	}

	public Object find(String name) {
		logger.verbose("Requested resource " + name);
		if(resources.containsKey(name))
			return resources.get(name);
		logger.debug("Resource " + name + " not found. Returning default.");
		return null;
	}

	public void setLogger(IAndroidLogger logger) {
		this.logger = logger.getLogger(this);
	}
}
