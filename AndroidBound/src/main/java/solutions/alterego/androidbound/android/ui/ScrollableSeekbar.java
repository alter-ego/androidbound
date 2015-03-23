package solutions.alterego.androidbound.android.ui;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.ColorFilter;
import android.graphics.Paint;
import android.graphics.PixelFormat;
import android.graphics.PointF;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.MotionEvent.PointerCoords;
import android.view.VelocityTracker;
import android.view.View;
import android.view.ViewConfiguration;
import android.view.ViewGroup;
import android.view.ViewGroup.OnHierarchyChangeListener;
import android.widget.FrameLayout;
import android.widget.Scroller;

import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.TimeUnit;

import rx.Observable;
import rx.Observer;
import rx.Scheduler;
import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;
import rx.subjects.PublishSubject;
import solutions.alterego.androidbound.binds.BindableMap;
import solutions.alterego.androidbound.interfaces.ICommand;
import solutions.alterego.androidbound.interfaces.INotifyPropertyChanged;
import solutions.alterego.androidbound.zzzztoremove.reactive.IDisposable;
import solutions.alterego.androidbound.zzzztoremove.reactive.Iterables;

public class ScrollableSeekbar extends FrameLayout implements OnHierarchyChangeListener,
        INotifyPropertyChanged, IDisposable {

    private static final int interceptW2 = 20; // dp?

    private static final int interceptH2 = 20; // dp?

    private boolean disposed;

    private PublishSubject<String> propertyChanged = PublishSubject.create();

    private Map<String, ICommand> progressTrackBegin;

    private Map<String, ICommand> progressTrackEnd;

    private Map<String, ICommand> progressTrackChanged;

    private ICommand tapTrack = ICommand.empty;

    private Context context;

    // width of the tap area. it affects the value range of the tap
    private int tapWidth;

    private Float[] tapInterval;

    private float minValue;

    private float maxValue;

    private BindableMap<String, Float[]> values;

    private Subscription valuesSubscription;

    private BindableMap<String, Boolean> thumbEnablings;

    private Subscription thumbEnablingsSubscription;

    private float zoom;

    private float minZoom;

    private float maxZoom;

    private float contentWidth;

    private float contentHeight;

    private View content;

    private boolean attached;

    private boolean isDragging;

    private String isDraggingKey;

    private GestureRecognizer gestureRecognizer;

    private Scroller scroller;

    private VelocityTracker velocityTracker;

    private int velocityMax;

    private ThumbDrawable thumbDrawable;

    private Set<String> centerZoom;

    private Set<String> seekOnTaps;

    private OnScrollableSeekBarChangeListener listener;

    private OnScrollableSeekbarTapListener tapListener;

    public ScrollableSeekbar(Context context, AttributeSet attrs) {
        super(context, attrs);

        // this.setFillViewport(true);

        this.setOnHierarchyChangeListener(this);

        this.isDragging = false;
        this.isDraggingKey = "";

        this.context = context;
        this.centerZoom = new HashSet<String>();

        this.minValue = 0f;
        this.maxValue = 100f;
        //this.value = this.minValue;
        this.progressTrackBegin = new HashMap<String, ICommand>();
        this.progressTrackChanged = new HashMap<String, ICommand>();
        this.progressTrackEnd = new HashMap<String, ICommand>();

        this.seekOnTaps = new HashSet<String>();

        this.scroller = new Scroller(this.getContext());

        this.velocityMax = ViewConfiguration.get(context).getScaledMaximumFlingVelocity();

        this.thumbEnablings = new BindableMap<String, Boolean>(new BindableMap.IValidator<String, Boolean>() {
            @Override
            public boolean isValid(Map<String, Boolean> map, String key, Boolean value) {
                return value != null;
            }

            @Override
            public boolean canMakeValid(Map<String, Boolean> map, String key, Boolean value) {
                return true;
            }

            @Override
            public Boolean makeValid(Map<String, Boolean> map, String key, Boolean value) {
                return value != null && value;
            }
        }, Boolean.TRUE);

        this.values = new BindableMap<String, Float[]>(new BindableMap.IValidator<String, Float[]>() {
            @Override
            public boolean isValid(Map<String, Float[]> map, String key, Float[] value) {
                return value != null && value.length == 3;
            }

            @Override
            public boolean canMakeValid(Map<String, Float[]> map, String key, Float[] value) {
                return true;
            }

            @Override
            public Float[] makeValid(Map<String, Float[]> map, String key, Float[] value) {
                if (value == null || value.length != 3) {
                    return new Float[]{
                            minValue, minValue, minValue
                    };
                }

                Float[] newValue = value.clone();
                for (int i = 0; i < newValue.length; i++) {
                    float v = newValue[i];
                    if (v < minValue) {
                        v = minValue;
                    }
                    if (v > maxValue) {
                        v = maxValue;
                    }
                    newValue[i] = v;
                }
                return newValue;
            }
        }, new Float[]{
                0f, 0f, 0f
        });

        this.tapInterval = new Float[]{
                0f, 0f, 0f
        };

        this.tapWidth = 20;

        this.minZoom = 1f;
        this.maxZoom = 10f;
        this.zoom = 1f;

        this.contentWidth = 0f;
        this.contentHeight = 0f;

        this.gestureRecognizer = new GestureRecognizer(GestureRecognizer.GESTURE_ALL);

        this.thumbDrawable = new ThumbDrawable();

        int attr = attrs.getAttributeResourceValue(null, "thumb", 0);
        // TODO how to initialize from layout a thumb?
        //        if (attr != 0) {
        //            this.setThumb("--", attr);
        //        } else {
        //            this.setThumb("--", null);
        //        }

        //this.getValues().put("--", this.minValue);

        super.setForeground(this.thumbDrawable);

        this.valuesSubscription = this.values.onPropertyChanged()
                .subscribe(new Observer<String>() {
                    @Override
                    public void onCompleted() {

                    }

                    @Override
                    public void onError(Throwable e) {

                    }

                    @Override
                    public void onNext(String obj) {
                        refreshThumbPositions(obj);
                    }
                });

        this.thumbEnablingsSubscription = this.thumbEnablings.onPropertyChanged().subscribe(new Observer<String>() {
            @Override
            public void onCompleted() {

            }

            @Override
            public void onError(Throwable e) {

            }

            @Override
            public void onNext(String obj) {
                refreshThumbEnablings(obj);
            }
        });
    }

    public static Observable<Float> linear(final Float start, final Float end, final long duration, final long rate, TimeUnit unit,
            Scheduler scheduler) {
        long durationMsec = TimeUnit.MILLISECONDS.convert(duration, unit);
        long rateMsec = TimeUnit.MILLISECONDS.convert(rate, unit);

        if (rateMsec <= 0 || durationMsec <= rateMsec) {
            throw new IllegalArgumentException("duration and rate must be valid");
        }

        final long startTimestamp = System.currentTimeMillis();
        final Float scaleFactor = (end - start) / (float) durationMsec;
        final boolean ascending = end >= start;

        return Observable
                .interval(rate, TimeUnit.MILLISECONDS, scheduler)
                .map(aLong -> {
                    long currentTimestamp = System.currentTimeMillis();

                    Float retval = start + (currentTimestamp - startTimestamp) * scaleFactor;
                    if ((ascending && retval > end) || (!ascending && retval < end)) {
                        retval = end;
                    }

                    return retval;
                })
                .takeUntil(Observable.just(end));
    }

    public Observable<String> onPropertyChanged() {
        if (this.propertyChanged == null) {
            this.propertyChanged = PublishSubject.create();
        }

        return this.propertyChanged;
    }

    public void setSeekOnTap(String key, boolean seek) {
        if (!seek) {
            this.seekOnTaps.remove(key);
            return;
        }

        this.seekOnTaps.add(key);
    }

    public boolean getSeekOnTap(String key) {
        return this.seekOnTaps.contains(key);
    }

    public ICommand getTapTrack() {
        return this.tapTrack;
    }

    public void setTapTrack(ICommand cmd) {
        if (cmd == null) {
            this.tapTrack = ICommand.empty;
            return;
        }

        this.tapTrack = cmd;
    }

    public Map<String, ICommand> getProgressTrackBegin() {
        return this.progressTrackBegin;
    }

    public Map<String, ICommand> getProgressTrackEnd() {
        return this.progressTrackEnd;
    }

    public Map<String, ICommand> getProgressTrackChanged() {
        return this.progressTrackChanged;
    }

    public void setOnScrollableSeekBarChangeListener(OnScrollableSeekBarChangeListener listener) {
        this.listener = listener;
    }

    public void setOnScrollableSeekBarTapListener(OnScrollableSeekbarTapListener listener) {
        this.tapListener = listener;
    }

    public void setThumb(String key, Drawable thumb, boolean initialEnabling) {
        this.thumbEnablings.put(key, initialEnabling);
        if (thumb == null) {
            thumb = new DefaultThumb(interceptW2 * 2, interceptH2 * 2);
        }

        this.thumbDrawable.setThumb(key, new VisibilityAnimatedDrawable(thumb, initialEnabling));

        this.refreshThumbSizes(key);
        this.refreshThumbPositions(key);
    }

    public void setThumb(String key, Drawable thumb) {
        this.setThumb(key, thumb, true);
    }

    public void setThumb(String key, int thumbResource, boolean initialEnabling) {
        this.thumbEnablings.put(key, initialEnabling);
        try {
            this.setThumb(key, this.context.getResources().getDrawable(thumbResource), initialEnabling);
        } catch (Exception ex) {
            this.setThumb(key, null, initialEnabling);
        }
    }

    public void setThumb(String key, int thumbResource) {
        this.setThumb(key, thumbResource, true);
    }

    public Drawable getThumb(String key) {
        return this.thumbDrawable.getThumb(key);
    }

    public synchronized Map<String, Boolean> getThumbEnablings() {
        return this.thumbEnablings;
    }

    public synchronized void setThumbEnabled(String key, boolean enabled) {
        this.thumbEnablings.put(key, enabled);
    }

    public synchronized boolean isThumbEnabled(String key) {
        return this.thumbEnablings.get(key);
    }

    public synchronized void setCenterZoom(String key, boolean value) {
        if (!value) {
            this.centerZoom.remove(key);
            return;
        }

        this.centerZoom.add(key);
    }

    public synchronized boolean getCenterZoom(String key) {
        return this.centerZoom.contains(key);
    }

    public synchronized float getMinValue() {
        return this.minValue;
    }

    public synchronized void setMinValue(float newvalue) {
        this.minValue = newvalue;
        if (this.minValue > this.maxValue) {
            this.maxValue = this.minValue;
        }

        for (int i = 0; i < this.tapInterval.length; i++) {
            if (this.tapInterval[i] < this.minValue) {
                this.tapInterval[i] = this.minValue;
            } else if (this.tapInterval[i] > this.maxValue) {
                this.tapInterval[i] = this.maxValue;
            }
        }

        this.values.forceValidation();

        this.raisePropertyChanged("MinValue");

        this.refreshThumbPositions();
    }

    public synchronized float getMaxValue() {
        return this.maxValue;
    }

    public synchronized void setMaxValue(float newvalue) {
        this.maxValue = newvalue;
        if (this.maxValue < this.minValue) {
            this.minValue = this.maxValue;
        }

        for (int i = 0; i < this.tapInterval.length; i++) {
            if (this.tapInterval[i] < this.minValue) {
                this.tapInterval[i] = this.minValue;
            } else if (this.tapInterval[i] > this.maxValue) {
                this.tapInterval[i] = this.maxValue;
            }
        }

        this.values.forceValidation();

        this.raisePropertyChanged("MaxValue");

        this.refreshThumbPositions();
    }

    public synchronized Map<String, Float[]> getValues() {
        return this.values;
    }

    public synchronized Float[] getTapInterval() {
        return this.tapInterval;
    }

    public synchronized void setTapInterval(Float[] interval) {
        if (interval == null || interval.length < 3) {
            interval = new Float[3];
        }

        this.tapInterval = interval;

        this.raisePropertyChanged("TapInterval");
    }

    public synchronized int getTapWidth() {
        return this.tapWidth;
    }

    public synchronized void setTapWidth(int width) {
        if (width == this.tapWidth) {
            return;
        }

        this.tapWidth = width;
        this.raisePropertyChanged("TapWidth");
    }

    public synchronized float getMinZoomPercent() {
        return this.minZoom * 100f;
    }

    public synchronized void setMinZoomPercent(float zoom) {
        zoom = zoom / 100f;

        this.minZoom = zoom;
        if (this.minZoom > this.maxZoom) {
            this.maxZoom = this.minZoom;
        }
        if (this.zoom < this.minZoom) {
            this.zoom = this.minZoom;
        } else if (this.zoom > this.maxZoom) {
            this.zoom = this.maxZoom;
        }

        this.raisePropertyChanged("MinZoomPercent");

        this.refreshContentSize();
        this.refreshThumbPositions();
    }

    public synchronized float getMaxZoomPercent() {
        return this.maxZoom * 100f;
    }

    public synchronized void setMaxZoomPercent(float zoom) {
        zoom = zoom / 100f;

        this.maxZoom = zoom;
        if (this.maxZoom < this.minZoom) {
            this.minZoom = this.maxZoom;
        }
        if (this.zoom < this.minZoom) {
            this.zoom = this.minZoom;
        } else if (this.zoom > this.maxZoom) {
            this.zoom = this.maxZoom;
        }

        this.raisePropertyChanged("MaxZoomPercent");

        this.refreshContentSize();
        this.refreshThumbPositions();
    }

    public synchronized float getZoomPercent() {
        return this.zoom * 100f;
    }

    public synchronized void setZoomPercent(float newzoom) {
        newzoom = newzoom / 100f;

        if (newzoom < this.minZoom) {
            newzoom = this.minZoom;
        } else if (newzoom > this.maxZoom) {
            newzoom = this.maxZoom;
        }

        if (newzoom == this.zoom) {
            return;
        }

        this.zoom = newzoom;

        List<String> keys = this.thumbDrawable.getThumbKeys();

        if (keys.size() > 0) {
            float oldCenterX = 0f;
            int oldCenterCount = 0;
            for (String key : keys) {
                if (!this.isThumbEnabled(key) || !this.centerZoom.contains(key)) {
                    continue;
                }
                RectF thumbRect = this.thumbDrawable.getThumbRect(key);
                oldCenterX += thumbRect.centerX();
                oldCenterCount++;
            }

            if (oldCenterCount > 0) {
                oldCenterX /= oldCenterCount;
            }

            this.refreshContentSize();
            this.refreshThumbPositions();

            if (this.centerZoom.size() > 0) {
                float newCenterX = 0f;
                int newCenterCount = 0;
                for (String key : keys) {
                    if (!this.isThumbEnabled(key) || !this.centerZoom.contains(key)) {
                        continue;
                    }
                    RectF thumbRect = this.thumbDrawable.getThumbRect(key);
                    newCenterX += thumbRect.centerX();
                    newCenterCount++;
                }

                if (newCenterCount > 0) {
                    newCenterX /= newCenterCount;

                    this.scrollBy((int) (newCenterX - oldCenterX), 0);
                }
                // this.smoothScrollBy((int) (newCenterX - oldCenterX), 0);
            }
        } else {
            this.refreshContentSize();
            this.refreshThumbPositions();
        }

        this.raisePropertyChanged("ZoomPercent");

        this.refreshScroll();
    }

    @Override
    public void addView(View child) {
        super.addView(child);
    }

    @Override
    public void onChildViewAdded(View parent, View child) {
        if (parent != this) {
            return;
        }

        if (this.content != null) {
            throw new IllegalStateException("can handle only one child");
        }

        this.content = child;

        this.refreshContentSize();
        this.refreshThumbPositions();
    }

    @Override
    public void onChildViewRemoved(View parent, View child) {
        if (parent != this) {
            return;
        }

        if (child == this.content) {
            this.content = null;
        }

        this.refreshContentSize();
        this.refreshThumbPositions();
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

        if (this.thumbEnablingsSubscription != null) {
            this.thumbEnablingsSubscription.unsubscribe();
            this.thumbEnablingsSubscription = null;
        }

        if (this.valuesSubscription != null) {
            this.valuesSubscription.unsubscribe();
            this.valuesSubscription = null;
        }

        if (this.thumbEnablings != null) {
            this.thumbEnablings.dispose();
            this.thumbEnablings = null;
        }

        if (this.values != null) {
            this.values.dispose();
            this.values = null;
        }

        this.propertyChanged = null;
        this.progressTrackBegin = null;
        this.progressTrackChanged = null;
        this.progressTrackEnd = null;
        this.tapTrack = null;
    }

    @Override
    public boolean onInterceptTouchEvent(MotionEvent event) {
        if (!this.isEnabled()) {
            return false;
        }

        return true;
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        if (!this.isEnabled()) {
            return false;
        }

        this.scroller.forceFinished(true);

        int oldGesture = this.gestureRecognizer.getCurrentGesture();

        this.gestureRecognizer.handleEvent(event);

        int newGesture = this.gestureRecognizer.getCurrentGesture();
        boolean newDragging = false;
        String newDraggingKey = "";

        switch (newGesture) {
            case GestureRecognizer.GESTURE_PAN:
                if (newGesture != oldGesture) {
                    PointF sloc = this.gestureRecognizer.getPanStartLocation();
                    for (String key : this.thumbDrawable.getThumbKeys()) {
                        if (!this.isThumbEnabled(key)) {
                            continue;
                        }
                        RectF r = this.getDraggableRect(this.thumbDrawable.getThumbRect(key));
                        if (r.contains(sloc.x, sloc.y)) {
                            newDragging = true;
                            newDraggingKey = key;
                            break;
                        }
                    }
                } else {
                    newDragging = this.isDragging;
                    newDraggingKey = this.isDraggingKey;
                }

                if (newDragging) {
                    this.setPressed(true);
                    this.getValues().put(newDraggingKey, this.valuesFromPosition(this.gestureRecognizer.getPanLocation().x));
                    this.claimDrag();
                } else {
                    this.setPressed(false);
                    this.awakenScrollBars();
                    this.scrollBy(-(int) this.gestureRecognizer.getPanDelta().x, 0);
                    // this.smoothScrollBy(-(int)
                    // this.gestureRecognizer.getPanDelta().x, 0);
                    this.refreshThumbPositions();
                    this.claimDrag();
                }
                break;
            case GestureRecognizer.GESTURE_SCALE:
                this.setPressed(false);
                this.setZoomPercent(this.zoom * 100f * this.gestureRecognizer.getScaleDelta().x);
                this.claimDrag();
                break;
            case GestureRecognizer.GESTURE_TAP:
                break;
            default:
                this.setPressed(false);
                break;
        }

        if (newGesture == GestureRecognizer.GESTURE_PAN) {
            if (this.velocityTracker == null) {
                this.velocityTracker = VelocityTracker.obtain();
            }
            this.velocityTracker.addMovement(event);
        } else {
            boolean hasVelocity = false;
            int velocityX = 0;
            if (this.velocityTracker != null) {
                hasVelocity = true;
                this.velocityTracker.computeCurrentVelocity(1000, velocityMax);
                velocityX = (int) this.velocityTracker.getXVelocity();
                this.velocityTracker.recycle();
                this.velocityTracker = null;
            }

            if (oldGesture == GestureRecognizer.GESTURE_PAN && hasVelocity) {
                int smax = (int) this.contentWidth - (this.getWidth() - this.getPaddingLeft() - this.getPaddingRight());
                if (smax > 0) {
                    this.scroller.fling(this.getScrollX(), 0, -velocityX, 0, 0, smax, 0, 0);
                    this.postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            boolean finished = !scroller.computeScrollOffset();
                            if (!finished) {
                                scrollTo(scroller.getCurrX(), 0);
                                postDelayed(this, 15);
                            }
                        }
                    }, 15);
                }
            }
        }

        if (newDragging != this.isDragging) {
            if (newDragging) {
                this.onStartTrack(newDraggingKey);
            } else {
                this.onEndTrack(this.isDraggingKey);
            }
        } else if (newDragging) {
            this.onTrackChange(newDraggingKey);
        }

        this.isDragging = newDragging;
        this.isDraggingKey = newDraggingKey;

        if (newGesture == GestureRecognizer.GESTURE_TAP) {
            if (this.seekOnTaps.size() > 0) {
                this.setPressed(true);

                for (String seekOnTap : this.seekOnTaps) {
                    this.onStartTrack(seekOnTap);
                    this.getValues().put(seekOnTap, this.valuesFromPosition(this.gestureRecognizer.getTapLocation().x));
                    this.onTrackChange(seekOnTap);
                    this.onEndTrack(seekOnTap);
                }

                this.setPressed(false);
            }

            this.setTapInterval(this.valuesFromPosition(this.gestureRecognizer.getTapLocation().x));
            this.onTap();
        }

        /*
        if (newGesture == GestureRecognizer.GESTURE_NONE) {
            super.onTouchEvent(event);
        }
        */
        return true;
    }

    @Override
    public void scrollTo(int x, int y) {
        int w = this.getWidth() - this.getPaddingLeft() - this.getPaddingRight();

        float sd = (w - this.contentWidth) / 2f;

        if (sd >= 0) {
            x = 0;
        } else {
            int dx = x - this.getPaddingLeft();
            sd = -sd;
            if (dx < 0) {
                dx = 0;
            } else if (dx > 2 * sd) {
                dx = (int) (2 * sd);
            }

            x = dx + this.getPaddingLeft();
        }

        super.scrollTo(x, 0);
    }

    @Override
    public void setForeground(Drawable drawable) {
        // To be left empty
    }

    protected void onAttachedToWindow() {
        super.onAttachedToWindow();

        this.attached = true;
    }

    @Override
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();

        this.attached = false;
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);

        this.refreshContentSize();
        this.refreshThumbSizes();
        this.refreshThumbPositions();
    }

    @Override
    protected void drawableStateChanged() {
        super.drawableStateChanged();

        for (Drawable dr : this.thumbDrawable.getThumbs()) {
            dr.setState(getDrawableState());
            this.invalidateDrawable(dr);
        }
    }

    @Override
    protected void onLayout(boolean changed, int left, int top, int right, int bottom) {
        if (this.content == null || this.content.getVisibility() == GONE) {
            return;
        }

        int parentLeft = this.getPaddingLeft();
        int parentTop = this.getPaddingTop();

        this.content.layout(parentLeft, parentTop,
                (int) Math.round(parentLeft + this.contentWidth),
                (int) Math.round(parentTop + this.contentHeight));
    }

    @Override
    protected void onScrollChanged(int l, int t, int oldl, int oldt) {
        super.onScrollChanged(l, t, oldl, oldt);

        this.thumbDrawable.setScrollX(l);
        this.thumbDrawable.setScrollY(t);

        this.refreshThumbPositions();
    }

    protected void onTap() {
        if (this.tapListener != null) {
            this.tapListener.onTap(this, this.getTapInterval());
        }

        if (this.tapTrack.canExecute(this.getTapInterval())) {
            this.tapTrack.execute(this.getTapInterval());
        }
    }

    protected void onStartTrack(String key) {
        if (this.listener != null) {
            this.listener.onStartTrackingTouch(this, key);
        }

        if (this.progressTrackBegin.containsKey(key)) {
            if (this.progressTrackBegin.get(key).canExecute(null)) {
                this.progressTrackBegin.get(key).execute(key);
            }
        }
    }

    protected void onTrackChange(String key) {
        if (this.listener != null) {
            this.listener.onTrackValueChanged(this, key, this.values.get(key));
        }

        if (this.progressTrackChanged.containsKey(key)) {
            if (this.progressTrackChanged.get(key).canExecute(null)) {
                this.progressTrackChanged.get(key).execute(key);
            }
        }
    }

    protected void onEndTrack(String key) {
        if (this.listener != null) {
            this.listener.onStopTrackingTouch(this, key);
        }

        if (this.progressTrackEnd.containsKey(key)) {
            if (this.progressTrackEnd.get(key).canExecute(null)) {
                this.progressTrackEnd.get(key).execute(key);
            }
        }
    }

    private RectF getDraggableRect(RectF source) {
        RectF retval = new RectF(source);
        retval.inset(-interceptW2, -interceptH2);
        return retval;
    }

    private void raisePropertyChanged(String prop) {
        if (this.propertyChanged == null) {
            return;
        }

        this.propertyChanged.onNext(prop);
    }

    private void refreshContentSize() {
        int w = this.getWidth() - this.getPaddingLeft() - this.getPaddingRight();
        int h = this.getHeight() - this.getPaddingTop() - this.getPaddingBottom();

        this.contentWidth = Math.round(w * this.zoom);
        this.contentHeight = h;

        if (this.content == null) {
            return;
        }

        ViewGroup.LayoutParams lp = this.content.getLayoutParams();
        lp.width = (int) this.contentWidth;
        lp.height = (int) this.contentHeight;

        this.content.setLayoutParams(lp);
        // this.requestLayout();
    }

    private void refreshThumbSizes() {
        for (String key : this.thumbDrawable.getThumbKeys()) {
            this.refreshThumbSizes(key);
        }
    }

    private void refreshThumbSizes(String key) {
        RectF thumbRect = this.thumbDrawable.getThumbRect(key);
        if (thumbRect == null) {
            return;
        }

        float cx = thumbRect.centerX();
        float cy = thumbRect.centerY();
        float dw2 = interceptW2;
        float dh2 = interceptH2;

        if (this.thumbDrawable.getThumb(key) != null) {
            dw2 = this.thumbDrawable.getThumb(key).getIntrinsicWidth() / 2f;
            dh2 = this.thumbDrawable.getThumb(key).getIntrinsicHeight() / 2f;
            if (dw2 <= 0f) {
                dw2 = interceptW2;
            }
            if (dh2 <= 0f) {
                dh2 = interceptH2;
            }
        }

        thumbRect.set(cx - dw2, cy - dh2, cx + dw2, cy + dh2);

        this.thumbDrawable.setThumbRect(key, thumbRect);

        this.invalidateDrawable(this.thumbDrawable);
    }

    private void refreshThumbPositions() {
        for (String key : this.thumbDrawable.getThumbKeys()) {
            this.refreshThumbPositions(key);
        }
    }

    private void refreshThumbPositions(String key) {
        if (!this.attached) {
            return;
        }

        RectF thumbRect = this.thumbDrawable.getThumbRect(key);
        if (thumbRect == null) {
            return;
        }

        float newtop = this.getPaddingTop()
                + (this.getHeight() - this.getPaddingTop() - this.getPaddingBottom() - thumbRect
                .height()) / 2f;
        float newleft = this.positionFromValue(this.values.get(key)[1]) - thumbRect.width() / 2f;

        thumbRect.offsetTo(newleft, newtop);

        this.thumbDrawable.setThumbRect(key, thumbRect);

        this.postInvalidate();
        // this.invalidateDrawable(this.thumbDrawable);
    }

    private void refreshThumbEnablings(String key) {
        if (!this.attached) {
            return;
        }

        Drawable dr = this.thumbDrawable.getThumb(key);
        if (dr == null) {
            return;
        }

        dr.setVisible(this.thumbEnablings.get(key), true);
    }

    private void refreshScroll() {
        this.scrollBy(0, 0);
    }

    private float valueFromPosition(float position) {
        if (this.contentWidth <= 0f) {
            return 0f;
        }

        return this.minValue + (this.maxValue - this.minValue)
                * (position - this.getPaddingLeft() + this.getScrollX()) / this.contentWidth;
    }

    private Float[] valuesFromPosition(Float centerPosition) {
        return new Float[]{
                this.valueFromPosition(centerPosition - this.tapWidth / 2f),
                this.valueFromPosition(centerPosition),
                this.valueFromPosition(centerPosition + this.tapWidth / 2f)
        };
    }

    private float positionFromValue(float val) {
        int retval = this.getPaddingLeft() - this.getScrollX();
        if (this.maxValue != this.minValue) {
            retval += this.contentWidth * (val - this.minValue)
                    / (this.maxValue - this.minValue);
        }

        return retval;
    }

    private void claimDrag() {
        if (!this.attached || this.getParent() == null) {
            return;
        }

        this.getParent().requestDisallowInterceptTouchEvent(true);
    }

    public static interface OnScrollableSeekBarChangeListener {

        void onTrackValueChanged(ScrollableSeekbar seekBar, String key, Float[] valueRange);

        void onStartTrackingTouch(ScrollableSeekbar seekBar, String key);

        void onStopTrackingTouch(ScrollableSeekbar seekBar, String key);
    }

    public static interface OnScrollableSeekbarTapListener {

        void onTap(ScrollableSeekbar seekBar, Float[] valueRange);
    }

    private static class GestureRecognizer {

        public static final int GESTURE_NONE = 0;

        //public static final int GESTURE_DRAG = 1;
        public static final int GESTURE_PAN = 2;

        public static final int GESTURE_SCALE = 4;

        public static final int GESTURE_TAP = 8;

        public static final int GESTURE_ALL = 65535;

        private static final int motionDetachTolerance = 10;

        //private RectF draggableRect;
        private int registeredGestures;

        private int currentGesture;

        private PointF downLocation;

        private PointF upLocation;

        private PointF panStartLocation;

        private PointF scaleStartSize;

        private PointF panLocation;

        private PointF scaleSize;

        private PointF tapLocation;

        public GestureRecognizer() {
            this(GESTURE_ALL);
        }

        public GestureRecognizer(int registeredGestures) {
            this(registeredGestures, null);
        }

        public GestureRecognizer(int registeredGestures, RectF draggableRect) {
            this.registeredGestures = registeredGestures;
        }

        private static PointF getPoint(MotionEvent ev) {
            int cnt = ev.getPointerCount();
            if (cnt < 1) {
                return new PointF();
            }

            float x = 0;
            float y = 0;

            PointerCoords c = new PointerCoords();

            for (int ptrId = 0; ptrId < cnt; ptrId++) {
                ev.getPointerCoords(ptrId, c);

                x += c.x;
                y += c.y;

                c.x = 0f;
                c.y = 0f;
            }

            return new PointF(x / cnt, y / cnt);
        }

        private static PointF getSize(MotionEvent ev) {
            int cnt = ev.getPointerCount();
            if (cnt < 2) {
                return new PointF(0f, 0f);
            }

            float minX = Float.MAX_VALUE;
            float minY = Float.MAX_VALUE;
            float maxX = Float.MIN_VALUE;
            float maxY = Float.MIN_VALUE;

            PointerCoords c = new PointerCoords();

            for (int ptrId = 0; ptrId < cnt; ptrId++) {
                ev.getPointerCoords(ptrId, c);

                if (c.x < minX) {
                    minX = c.x;
                }
                if (c.x > maxX) {
                    maxX = c.x;
                }
                if (c.y < minY) {
                    minY = c.y;
                }
                if (c.y > maxY) {
                    maxY = c.y;
                }
            }

            return new PointF(maxX - minX, maxY - minY);
        }

        public void handleEvent(MotionEvent ev) {
            int newGesture = GESTURE_NONE;

            if (ev.getPointerCount() > 0) {
                if ((this.currentGesture & (GESTURE_PAN & this.registeredGestures)) == 0) {
                    newGesture |= GESTURE_PAN;
                } else {
                    newGesture = this.currentGesture;
                }
            }

            if (ev.getPointerCount() > 1) {
                newGesture |= GESTURE_SCALE;
            }

            switch (ev.getActionMasked()) {
                case MotionEvent.ACTION_MOVE:
                    break;
                case MotionEvent.ACTION_DOWN:
                    this.downLocation = getPoint(ev);
                    this.reset();
                    return;
                case MotionEvent.ACTION_POINTER_DOWN:
                    return;
                case MotionEvent.ACTION_POINTER_UP:
                    return;
                case MotionEvent.ACTION_UP:
                    this.upLocation = getPoint(ev);
                    if (this.currentGesture != GESTURE_NONE) {
                        this.reset();
                        return;
                    }
                    newGesture = GESTURE_TAP;
                    break;
                case MotionEvent.ACTION_CANCEL:
                    this.reset();
                    return;
                default:
                    return;
            }

            newGesture = newGesture & this.registeredGestures;

            if ((newGesture & GESTURE_SCALE) != 0) {
                if (this.currentGesture != GESTURE_SCALE) {
                    this.reset();
                }

                PointF sz = getSize(ev);

                this.scaleStartSize = this.scaleSize;
                if (this.scaleStartSize == null) {
                    this.scaleStartSize = sz;
                }
                this.scaleSize = sz;

                newGesture = GESTURE_SCALE;
                this.currentGesture = GESTURE_SCALE;
            }

            if ((newGesture & GESTURE_PAN) != 0) {
                if (this.currentGesture != GESTURE_PAN) {
                    this.reset();
                }

                PointF pt = getPoint(ev);

                int pdelta = 0;
                if (this.downLocation != null) {
                    pdelta = (int) Math.sqrt((this.downLocation.x - pt.x) * (this.downLocation.x - pt.x) + (this.downLocation.y - pt.y)
                            * (this.downLocation.y - pt.y));
                }

                if (pdelta > motionDetachTolerance) {
                    this.panStartLocation = this.panLocation;
                    if (this.panStartLocation == null) {
                        this.panStartLocation = pt;
                    }
                    this.panLocation = pt;

                    newGesture = GESTURE_PAN;
                    this.currentGesture = GESTURE_PAN;
                }
            }

            if ((newGesture & GESTURE_TAP) != 0) {
                if (this.currentGesture != GESTURE_TAP) {
                    this.reset();
                }

                PointF pt = getPoint(ev);

                this.tapLocation = pt;

                newGesture = GESTURE_TAP;
                this.currentGesture = GESTURE_TAP;
            }
        }

        public int getCurrentGesture() {
            return this.currentGesture;
        }

        public PointF getPanLocation() {
            return this.panLocation;
        }

        public PointF getPanStartLocation() {
            return this.panStartLocation;
        }

        public PointF getTapLocation() {
            return this.tapLocation;
        }

        public PointF getPanDelta() {
            if (this.panLocation == null || this.panStartLocation == null) {
                return new PointF();
            }

            return new PointF(this.panLocation.x - this.panStartLocation.x, this.panLocation.y
                    - this.panStartLocation.y);
        }

        public PointF getScaleSize() {
            return this.scaleSize;
        }

        public PointF getScaleStartSize() {
            return this.scaleStartSize;
        }

        public PointF getScaleDelta() {
            if (this.scaleSize == null || this.scaleStartSize == null) {
                return new PointF(1f, 1f);
            }

            return new PointF(this.scaleStartSize.x != 0f ? this.scaleSize.x
                    / this.scaleStartSize.x : 1f, this.scaleStartSize.y != 0f ? this.scaleSize.y
                    / this.scaleStartSize.y : 1f);
        }

        public void reset() {
            this.currentGesture = GESTURE_NONE;
            this.panStartLocation = this.panLocation = this.scaleStartSize = this.scaleSize = this.tapLocation = null;
        }
    }

    private static class DefaultThumb extends Drawable {

        private int width;

        private int height;

        private int alpha;

        private Paint fill;

        public DefaultThumb(int w, int h) {
            this.width = w;
            this.height = h;
            this.alpha = 255;
            this.fill = new Paint();
            this.fill.setColor(Color.WHITE);
        }

        @Override
        public void draw(Canvas canvas) {
            Rect r = this.getBounds();
            canvas.drawCircle(r.centerX(), r.centerY(), r.width() / 2, this.fill);
        }

        @Override
        public void setAlpha(int alpha) {
            this.alpha = alpha;
        }

        @Override
        public void setColorFilter(ColorFilter cf) {
        }

        @Override
        public int getOpacity() {
            return PixelFormat.TRANSPARENT;
        }

        @Override
        public int getIntrinsicWidth() {
            return this.width;
        }

        @Override
        public int getIntrinsicHeight() {
            return this.height;
        }
    }

    private static class ThumbDrawable extends Drawable {

        private static BindableMap.IValidator<String, Boolean> enablingsValidator = new BindableMap.IValidator<String, Boolean>() {
            @Override
            public boolean isValid(Map<String, Boolean> map, String key, Boolean value) {
                return value != null;
            }

            @Override
            public boolean canMakeValid(Map<String, Boolean> map, String key, Boolean value) {
                return true;
            }

            @Override
            public Boolean makeValid(Map<String, Boolean> map, String key, Boolean value) {
                if (value == null) {
                    return Boolean.FALSE;
                }

                return value;
            }
        };

        private int scrollX;

        private int scrollY;

        private int alpha = 255;

        private Drawable.Callback callback = new Callback() {
            @Override
            public void unscheduleDrawable(Drawable who, Runnable what) {
                ThumbDrawable.this.unscheduleSelf(what);
            }

            @Override
            public void scheduleDrawable(Drawable who, Runnable what, long when) {
                ThumbDrawable.this.scheduleSelf(what, when);
            }

            @Override
            public void invalidateDrawable(Drawable who) {
                ThumbDrawable.this.invalidateSelf();
            }
        };

        // TODO can be grouped into a single map of classes...
        private Map<String, Drawable> thumbs;

        private Map<String, RectF> thumbRects;

        public ThumbDrawable() {
            this.thumbRects = new HashMap<String, RectF>();
            this.thumbs = new HashMap<String, Drawable>();
        }

        public void setThumb(String key, Drawable thumb) {
            if (this.thumbs.containsKey(key)) {
                this.thumbs.get(key).setCallback(null);
            }
            thumb.setCallback(this.callback);
            this.thumbs.put(key, thumb);
        }

        public List<String> getThumbKeys() {
            return Iterables.monoFrom(this.thumbs.keySet()).toList();
        }

        public List<Drawable> getThumbs() {
            return Iterables.monoFrom(this.thumbs.values()).toList();
        }

        public Drawable getThumb(String key) {
            return this.thumbs.get(key);
        }

        public void setThumbRect(String key, RectF rect) {
            if (rect == null) {
                rect = new RectF();
            }

            this.thumbRects.put(key, rect);
        }

        public RectF getThumbRect(String key) {
            if (!this.thumbRects.containsKey(key)) {
                return new RectF();
            }

            return this.thumbRects.get(key);
        }

        public int getScrollX() {
            return this.scrollX;
        }

        public void setScrollX(int sx) {
            this.scrollX = sx;
        }

        public int getScrollY() {
            return this.scrollY;
        }

        public void setScrollY(int sy) {
            this.scrollY = sy;
        }

        @Override
        public void setColorFilter(ColorFilter cf) {
        }

        @Override
        public void setAlpha(int alpha) {
            this.alpha = alpha;
        }

        @Override
        public int getOpacity() {
            return PixelFormat.TRANSPARENT;
        }

        @Override
        public void draw(Canvas canvas) {
            if (this.thumbs.size() < 1 || this.thumbRects.size() < 1) {
                return;
            }

            // D'OH!
            canvas.save();
            canvas.translate(this.scrollX, 0);

            for (String k : this.thumbs.keySet()) {
                if (!this.thumbRects.containsKey(k)) {
                    continue;
                }

                Drawable thumb = this.thumbs.get(k);
                RectF thumbRect = this.thumbRects.get(k);

                thumb.setBounds((int) thumbRect.left, (int) thumbRect.top, (int) thumbRect.right, (int) thumbRect.bottom);
                thumb.draw(canvas);
            }

            canvas.restore();
        }
    }

    private static class VisibilityAnimatedDrawable extends Drawable {

        private long animationDuration = 800;

        private long animationRate = 25;

        private Drawable toAnimate;

        private Subscription animationSubscription;

        private boolean isAnimating;

        public VisibilityAnimatedDrawable(Drawable toAnimate, boolean initialVisibility) {
            this.toAnimate = toAnimate;
            super.setVisible(initialVisibility, false);
        }

        public VisibilityAnimatedDrawable(Drawable toAnimate, boolean initialVisibility, long duration, long rate) {
            this.animationDuration = duration;
            this.animationRate = rate;
            this.toAnimate = toAnimate;
            super.setVisible(initialVisibility, false);
        }

        @Override
        public void draw(Canvas canvas) {
            if (this.isVisible() || this.isAnimating) {
                this.toAnimate.draw(canvas);
            }
        }

        @Override
        public void setAlpha(int alpha) {
        }

        @Override
        public void setColorFilter(ColorFilter cf) {
            this.toAnimate.setColorFilter(cf);
        }

        @Override
        public int getOpacity() {
            return this.toAnimate.getOpacity();
        }

        @Override
        public int getIntrinsicWidth() {
            return this.toAnimate.getIntrinsicWidth();
        }

        @Override
        public int getIntrinsicHeight() {
            return this.toAnimate.getIntrinsicHeight();
        }

        @Override
        public void setBounds(int left, int top, int right, int bottom) {
            this.toAnimate.setBounds(left, top, right, bottom);
        }

        @Override
        public boolean setVisible(boolean visible, boolean restart) {
            if (this.isVisible() == visible) {
                return false;
            }

            this.isAnimating = false;
            if (this.animationSubscription != null) {
                this.animationSubscription.unsubscribe();
                this.animationSubscription = null;
            }

            Float start = 0f;
            Float end = 255f;
            if (!visible) {
                start = 255f;
                end = 0f;
            }

            this.toAnimate.setAlpha((int) (float) start);

            this.isAnimating = true;
            this.animationSubscription = linear(start, end, animationDuration, animationRate, TimeUnit.MILLISECONDS,
                    AndroidSchedulers.mainThread())
                    .subscribe(new rx.Observer<Float>() {
                        @Override
                        public void onCompleted() {
                            isAnimating = false;
                            animationSubscription = null;
                        }

                        @Override
                        public void onError(Throwable e) {
                            isAnimating = false;
                            animationSubscription = null;
                        }

                        @Override
                        public void onNext(Float aFloat) {
                            if (!isAnimating) {
                                return;
                            }
                            toAnimate.setAlpha((int) (float) aFloat);
                            invalidateSelf();
                        }
                    });

            return super.setVisible(visible, restart);
        }
    }
}
