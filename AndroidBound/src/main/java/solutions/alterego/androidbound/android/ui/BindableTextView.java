package solutions.alterego.androidbound.android.ui;

import android.content.Context;
import android.content.res.ColorStateList;
import android.graphics.Typeface;
import android.util.AttributeSet;
import android.view.View;
import android.widget.TextView;

import rx.Observable;
import rx.subjects.PublishSubject;
import solutions.alterego.androidbound.interfaces.ICommand;
import solutions.alterego.androidbound.binding.interfaces.INotifyPropertyChanged;

public class BindableTextView extends TextView implements INotifyPropertyChanged, View.OnClickListener, View.OnLongClickListener {

    private PublishSubject<String> propertyChanged = PublishSubject.create();

    private boolean disposed;

    private ICommand onClick = ICommand.empty;

    private ICommand onLongClick = ICommand.empty;

    public BindableTextView(Context context) {
        super(context);
    }

    public BindableTextView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public BindableTextView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }

    public Typeface getTypeface() {
        return super.getTypeface();
    }

    public void setTypeface(Typeface font) {
        super.setTypeface(font);
        if (disposed || propertyChanged == null) {
            return;
        }
        propertyChanged.onNext("Typeface");
    }

    @Override
    public void dispose() {
        if (disposed) {
            return;
        }

        disposed = true;
        if (propertyChanged != null) {
            propertyChanged.onCompleted();
        }

        propertyChanged = null;
        onClick = null;
        onLongClick = null;
    }

    @Override
    public Observable<String> onPropertyChanged() {
        if (propertyChanged == null) {
            propertyChanged = PublishSubject.create();
        }

        return propertyChanged;
    }

    public void setTextColorState(ColorStateList colors) {
        super.setTextColor(colors);
    }

    public ColorStateList getTextColor() {
        return super.getTextColors();
    }

    public void setTextColor(int color) {
        super.setTextColor(color);
    }

    public int getBackgroundColor() {
        return 0;
    }

    public void setBackgroundColor(int color) {
        super.setBackgroundColor(color);
    }

    public ICommand getClick() {
        return onClick;
    }

    public void setClick(ICommand value) {
        if (value == null) {
            onClick = ICommand.empty;
            setOnClickListener(null);
            return;
        }
        setOnClickListener(this);
        onClick = value;
    }

    @Override
    public void onClick(View v) {
        if (onClick.canExecute(null)) {
            onClick.execute(null);
        }
    }

    public ICommand getLongClick() {
        return onLongClick;
    }

    public void setLongClick(ICommand value) {
        if (value == null) {
            setClickable(false);
            setOnLongClickListener(null);
            onLongClick = ICommand.empty;
            return;
        }
        setClickable(true);
        setOnLongClickListener(this);
        onLongClick = value;
    }

    @Override
    public boolean onLongClick(View arg0) {

        if (onLongClick.canExecute(null)) {
            onLongClick.execute(null);
            return true;
        } else {
            return false;
        }
    }

}