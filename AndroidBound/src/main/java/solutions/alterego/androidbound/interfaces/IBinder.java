package solutions.alterego.androidbound.interfaces;

import java.util.List;


public interface IBinder extends INeedsLogger {
	List<IBindingAssociation> bind(Object source, Object target, String bindingSpecifications);
}
