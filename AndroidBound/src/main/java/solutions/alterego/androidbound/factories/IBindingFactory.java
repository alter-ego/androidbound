package solutions.alterego.androidbound.factories;

import java.util.List;

import solutions.alterego.androidbound.binding.interfaces.IBinding;
import solutions.alterego.androidbound.interfaces.INeedsLogger;


public interface IBindingFactory extends INeedsLogger {

    IBinding create(Object source, String combinedPath, boolean needChangesIfPossible);

    IBinding create(Object source, List<String> pathTokens, boolean needChangesIfPossible);
}
