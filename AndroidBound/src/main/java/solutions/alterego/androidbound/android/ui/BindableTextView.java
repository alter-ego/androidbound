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

public class BindableTextView extends TextView implements INotifyPropertyChanged, View.OnClickListener {

    private PublishSubject<String> propertyChanged = PublishSubject.create();

    private boolean disposed;

    private ICommand onClick = ICommand.empty;

    public BindableTextView(Context context) {
        super(context);
        setOnClickListener(this);
    }

    public BindableTextView(Context context, AttributeSet attrs) {
        super(context, attrs);
        setOnClickListener(this);
    }

    public BindableTextView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        setOnClickListener(this);
    }

    public Typeface getTypeface() {
        return super.getTypeface();
    }

    public void setTypeface(Typeface font) {
        super.setTypeface(font);
        if (this.disposed || this.propertyChanged == null) {
            return;
        }
        this.propertyChanged.onNext("Typeface");
    }

    @Override
    public void dispose() {
        if (this.disposed) {
            return;
        }

        this.disposed = true;
        if (this.propertyChanged != null) {
            this.propertyChanged.onCompleted();
        }

        this.propertyChanged = null;

    }

    @Override
    public Observable<String> onPropertyChanged() {
        if (this.propertyChanged == null) {
            this.propertyChanged = PublishSubject.create();
        }

        return this.propertyChanged;
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
            return;
        }
        onClick = value;
    }

    @Override
    public void onClick(View v) {
        if (onClick.canExecute(null)) {
            onClick.execute(null);
        }
    }

}