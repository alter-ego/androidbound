package solutions.alterego.androidbound.interfaces;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;

import java.util.List;

import solutions.alterego.androidbound.binding.interfaces.IBindingAssociationEngine;
import solutions.alterego.androidbound.converters.interfaces.IValueConverterRegistry;
import solutions.alterego.androidbound.resources.interfaces.IResourceRegistry;
import solutions.alterego.androidbound.viewresolvers.interfaces.IViewResolver;


public interface IViewBinder extends IResourceRegistry, IValueConverterRegistry {

    public abstract void clearBindingForViewAndChildren(View rootView);

    public abstract void clearBindingsFor(View view);

    public abstract void clearAllBindings();

    public abstract View inflate(Context context, Object source, int layoutResID, ViewGroup viewGroup);

    public abstract View inflate(Context context, Object source, int layoutResID, ViewGroup viewGroup, IViewResolver additionalResolver);

    public abstract void registerBindingsFor(View view, List<IBindingAssociationEngine> bindings);

    public abstract List<IBindingAssociationEngine> getBindingsForViewAndChildren(View rootView);

    public abstract List<IBindingAssociationEngine> getBindingsFor(View view);

    public abstract void registerViewResolver(IViewResolver resolver);

    public abstract void unregisterViewResolver(IViewResolver resolver);

}
