package solutions.alterego.androidbound.example.support.nestedrvs.viewmodel;


import java.util.ArrayList;
import java.util.List;

import lombok.Getter;
import lombok.experimental.Accessors;
import solutions.alterego.androidbound.ViewModel;

@Accessors(prefix = "m")
public class NestedViewModel extends ViewModel {

    @Getter
    private List<MainNestedViewModel.RecyclerViewItem> mNestedDataSet;

    public NestedViewModel() {
        mNestedDataSet = new ArrayList<MainNestedViewModel.RecyclerViewItem>();
        for (int i = 0; i < 20; i++) {
            mNestedDataSet.add(new MainNestedViewModel.RecyclerViewItem("nested item " + i,
                    "https://www.google.co.uk/images/branding/googlelogo/1x/googlelogo_color_272x92dp.png"));
        }
    }

    public boolean getIsRtl() {
        return true;
    }

}
