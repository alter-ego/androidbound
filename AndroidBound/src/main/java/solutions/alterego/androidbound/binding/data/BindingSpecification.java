package solutions.alterego.androidbound.binding.data;

import solutions.alterego.androidbound.converters.interfaces.IValueConverter;

public class BindingSpecification {

    private String mTarget;

    private String mSource;

    private IValueConverter mValueConverter;

    private Object mConverterParameter;

    private BindingMode mMode;

    private Object mFallbackValue;

    public BindingSpecification() {
    }

    public String getTarget() {
        return this.mTarget;
    }

    public String getSource() {
        return this.mSource;
    }

    public IValueConverter getValueConverter() {
        return this.mValueConverter;
    }

    public Object getConverterParameter() {
        return this.mConverterParameter;
    }

    public BindingMode getMode() {
        return this.mMode;
    }

    public Object getFallbackValue() {
        return this.mFallbackValue;
    }

    public void setTarget(String mTarget) {
        this.mTarget = mTarget;
    }

    public void setSource(String mSource) {
        this.mSource = mSource;
    }

    public void setValueConverter(IValueConverter mValueConverter) {
        this.mValueConverter = mValueConverter;
    }

    public void setConverterParameter(Object mConverterParameter) {
        this.mConverterParameter = mConverterParameter;
    }

    public void setMode(BindingMode mMode) {
        this.mMode = mMode;
    }

    public void setFallbackValue(Object mFallbackValue) {
        this.mFallbackValue = mFallbackValue;
    }

    public boolean equals(Object o) {
        if (o == this) {
            return true;
        }
        if (!(o instanceof BindingSpecification)) {
            return false;
        }
        final BindingSpecification other = (BindingSpecification) o;
        if (!other.canEqual((Object) this)) {
            return false;
        }
        final Object this$mTarget = this.getTarget();
        final Object other$mTarget = other.getTarget();
        if (this$mTarget == null ? other$mTarget != null : !this$mTarget.equals(other$mTarget)) {
            return false;
        }
        final Object this$mSource = this.getSource();
        final Object other$mSource = other.getSource();
        if (this$mSource == null ? other$mSource != null : !this$mSource.equals(other$mSource)) {
            return false;
        }
        final Object this$mValueConverter = this.getValueConverter();
        final Object other$mValueConverter = other.getValueConverter();
        if (this$mValueConverter == null ? other$mValueConverter != null : !this$mValueConverter.equals(other$mValueConverter)) {
            return false;
        }
        final Object this$mConverterParameter = this.getConverterParameter();
        final Object other$mConverterParameter = other.getConverterParameter();
        if (this$mConverterParameter == null ? other$mConverterParameter != null : !this$mConverterParameter.equals(other$mConverterParameter)) {
            return false;
        }
        final Object this$mMode = this.getMode();
        final Object other$mMode = other.getMode();
        if (this$mMode == null ? other$mMode != null : !this$mMode.equals(other$mMode)) {
            return false;
        }
        final Object this$mFallbackValue = this.getFallbackValue();
        final Object other$mFallbackValue = other.getFallbackValue();
        if (this$mFallbackValue == null ? other$mFallbackValue != null : !this$mFallbackValue.equals(other$mFallbackValue)) {
            return false;
        }
        return true;
    }

    public int hashCode() {
        final int PRIME = 59;
        int result = 1;
        final Object $mTarget = this.getTarget();
        result = result * PRIME + ($mTarget == null ? 43 : $mTarget.hashCode());
        final Object $mSource = this.getSource();
        result = result * PRIME + ($mSource == null ? 43 : $mSource.hashCode());
        final Object $mValueConverter = this.getValueConverter();
        result = result * PRIME + ($mValueConverter == null ? 43 : $mValueConverter.hashCode());
        final Object $mConverterParameter = this.getConverterParameter();
        result = result * PRIME + ($mConverterParameter == null ? 43 : $mConverterParameter.hashCode());
        final Object $mMode = this.getMode();
        result = result * PRIME + ($mMode == null ? 43 : $mMode.hashCode());
        final Object $mFallbackValue = this.getFallbackValue();
        result = result * PRIME + ($mFallbackValue == null ? 43 : $mFallbackValue.hashCode());
        return result;
    }

    protected boolean canEqual(Object other) {
        return other instanceof BindingSpecification;
    }

    public String toString() {
        return "BindingSpecification(mTarget=" + this.getTarget() + ", mSource=" + this.getSource() + ", mValueConverter=" + this.getValueConverter()
                + ", mConverterParameter=" + this.getConverterParameter() + ", mMode=" + this.getMode() + ", mFallbackValue=" + this
                .getFallbackValue() + ")";
    }
}