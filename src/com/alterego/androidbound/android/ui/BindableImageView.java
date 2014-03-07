
package com.alterego.androidbound.android.ui;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.animation.AccelerateInterpolator;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.AnimationSet;
import android.view.animation.DecelerateInterpolator;
import android.widget.ImageView;

import com.alterego.androidbound.CommonSettings;
import com.alterego.androidbound.android.cache.CacheSystem;
import com.alterego.androidbound.interfaces.ICommand;
import com.alterego.androidbound.interfaces.INotifyPropertyChanged;
import com.alterego.androidbound.zzzztoremove.Exceptional;
import com.alterego.androidbound.zzzztoremove.HttpBitmapProvider;
import com.alterego.androidbound.zzzztoremove.IContentProvider;
import com.alterego.androidbound.zzzztoremove.UiThreadScheduler;
import com.alterego.androidbound.zzzztoremove.reactive.IObservable;
import com.alterego.androidbound.zzzztoremove.reactive.ISubject;
import com.alterego.androidbound.zzzztoremove.reactive.Subject;
import com.alterego.androidbound.zzzztoremove.reactive.ThreadPoolScheduler;

public class BindableImageView extends ImageView implements OnClickListener, INotifyPropertyChanged {

	ImageView mImageView = null;
	static Context context;
	private final static IContentProvider<Bitmap> provider = new CacheSystem<Bitmap>(new HttpBitmapProvider(), CommonSettings.CacheImage.cache);


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
		mImageView = this;
		source = value;
		loadImage();
	}

	private void loadImage() {
		ThreadPoolScheduler.instance.schedule(new Runnable() {
			public void run() {
				final Exceptional<Bitmap> result = provider.getContent(source);
				if (result.hasValue()) {
					UiThreadScheduler.instance.schedule(new Runnable() {
						public void run() {
							setImage(mImageView, result.value());
						}
					});
				}
			}
		});
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
	private String source;

	private void setImage(final ImageView view, Bitmap remoteImage) {
		int fadeInDuration = 2000;
		int fadeOutDuration = 1500;

		view.setVisibility(View.VISIBLE);

		if (CommonSettings.Images.isAnimated) {
			Animation fadeIn = new AlphaAnimation(0, 1);
			fadeIn.setInterpolator(new DecelerateInterpolator());
			fadeIn.setDuration(fadeInDuration);

			Animation fadeOut = new AlphaAnimation(1, 0);
			fadeOut.setInterpolator(new AccelerateInterpolator());
			fadeOut.setStartOffset(fadeInDuration);
			fadeOut.setDuration(fadeOutDuration);

			AnimationSet animation = new AnimationSet(false);
			animation.addAnimation(fadeIn);
			animation.setRepeatCount(0);
			view.setAnimation(animation);
		}
		view.setImageBitmap(remoteImage);
	}
}
