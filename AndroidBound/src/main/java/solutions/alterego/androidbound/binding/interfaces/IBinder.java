package solutions.alterego.androidbound.binding.interfaces;

import android.view.View;

import java.util.List;

import solutions.alterego.androidbound.interfaces.INeedsLogger;

public interface IBinder extends INeedsLogger {

    List<IBindingAssociationEngine> bind(Object source, View target, String bindingSpecifications);
}
