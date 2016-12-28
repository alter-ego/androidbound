package solutions.alterego.androidbound.binding.interfaces;

import solutions.alterego.androidbound.interfaces.IDisposable;
import solutions.alterego.androidbound.interfaces.INeedsLogger;

public interface IBindingAssociationEngine extends INeedsLogger, IDisposable {

    Object getDataContext();

    void setDataContext(Object value);
}