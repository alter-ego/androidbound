package solutions.alterego.androidbound.android.ui;

import android.content.Context;
import android.util.AttributeSet;
import android.view.ViewGroup;
import android.widget.FrameLayout;

import java.util.List;

import solutions.alterego.androidbound.ViewBinder;
import solutions.alterego.androidbound.binding.interfaces.IBindingAssociationEngine;
import solutions.alterego.androidbound.interfaces.IViewBinder;

public class BindableRecyclerViewItemView extends FrameLayout {


    private IViewBinder mViewBinder;

    private int mItemTemplate;

    private BindableRecyclerViewItemView(Context context) {
        super(context);
    }

    public BindableRecyclerViewItemView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public BindableRecyclerViewItemView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }

    public BindableRecyclerViewItemView(Context context, IViewBinder viewBinder, int itemTemplate, Object source, ViewGroup parent) {
        super(context);
        mViewBinder = viewBinder;
        mItemTemplate = itemTemplate;

        if (mViewBinder != null) {
            viewBinder.inflate(context, source, itemTemplate, this);
        }
    }

    public int getItemTemplate() {
        return mItemTemplate;
    }

    public void bindTo(Object source) {
        if (mViewBinder == null) {
            mViewBinder.getLogger().verbose("BindableListItemView bindTo mViewBinder == null");
            return;
        }

        if (source == null) {
            mViewBinder.getLogger().verbose("BindableListItemView bindTo source == null");
            return;
        }

        List<IBindingAssociationEngine> bindings = mViewBinder.getBindingsForViewAndChildren(this);

        if (bindings == null || bindings.size() < 1) {
            mViewBinder.getLogger().verbose("BindableListItemView bindTo bindings == null or 0, doing lazy binding");
            mViewBinder.lazyBindView(this, source);
        } else {
            mViewBinder.getLogger().verbose("BindableListItemView bindTo continue with binding");
            for (IBindingAssociationEngine binding : bindings) {
                binding.setDataContext(source);
            }
        }
    }

    public void unbind() {
        if (mViewBinder == null) {
            return;
        }

        mViewBinder.clearBindingForViewAndChildren(this);
    }

}
