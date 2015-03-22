
package solutions.alterego.androidbound.interfaces;

public interface IBindableView {
    public abstract void setViewBinder(IViewBinder viewBinder);

    public abstract IViewBinder getViewBinder();
}
