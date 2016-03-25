package solutions.alterego.androidbound.android.ui;

import android.content.Context;
import android.content.res.ColorStateList;
import android.util.AttributeSet;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;

import solutions.alterego.androidbound.interfaces.ICommand;

public class BindableButton extends Button implements OnClickListener {

    private ICommand onClick = ICommand.empty;

    public BindableButton(Context context, AttributeSet attrs) {
        super(context, attrs);
        setOnClickListener(this);
    }

    public BindableButton(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        setOnClickListener(this);
    }

    public ICommand getClick() {
        return onClick;
    }

    public void setClick(ICommand value) {
        if (value == null) {
            onClick = ICommand.empty;
            return;
        }
        onClick = value;
    }

    @Override
    public void onClick(View v) {
        if (onClick.canExecute(null)) {
            onClick.execute(null);
        }
    }

    public ColorStateList getTextColor() {
        return super.getTextColors();
    }

    public void setTextColor(int color) {
        super.setTextColor(color);
    }

    public int getBackgroundColor() {
        return 0;
    }

    public void setBackgroundColor(int color) {
        super.setBackgroundColor(color);
    }

    public void setTextColorState(ColorStateList colors) {
        super.setTextColor(colors);
    }
}
