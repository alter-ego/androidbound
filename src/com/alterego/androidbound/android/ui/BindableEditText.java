package com.alterego.androidbound.android.ui;

import android.content.Context;
import android.content.res.ColorStateList;
import android.graphics.Typeface;
import android.text.Editable;
import android.text.SpannableStringBuilder;
import android.text.TextWatcher;
import android.util.AttributeSet;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import com.alterego.androidbound.interfaces.ICommand;
import com.alterego.androidbound.interfaces.INotifyPropertyChanged;
import com.alterego.androidbound.zzzztoremove.reactive.IObservable;
import com.alterego.androidbound.zzzztoremove.reactive.ISubject;
import com.alterego.androidbound.zzzztoremove.reactive.Subject;

public class BindableEditText extends EditText implements INotifyPropertyChanged {

    private ISubject<String> propertyChanged;

    private boolean disposed;

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
            this.propertyChanged.dispose();
        }

        this.propertyChanged = null;

    }

    @Override
    public IObservable<String> onPropertyChanged() {
        if (this.propertyChanged == null) {
            this.propertyChanged = new Subject<String>();
        }

        return this.propertyChanged;
    }

    public void setTextColor(int color) {
        super.setTextColor(color);
    }

    public void setTextColorState(ColorStateList colors) {
        super.setTextColor(colors);
    }

    public ColorStateList getTextColor() {
        return super.getTextColors();
    }

    public void setBackgroundColor(int color) {
        super.setBackgroundColor(color);
    }

    public int getBackgroundColor() {
        return 0;
    }

    public String getTextString() {
        return this.getText().toString();
    }

    public void setTextString(String text) {
        this.setText(text);
    }

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
}