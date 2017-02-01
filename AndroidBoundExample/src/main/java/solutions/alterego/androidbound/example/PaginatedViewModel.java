package solutions.alterego.androidbound.example;

import android.app.Activity;
import android.util.Log;

import java.util.ArrayList;
import java.util.List;

import lombok.Data;
import lombok.Getter;
import lombok.experimental.Accessors;
import solutions.alterego.androidbound.ViewModel;
import solutions.alterego.androidbound.android.adapters.PageDescriptor;
import solutions.alterego.androidbound.interfaces.ILogger;

@Accessors(prefix = "m")
public class PaginatedViewModel extends ViewModel {

    @Data
    @Accessors(prefix = "m")
    public static class RecyclerViewItem {

        private String mName;

        private String mImageUrl;

        public RecyclerViewItem(String name, String imageUrl) {
            mName = name;
            mImageUrl = imageUrl;
        }
    }

    @Getter
    private PageDescriptor mPageDescriptor;

    @Getter
    public PageDescriptor mLoadNextPage;

    @Getter
    private List<RecyclerViewItem> mDataItems;

    public PaginatedViewModel(Activity activity, ILogger logger) {
        setLogger(logger);
        setParentActivity(activity);
        mPageDescriptor = new PageDescriptor.PageDescriptorBuilder()
                .setPageSize(10)
                .setStartPage(1)
                .setThreshold(2).build();
    }

    private void createItems(int page) {
        int size = mDataItems == null ? 0 : mDataItems.size();
        mDataItems = new ArrayList<RecyclerViewItem>();
        for (int i = 0; i < mPageDescriptor.getPageSize(); i++) {
            mDataItems.add(new RecyclerViewItem("item " + (((page - 1) * size) + i), ""));
        }
        raisePropertyChanged("DataItems");
    }

    public void addDataItems(List<RecyclerViewItem> items) {
        Log.e("TEST", "items  " + items);
    }


    public void setLoadNextPage(PageDescriptor page) {
        createItems(page.getCurrentPage());
    }
}
