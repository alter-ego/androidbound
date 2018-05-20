package solutions.alterego.androidbound.android.adapters;

public class PageDescriptor {

    private int mStartPage = 1;

    private int mPageSize = 20;

    private int mThreshold = 5;

    private int mCurrentPage;

    public int getStartPage() {
        return mStartPage;
    }

    public int getPageSize() {
        return mPageSize;
    }

    public int getThreshold() {
        return mThreshold;
    }

    public int getCurrentPage() {
        return mCurrentPage;
    }

    public void setCurrentPage(int currentPage) {
        mCurrentPage = currentPage;
    }

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