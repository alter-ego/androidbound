package solutions.alterego.androidbound.support.android.viewresolvers;

import java.util.HashMap;
import java.util.Map;

import solutions.alterego.androidbound.android.ui.BindableButton;
import solutions.alterego.androidbound.android.ui.BindableEditText;
import solutions.alterego.androidbound.android.ui.BindableFrameLayout;
import solutions.alterego.androidbound.android.ui.BindableGridView;
import solutions.alterego.androidbound.android.ui.BindableHorizontalScrollView;
import solutions.alterego.androidbound.android.ui.BindableImageView;
import solutions.alterego.androidbound.android.ui.BindableLinearLayout;
import solutions.alterego.androidbound.android.ui.BindableListView;
import solutions.alterego.androidbound.android.ui.BindableProgressBar;
import solutions.alterego.androidbound.android.ui.BindableRecyclerView;
import solutions.alterego.androidbound.android.ui.BindableRelativeLayout;
import solutions.alterego.androidbound.android.ui.BindableScrollView;
import solutions.alterego.androidbound.android.ui.BindableSeekbar;
import solutions.alterego.androidbound.android.ui.BindableSwipeRefreshLayout;
import solutions.alterego.androidbound.android.ui.BindableSwitch;
import solutions.alterego.androidbound.android.ui.BindableTextView;
import solutions.alterego.androidbound.android.ui.BindableToggleButton;
import solutions.alterego.androidbound.android.ui.BindableVideoView;
import solutions.alterego.androidbound.android.ui.BindableView;
import solutions.alterego.androidbound.android.viewresolvers.ViewResolver;
import solutions.alterego.androidbound.interfaces.ILogger;

public class SupportViewResolver extends ViewResolver {

    @SuppressWarnings("serial")
    private static final Map<String, Class<?>> mappings = new HashMap<String, Class<?>>() {
        {
            put("android.widget.View", BindableView.class);
            put("android.widget.TextView", BindableTextView.class);
            put("android.widget.ListView", BindableListView.class);
            put("android.widget.ImageView", BindableImageView.class);
            put("android.widget.VideoView", BindableVideoView.class);
            put("android.widget.Button", BindableButton.class);
            put("android.widget.SeekBar", BindableSeekbar.class);
            put("android.widget.HorizontalScrollView", BindableHorizontalScrollView.class);
            put("android.widget.ScrollView", BindableScrollView.class);
            put("android.widget.RelativeLayout", BindableRelativeLayout.class);
            put("android.widget.LinearLayout", BindableLinearLayout.class);
            put("android.widget.GridView", BindableGridView.class);
            put("android.widget.Switch", BindableSwitch.class);
            put("android.widget.ToggleButton", BindableToggleButton.class);
            put("android.widget.EditText", BindableEditText.class);
            put("android.support.v7.widget.RecyclerView", BindableRecyclerView.class);
            put("android.widget.ProgressBar", BindableProgressBar.class);
            put("android.widget.FrameLayout", BindableFrameLayout.class);
            put("android.support.v4.widget.SwipeRefreshLayout", BindableSwipeRefreshLayout.class);

            put("android.support.v7.widget.AppCompatButton", BindableButton.class);
            put("android.support.v7.widget.AppCompatEditText", BindableEditText.class);
            put("android.support.v7.widget.AppCompatImageView", BindableImageView.class);
            put("android.support.v7.widget.AppCompatSeekBar", BindableSeekbar.class);
            put("android.support.v7.widget.AppCompatTextView", BindableTextView.class);
        }
    };

    public SupportViewResolver(ILogger logger) {
        super(logger);
    }

    @Override
    protected Map<String, Class<?>> getMappings() {
        return mappings;
    }
}
