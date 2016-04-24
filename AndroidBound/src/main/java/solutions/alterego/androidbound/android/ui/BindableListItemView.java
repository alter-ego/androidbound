package solutions.alterego.androidbound.android.ui;

import android.content.Context;
import android.view.View;
import android.widget.FrameLayout;

import java.util.List;

import solutions.alterego.androidbound.ViewBinder;
import solutions.alterego.androidbound.binding.interfaces.IBindingAssociationEngine;
import solutions.alterego.androidbound.interfaces.IViewBinder;

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

        if (mViewBinder != null) {
            mView = viewBinder.inflate(context, source, itemTemplate, this);
        }
    }

    public int getItemTemplate() {
        return mItemTemplate;
    }

    public void bindTo(Object source) {
        if (mViewBinder == null) {
            ViewBinder.getLogger().verbose("BindableListItemView bindTo mViewBinder == null");
            return;
        }

        List<IBindingAssociationEngine> bindings = mViewBinder.getBindingsForViewAndChildren(this);
        if (bindings == null || bindings.size() < 1) {
            ViewBinder.getLogger().verbose("BindableListItemView bindTo bindings == null or 0");
            return;
        }

        ViewBinder.getLogger().verbose("BindableListItemView bindTo continue with binding");
        for (IBindingAssociationEngine binding : bindings) {
            binding.setDataContext(source);
        }

    }

    public void unbind() {
        if (mViewBinder == null) {
            return;
        }

        mViewBinder.clearBindingForViewAndChildren(this);
    }
}
