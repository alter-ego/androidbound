package solutions.alterego.androidbound.example;

import android.app.Activity;

import java.util.ArrayList;
import java.util.List;

import lombok.Data;
import lombok.Getter;
import lombok.experimental.Accessors;
import solutions.alterego.androidbound.ViewModel;
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
    private List<RecyclerViewItem> mDataItems;

    public PaginatedViewModel(Activity activity, ILogger logger) {
        setLogger(logger);
        setParentActivity(activity);
        createItems();
    }

    private void createItems() {
        int size = mDataItems == null ? 0 : mDataItems.size();
        mDataItems = new ArrayList<RecyclerViewItem>();
        for (int i = 0; i < 20; i++) {
            mDataItems.add(new RecyclerViewItem("item " + (size + i), ""));
        }
        raisePropertyChanged("DataItems");
    }
}
