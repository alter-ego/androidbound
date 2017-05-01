package solutions.alterego.androidbound.example.fragment.viewmodel;

import java.util.ArrayList;
import java.util.List;

import lombok.Getter;
import lombok.experimental.Accessors;
import solutions.alterego.androidbound.ViewModel;
import solutions.alterego.androidbound.example.RecyclerViewWithObjectsActivityViewModel;

@Accessors(prefix = "m")
public class FragmentViewModel extends ViewModel {

    @Getter
    private List<RecyclerViewWithObjectsActivityViewModel.ListViewItem> mExampleListLinear
            = new ArrayList<RecyclerViewWithObjectsActivityViewModel.ListViewItem>();

    public FragmentViewModel() {
        for (int i = 0; i < 20; i++) {
            mExampleListLinear.add(new RecyclerViewWithObjectsActivityViewModel.ListViewItem(Integer.toString(i)));
        }
        raisePropertyChanged("ExampleListLinear");
    }
}
