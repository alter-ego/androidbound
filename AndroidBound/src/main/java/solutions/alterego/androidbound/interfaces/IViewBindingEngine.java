package solutions.alterego.androidbound.interfaces;

import android.content.Context;
import android.view.View;

import java.util.List;

import solutions.alterego.androidbound.android.interfaces.INeedsImageLoader;
import solutions.alterego.androidbound.binding.interfaces.IBindingAssociationEngine;
import solutions.alterego.androidbound.converters.interfaces.IValueConverterRegistry;
import solutions.alterego.androidbound.resources.interfaces.IResourceRegistry;

public interface IViewBindingEngine extends IDisposable, INeedsImageLoader, IHasLogger, IHasDebugMode, IResourceRegistry, IValueConverterRegistry {

    void lazyBindView(View view, Object source);

    void registerLazyBindingsFor(View view, String bindingString);

    void bindViewToSource(Object source, View view, String bindingString);

    void registerBindingsFor(View view, List<IBindingAssociationEngine> bindings);

    List<IBindingAssociationEngine> getBindingsForView(View rootView);

    void clearBindingForViewAndChildren(View rootView);

    void clearAllBindings();

    void disposeOf(Context context);
}
