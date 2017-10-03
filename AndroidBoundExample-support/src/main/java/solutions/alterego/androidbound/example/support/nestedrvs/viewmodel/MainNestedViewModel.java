package solutions.alterego.androidbound.example.support.nestedrvs.viewmodel;


import java.util.ArrayList;
import java.util.List;

import lombok.experimental.Accessors;
import solutions.alterego.androidbound.ViewModel;

public class MainNestedViewModel extends ViewModel {

    public List<Object> getDataItems() {
        return this.mDataItems;
    }

    @Accessors(prefix = "m")
    public static class RecyclerViewItem {

        private String mName;

        private String mImageUrl;

        public RecyclerViewItem(String name, String imageUrl) {
            mName = name;
            mImageUrl = imageUrl;
        }

        public String getName() {
            return this.mName;
        }

        public String getImageUrl() {
            return this.mImageUrl;
        }

        public void setName(String mName) {
            this.mName = mName;
        }

        public void setImageUrl(String mImageUrl) {
            this.mImageUrl = mImageUrl;
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

    private List<Object> mDataItems;

    public MainNestedViewModel() {
        mDataItems = new ArrayList<Object>();
        mDataItems.add(new NestedViewModel());
        for (int i = 0; i < 50; i++) {
            mDataItems.add(new RecyclerViewItem("item " + i, "http://icons.iconarchive.com/icons/danleech/simple/128/android-icon.png"));
        }
    }

}
