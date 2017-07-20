package solutions.alterego.androidbound.lint;

import com.google.common.collect.ImmutableList;

import com.android.annotations.NonNull;
import com.android.tools.lint.checks.MissingIdDetector;
import com.android.tools.lint.detector.api.Category;
import com.android.tools.lint.detector.api.Implementation;
import com.android.tools.lint.detector.api.Issue;
import com.android.tools.lint.detector.api.LayoutDetector;
import com.android.tools.lint.detector.api.Scope;
import com.android.tools.lint.detector.api.Severity;
import com.android.tools.lint.detector.api.XmlContext;

import org.w3c.dom.Element;

import java.util.Collection;

import static com.android.SdkConstants.ANDROID_URI;
import static com.android.SdkConstants.ATTR_ID;
import static com.android.SdkConstants.BUTTON;
import static com.android.SdkConstants.EDIT_TEXT;
import static com.android.SdkConstants.FRAME_LAYOUT;
import static com.android.SdkConstants.GRID_VIEW;
import static com.android.SdkConstants.HORIZONTAL_SCROLL_VIEW;
import static com.android.SdkConstants.IMAGE_VIEW;
import static com.android.SdkConstants.LINEAR_LAYOUT;
import static com.android.SdkConstants.LIST_VIEW;
import static com.android.SdkConstants.PROGRESS_BAR;
import static com.android.SdkConstants.RECYCLER_VIEW;
import static com.android.SdkConstants.RELATIVE_LAYOUT;
import static com.android.SdkConstants.SCROLL_VIEW;
import static com.android.SdkConstants.SEEK_BAR;
import static com.android.SdkConstants.SWITCH;
import static com.android.SdkConstants.TEXT_VIEW;
import static com.android.SdkConstants.TOGGLE_BUTTON;
import static com.android.SdkConstants.VIDEO_VIEW;
import static com.android.SdkConstants.VIEW;

/**
 * Check which looks for missing id's in views where they are probably needed
 */
public class MissingViewIdDetector extends LayoutDetector {

    public static final Issue ISSUE = Issue.create(
            "MissingId", //$NON-NLS-1$
            "Views should specify an `id`",
            "If you do not specify an android:id, bindings cannot be created compile-time " +
                    "but will be executed run-time, which slows down the view initialization." +
                    "\n" +
                    "* Supply the android:id attribute with a unique ID.\n",
            Category.PERFORMANCE,
            8,
            Severity.ERROR,
            new Implementation(
                    MissingViewIdDetector.class,
                    Scope.RESOURCE_FILE_SCOPE));

    /** Constructs a new {@link MissingIdDetector} */
    public MissingViewIdDetector() {
    }

    @Override
    public Collection<String> getApplicableElements() {

        Collection<String> android_components = ImmutableList
                .of(BUTTON, EDIT_TEXT, FRAME_LAYOUT, GRID_VIEW, HORIZONTAL_SCROLL_VIEW, IMAGE_VIEW, LINEAR_LAYOUT, LIST_VIEW, PROGRESS_BAR,
                        RECYCLER_VIEW, RELATIVE_LAYOUT, SCROLL_VIEW, SEEK_BAR, SWITCH, TEXT_VIEW, TOGGLE_BUTTON, VIDEO_VIEW,
                        VIEW, "SwipeRefreshLayout");

        Collection<String> bindable_components = ImmutableList
                .of("solutions.alterego.androidbound.android.ui.BindableButton",
                        "solutions.alterego.androidbound.android.ui.BindableEditText",
                        "solutions.alterego.androidbound.android.ui.BindableFrameLayout",
                        "solutions.alterego.androidbound.android.ui.BindableGridView",
                        "solutions.alterego.androidbound.android.ui.BindableHorizontalScrollView",
                        "solutions.alterego.androidbound.android.ui.BindableImageView",
                        "solutions.alterego.androidbound.android.ui.BindableLinearLayout",
                        "solutions.alterego.androidbound.android.ui.BindableListView",
                        "solutions.alterego.androidbound.android.ui.BindableProgressBar",
                        "solutions.alterego.androidbound.android.ui.BindableRecyclerView",
                        "solutions.alterego.androidbound.android.ui.BindableRelativeLayout",
                        "solutions.alterego.androidbound.android.ui.BindableScrollView",
                        "solutions.alterego.androidbound.android.ui.BindableSeekbar",
                        "solutions.alterego.androidbound.android.ui.BindableSwipeRefreshLayout",
                        "solutions.alterego.androidbound.android.ui.BindableSwitch",
                        "solutions.alterego.androidbound.android.ui.BindableTextView",
                        "solutions.alterego.androidbound.android.ui.BindableToggleButton",
                        "solutions.alterego.androidbound.android.ui.BindableVideoView",
                        "solutions.alterego.androidbound.android.ui.BindableView");

        return new ImmutableList.Builder().addAll(android_components).addAll(bindable_components).build();
    }

    @Override
    public void visitElement(@NonNull XmlContext context, @NonNull Element element) {
        if (!element.hasAttributeNS(ANDROID_URI, ATTR_ID) && element.hasAttribute("binding")) {
            context.report(ISSUE, element, context.getLocation(element),
                    "This View should specify an id in order for it to be bound " +
                            "compile-time");
        }
    }
}
