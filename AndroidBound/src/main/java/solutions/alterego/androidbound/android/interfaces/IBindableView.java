package solutions.alterego.androidbound.android.interfaces;

import solutions.alterego.androidbound.interfaces.IViewBinder;

public interface IBindableView {

    public abstract IViewBinder getViewBinder();

    public abstract void setViewBinder(IViewBinder viewBinder);
}
