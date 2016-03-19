package solutions.alterego.androidbound.binding.interfaces;

import solutions.alterego.androidbound.interfaces.IDisposable;
import solutions.alterego.androidbound.interfaces.INeedsLogger;

public interface IBindingAssociationEngine extends INeedsLogger, IDisposable {

    public abstract Object getDataContext();

    public abstract void setDataContext(Object value);
}