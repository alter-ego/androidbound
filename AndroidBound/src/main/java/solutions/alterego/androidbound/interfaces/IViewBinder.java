package solutions.alterego.androidbound.interfaces;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;

import solutions.alterego.androidbound.android.interfaces.IFontManager;
import solutions.alterego.androidbound.android.interfaces.INeedsFontManager;
import solutions.alterego.androidbound.android.interfaces.INeedsImageLoader;
import solutions.alterego.androidbound.converters.interfaces.IValueConverterRegistry;
import solutions.alterego.androidbound.resources.interfaces.IResourceRegistry;
import solutions.alterego.androidbound.android.interfaces.IViewResolver;


public interface IViewBinder extends IResourceRegistry, IValueConverterRegistry, IDisposable, INeedsImageLoader, IHasLogger, INeedsFontManager,
        IHasDebugMode {

    void registerViewResolver(IViewResolver resolver);

    void unregisterViewResolver(IViewResolver resolver);

    Context getContext();

    void setContext(Context context);

    IFontManager getFontManager();

    IViewBindingEngine getViewBindingEngine();

    View inflate(Context context, Object source, int layoutResID, ViewGroup viewGroup);

    View inflate(Context context, Object source, int layoutResID, ViewGroup viewGroup, boolean attachToRoot);

    View inflate(Context context, Object source, int layoutResID, ViewGroup viewGroup, IViewResolver additionalResolver);

    void disposeOf(Context context);

}
