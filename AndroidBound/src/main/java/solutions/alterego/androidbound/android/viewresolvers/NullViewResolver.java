package solutions.alterego.androidbound.android.viewresolvers;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;

import solutions.alterego.androidbound.android.interfaces.IViewResolver;
import solutions.alterego.androidbound.interfaces.ILogger;

public class NullViewResolver implements IViewResolver {

    public static final IViewResolver instance = new NullViewResolver();

    @Override
    public void setLogger(ILogger logger) {

    }

    @Override
    public View createView(String name, Context context, AttributeSet attrs) {
        return null;
    }

    @Override
    public void dispose() {

    }
}
