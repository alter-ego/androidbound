package solutions.alterego.androidbound.example.support.fragment.viewmodel;

import java.util.ArrayList;
import java.util.List;

import lombok.Getter;
import lombok.experimental.Accessors;
import solutions.alterego.androidbound.ViewModel;
import solutions.alterego.androidbound.example.support.listviewitems.ListViewItem;

@Accessors(prefix = "m")
public class FragmentViewModel extends ViewModel {

    @Getter
    private List<ListViewItem> mExampleListLinear = new ArrayList<ListViewItem>();

    public FragmentViewModel() {
        for (int i = 0; i < 20; i++) {
            mExampleListLinear.add(new ListViewItem(Integer.toString(i)));
        }
        raisePropertyChanged("ExampleListLinear");
    }
}
