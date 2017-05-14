package solutions.alterego.androidbound.android.ui;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.StateListDrawable;
import android.os.Build;
import android.support.annotation.Nullable;
import android.support.v4.widget.SwipeRefreshLayout;
import android.util.AttributeSet;
import android.view.View;

import rx.Observable;
import solutions.alterego.androidbound.binding.interfaces.INotifyPropertyChanged;
import solutions.alterego.androidbound.interfaces.ICommand;

public class BindableSwipeRefreshLayout extends SwipeRefreshLayout implements INotifyPropertyChanged, SwipeRefreshLayout.OnRefreshListener {

    private BindableViewDelegate mDelegate;

    ICommand onRefresh = ICommand.empty;

    public BindableSwipeRefreshLayout(Context context) {
        this(context, null);
    }

    public BindableSwipeRefreshLayout(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        mDelegate = createDelegate(this);
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
        onRefresh = ICommand.empty;
    }

    /****** end of the delegated methods, to be copy/pasted in every bindable view ******/

    public ICommand getOnRefresh() {
        return onRefresh;
    }

    public void setOnRefresh(ICommand value) {
        if (value == null) {
            setEnabled(false);
            setOnRefreshListener(null);
            onRefresh = ICommand.empty;
            return;
        }
        setEnabled(true);
        setOnRefreshListener(this);
        onRefresh = value;
    }

    @Override
    public void onRefresh() {
        if (onRefresh.canExecute(null)) {
            onRefresh.execute(null);
        }
    }
}
