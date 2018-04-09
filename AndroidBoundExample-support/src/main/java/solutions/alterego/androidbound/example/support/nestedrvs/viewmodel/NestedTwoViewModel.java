package solutions.alterego.androidbound.example.support.nestedrvs.viewmodel;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class NestedTwoViewModel {

    private String mName;

    private String mImageUrl;

    private List<RecyclerViewItem> mNestedDataSet;

    public NestedTwoViewModel(String name, String imageUrl) {
        mNestedDataSet = new ArrayList<RecyclerViewItem>();

        int numberOfItems = new Random().nextInt(3);
        for (int i = 0; i < numberOfItems; i++) {
            mNestedDataSet.add(new RecyclerViewItem("nested item " + i,
                    "https://www.google.co.uk/images/branding/googlelogo/1x/googlelogo_color_272x92dp.png"));
        }

        mName = name + "\nnested items = " + numberOfItems;
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

    public boolean getIsRtl() {
        return false;
    }

    public List<RecyclerViewItem> getNestedDataSet() {
        return mNestedDataSet;
    }

    public String toString() {
        return "NestedTwoViewModel(mName=" + this.getName() + ", mImageUrl=" + this.getImageUrl() + ")";
    }
}
