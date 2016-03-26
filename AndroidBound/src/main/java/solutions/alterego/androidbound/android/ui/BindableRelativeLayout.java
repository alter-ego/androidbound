package solutions.alterego.androidbound.android.ui;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.RelativeLayout;

import rx.Observable;
import rx.subjects.PublishSubject;
import solutions.alterego.androidbound.interfaces.ICommand;
import solutions.alterego.androidbound.binding.interfaces.INotifyPropertyChanged;

public class BindableRelativeLayout extends RelativeLayout implements INotifyPropertyChanged, OnClickListener {

    private boolean disposed;

    private PublishSubject<String> propertyChanged = PublishSubject.create();

    private ICommand onClick = ICommand.empty;

    public BindableRelativeLayout(Context context) {
        super(context);
    }

    public BindableRelativeLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public BindableRelativeLayout(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }

    @Override
    public Observable<String> onPropertyChanged() {
        if (propertyChanged == null) {
            propertyChanged = PublishSubject.create();
        }

        return propertyChanged;
    }

    @Override
    public void dispose() {
        if (disposed) {
            return;
        }

        disposed = true;
        if (propertyChanged != null) {
            propertyChanged.onCompleted();
            propertyChanged = null;
        }

        propertyChanged = null;
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);

        if (disposed || propertyChanged == null) {
            return;
        }

        if (w != oldw) {
            propertyChanged.onNext("Width");
        }

        if (h != oldh) {
            propertyChanged.onNext("Height");
        }
    }

    public void setWidth(int width) {
        if (width == getWidth()) {
            return;
        }

        ViewGroup.LayoutParams p = getLayoutParams();
        p.width = width;
        setLayoutParams(p);
    }

    public void setHeight(int height) {
        if (height == getHeight()) {
            return;
        }

        ViewGroup.LayoutParams p = getLayoutParams();
        p.height = height;
        setLayoutParams(p);
    }

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
        if (onClick.canExecute(null)) {
            onClick.execute(null);
        }
    }

    public int getBackgroundColor() {
        return 0;
    }

    public void setBackgroundColor(int color) {
        super.setBackgroundColor(color);
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

    public int getBackgroundResource() {
        return 0;
    }

    public void setBackgroundResource(int res) {
        super.setBackgroundResource(res);
    }

    public int getBackgroundDrawable() {
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
}
