package solutions.alterego.androidbound.interfaces;

import solutions.alterego.androidbound.zzzztoremove.reactive.IDisposable;

public interface IBindingAssociation extends INeedsLogger, IDisposable {

    public abstract Object getDataContext();

    public abstract void setDataContext(Object value);
}