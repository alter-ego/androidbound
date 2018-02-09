package solutions.alterego.androidbound.helpers.reflector;

import java.lang.reflect.Field;

public class FieldInfo {

    private final Field mFieldOriginal;

    private final String mFieldName;

    private final Class<?> mFieldType;

    public FieldInfo(Field field) {
        mFieldOriginal = field;
        mFieldName = field.getName();
        mFieldType = field.getType();
    }

    public Field getFieldOriginal() {
        return this.mFieldOriginal;
    }

    public String getFieldName() {
        return this.mFieldName;
    }

    public Class<?> getFieldType() {
        return this.mFieldType;
    }

    public boolean equals(Object o) {
        if (o == this) {
            return true;
        }
        if (!(o instanceof FieldInfo)) {
            return false;
        }
        final FieldInfo other = (FieldInfo) o;
        if (!other.canEqual((Object) this)) {
            return false;
        }
        final Object this$mFieldOriginal = this.getFieldOriginal();
        final Object other$mFieldOriginal = other.getFieldOriginal();
        if (this$mFieldOriginal == null ? other$mFieldOriginal != null : !this$mFieldOriginal.equals(other$mFieldOriginal)) {
            return false;
        }
        final Object this$mFieldName = this.getFieldName();
        final Object other$mFieldName = other.getFieldName();
        if (this$mFieldName == null ? other$mFieldName != null : !this$mFieldName.equals(other$mFieldName)) {
            return false;
        }
        final Object this$mFieldType = this.getFieldType();
        final Object other$mFieldType = other.getFieldType();
        if (this$mFieldType == null ? other$mFieldType != null : !this$mFieldType.equals(other$mFieldType)) {
            return false;
        }
        return true;
    }

    public int hashCode() {
        final int PRIME = 59;
        int result = 1;
        final Object $mFieldOriginal = this.getFieldOriginal();
        result = result * PRIME + ($mFieldOriginal == null ? 43 : $mFieldOriginal.hashCode());
        final Object $mFieldName = this.getFieldName();
        result = result * PRIME + ($mFieldName == null ? 43 : $mFieldName.hashCode());
        final Object $mFieldType = this.getFieldType();
        result = result * PRIME + ($mFieldType == null ? 43 : $mFieldType.hashCode());
        return result;
    }

    protected boolean canEqual(Object other) {
        return other instanceof FieldInfo;
    }

    public String toString() {
        return "FieldInfo(mFieldOriginal=" + this.getFieldOriginal() + ", mFieldName=" + this.getFieldName() + ", mFieldType=" + this.getFieldType()
                + ")";
    }
}
