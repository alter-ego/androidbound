package solutions.alterego.androidbound.binds;

import solutions.alterego.androidbound.interfaces.IValueConverter;

import lombok.Data;
import lombok.experimental.Accessors;

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