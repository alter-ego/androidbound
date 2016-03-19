package solutions.alterego.androidbound.binds;

import lombok.Data;
import lombok.experimental.Accessors;
import solutions.alterego.androidbound.interfaces.IValueConverter;

@Data
@Accessors(prefix = "m")
public class BindingSpecification {

    private String mTarget;

    private String mPath;

    private IValueConverter mValueConverter;

    private Object mConverterParameter;

    private BindingMode mMode;

    private Object mFallbackValue;
}