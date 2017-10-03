package solutions.alterego.androidbound.support.android.ui;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.StateListDrawable;
import android.os.Build;
import android.support.v7.widget.AppCompatSeekBar;
import android.util.AttributeSet;
import android.view.View;
import android.widget.SeekBar;

import io.reactivex.Observable;
import solutions.alterego.androidbound.android.ui.BindableViewDelegate;
import solutions.alterego.androidbound.binding.interfaces.INotifyPropertyChanged;
import solutions.alterego.androidbound.interfaces.ICommand;

public class BindableCompatSeekBar extends AppCompatSeekBar implements INotifyPropertyChanged {

    private ICommand progressTrackBegin = ICommand.empty;

    private ICommand progressTrackEnd = ICommand.empty;

    private ICommand progressTrackChanged = ICommand.empty;

    protected BindableViewDelegate mDelegate;

    public BindableCompatSeekBar(Context context) {
        this(context, null);
    }

    public BindableCompatSeekBar(Context context, AttributeSet attrs) {
        super(context, attrs);
        mDelegate = createDelegate(this);
        setOnSeekBarChangeListener(internalSeekbarListener);
    }

    public BindableCompatSeekBar(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        mDelegate = createDelegate(this);
        setOnSeekBarChangeListener(internalSeekbarListener);
    }

    /****** beginning of the delegated methods, to be copy/pasted in every bindable view ******/

    protected BindableViewDelegate createDelegate(View view) {
        return new BindableViewDelegate(view);
    }

    public ICommand getClick() {
        return mDelegate.getClick();
    }

    public void setClick(ICommand value) {
        mDelegate.setClick(value);
    }

    public ICommand getLongClick() {
        return mDelegate.getLongClick();
    }

    public void setLongClick(ICommand value) {
        mDelegate.setLongClick(value);
    }

    public int getBackgroundColor() {
        return mDelegate.getBackgroundColor();
    }

    public void setBackgroundColor(int color) {
        mDelegate.setBackgroundColor(color);
        super.setBackgroundColor(color);
    }

    public StateListDrawable getBackgroundDrawableState() {
        return mDelegate.getBackgroundDrawableState();
    }

    public void setBackgroundDrawableState(StateListDrawable colors) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
            super.setBackground(colors);
            mDelegate.setBackgroundDrawableState(colors);
        }
    }

    public int getBackgroundResource() {
        return 0;
    }

    public void setBackgroundResource(int res) {
        super.setBackgroundResource(res);
    }

    public int getBackgroundDrawable() {
        return 0;
    }

    public void setBackgroundDrawable(Drawable res) {
        super.setBackgroundDrawable(res);
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        mDelegate.onSizeChanged(w, h, oldw, oldh);
    }

    public void setWidth(int width) {
        mDelegate.setWidth(width);
    }

    public void setHeight(int height) {
        mDelegate.setHeight(height);
    }

    @Override
    public Observable<String> onPropertyChanged() {
        return mDelegate.onPropertyChanged();
    }

    @Override
    public void dispose() {
        mDelegate.dispose();

        progressTrackBegin = null;
        progressTrackChanged = null;
        progressTrackEnd = null;
        setOnSeekBarChangeListener(null);
    }

    /****** end of the delegated methods, to be copy/pasted in every bindable view ******/

    public ICommand getProgressTrackBegin() {
        return progressTrackBegin;
    }

    public void setProgressTrackBegin(ICommand cmd) {
        if (cmd == null) {
            progressTrackBegin = ICommand.empty;
            return;
        }
        progressTrackBegin = cmd;
    }

    public ICommand getProgressTrackEnd() {
        return progressTrackEnd;
    }

    public void setProgressTrackEnd(ICommand cmd) {
        if (cmd == null) {
            progressTrackEnd = ICommand.empty;
            return;
        }
        progressTrackEnd = cmd;
    }

    public ICommand getProgressTrackChanged() {
        return progressTrackChanged;
    }

    public void setProgressTrackChanged(ICommand cmd) {
        if (cmd == null) {
            progressTrackChanged = ICommand.empty;
        }
        progressTrackChanged = cmd;
    }

    OnSeekBarChangeListener internalSeekbarListener = new OnSeekBarChangeListener() {
        @Override
        public void onStopTrackingTouch(SeekBar seekBar) {
            if (!mDelegate.isDisposed() && progressTrackEnd.canExecute(null)) {
                progressTrackEnd.execute(null);
            }
        }

        @Override
        public void onStartTrackingTouch(SeekBar seekBar) {
            if (!mDelegate.isDisposed() && progressTrackBegin.canExecute(null)) {
                progressTrackBegin.execute(null);
            }
        }

        @Override
        public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
            if (!mDelegate.isDisposed() && progressTrackChanged.canExecute(null)) {
                progressTrackChanged.execute(null);
                mDelegate.notifyPropertyChanged("Progress");
            }
        }
    };

}
