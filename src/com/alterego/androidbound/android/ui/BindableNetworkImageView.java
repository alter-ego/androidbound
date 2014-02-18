
package com.alterego.androidbound.android.ui;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.android.volley.toolbox.NetworkImageView;
import com.alterego.androidbound.ViewBinder;
import com.alterego.androidbound.interfaces.ICommand;
import com.alterego.androidbound.interfaces.INotifyPropertyChanged;
import com.alterego.androidbound.zzzztoremove.reactive.IObservable;
import com.alterego.androidbound.zzzztoremove.reactive.ISubject;
import com.alterego.androidbound.zzzztoremove.reactive.Subject;

public class BindableNetworkImageView extends NetworkImageView implements OnClickListener, INotifyPropertyChanged {

    ImageView mImageView = null;
    static Context context;

    public BindableNetworkImageView(Context context, AttributeSet attrs) {
        super(context, attrs);
        BindableNetworkImageView.context = context;
    }

    public BindableNetworkImageView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        BindableNetworkImageView.context = context;
    }

    public String getSource() {
        return mImageSourceUrl;
    }

//    public void setSource(Uri uri) {
//    	setSource(uri.toString());
//    }
    
    /**
     * Sets URL of the image that should be loaded into this view. Note that calling this will
     * immediately either set the cached image (if available) or the default image specified by
     * {@link NetworkImageView#setDefaultImageResId(int)} on the view.
     *
     * NOTE: If applicable, {@link NetworkImageView#setDefaultImageResId(int)} and
     * {@link NetworkImageView#setErrorImageResId(int)} should be called prior to calling
     * this function.
     *
     * @param url The URL that should be loaded into this ImageView.
     */
    public void setSource(String value) {
    	//if (value instanceof String)
    		mImageSourceUrl = (String) value;
//    	else
//    		mImageSourceUrl = value.toString();
    	
    	ViewBinder.getLogger().debug("setSource value = " + value + ", mImageSourceUrl = " + mImageSourceUrl);
        //mImageView = this;
        //mImageSourceUrl = value;
        setImageUrl(mImageSourceUrl, ViewBinder.getImageLoader());
    }

    /**
     * Sets the default image resource ID to be used for this view until the attempt to load it
     * completes.
     */
    public void setDefaultImageResId(int defaultImage) {
    	setDefaultImageResId(defaultImage);        
    }

    /**
     * Sets the error image resource ID to be used for this view in the event that the image
     * requested fails to load.
     */
    public void setErrorImageResId(int errorImage) {
    	setErrorImageResId(errorImage);
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
        this.onClick = null;
    }

    @Override
    public IObservable<String> onPropertyChanged() {
        if (this.propertyChanged == null) {
            this.propertyChanged = new Subject<String>();
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

    public void setBitmap(Bitmap bmp) {
        this.currentBitmap = bmp;
        this.setImageBitmap(bmp);
        this.invalidate();
    }

    public Bitmap getBitmap() {
        return this.currentBitmap;
    }

    public void setDrawable(Drawable drawable) {
        this.currentDrawable = drawable;
        this.setImageDrawable(drawable);
        this.invalidate();
    }

    public Drawable getDrawable() {
        return this.currentDrawable;
    }

    public void setResource(Integer resId) {
        this.currentResId = resId;
        this.setImageResource(this.currentResId);
        this.invalidate();
    }

    public Integer getResource() {
        return this.currentResId;
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
    private int currentResId;
    private Bitmap currentBitmap;
    private Drawable currentDrawable;
    private ISubject<String> propertyChanged;
    private ICommand onClick = ICommand.empty;
    private String mImageSourceUrl;
    //private final static IContentProvider<Bitmap> provider = new CacheSystem<Bitmap>(new HttpBitmapProvider(), CommonSettings.CacheImage.cache);

    private void setImage(final ImageView view, Bitmap remoteImage) {
        int fadeInDuration = 2000;
        int fadeOutDuration = 1500;

        view.setVisibility(View.VISIBLE);

//        if (CommonSettings.Images.isAnimated) {
//            Animation fadeIn = new AlphaAnimation(0, 1);
//            fadeIn.setInterpolator(new DecelerateInterpolator());
//            fadeIn.setDuration(fadeInDuration);
//
//            Animation fadeOut = new AlphaAnimation(1, 0);
//            fadeOut.setInterpolator(new AccelerateInterpolator());
//            fadeOut.setStartOffset(fadeInDuration);
//            fadeOut.setDuration(fadeOutDuration);
//
//            AnimationSet animation = new AnimationSet(false);
//            animation.addAnimation(fadeIn);
//            animation.setRepeatCount(0);
//            view.setAnimation(animation);
//        }
        view.setImageBitmap(remoteImage);
    }
}
