package solutions.alterego.androidbound.android.interfaces;

import solutions.alterego.androidbound.interfaces.IViewBinder;

public interface IBindableView {

    IViewBinder getViewBinder();

    void setViewBinder(IViewBinder viewBinder);
}
