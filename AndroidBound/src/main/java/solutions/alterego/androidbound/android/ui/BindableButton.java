package solutions.alterego.androidbound.android.ui;

import android.content.Context;
import android.content.res.ColorStateList;
import android.util.AttributeSet;
import android.view.View;
import android.widget.Button;

import rx.Observable;
import solutions.alterego.androidbound.binding.interfaces.INotifyPropertyChanged;
import solutions.alterego.androidbound.interfaces.ICommand;
import solutions.alterego.androidbound.interfaces.IDisposable;

public class BindableButton extends Button implements IDisposable, INotifyPropertyChanged {

    private BindableViewDelegate mDelegate;

    public BindableButton(Context context, AttributeSet attrs) {
        super(context, attrs);
        mDelegate = createDelegate(this);
    }

    public BindableButton(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
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

    public int getBackgroundColor() {
        return mDelegate.getBackgroundColor();
    }

    public void setBackgroundColor(int color) {
        mDelegate.setBackgroundColor(color);
        super.setBackgroundColor(color);
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

    public ColorStateList getTextColor() {
        return super.getTextColors();
    }

    public void setTextColor(int color) {
        super.setTextColor(color);
    }

    public void setTextColorState(ColorStateList colors) {
        super.setTextColor(colors);
    }

    public ColorStateList getTextColorState() {
        return super.getTextColors();
    }

}
