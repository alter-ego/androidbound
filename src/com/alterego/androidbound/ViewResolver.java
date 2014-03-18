
package com.alterego.androidbound;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;

import com.alterego.advancedandroidlogger.interfaces.IAndroidLogger;
import com.alterego.androidbound.android.ui.BindableButton;
import com.alterego.androidbound.android.ui.BindableGridView;
import com.alterego.androidbound.android.ui.BindableHorizontalListView;
import com.alterego.androidbound.android.ui.BindableHorizontalScrollView;
import com.alterego.androidbound.android.ui.BindableImageView;
import com.alterego.androidbound.android.ui.BindableLinearLayout;
import com.alterego.androidbound.android.ui.BindableListView;
import com.alterego.androidbound.android.ui.BindableRelativeLayout;
import com.alterego.androidbound.android.ui.BindableScrollView;
import com.alterego.androidbound.android.ui.BindableSeekbar;
import com.alterego.androidbound.android.ui.BindableSwitch;
import com.alterego.androidbound.android.ui.BindableTextView;
import com.alterego.androidbound.android.ui.BindableToggleButton;
import com.alterego.androidbound.android.ui.BindableVideoView;
import com.alterego.androidbound.helpers.Reflector;
import com.alterego.androidbound.interfaces.IViewResolver;

import java.util.HashMap;
import java.util.Map;

public class ViewResolver implements IViewResolver {
    private static final Class<?>[] oneArg = new Class<?>[] {
        Context.class
    };

    private static final Class<?>[] twoArgs = new Class<?>[] {
            Context.class, AttributeSet.class
    };

    @SuppressWarnings("serial")
    private static final Map<String, Class<?>> mappings = new HashMap<String, Class<?>>() {
        {
            put("android.widget.TextView", BindableTextView.class);
            put("android.widget.ListView", BindableListView.class);
            put("android.widget.ImageView", BindableImageView.class);
            put("android.widget.VideoView", BindableVideoView.class);
            put("android.widget.Button", BindableButton.class);
            put("android.widget.SeekBar", BindableSeekbar.class);
            put("android.widget.HorizontalScrollView", BindableHorizontalScrollView.class);
            put("android.widget.HorizontalListView", BindableHorizontalListView.class);
            put("android.widget.ScrollView", BindableScrollView.class);
            put("android.widget.RelativeLayout", BindableRelativeLayout.class);
            put("android.widget.LinearLayout", BindableLinearLayout.class);
            put("android.widget.GridView", BindableGridView.class);
            if (android.os.Build.VERSION.SDK_INT > 13)
                put("android.widget.Switch", BindableSwitch.class);
            put("android.widget.ToggleButton", BindableToggleButton.class);
        }
    };

    private IAndroidLogger logger;

    public ViewResolver(IAndroidLogger logger) {
        setLogger(logger);
    }

    @Override
    public View createView(String name, Context context, AttributeSet attrs) {
        Class<?> resolvedClass = resolveName(name);
        if (resolvedClass == null) {
            logger.warning("View not found for name " + name);
            return null;
        }
        try {
            if (Reflector.canCreateInstance(resolvedClass, twoArgs)) {
                return Reflector.createInstance(resolvedClass, twoArgs, new Object[] {
                        context, attrs
                });
            }

            if (Reflector.canCreateInstance(resolvedClass, oneArg)) {
                return Reflector.createInstance(resolvedClass, oneArg, new Object[] {
                    context
                });
            }

            if (Reflector.canCreateInstance(resolvedClass, null)) {
                return Reflector.createInstance(resolvedClass, null, null);
            }

            throw new Exception("constructor not found");
            // Constructor<?> constructor =
            // resolvedClass.getConstructor(Context.class, AttributeSet.class);
            // View instance = (View) constructor.newInstance(context, attrs);
            // return instance;
        } catch (Exception e) {
            logger.warning("failed creating instance of class " + resolvedClass + ", message: " + e.getMessage());
        }

        return null;
    }

    private Class<?> resolveName(String name) {
        String result = name;

        if (name.contains(".")) {
            result = name;
        } else if (name.equals("View") || name.equals("ViewGroup")) {
            result = "android.view." + name;
        } else {
            result = "android.widget." + name;
        }

        if (mappings.containsKey(result)) {
            return mappings.get(result);
        }

        try {
            logger.debug("Resolving " + name + " with " + result);
            return Class.forName(result);
        } catch (ClassNotFoundException e) {
            return null;
        }
    }

    @Override
    public void setLogger(IAndroidLogger logger) {
        this.logger = logger.getLogger(this);
    }
}
