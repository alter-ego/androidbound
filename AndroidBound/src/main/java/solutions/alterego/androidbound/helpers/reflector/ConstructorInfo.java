package solutions.alterego.androidbound.helpers.reflector;

import java.lang.reflect.Constructor;

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

    public Constructor<?> getConstructorOriginal() {
        return mConstructorOriginal;
    }

    public String getConstructorName() {
        return mConstructorName;
    }

    public int getConstructorParameterCount() {
        return mConstructorParameterCount;
    }

    public Class<?>[] getConstructorParameterTypes() {
        return mConstructorParameterTypes;
    }

    public boolean equals(Object o) {
        if (o == this) {
            return true;
        }
        if (!(o instanceof ConstructorInfo)) {
            return false;
        }
        final ConstructorInfo other = (ConstructorInfo) o;
        if (!other.canEqual((Object) this)) {
            return false;
        }
        final Object this$mConstructorOriginal = this.getConstructorOriginal();
        final Object other$mConstructorOriginal = other.getConstructorOriginal();
        if (this$mConstructorOriginal == null ? other$mConstructorOriginal != null : !this$mConstructorOriginal.equals(other$mConstructorOriginal)) {
            return false;
        }
        final Object this$mConstructorName = this.getConstructorName();
        final Object other$mConstructorName = other.getConstructorName();
        if (this$mConstructorName == null ? other$mConstructorName != null : !this$mConstructorName.equals(other$mConstructorName)) {
            return false;
        }
        if (this.getConstructorParameterCount() != other.getConstructorParameterCount()) {
            return false;
        }
        if (!java.util.Arrays.deepEquals(this.getConstructorParameterTypes(), other.getConstructorParameterTypes())) {
            return false;
        }
        return true;
    }

    public int hashCode() {
        final int PRIME = 59;
        int result = 1;
        final Object $mConstructorOriginal = this.getConstructorOriginal();
        result = result * PRIME + ($mConstructorOriginal == null ? 43 : $mConstructorOriginal.hashCode());
        final Object $mConstructorName = this.getConstructorName();
        result = result * PRIME + ($mConstructorName == null ? 43 : $mConstructorName.hashCode());
        result = result * PRIME + this.getConstructorParameterCount();
        result = result * PRIME + java.util.Arrays.deepHashCode(this.getConstructorParameterTypes());
        return result;
    }

    protected boolean canEqual(Object other) {
        return other instanceof ConstructorInfo;
    }

    public String toString() {
        return "ConstructorInfo(mConstructorOriginal=" + getConstructorOriginal() + ", mConstructorName=" + getConstructorName()
                + ", mConstructorParameterCount=" + getConstructorParameterCount() + ", mConstructorParameterTypes=" + java.util.Arrays
                .deepToString(getConstructorParameterTypes()) + ")";
    }
}
