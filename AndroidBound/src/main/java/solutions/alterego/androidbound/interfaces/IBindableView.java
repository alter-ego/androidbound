package solutions.alterego.androidbound.interfaces;

public interface IBindableView {

    public abstract IViewBinder getViewBinder();

    public abstract void setViewBinder(IViewBinder viewBinder);
}
