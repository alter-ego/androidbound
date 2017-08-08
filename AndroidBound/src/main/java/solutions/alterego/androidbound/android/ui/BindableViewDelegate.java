package solutions.alterego.androidbound.android.ui;

import android.graphics.drawable.StateListDrawable;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;

import io.reactivex.Observable;
import io.reactivex.subjects.PublishSubject;
import lombok.Getter;
import lombok.experimental.Accessors;
import solutions.alterego.androidbound.binding.interfaces.INotifyPropertyChanged;
import solutions.alterego.androidbound.interfaces.ICommand;

@Accessors(prefix = "m")
public class BindableViewDelegate implements INotifyPropertyChanged, View.OnClickListener, View.OnLongClickListener {

    private View mOriginalView;

    @Getter
    private boolean mDisposed = false;

    private PublishSubject<String> mPropertyChanged = PublishSubject.create();

    @Getter
    private ICommand mClick = ICommand.empty;

    @Getter
    private ICommand mLongClick = ICommand.empty;

    @Getter
    private int mBackgroundColor = 0;

    private StateListDrawable mBackgroundDrawableState;

    public BindableViewDelegate(View view) {
        mOriginalView = view;
    }

    public void onSizeChanged(int w, int h, int oldw, int oldh) {
        if (mDisposed || mPropertyChanged == null || mPropertyChanged.hasComplete()) {
            return;
        }

        if (w != oldw) {
            mPropertyChanged.onNext("Width");
        }

        if (h != oldh) {
            mPropertyChanged.onNext("Height");
        }
    }

    public void setWidth(int width) {
        if (width == mOriginalView.getWidth()) {
            return;
        }

        ViewGroup.LayoutParams p = mOriginalView.getLayoutParams();
        p.width = width;
        mOriginalView.setLayoutParams(p);
    }

    public void setHeight(int height) {
        if (height == mOriginalView.getHeight()) {
            return;
        }

        ViewGroup.LayoutParams p = mOriginalView.getLayoutParams();
        p.height = height;
        mOriginalView.setLayoutParams(p);
    }

    public void setClick(ICommand value) {
        if (value == null || value == ICommand.empty || mDisposed) {
            mOriginalView.setClickable(false);
            setClickListener(false);
            mClick = ICommand.empty;
            return;
        }

        mOriginalView.setClickable(true);
        setClickListener(true);
        mClick = value;
    }

    private void setClickListener(boolean setListener) {
        if (mOriginalView instanceof AbsListView) {
            //do nothing, handled by original view
        } else {
            mOriginalView.setOnClickListener(setListener ? this : null);
        }
    }

    public void setLongClick(ICommand value) {
        if (value == null || value == ICommand.empty || mDisposed) {
            mOriginalView.setClickable(false);
            setLongClickListener(false);
            mLongClick = ICommand.empty;
            return;
        }

        mOriginalView.setClickable(true);
        setLongClickListener(true);
        mLongClick = value;
    }

    private void setLongClickListener(boolean setListener) {
        if (mOriginalView instanceof AbsListView) {
            //do nothing, handled by original view
        } else {
            mOriginalView.setOnLongClickListener(setListener ? this : null);
        }
    }

    public void setBackgroundColor(int color) {
        mBackgroundColor = color;
    }

    public void setBackgroundDrawableState(StateListDrawable colors) {
        mBackgroundDrawableState = colors;
    }

    public StateListDrawable getBackgroundDrawableState() {
        return mBackgroundDrawableState;
    }

    public void notifyPropertyChanged(String property) {
        if (!mDisposed && !mPropertyChanged.hasComplete() && property != null) {
            mPropertyChanged.onNext(property);
        }
    }

    @Override
    public Observable<String> onPropertyChanged() {
        return mPropertyChanged;
    }

    @Override
    public void dispose() {
        if (mDisposed) {
            return;
        }

        mDisposed = true;

        if (mPropertyChanged != null && !mPropertyChanged.hasComplete() && mPropertyChanged.hasObservers()) {
            mPropertyChanged.onComplete();
            mPropertyChanged = null;
        }

        setClick(null);
        setLongClick(null);
        mOriginalView = null;
    }

    @Override
    public void onClick(View v) {
        if (!mDisposed && mClick.canExecute(null)) {
            mClick.execute(null);
        }
    }

    @Override
    public boolean onLongClick(View arg0) {
        if (!mDisposed && mLongClick.canExecute(null)) {
            mLongClick.execute(null);
            return true;
        } else {
            return false;
        }
    }

}
