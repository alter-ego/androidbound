package com.alterego.androidbound.android.ui;

import android.content.Context;
import android.content.res.ColorStateList;
import android.graphics.Typeface;
import android.util.AttributeSet;
import android.widget.TextView;

import com.alterego.androidbound.interfaces.INotifyPropertyChanged;
import com.alterego.androidbound.zzzztoremove.reactive.IObservable;
import com.alterego.androidbound.zzzztoremove.reactive.ISubject;
import com.alterego.androidbound.zzzztoremove.reactive.Subject;

public class BindableTextView extends TextView implements INotifyPropertyChanged {

    private ISubject<String> propertyChanged;

    private boolean disposed;

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

    //    public Typeface getFont() {
    //        return getTypeface();
    //    }
    //
    //    public void setFont(Typeface font) {
    //        setTypeface(font);
    //    }

    public void setTextColor(int color) {
        super.setTextColor(color);
    }

    public void setTextColor(ColorStateList colors) {
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

}