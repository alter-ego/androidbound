package solutions.alterego.androidbound.resources;

import java.util.HashMap;
import java.util.Map;

import solutions.alterego.androidbound.interfaces.ILogger;
import solutions.alterego.androidbound.interfaces.INeedsLogger;
import solutions.alterego.androidbound.resources.interfaces.IResourceProvider;
import solutions.alterego.androidbound.resources.interfaces.IResourceRegistry;

public class ResourceService implements IResourceProvider, IResourceRegistry, INeedsLogger {

    Map<String, Object> resources = new HashMap<String, Object>();

    private ILogger logger;

    public ResourceService(ILogger logger) {
        setLogger(logger);
    }

    public void registerResource(String name, Object resource) {
        logger.verbose("Registering resource " + name);
        resources.put(name, resource);
    }

    public Object find(String name) {
        logger.verbose("Requested resource " + name);
        if (resources.containsKey(name)) {
            return resources.get(name);
        }
        logger.debug("Resource " + name + " not found. Returning default.");
        return null;
    }

    public void setLogger(ILogger logger) {
        this.logger = logger.getLogger(this);
    }
}
