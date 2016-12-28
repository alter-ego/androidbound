package solutions.alterego.androidbound.viewresolvers.interfaces;


import android.content.Context;
import android.util.AttributeSet;
import android.view.View;

import solutions.alterego.androidbound.interfaces.INeedsLogger;

public interface IViewResolver extends INeedsLogger {

    View createView(String name, Context context, AttributeSet attrs);
}
