package solutions.alterego.androidbound.android.ui;

import android.content.Context;
import android.view.View;
import android.widget.FrameLayout;

import java.util.List;

import solutions.alterego.androidbound.ViewBinder;
import solutions.alterego.androidbound.android.interfaces.INeedsBoundView;
import solutions.alterego.androidbound.binding.interfaces.IBindingAssociationEngine;
import solutions.alterego.androidbound.interfaces.IViewBinder;

public class BindableListItemView extends FrameLayout {

    private IViewBinder mViewBinder;

    private int mItemTemplate;

    private BindableListItemView(Context context) {
        super(context);
    }

    public BindableListItemView(Context context, IViewBinder viewBinder, int itemTemplate, Object source) {
        super(context);
        View view = null;
        mViewBinder = viewBinder;
        mItemTemplate = itemTemplate;

        if (mViewBinder != null) {
            view = viewBinder.inflate(context, source, itemTemplate, this);
        }

        if (view != null && source instanceof INeedsBoundView) {
            ((INeedsBoundView) source).setBoundView(view);
        }
    }

    public int getItemTemplate() {
        return mItemTemplate;
    }

    public void bindTo(Object source) {
        if (mViewBinder == null) {
            return;
        }

        List<IBindingAssociationEngine> bindings = mViewBinder.getBindingsForViewAndChildren(this);
        if (bindings == null || bindings.size() < 1) {
            mViewBinder.getLogger().verbose("BindableListItemView bindTo bindings == null or 0");
            return;
        }

        mViewBinder.getLogger().verbose("BindableListItemView bindTo continue with binding");
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
