
package com.alterego.androidbound.android.ui;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.Typeface;
import android.text.Layout;
import android.text.StaticLayout;
import android.text.TextPaint;
import android.util.AttributeSet;
import android.view.View;

import com.alterego.androidbound.BindingResources;
import com.alterego.androidbound.ViewBinder;
import com.alterego.androidbound.interfaces.INotifyPropertyChanged;
import com.alterego.androidbound.zzzztoremove.reactive.IObservable;
import com.alterego.androidbound.zzzztoremove.reactive.ISubject;
import com.alterego.androidbound.zzzztoremove.reactive.Subject;

public class BindableExtendedTextView extends View implements INotifyPropertyChanged {

    private Context mContext;
    private ISubject<String> propertyChanged;

    private boolean disposed;
    private Typeface mTypeface;
    private int mTextColor;

    public BindableExtendedTextView(Context context) {
        super(context);
        mContext = context;
        initCustomTextView();
    }

    public BindableExtendedTextView(Context context, AttributeSet attrs) {
        super(context, attrs);
        mContext = context;
        readStyleable(mContext, attrs);
        initCustomTextView();
    }

    public BindableExtendedTextView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        mContext = context;
        readStyleable(mContext, attrs);
        initCustomTextView();
    }

    private void readStyleable(Context context, AttributeSet attrs) {

//        setTextColor(a.getColor(R.styleable.CustomTextView_textColor, 0xFF000000));

//        int textSize = a.getDimensionPixelOffset(R.styleable.CustomTextView_textSize, 0);
//        if (textSize > 0) {
//            mTextSize = textSize;
//            setTextSize(textSize);
//        }

        mTextSize = getTextSize(attrs);
        //int color = getTextColor(attrs);
        mTextColor = getTextColor(attrs);
    }

    private static int getTextSize(AttributeSet attrs) {
        int value = attrs.getAttributeIntValue("", BindingResources.attr.BindableTextView.textSize, 0);
        ViewBinder.getLogger().debug("value = " + value);
//        float value2 = attrs.getAttributeFloatValue(null, BindingResources.attr.BindableTextView.textSize, 0f);
//        ViewBinder.getLogger().debug("value2 = " + value2);

        return value;
    }

    private static int getTextColor(AttributeSet attrs) {
        int value = 0;
        ViewBinder.getLogger().debug("");
        try {
            value = attrs.getAttributeResourceValue(null, BindingResources.attr.BindableTextView.textColor, 0xFF000000);
            ViewBinder.getLogger().debug("value = " + value);
            int value2 = attrs.getAttributeIntValue(null, BindingResources.attr.BindableTextView.textColor, 0xFF000000);
            ViewBinder.getLogger().debug("value2 = " + value2);
        } catch (Exception e) {
            ViewBinder.getLogger().warning("exception = " + e);
        }
        return value;
    }

//    public Typeface getTypeface() {
//        return super.getTypeface();
//    }

//    public void setTypeface(Typeface font) {
//        super.setTypeface(font);
//        if (this.disposed || this.propertyChanged == null) {
//            return;
//        }
//        this.propertyChanged.onNext("Typeface");
//    }

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
    public IObservable<String> onPropertyChanged() {
        if (this.propertyChanged == null) {
            this.propertyChanged = new Subject<String>();
        }

        return this.propertyChanged;
    }

//    public void setTextColor(int color) {
//        super.setTextColor(color);
//    }

