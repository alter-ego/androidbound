package solutions.alterego.androidbound.android.ui;

import android.annotation.TargetApi;
import android.content.Context;
import android.graphics.drawable.StateListDrawable;
import android.os.Build;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.View;

import rx.Observable;
import solutions.alterego.androidbound.binding.interfaces.INotifyPropertyChanged;
import solutions.alterego.androidbound.interfaces.ICommand;
import solutions.alterego.androidbound.interfaces.IDisposable;

public class BindableView extends View implements IDisposable, INotifyPropertyChanged {

    private BindableViewDelegate mDelegate;

    public BindableView(Context context) {
        this(context, null, 0);
    }

    public BindableView(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public BindableView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        mDelegate = getDelegate(this);
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    public BindableView(Context context, @Nullable AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        mDelegate = getDelegate(this);
    }

    /****** beginning of the delegated methods, to be copy/pasted in every bindable view ******/

    protected BindableViewDelegate getDelegate(View view) {
        return new BindableViewDelegate(view);
    }

    public ICommand getClick() {
        return mDelegate.getClick();
    }

    public void setClick(ICommand value) {
        mDelegate.setClick(value);
    }

    public int getBackgroundColor() {
        return mDelegate.getBackgroundColor();
    }

    public void setBackgroundColor(int color) {
        mDelegate.setBackgroundColor(color);
        super.setBackgroundColor(color);
    }

    public void setBackgroundDrawableState(StateListDrawable colors) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
            super.setBackground(colors);
            mDelegate.setBackgroundDrawableState(colors);
        }
    }

    public StateListDrawable getBackgroundDrawableState() {
        return mDelegate.getBackgroundDrawableState();
    }

    public int getBackgroundResource() {
        return 0;
    }

    public void setBackgroundResource(int res) {
        super.setBackgroundResource(res);
    }

    @Override
    public Observable<String> onPropertyChanged() {
        return mDelegate.onPropertyChanged();
    }

    @Override
    public void dispose() {
        mDelegate.dispose();
    }

    /****** end of the delegated methods, to be copy/pasted in every bindable view ******/
}
