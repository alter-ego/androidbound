package solutions.alterego.androidbound.interfaces;

import java.util.List;


public interface IBindingFactory extends INeedsLogger {
	IBinding create(Object source, String combinedPath, boolean needChangesIfPossible);
	IBinding create(Object source, List<String> pathTokens, boolean needChangesIfPossible);
}
