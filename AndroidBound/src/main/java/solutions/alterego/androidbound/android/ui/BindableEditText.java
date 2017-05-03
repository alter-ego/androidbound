package solutions.alterego.androidbound.android.ui;

import android.content.Context;
import android.content.res.ColorStateList;
import android.graphics.Typeface;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.StateListDrawable;
import android.os.Build;
import android.support.v7.widget.AppCompatEditText;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.AttributeSet;
import android.view.View;

import rx.Observable;
import solutions.alterego.androidbound.binding.interfaces.INotifyPropertyChanged;
import solutions.alterego.androidbound.interfaces.ICommand;

public class BindableEditText extends AppCompatEditText implements INotifyPropertyChanged {

    private BindableViewDelegate mDelegate;

    public BindableEditText(Context context) {
        this(context, null);
    }

    public BindableEditText(Context context, AttributeSet attrs) {
        super(context, attrs);
        mDelegate = createDelegate(this);
    }

    public BindableEditText(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        mDelegate = createDelegate(this);
    }

    private final TextWatcher textWatcher = new TextWatcher() {
        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {

        }

        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {

        }

        @Override
        public void afterTextChanged(Editable s) {
            mDelegate.getPropertyChanged().onNext("TextString");
        }
    };

    @Override
    protected void onAttachedToWindow() {
        super.onAttachedToWindow();
        addTextChangedListener(textWatcher);
    }

    @Override
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        removeTextChangedListener(textWatcher);
    }

    /****** beginning of the delegated methods, to be copy/pasted in every bindable view ******/

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

    public int getBackgroundDrawable() {
        return 0;
    }

    public void setBackgroundDrawable(Drawable res) {
        super.setBackgroundDrawable(res);
    }

    public Typeface getTypeface() {
        return super.getTypeface();
    }

    public void setTypeface(Typeface font) {
        super.setTypeface(font);
    }

    public ColorStateList getTextColor() {
        return super.getTextColors();
    }

    public void setTextColor(int color) {
        super.setTextColor(color);
    }

    public ColorStateList getTextColorState() {
        return super.getTextColors();
    }

    public void setTextColorState(ColorStateList colors) {
        super.setTextColor(colors);
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
    }

    /****** end of the delegated methods, to be copy/pasted in every bindable view ******/

    public String getTextString() {
        return getText().toString();
    }

    public void setTextString(String text) {
        setText(text);
    }

    public void setEditTextCharSequence(CharSequence text) {
        super.setText(text);
    }

    public CharSequence getEditTextCharSequence() {
        return getTextString();
    }

    public void setHintText(CharSequence hint) {
        setHint(hint);
    }

    public CharSequence getHintText() {
        return getHint();
    }

    public void setHintColor(int color) {
        setHintTextColor(color);
    }

    public void setHintColor(ColorStateList color) {
        setHintTextColor(color);
    }

    public void setErrorText(CharSequence text) {
        setError(text);
    }

    public CharSequence getErrorText() {
        return getError();
    }
}