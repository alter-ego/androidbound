package solutions.alterego.androidbound.example;

import android.app.Activity;
import android.os.Handler;
import android.util.Log;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

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

    @Getter
    private List<RecyclerViewItem> mRemoveItems;

    private List<RecyclerViewItem> tmep = new ArrayList<RecyclerViewItem>();

    final Handler handler = new Handler();

    private Runnable mRunnable = (new Runnable() {
        @Override
        public void run() {
            setRemoveItems();
            handler.postDelayed(this, 5000);
        }
    });

    public PaginatedViewModel(Activity activity, ILogger logger) {
        setLogger(logger);
        setParentActivity(activity);
        mPageDescriptor = new PageDescriptor.PageDescriptorBuilder()
                .setPageSize(30)
                .setStartPage(1)
                .setThreshold(2).build();

     //   handler.postDelayed(mRunnable, 5000);
    }

    private void createItems(int page) {
        int size = mDataItems == null ? 0 : mDataItems.size();
        mDataItems = new ArrayList<RecyclerViewItem>();
        for (int i = 0; i < mPageDescriptor.getPageSize(); i++) {
            mDataItems.add(new RecyclerViewItem("item " + (((page - 1) * size) + i), ""));
        }
        tmep.addAll(mDataItems);
        raisePropertyChanged("DataItems");
    }

    public void addDataItems(List<RecyclerViewItem> items) {
        Log.e("TEST", "items  " + items);
    }

    public void setRemoveItems() {
        if (mDataItems == null || mDataItems.isEmpty()) {
            handler.removeCallbacks(mRunnable);
            return;
        }
        Random random = new Random();
        int rand = random.nextInt(Math.min(5, tmep.size()));

        mRemoveItems = new ArrayList<RecyclerViewItem>();
        for (int i = 0; i < rand; i++) {
            mRemoveItems.add(tmep.remove(random.nextInt(rand)));
        }
        raisePropertyChanged("RemoveItems");
    }

    public void setLoadNextPage(PageDescriptor page) {
        if (page != null) {
            createItems(page.getCurrentPage());
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        handler.removeCallbacks(mRunnable);
    }
}
