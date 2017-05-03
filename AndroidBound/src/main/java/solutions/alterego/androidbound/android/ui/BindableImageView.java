package solutions.alterego.androidbound.android.ui;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.StateListDrawable;
import android.net.Uri;
import android.os.Build;
import android.support.v7.widget.AppCompatImageView;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.view.View;

import rx.Observable;
import solutions.alterego.androidbound.android.interfaces.IImageLoader;
import solutions.alterego.androidbound.android.interfaces.INeedsImageLoader;
import solutions.alterego.androidbound.binding.interfaces.INotifyPropertyChanged;
import solutions.alterego.androidbound.interfaces.ICommand;

public class BindableImageView extends AppCompatImageView implements INotifyPropertyChanged, INeedsImageLoader {

    private BindableViewDelegate mDelegate;

    private int currentResId;

    private String source;

    private IImageLoader mImageLoader = IImageLoader.nullImageLoader;

    public BindableImageView(Context context) {
        this(context, null);
    }

    public BindableImageView(Context context, AttributeSet attrs) {
        super(context, attrs);
        mDelegate = createDelegate(this);
    }

    public BindableImageView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        mDelegate = createDelegate(this);
    }

    /****** beginning of the delegated methods ******/

    protected BindableViewDelegate createDelegate(View view) {
        return new BindableViewDelegate(view);
    }

    public ICommand getClick() {
        return mDelegate.getClick();
    }

    public void setClick(ICommand value) {
        mDelegate.setClick(value);
    }

    public ICommand getLongClick() {
        return mDelegate.getClick();
    }

    public void setLongClick(ICommand value) {
        mDelegate.setClick(value);
    }

    public int getBackgroundColor() {
        return mDelegate.getBackgroundColor();
    }

    public void setBackgroundColor(int color) {
        mDelegate.setBackgroundColor(color);
        super.setBackgroundColor(color);
    }

    public StateListDrawable getBackgroundDrawableState() {
        return mDelegate.getBackgroundDrawableState();
    }

    public void setBackgroundDrawableState(StateListDrawable colors) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
            super.setBackground(colors);
            mDelegate.setBackgroundDrawableState(colors);
        }
    }

    public int getBackgroundResource() {
        return 0;
    }

    public void setBackgroundResource(int res) {
        super.setBackgroundResource(res);
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        mDelegate.onSizeChanged(w, h, oldw, oldh);
    }

    public void setWidth(int width) {
        mDelegate.setWidth(width);
    }

    public void setHeight(int height) {
        mDelegate.setHeight(height);
    }

    @Override
    public Observable<String> onPropertyChanged() {
        return mDelegate.onPropertyChanged();
    }

    @Override
    public void dispose() {
        mDelegate.dispose();
        mImageLoader = IImageLoader.nullImageLoader;
        source = null;
        currentResId = 0;
    }

    /****** end of the delegated methods ******/

    public String getSource() {
        return source;
    }

    public void setSource(String value) {
        source = value;
        mImageLoader.loadImageFromUri(source, this);
    }

    public Uri getSourceUri() {
        if (!TextUtils.isEmpty(source)) {
            return Uri.parse(source);
        } else {
            return Uri.EMPTY;
        }
    }

    public void setSourceUri(Uri value) {
        setSource(value.toString());
    }

    public void setBitmap(Bitmap bmp) {
        setImageBitmap(bmp);
    }

    public void setDrawable(Drawable drawable) {
        setImageDrawable(drawable);
    }

    public Integer getResource() {
        return currentResId;
    }

    public void setResource(Integer resId) {
        currentResId = resId;
        setImageResource(currentResId);
    }

    @Override
    public void setImageLoader(IImageLoader imageLoader) {
        mImageLoader = imageLoader;
    }
}