//    public void setTextColor(ColorStateList colors) {
//        super.setTextColor(colors);
//    }
//
//    public ColorStateList getTextColor() {
//        return super.getTextColors();
//    }

    public void setBackgroundColor(int color) {
        super.setBackgroundColor(color);
    }

    public int getBackgroundColor() {
        return 0;
    }


    /**
     * ************************ from CUSTOM TEXT VIEW **************************
     */

    private Paint mPaint;
    private TextPaint mTextPaint;
    private String mText;
    private int mAscent;
    private Rect mRect;
    private int mWidth = 0;
    private int mHeight = 0;
    private int mTextSize = 0;
    private StaticLayout text_to_print = null;
    private Bitmap mBackgroundDrawable = null;
    private Layout.Alignment mAlignmentHorizontal = Layout.Alignment.ALIGN_NORMAL;


    private static final float mSpacingMult = 1.0f;
    private static final float mSpacingAdd = 0.0f;

    private final void initCustomTextView() {
        mPaint = new Paint();
        mTextPaint = new TextPaint();
        mRect = new Rect();
        mPaint.setAntiAlias(true);
        mTextPaint.setAntiAlias(true);
        // Must manually scale the desired text size to match screen density
        if (mTextSize == 0) {
            mPaint.setTextSize(16 * mContext.getResources().getDisplayMetrics().density);
            mTextPaint.setTextSize(16 * mContext.getResources().getDisplayMetrics().density);
        }

        mPaint.setColor(mTextColor);
        mTextPaint.setColor(mTextColor);
        //setPadding (mPaddingLeft, mPaddingTop, mPaddingRight, mPaddingBottom);
    }

    /**
     * Gets the text displayed in this label
     */
    public String getText() {
        return mText;
    }

    /**
     * Sets the text to display in this label
     *
     * @param text The text to display. This will be drawn as one line.
     */
    public void setText(String text) {
        ViewBinder.getLogger().debug("text = " + text);
        mText = text;
        this.propertyChanged.onNext("Text");
        requestLayout();
        invalidate();
    }


    /**
     * Sets the text size for this label
     *
     * @param size Font size
     */
    public void setTextSize(int size) {
        // This text size has been pre-scaled by the getDimensionPixelOffset method
        ViewBinder.getLogger().debug("text size = " + size);
        mTextSize = size;
        ViewBinder.getLogger().debug("");
        if (mPaint != null)
            mPaint.setTextSize(size);
        ViewBinder.getLogger().debug("");
        if (mTextPaint != null)
            mTextPaint.setTextSize(size);
        ViewBinder.getLogger().debug("");
        requestLayout();
        ViewBinder.getLogger().debug("");
        invalidate();
        ViewBinder.getLogger().debug("");
    }

    /**
     * Sets the text typeface for this label
     *
     * @param font Typeface
     */
    public void setTypeface(Typeface font) {
        //super.setTypeface(font);
        ViewBinder.getLogger().debug("typeface = " + font);
        mTypeface = font;
        if (this.disposed || this.propertyChanged == null) {
            return;
        }
        // This text size has been pre-scaled by the getDimensionPixelOffset method
        if (mPaint != null)
            mPaint.setTypeface(font);
        if (mTextPaint != null)
            mTextPaint.setTypeface(font);
        this.propertyChanged.onNext("Typeface");
        requestLayout();
        invalidate(); //TODO do we need this along with propertyChanged?
    }

    public Typeface getTypeface() {
        return mTypeface;
    }

    /**
     * Sets the text color for this label.
     *
     * @param color ARGB value for the text
     */
    public void setTextColor(int color) {
        //super.setTextColor(color);
        ViewBinder.getLogger().debug("text color = " + color);
        if (mPaint != null)
            mPaint.setColor(color);
        ViewBinder.getLogger().debug("");
        if (mTextPaint != null)
            mTextPaint.setColor(color);
        ViewBinder.getLogger().debug("");
        invalidate();
    }

    public int getTextColor() {
        return mPaint.getColor();
    }

