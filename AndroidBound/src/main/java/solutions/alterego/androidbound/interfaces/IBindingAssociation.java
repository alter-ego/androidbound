package solutions.alterego.androidbound.interfaces;

public interface IBindingAssociation extends INeedsLogger, IDisposable {

    public abstract Object getDataContext();

    public abstract void setDataContext(Object value);
}