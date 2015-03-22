
package solutions.alterego.androidbound.interfaces;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;

import java.util.List;


public interface IViewBinder extends IResourceRegistry, IValueConverterRegistry {
    public abstract void clearBindingForViewAndChildren(View rootView);

    public abstract void clearBindingsFor(View view);

    public abstract void clearAllBindings();

    public abstract View inflate(Context context, Object source, int layoutResID, ViewGroup viewGroup);
    
    public abstract View inflate(Context context, Object source, int layoutResID, ViewGroup viewGroup, IViewResolver additionalResolver);

    public abstract void registerBindingsFor(View view, List<IBindingAssociation> bindings);

    public abstract List<IBindingAssociation> getBindingsForViewAndChildren(View rootView);

    public abstract List<IBindingAssociation> getBindingsFor(View view);
    
    public abstract void registerViewResolver(IViewResolver resolver);
    
    public abstract void unregisterViewResolver(IViewResolver resolver);

}
