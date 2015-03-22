package solutions.alterego.androidbound.interfaces;


import android.content.Context;
import android.util.AttributeSet;
import android.view.View;

public interface IViewResolver extends INeedsLogger {
	public abstract View createView(String name, Context context, AttributeSet attrs);
}
