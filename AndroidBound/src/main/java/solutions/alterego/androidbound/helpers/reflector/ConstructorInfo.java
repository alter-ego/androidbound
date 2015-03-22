package solutions.alterego.androidbound.helpers.reflector;

import java.lang.reflect.Constructor;

import lombok.Data;
import lombok.experimental.Accessors;

@Data
@Accessors(prefix = "m")
public class ConstructorInfo {

    private final Constructor<?> mConstructorOriginal;

    private final String mConstructorName;

    private final int mConstructorParameterCount;

    private final Class<?>[] mConstructorParameterTypes;

    public ConstructorInfo(Constructor<?> constructor) {
        mConstructorOriginal = constructor;
        mConstructorName = constructor.getName();
        mConstructorParameterTypes = constructor.getParameterTypes();
        mConstructorParameterCount = constructor.getParameterTypes().length;
    }
}
