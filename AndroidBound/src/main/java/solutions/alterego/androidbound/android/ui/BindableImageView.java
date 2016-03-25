package solutions.alterego.androidbound.android.ui;

import com.nostra13.universalimageloader.core.ImageLoader;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnLongClickListener;
import android.view.ViewGroup;
import android.widget.ImageView;

import rx.Observable;
import rx.subjects.PublishSubject;
import solutions.alterego.androidbound.interfaces.ICommand;
import solutions.alterego.androidbound.binding.interfaces.INotifyPropertyChanged;

public class BindableImageView extends ImageView implements OnClickListener, INotifyPropertyChanged, OnLongClickListener {

    static Context context;

    private boolean disposed;

    private int currentResId;

    private Bitmap currentBitmap;

    private Drawable currentDrawable;

    private PublishSubject<String> propertyChanged = PublishSubject.create();

    private ICommand onLongClick = ICommand.empty;

    private ICommand onClick = ICommand.empty;

    private String source;

    public BindableImageView(Context context, AttributeSet attrs) {
        super(context, attrs);
        BindableImageView.context = context;
    }

    public BindableImageView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        BindableImageView.context = context;
    }

    public String getSource() {
        return source;
    }

    public void setSource(String value) {
        source = value;
        ImageLoader.getInstance().displayImage(source, this);
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

    public ICommand getLongClick() {
        return onLongClick;
    }

    public void setLongClick(ICommand value) {
        if (value == null) {
            setClickable(false);
            setOnLongClickListener(null);
            onLongClick = ICommand.empty;
            return;
        }
        setClickable(true);
        setOnLongClickListener(this);
        onLongClick = value;
    }

    @Override
    public boolean onLongClick(View arg0) {

        if (onLongClick.canExecute(null)) {
            onLongClick.execute(null);
            return true;
        } else {
            return false;
        }
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
        this.onClick = null;
        onLongClick = null;
    }

    @Override
    public Observable<String> onPropertyChanged() {
        if (this.propertyChanged == null) {
            this.propertyChanged = PublishSubject.create();
        }

        return this.propertyChanged;
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

    public Bitmap getBitmap() {
        return this.currentBitmap;
    }

    public void setBitmap(Bitmap bmp) {
        this.currentBitmap = bmp;
        this.setImageBitmap(bmp);
        this.invalidate();
    }

    public Drawable getDrawable() {
        return this.currentDrawable;
    }

    public void setDrawable(Drawable drawable) {
        this.currentDrawable = drawable;
        this.setImageDrawable(drawable);
        this.invalidate();
    }

    public Integer getResource() {
        return this.currentResId;
    }

    public void setResource(Integer resId) {
        this.currentResId = resId;
        this.setImageResource(this.currentResId);
        this.invalidate();
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
