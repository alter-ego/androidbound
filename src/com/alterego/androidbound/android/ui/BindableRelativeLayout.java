
package com.alterego.androidbound.android.ui;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.RelativeLayout;

import com.alterego.androidbound.interfaces.ICommand;
import com.alterego.androidbound.interfaces.INotifyPropertyChanged;
import com.alterego.androidbound.zzzztoremove.reactive.IObservable;
import com.alterego.androidbound.zzzztoremove.reactive.ISubject;
import com.alterego.androidbound.zzzztoremove.reactive.Subject;

public class BindableRelativeLayout extends RelativeLayout implements INotifyPropertyChanged, OnClickListener {
    public BindableRelativeLayout(Context context) {
        super(context);
        // TODO Auto-generated constructor stub
    }

    public BindableRelativeLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
        // TODO Auto-generated constructor stub
    }

    public BindableRelativeLayout(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        // TODO Auto-generated constructor stub
    }

    @Override
    public IObservable<String> onPropertyChanged() {
        if (this.propertyChanged == null) {
            this.propertyChanged = new Subject<String>();
        }

        return this.propertyChanged;
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
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);

        if (this.disposed || this.propertyChanged == null) {
            return;
        }

        if (w != oldw) {
            this.propertyChanged.onNext("Width");
        }

        if (h != oldh) {
            this.propertyChanged.onNext("Height");
        }
    }

    public void setWidth(int width) {
        if (width == this.getWidth()) {
            return;
        }

        ViewGroup.LayoutParams p = this.getLayoutParams();
        p.width = width;
        this.setLayoutParams(p);
    }

    public void setHeight(int height) {
        if (height == this.getHeight()) {
            return;
        }

        ViewGroup.LayoutParams p = this.getLayoutParams();
        p.height = height;
        this.setLayoutParams(p);
    }

    private boolean disposed;

    private ISubject<String> propertyChanged;

    private ICommand onClick = ICommand.empty;

    public ICommand getClick() {
        return onClick;
    }

    public void setClick(ICommand value) {
        if (value == null) {
            setClickable(false);
            setOnClickListener(null);
            onClick = ICommand.empty;
            return;
        }
        setClickable(true);
        setOnClickListener(this);
        onClick = value;
    }

    @Override
    public void onClick(View v) {
        if (onClick.canExecute(null))
            onClick.execute(null);
    }

    public void setBackgroundColor(int color) {
        super.setBackgroundColor(color);
    }

    public int getBackgroundColor() {
        return 0;
    }

    @SuppressLint("NewApi")
    public void setBackground(Drawable bgd) {
        try {
            super.setBackgroundDrawable(bgd);
            super.setBackground(bgd);
        } catch (Exception e) {
            //failed because of API level, probably
            super.setBackgroundDrawable(bgd);
        }
    }

    public void setBackgroundResource(int res) {
        super.setBackgroundResource(res);
    }

    public int getBackgroundResource() {
        return 0;
    }

    @SuppressLint("NewApi")
    @SuppressWarnings("deprecation")
    public void setBackgroundDrawable(Drawable res) {

        //        if (android.os.Build.VERSION.SDK_INT > 15)
        //            super.setBackground(res);
        //        else
        super.setBackgroundDrawable(res);

    }

    public int getBackgroundDrawable() {
        return 0;
    }
}
