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
    }

    public BindableScrollView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public BindableScrollView(Context context, AttributeSet attrs, int defStyle) {
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
    protected void onScrollChanged(int l, int t, int oldl, int oldt) {
        super.onScrollChanged(l, t, oldl, oldt);

        if (disposed || propertyChanged == null) {
            return;
        }

        if (l != oldl) {
            propertyChanged.onNext("ScrollX");
        }

        if (t != oldt) {
            propertyChanged.onNext("ScrollY");
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
}
