package solutions.alterego.androidbound.android.interfaces;


import android.content.Context;
import android.util.AttributeSet;
import android.view.View;

import solutions.alterego.androidbound.interfaces.IDisposable;
import solutions.alterego.androidbound.interfaces.INeedsLogger;

public interface IViewResolver extends INeedsLogger, IDisposable {

    View createView(String name, Context context, AttributeSet attrs);
}
