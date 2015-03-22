
package solutions.alterego.androidbound.android.ui;

import android.content.Context;
import android.view.View;
import android.widget.FrameLayout;

import solutions.alterego.androidbound.ViewBinder;
import solutions.alterego.androidbound.interfaces.IBindingAssociation;
import solutions.alterego.androidbound.interfaces.IViewBinder;

import java.util.List;

public class BindableListItemView extends FrameLayout {

    private IViewBinder mViewBinder;
    private View mView;
    private int mItemTemplate;

    private BindableListItemView(Context context) {
        super(context);
    }

    public BindableListItemView(Context context, IViewBinder viewBinder, int itemTemplate, Object source) {
        super(context);
        mViewBinder = viewBinder;
        mItemTemplate = itemTemplate;

        if (mViewBinder != null)
            mView = viewBinder.inflate(context, source, itemTemplate, this);
    }

    public int getItemTemplate() {
        return mItemTemplate;
    }

    public void bindTo(Object source) {
        if (mViewBinder == null) {
            ViewBinder.getLogger().debug("BindableListItemView bindTo mViewBinder == null");
            return;
        }

        List<IBindingAssociation> bindings = mViewBinder.getBindingsForViewAndChildren(this);
        if (bindings == null || bindings.size() < 1) {
            ViewBinder.getLogger().debug("BindableListItemView bindTo bindings == null or 0");
            return;
        }

        ViewBinder.getLogger().debug("BindableListItemView bindTo continue with binding");
        for (IBindingAssociation binding : bindings) {
            binding.setDataContext(source);
        }

        ViewBinder.getLogger().debug("BindableListItemView bindTo invalidate & request layout");
        invalidate();
        requestLayout();
    }

    public void unbind() {
        if (mViewBinder == null) {
            return;
        }

        mViewBinder.clearBindingForViewAndChildren(this);
    }
}
