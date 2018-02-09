package solutions.alterego.androidbound.binding.data;

public class BindingRequest {

    private Object mSource;

    private Object mTarget;

    private BindingSpecification mSpecification;

    public BindingRequest() {
    }

    public Object getSource() {
        return this.mSource;
    }

    public Object getTarget() {
        return this.mTarget;
    }

    public BindingSpecification getSpecification() {
        return this.mSpecification;
    }

    public void setSource(Object mSource) {
        this.mSource = mSource;
    }

    public void setTarget(Object mTarget) {
        this.mTarget = mTarget;
    }

    public void setSpecification(BindingSpecification mSpecification) {
        this.mSpecification = mSpecification;
    }

    public boolean equals(Object o) {
        if (o == this) {
            return true;
        }
        if (!(o instanceof BindingRequest)) {
            return false;
        }
        final BindingRequest other = (BindingRequest) o;
        if (!other.canEqual((Object) this)) {
            return false;
        }
        final Object this$mSource = this.getSource();
        final Object other$mSource = other.getSource();
        if (this$mSource == null ? other$mSource != null : !this$mSource.equals(other$mSource)) {
            return false;
        }
        final Object this$mTarget = this.getTarget();
        final Object other$mTarget = other.getTarget();
        if (this$mTarget == null ? other$mTarget != null : !this$mTarget.equals(other$mTarget)) {
            return false;
        }
        final Object this$mSpecification = this.getSpecification();
        final Object other$mSpecification = other.getSpecification();
        if (this$mSpecification == null ? other$mSpecification != null : !this$mSpecification.equals(other$mSpecification)) {
            return false;
        }
        return true;
    }

    public int hashCode() {
        final int PRIME = 59;
        int result = 1;
        final Object $mSource = this.getSource();
        result = result * PRIME + ($mSource == null ? 43 : $mSource.hashCode());
        final Object $mTarget = this.getTarget();
        result = result * PRIME + ($mTarget == null ? 43 : $mTarget.hashCode());
        final Object $mSpecification = this.getSpecification();
        result = result * PRIME + ($mSpecification == null ? 43 : $mSpecification.hashCode());
        return result;
    }

    protected boolean canEqual(Object other) {
        return other instanceof BindingRequest;
    }

    public String toString() {
        return "BindingRequest(mSource=" + this.getSource() + ", mTarget=" + this.getTarget() + ", mSpecification=" + this.getSpecification() + ")";
    }
}
