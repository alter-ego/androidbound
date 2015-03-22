
package solutions.alterego.androidbound.android.ui;

import java.util.List;

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

import solutions.alterego.androidbound.interfaces.IBindingAssociation;
import solutions.alterego.androidbound.interfaces.ICommand;
import solutions.alterego.androidbound.interfaces.INotifyPropertyChanged;
import solutions.alterego.androidbound.interfaces.IViewBinder;
import solutions.alterego.androidbound.zzzztoremove.reactive.IObservable;
import solutions.alterego.androidbound.zzzztoremove.reactive.ISubject;
import solutions.alterego.androidbound.zzzztoremove.reactive.Subject;

public class BindableLinearLayout extends LinearLayout implements INotifyPropertyChanged, OnClickListener {
    private boolean disposed;
    private IViewBinder mViewBinder;
    private View content;
    private int itemTemplate;
    private ISubject<String> propertyChanged;
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

    public BindableLinearLayout(Context context, IViewBinder viewbinder, int itemTemplate, Object source) {
        super(context);
        this.mViewBinder = viewbinder;
        this.itemTemplate = itemTemplate;

        if (this.mViewBinder != null)
            content = mViewBinder.inflate(context, source, itemTemplate, this);
    }

    public int getItemTemplate() {
        return itemTemplate;
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

    @Override
    public void onClick(View v) {
        if (onClick.canExecute(null))
            onClick.execute(null);
    }

    public ICommand getClick() {
        return onClick;
    }

    public void setClick(ICommand value) {
        if (value == null) {
            onClick = ICommand.empty;
            this.setOnClickListener(null);
            return;
        }

        this.setOnClickListener(this);
        onClick = value;
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

    public void setBackgroundColor(int color) {
        super.setBackgroundColor(color);
    }

    public int getBackgroundColor() {
        return 0;
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

    public void bindTo(Object source) {
        if (this.mViewBinder == null) {
            return;
        }

        List<IBindingAssociation> bindings = this.mViewBinder.getBindingsForViewAndChildren(this);
        if (bindings == null || bindings.size() < 1) {
            return;
        }

        for (IBindingAssociation binding : bindings) {
            binding.setDataContext(source);
        }

        this.invalidate();
        this.requestLayout();

    }

    public void unbind() {
        if (this.mViewBinder == null) {
            return;
        }

        this.mViewBinder.clearBindingForViewAndChildren(this);
    }
}
