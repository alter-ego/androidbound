package solutions.alterego.androidbound.binding;

import android.content.Context;
import android.view.View;

import java.util.List;

import solutions.alterego.androidbound.android.interfaces.IImageLoader;
import solutions.alterego.androidbound.binding.interfaces.IBindingAssociationEngine;
import solutions.alterego.androidbound.converters.interfaces.IValueConverter;
import solutions.alterego.androidbound.interfaces.ILogger;
import solutions.alterego.androidbound.interfaces.IViewBindingEngine;

public class NullViewBindingEngine implements IViewBindingEngine {

    public static final IViewBindingEngine instance = new NullViewBindingEngine();

    @Override
    public void dispose() {

    }

    @Override
    public void setDebugMode(boolean debugMode) {

    }

    @Override
    public ILogger getLogger() {
        return null;
    }

    @Override
    public void setImageLoader(IImageLoader imageLoader) {

    }

    @Override
    public void registerResource(String name, Object resource) {

    }

    @Override
    public void registerConverter(String name, IValueConverter converter) {

    }

    @Override
    public void lazyBindView(View view, Object source) {

    }

    @Override
    public void registerLazyBindingsFor(View view, String bindingString) {

    }

    @Override
    public void bindViewToSource(Object source, View view, String bindingString) {

    }

    @Override
    public void registerBindingsFor(View view, List<IBindingAssociationEngine> bindings) {

    }

    @Override
    public List<IBindingAssociationEngine> getBindingsForView(View rootView) {
        return null;
    }

    @Override
    public void clearBindingForViewAndChildren(View rootView) {

    }

    @Override
    public void clearAllBindings() {

    }

    @Override
    public void disposeOf(Context context) {

    }
}