//    public void setTextColor(String color) {
//        //super.setTextColor(color);
//        ViewBinder.getLogger().debug("text string color = " + color);
//        setTextColor(Integer.valueOf(color));
//    }

    /**
     * @see android.view.View#measure(int, int)
     */
    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        if (mText == null) mText = "";
        mPaint.getTextBounds(mText, 0, mText.length(), mRect);
        ViewBinder.getLogger().debug("onMeasure mRect of mPaint.getTextBounds, mText = " + mText + ", mRect.width() = " + mRect.width() + ", mRect.height() = " + mRect.height());
        mWidth = measureWidth(widthMeasureSpec);
        mHeight = measureHeight(heightMeasureSpec);
        //if (Preferences.isDebug) { Log.d(CLASS,"onMeasure dopo la misurazione, mWidth = " + mWidth + ", mHeight = " + mHeight); }
        setMeasuredDimension(mWidth, mHeight);

    }

    /**
     * Determines the width of this view
     *
     * @param measureSpec A measureSpec packed into an int
     * @return The width of the view, honoring constraints from measureSpec
     */
    private int measureWidth(int measureSpec) {

        int correction_factor = (int) (5 * mContext.getResources().getDisplayMetrics().density);
        int result = 0;
        int specMode = MeasureSpec.getMode(measureSpec);
        int specSize = MeasureSpec.getSize(measureSpec);

        if (specMode == MeasureSpec.EXACTLY) {
            // We were told how big to be
            result = specSize;
            ViewBinder.getLogger().debug("measureWidth EXACTLY, width = " + result);
        } else {
            result = mRect.width() + getPaddingLeft() + getPaddingRight() + correction_factor;
            ViewBinder.getLogger().debug("measureWidth, width = " + result);
            if (specMode == MeasureSpec.AT_MOST) {
                // Respect AT_MOST value if that was what is called for by measureSpec
                result = Math.min(result, specSize);
            }
        }

        return result;
    }

    /**
     * Determines the height of this view
     *
     * @param measureSpec A measureSpec packed into an int
     * @return The height of the view, honoring constraints from measureSpec
     */
    private int measureHeight(int measureSpec) {
        int result = 0;
        int specMode = MeasureSpec.getMode(measureSpec);
        int specSize = MeasureSpec.getSize(measureSpec);

        //if (Preferences.isDebug) { Log.d(CLASS,"measureHeight, specSize = " + specSize); }
        if (mText == null) mText = ""; //linea vuota
        //if (mWidth < 0) mWidth = 0;
        if (mWidth > 10000) mWidth = 0;
        //if (Preferences.isDebug) { Log.d(CLASS,"measureHeight, mWidth = " + mWidth); }
        text_to_print = new StaticLayout(mText, mTextPaint, Math.abs((mWidth - getPaddingLeft() - getPaddingRight())), mAlignmentHorizontal, mSpacingMult, mSpacingAdd, false);
        ViewBinder.getLogger().debug("measureHeight, height of static layout = " + text_to_print.getHeight());

        mAscent = (int) mPaint.ascent();
        if (specMode == MeasureSpec.EXACTLY) {
            // We were told how big to be
            result = specSize;
        } else {
            // Measure the text (beware: ascent is a negative number)
            /*result = (int) (-mAscent + mTextPaint.descent()) + getPaddingTop() + getPaddingBottom();
            if (Preferences.isDebug) { Log.d(CLASS,"measureHeight, old height = " + result); }
            result = (int) mRect.height() + getPaddingTop() + getPaddingBottom();
            if (Preferences.isDebug) { Log.d(CLASS,"measureHeight, new height = " + result); }
        	 */
            if (mText.length() > 0) {
                result = text_to_print.getHeight() + getPaddingTop() + getPaddingBottom();
                ViewBinder.getLogger().debug("measureHeight, total height = " + result);
            } else {
                result = (int) (-mAscent + mPaint.descent()) + getPaddingTop() + getPaddingBottom();
            }
            if (specMode == MeasureSpec.AT_MOST) {
                // Respect AT_MOST value if that was what is called for by measureSpec
                result = Math.min(result, specSize);
            }
        }
        return result;
    }

    /**
     * Render the text
     *
     * @see android.view.View#onDraw(android.graphics.Canvas)
     */
    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        canvas.save();
        if (mBackgroundDrawable != null) canvas.drawBitmap(mBackgroundDrawable, 0.0f, 0.0f, null);
        canvas.translate(getPaddingLeft(), getPaddingTop());
        text_to_print.draw(canvas);
        canvas.restore();
    }


}
