package solutions.alterego.androidbound.android.viewresolvers;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;

import java.util.HashMap;
import java.util.Map;

import solutions.alterego.androidbound.android.interfaces.IViewResolver;
import solutions.alterego.androidbound.android.ui.BindableButton;
import solutions.alterego.androidbound.android.ui.BindableEditText;
import solutions.alterego.androidbound.android.ui.BindableFrameLayout;
import solutions.alterego.androidbound.android.ui.BindableGridView;
import solutions.alterego.androidbound.android.ui.BindableHorizontalScrollView;
import solutions.alterego.androidbound.android.ui.BindableImageView;
import solutions.alterego.androidbound.android.ui.BindableLinearLayout;
import solutions.alterego.androidbound.android.ui.BindableListView;
import solutions.alterego.androidbound.android.ui.BindableProgressBar;
import solutions.alterego.androidbound.android.ui.BindableRelativeLayout;
import solutions.alterego.androidbound.android.ui.BindableScrollView;
import solutions.alterego.androidbound.android.ui.BindableSeekBar;
import solutions.alterego.androidbound.android.ui.BindableSwitch;
import solutions.alterego.androidbound.android.ui.BindableTextView;
import solutions.alterego.androidbound.android.ui.BindableToggleButton;
import solutions.alterego.androidbound.android.ui.BindableVideoView;
import solutions.alterego.androidbound.android.ui.BindableView;
import solutions.alterego.androidbound.helpers.Reflector;
import solutions.alterego.androidbound.interfaces.ILogger;

public class ViewResolver implements IViewResolver {

    private static final Class<?>[] oneArg = new Class<?>[]{
            Context.class
    };

    private static final Class<?>[] twoArgs = new Class<?>[]{
            Context.class, AttributeSet.class
    };

    @SuppressWarnings("serial")
    private static final Map<String, Class<?>> mappings = new HashMap<String, Class<?>>() {
        {
            put("android.widget.View", BindableView.class);
            put("android.widget.TextView", BindableTextView.class);
            put("android.widget.ListView", BindableListView.class);
            put("android.widget.ImageView", BindableImageView.class);
            put("android.widget.VideoView", BindableVideoView.class);
            put("android.widget.Button", BindableButton.class);
            put("android.widget.SeekBar", BindableSeekBar.class);
            put("android.widget.HorizontalScrollView", BindableHorizontalScrollView.class);
            put("android.widget.ScrollView", BindableScrollView.class);
            put("android.widget.RelativeLayout", BindableRelativeLayout.class);
            put("android.widget.LinearLayout", BindableLinearLayout.class);
            put("android.widget.GridView", BindableGridView.class);
            put("android.widget.Switch", BindableSwitch.class);
            put("android.widget.ToggleButton", BindableToggleButton.class);
            put("android.widget.EditText", BindableEditText.class);
            put("android.widget.ProgressBar", BindableProgressBar.class);
            put("android.widget.FrameLayout", BindableFrameLayout.class);
        }
    };

    private final boolean mDebugMode;

    protected ILogger logger;

    protected String mLoggingTag = getClass().getSimpleName();

    public ViewResolver(ILogger logger, boolean debugMode) {
        mDebugMode = debugMode;
        setLogger(logger);
    }

    @Override
    public View createView(String name, Context context, AttributeSet attrs) {
        Class<?> resolvedClass = resolveName(name);
        if (resolvedClass == null) {
            logger.debug(mLoggingTag + " couldn't find view = " + name);
            return null;
        }
        try {
            if (Reflector.canCreateInstance(resolvedClass, twoArgs)) {
                return Reflector.createInstance(resolvedClass, twoArgs, new Object[]{
                        context, attrs
                });
            }

            if (Reflector.canCreateInstance(resolvedClass, oneArg)) {
                return Reflector.createInstance(resolvedClass, oneArg, new Object[]{
                        context
                });
            }

            if (Reflector.canCreateInstance(resolvedClass, null)) {
                return Reflector.createInstance(resolvedClass, null, null);
            }

            throw new Exception("constructor not found");
        } catch (Exception e) {
            logger.warning("failed creating instance of class " + resolvedClass + ", exception: " + e);
            if (mDebugMode) {
                throw new RuntimeException(e);
            }
        }

        return null;
    }

    protected Class<?> resolveName(String name) {
        String result;

        if (name.contains(".")) {
            result = name;
        } else if (name.equals("View") || name.equals("ViewGroup")) {
            result = "android.view." + name;
        } else {
            result = "android.widget." + name;
        }

        if (mappings.containsKey(result)) {
            logger.debug(mLoggingTag + " mapping " + name + " to " + mappings.get(result));
            return mappings.get(result);
        }

        try {
            logger.debug(mLoggingTag + " resolving " + name + " with " + result);
            return Class.forName(result);
        } catch (ClassNotFoundException e) {
            if (mDebugMode) {
                throw new RuntimeException(e);
            }
            return null;
        }
    }

    @Override
    public void setLogger(ILogger logger) {
        this.logger = logger.getLogger(this);
    }

    @Override
    public void dispose() {

    }
}
