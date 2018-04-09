package solutions.alterego.androidbound.example.support.nestedrvs.viewmodel;


import android.os.Build;
import android.view.View;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import solutions.alterego.androidbound.ViewModel;

import static android.text.TextUtils.getLayoutDirectionFromLocale;

public class NestedViewModel extends ViewModel {

    private List<RecyclerViewItem> mNestedDataSet;

    public NestedViewModel() {
        mNestedDataSet = new ArrayList<RecyclerViewItem>();
        for (int i = 0; i < 20; i++) {
            mNestedDataSet.add(new RecyclerViewItem("nested item " + i,
                    "https://www.google.co.uk/images/branding/googlelogo/1x/googlelogo_color_272x92dp.png"));
        }
    }

    public boolean getIsRtl() {
        if (Build.VERSION.SDK_INT > Build.VERSION_CODES.JELLY_BEAN) {
            return getLayoutDirectionFromLocale(Locale.getDefault()) == View.LAYOUT_DIRECTION_RTL;
        } else {
            return true;
        }
    }

    public List<RecyclerViewItem> getNestedDataSet() {
        return mNestedDataSet;
    }
}
