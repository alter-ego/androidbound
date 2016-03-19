package solutions.alterego.androidbound.android.ui;

import android.content.Context;
import android.content.res.ColorStateList;
import android.graphics.Typeface;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.AttributeSet;
import android.widget.EditText;

import rx.Observable;
import rx.subjects.PublishSubject;
import solutions.alterego.androidbound.interfaces.INotifyPropertyChanged;

public class BindableEditText extends EditText implements INotifyPropertyChanged {

    private PublishSubject<String> propertyChanged = PublishSubject.create();

    private boolean disposed;

    private TextWatcher textWatcher = new TextWatcher() {
        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {

        }

        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {

        }

        @Override
        public void afterTextChanged(Editable s) {
            propertyChanged.onNext("TextString");
        }
    };

    public BindableEditText(Context context) {
        super(context);
        addTextChangedListener(textWatcher);
    }

    public BindableEditText(Context context, AttributeSet attrs) {
        super(context, attrs);
        addTextChangedListener(textWatcher);
    }

    public BindableEditText(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        addTextChangedListener(textWatcher);
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
            propertyChanged = null;
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

    public String getTextString() {
        return this.getText().toString();
    }

    public void setTextString(String text) {
        this.setText(text);
    }
}