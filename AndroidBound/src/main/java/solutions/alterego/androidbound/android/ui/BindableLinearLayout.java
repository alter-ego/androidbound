package solutions.alterego.androidbound.android.ui;

import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.content.Context;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.util.AttributeSet;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import java.util.List;

import rx.Observable;
import rx.subjects.PublishSubject;
import solutions.alterego.androidbound.binding.interfaces.IBindingAssociationEngine;
import solutions.alterego.androidbound.interfaces.ICommand;
import solutions.alterego.androidbound.binding.interfaces.INotifyPropertyChanged;
import solutions.alterego.androidbound.interfaces.IViewBinder;

public class BindableLinearLayout extends LinearLayout implements INotifyPropertyChanged, OnClickListener {

    private boolean disposed;

    private IViewBinder mViewBinder;

    private View content;

    private int itemTemplate;

    private PublishSubject<String> propertyChanged = PublishSubject.create();

    private ICommand onClick = ICommand.empty;

    public BindableLinearLayout(Context context) {
        super(context);
    }

    public BindableLinearLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    @TargetApi(Build.VERSION_CODES.HONEYCOMB)
    public BindableLinearLayout(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }

    public BindableLinearLayout(Context context, IViewBinder viewbinder, int iTemplate, Object source) {
        super(context);
        mViewBinder = viewbinder;
        itemTemplate = iTemplate;

        if (mViewBinder != null) {
            content = mViewBinder.inflate(context, source, itemTemplate, this);
        }
    }

    public int getItemTemplate() {
        return itemTemplate;
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

    @Override
    public void onClick(View v) {
        if (onClick.canExecute(null)) {
            onClick.execute(null);
        }
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

    public int getBackgroundColor() {
        return 0;
    }

    public void setBackgroundColor(int color) {
        super.setBackgroundColor(color);
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

    public void bindTo(Object source) {
        if (mViewBinder == null) {
            return;
        }

        List<IBindingAssociationEngine> bindings = mViewBinder.getBindingsForViewAndChildren(this);
        if (bindings == null || bindings.size() < 1) {
            return;
        }

        for (IBindingAssociationEngine binding : bindings) {
            binding.setDataContext(source);
        }

        invalidate();
        requestLayout();

    }

    public void unbind() {
        if (mViewBinder == null) {
            return;
        }

        mViewBinder.clearBindingForViewAndChildren(this);
    }
}
