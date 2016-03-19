package solutions.alterego.androidbound.example;

import com.alterego.advancedandroidlogger.interfaces.IAndroidLogger;

import lombok.Getter;
import lombok.experimental.Accessors;
import solutions.alterego.androidbound.ViewModel;
import solutions.alterego.androidbound.interfaces.IDisposable;

@Accessors(prefix="m")
public class MainActivityViewModel extends ViewModel implements IDisposable {

    @Getter
    private String mMainActivityTitle;

    public MainActivityViewModel(IAndroidLogger logger) {
        setLogger(logger);
        setMainActivityTitle("MainActivityTitle");
    }

    public void setMainActivityTitle(String title) {
        mMainActivityTitle = title;
        raisePropertyChanged("MainActivityTitle");
    }

    @Override
    public void dispose() {
        //do nothing
    }
}
