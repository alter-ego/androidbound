package solutions.alterego.androidbound.example.support.nestedrvs.viewmodel;

public class RecyclerViewItem {

    private String mName;

    private String mImageUrl;

    public RecyclerViewItem(String name, String imageUrl) {
        mName = name;
        mImageUrl = imageUrl;
    }

    public String getName() {
        return mName;
    }

    public String getImageUrl() {
        return mImageUrl;
    }

    public void setName(String name) {
        mName = name;
    }

    public void setImageUrl(String imageUrl) {
        mImageUrl = imageUrl;
    }

    public boolean equals(Object o) {
        if (o == this) {
            return true;
        }
        if (!(o instanceof RecyclerViewItem)) {
            return false;
        }
        final RecyclerViewItem other = (RecyclerViewItem) o;
        if (!other.canEqual((Object) this)) {
            return false;
        }
        final Object this$mName = this.getName();
        final Object other$mName = other.getName();
        if (this$mName == null ? other$mName != null : !this$mName.equals(other$mName)) {
            return false;
        }
        final Object this$mImageUrl = this.getImageUrl();
        final Object other$mImageUrl = other.getImageUrl();
        if (this$mImageUrl == null ? other$mImageUrl != null : !this$mImageUrl.equals(other$mImageUrl)) {
            return false;
        }
        return true;
    }

    public int hashCode() {
        final int PRIME = 59;
        int result = 1;
        final Object $mName = this.getName();
        result = result * PRIME + ($mName == null ? 43 : $mName.hashCode());
        final Object $mImageUrl = this.getImageUrl();
        result = result * PRIME + ($mImageUrl == null ? 43 : $mImageUrl.hashCode());
        return result;
    }

    protected boolean canEqual(Object other) {
        return other instanceof RecyclerViewItem;
    }

    public String toString() {
        return "MainNestedViewModel.RecyclerViewItem(mName=" + this.getName() + ", mImageUrl=" + this.getImageUrl() + ")";
    }
}
