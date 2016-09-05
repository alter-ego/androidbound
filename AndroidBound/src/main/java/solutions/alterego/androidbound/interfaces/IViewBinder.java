package solutions.alterego.androidbound.interfaces;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;

import java.util.List;

import solutions.alterego.androidbound.binding.interfaces.IBindingAssociationEngine;
import solutions.alterego.androidbound.converters.interfaces.IValueConverterRegistry;
import solutions.alterego.androidbound.resources.interfaces.IResourceRegistry;
import solutions.alterego.androidbound.viewresolvers.interfaces.IViewResolver;


public interface IViewBinder extends IResourceRegistry, IValueConverterRegistry, IDisposable {

    void clearBindingForViewAndChildren(View rootView);

    void clearBindingsFor(View view);

    void clearAllBindings();

    View inflate(Context context, Object source, int layoutResID, ViewGroup viewGroup);

    View inflate(Context context, Object source, int layoutResID, ViewGroup viewGroup, IViewResolver additionalResolver);

    void registerBindingsFor(View view, List<IBindingAssociationEngine> bindings);

    List<IBindingAssociationEngine> getBindingsForViewAndChildren(View rootView);

    List<IBindingAssociationEngine> getBindingsFor(View view);

    void registerViewResolver(IViewResolver resolver);

    void unregisterViewResolver(IViewResolver resolver);

    void registerLazyBindingsFor(View view, String bindingString);

    void lazyBindView(View view, Object source);

    void bindViewToSource(Object source, View view, String bindingString);

}
