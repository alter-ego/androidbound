package solutions.alterego.androidbound.android.adapters;

import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;

@Accessors(prefix = "m")
public class PageDescriptor {

    @Getter
    private int mStartPage = 1;

    @Getter
    private int mPageSize = 20;

    @Getter
    private int mThreshold = 5;

    @Getter
    @Setter
    private int mCurrentPage;

    public static class PageDescriptorBuilder {

        private int mStartPage = 1;

        private int mPageSize = 20;

        private int mThreshold = 5;

        public PageDescriptorBuilder setStartPage(int startPage) {
            mStartPage = startPage;
            return this;
        }

        public PageDescriptorBuilder setPageSize(int pageSize) {
            mPageSize = pageSize;
            return this;
        }

        public PageDescriptorBuilder setThreshold(int threshold) {
            mThreshold = threshold;
            return this;
        }

        public PageDescriptor build() {
            return new PageDescriptor(this);
        }
    }

    private PageDescriptor(PageDescriptorBuilder builder) {
        mStartPage = builder.mStartPage;
        mPageSize = builder.mPageSize;
        mThreshold = builder.mThreshold;
        mCurrentPage = mStartPage;
    }
}