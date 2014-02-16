
package com.alterego.androidbound.android.ui;

import com.alterego.androidbound.interfaces.IBindingAssociation;
import com.alterego.androidbound.interfaces.IViewBinder;

import android.content.Context;
import android.view.View;
import android.widget.FrameLayout;

import java.util.List;

public class BindableListItemView extends FrameLayout {
    private IViewBinder activity;

    private View content;

    private int itemTemplate;

    private BindableListItemView(Context context) {
        super(context);
    }

    public BindableListItemView(Context context, IViewBinder activity, int itemTemplate, Object source) {
        super(context);
        this.activity = activity;
        this.itemTemplate = itemTemplate;

        if (this.activity != null)
            content = activity.inflate(context, source, itemTemplate, this);
    }

    public int getItemTemplate() {
        return itemTemplate;
    }

    public void bindTo(Object source) {
        if (this.activity == null) {
            return;
        }

        List<IBindingAssociation> bindings = this.activity.getBindingsForViewAndChildren(this);
        if (bindings == null || bindings.size() < 1) {
            return;
        }

        for (IBindingAssociation binding : bindings) {
            binding.setDataContext(source);
        }

        this.invalidate();
        this.requestLayout();
    }

    public void unbind() {
        if (this.activity == null) {
            return;
        }

        this.activity.clearBindingForViewAndChildren(this);
    }
}
