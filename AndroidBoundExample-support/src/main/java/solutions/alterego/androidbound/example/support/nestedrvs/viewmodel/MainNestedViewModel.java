package solutions.alterego.androidbound.example.support.nestedrvs.viewmodel;


import java.util.ArrayList;
import java.util.List;

import lombok.Data;
import lombok.Getter;
import lombok.experimental.Accessors;
import solutions.alterego.androidbound.ViewModel;

@Accessors(prefix = "m")
public class MainNestedViewModel extends ViewModel {

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
    private List<Object> mDataItems;

    public MainNestedViewModel() {
        mDataItems = new ArrayList<Object>();
        mDataItems.add(new NestedViewModel());
        for (int i = 0; i < 50; i++) {
            mDataItems.add(new RecyclerViewItem("item " + i, "http://icons.iconarchive.com/icons/danleech/simple/128/android-icon.png"));
        }
    }

}
