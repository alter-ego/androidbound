package solutions.alterego.androidbound.support;

import android.content.Context;

import solutions.alterego.androidbound.NullLogger;
import solutions.alterego.androidbound.ViewBinder;
import solutions.alterego.androidbound.interfaces.ILogger;
import solutions.alterego.androidbound.interfaces.IViewBindingEngine;
import solutions.alterego.androidbound.support.android.viewresolvers.SupportViewResolver;
import solutions.alterego.androidbound.support.binding.SupportViewBindingEngine;

public class SupportViewBinder extends ViewBinder {

    public SupportViewBinder(Context ctx) {
        this(ctx, NullLogger.instance);
    }

    public SupportViewBinder(Context ctx, ILogger logger) {
        super(ctx, logger);
        registerViewResolver(new SupportViewResolver(logger));
    }

    @Override
    protected IViewBindingEngine createViewBindingEngine(ILogger logger) {
        return new SupportViewBindingEngine(logger);
    }
}
