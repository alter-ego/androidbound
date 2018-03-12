package solutions.alterego.androidbound.support.android.viewresolvers;

import java.util.HashMap;
import java.util.Map;

import solutions.alterego.androidbound.android.ui.BindableImageView;
import solutions.alterego.androidbound.android.viewresolvers.ViewResolver;
import solutions.alterego.androidbound.interfaces.ILogger;
import solutions.alterego.androidbound.support.android.ui.BindableCompatButton;
import solutions.alterego.androidbound.support.android.ui.BindableCompatEditText;
import solutions.alterego.androidbound.support.android.ui.BindableCompatImageView;
import solutions.alterego.androidbound.support.android.ui.BindableCompatProgressBar;
import solutions.alterego.androidbound.support.android.ui.BindableCompatSeekBar;
import solutions.alterego.androidbound.support.android.ui.BindableCompatTextView;
import solutions.alterego.androidbound.support.android.ui.BindableRecyclerView;
import solutions.alterego.androidbound.support.android.ui.BindableSwipeRefreshLayout;

public class SupportViewResolver extends ViewResolver {

    @SuppressWarnings("serial")
    private static final Map<String, Class<?>> supportMappings = new HashMap<String, Class<?>>() {
        {
            put("android.widget.Button", BindableCompatButton.class);
            put("android.widget.EditText", BindableCompatEditText.class);
            put("android.widget.ImageView", BindableCompatImageView.class);
            put("android.widget.SeekBar", BindableCompatSeekBar.class);
            put("android.widget.TextView", BindableCompatTextView.class);
            put("android.widget.ProgressBar", BindableCompatProgressBar.class);

            put("android.support.v7.widget.AppCompatButton", BindableCompatButton.class);
            put("android.support.v7.widget.AppCompatEditText", BindableCompatEditText.class);
            put("android.support.v7.widget.AppCompatImageView", BindableImageView.class);
            put("android.support.v7.widget.AppCompatSeekBar", BindableCompatSeekBar.class);
            put("android.support.v7.widget.AppCompatTextView", BindableCompatTextView.class);
            put("android.support.v7.widget.RecyclerView", BindableRecyclerView.class);
            put("android.support.v4.widget.SwipeRefreshLayout", BindableSwipeRefreshLayout.class);
        }
    };

    public SupportViewResolver(ILogger logger, boolean debugMode) {
        super(logger, debugMode);
    }

    @Override
    protected Class<?> resolveName(String name) {
        if (supportMappings.containsKey(name)) {
            logger.debug("Resolved " + name);
            return supportMappings.get(name);
        } else {
            return null;
        }
    }
}
