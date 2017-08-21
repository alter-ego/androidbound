package solutions.alterego.androidbound.binding.data;

import lombok.Data;
import lombok.experimental.Accessors;
import solutions.alterego.androidbound.converters.interfaces.IValueConverter;

@Data
@Accessors(prefix = "m")
public class BindingSpecification {

    private String mTarget;

    private String mSource;

    private IValueConverter mValueConverter;

    private Object mConverterParameter;

    private BindingMode mMode;

    private Object mFallbackValue;
}