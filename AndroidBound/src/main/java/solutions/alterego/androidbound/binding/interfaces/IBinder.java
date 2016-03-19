package solutions.alterego.androidbound.binding.interfaces;

import java.util.List;

import solutions.alterego.androidbound.binding.interfaces.IBindingAssociationEngine;
import solutions.alterego.androidbound.interfaces.INeedsLogger;


public interface IBinder extends INeedsLogger {

    List<IBindingAssociationEngine> bind(Object source, Object target, String bindingSpecifications);
}
