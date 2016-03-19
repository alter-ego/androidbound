package solutions.alterego.androidbound.android.ui;

import android.content.Context;
import android.util.AttributeSet;
import android.view.ViewGroup;
import android.widget.ScrollView;

import rx.Observable;
import rx.subjects.PublishSubject;
import solutions.alterego.androidbound.binding.interfaces.INotifyPropertyChanged;

public class BindableScrollView extends ScrollView implements INotifyPropertyChanged {

    private boolean disposed;

    private PublishSubject<String> propertyChanged = PublishSubject.create();

    public BindableScrollView(Context context) {
        super(context);
        // TODO Auto-generated constructor stub
    }

    public BindableScrollView(Context context, AttributeSet attrs) {
        super(context, attrs);
        // TODO Auto-generated constructor stub
    }

    public BindableScrollView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        // TODO Auto-generated constructor stub
    }

    @Override
    public Observable<String> onPropertyChanged() {
        if (this.propertyChanged == null) {
            this.propertyChanged = PublishSubject.create();
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
            this.propertyChanged.onCompleted();
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
    protected void onScrollChanged(int l, int t, int oldl, int oldt) {
        super.onScrollChanged(l, t, oldl, oldt);

        if (this.disposed || this.propertyChanged == null) {
            return;
        }

        if (l != oldl) {
            this.propertyChanged.onNext("ScrollX");
        }

        if (t != oldt) {
            this.propertyChanged.onNext("ScrollY");
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
}
