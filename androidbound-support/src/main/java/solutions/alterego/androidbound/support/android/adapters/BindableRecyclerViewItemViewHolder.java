package solutions.alterego.androidbound.support.android.adapters;

import android.support.annotation.NonNull;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.view.View;
import android.view.ViewGroup;

import java.util.List;

import solutions.alterego.androidbound.NullLogger;
import solutions.alterego.androidbound.binding.interfaces.IBindingAssociationEngine;
import solutions.alterego.androidbound.interfaces.ILogger;
import solutions.alterego.androidbound.interfaces.IViewBinder;

public class BindableRecyclerViewItemViewHolder extends RecyclerView.ViewHolder {

    private final ViewGroup mParent;

    private final IViewBinder mViewBinder;

    private ILogger mLogger = NullLogger.instance;

    public BindableRecyclerViewItemViewHolder(View itemView, IViewBinder viewBinder, ViewGroup parent) {
        super(itemView);
        mParent = parent;
        mViewBinder = viewBinder;
        mLogger = viewBinder != null && viewBinder.getLogger() != null
                ? viewBinder.getLogger()
                : NullLogger.instance;
    }

    public void onBindViewHolder(@NonNull Object objectForLayout) {
        bindTo(objectForLayout);
    }

    public void onBindViewHolderWithParentLayoutParams(@NonNull Object objectForLayout, RecyclerView.LayoutManager layoutManager) {
        bindTo(objectForLayout);

        if (itemView != null && mParent != null) {
            ViewGroup.LayoutParams layoutParams = mParent.getLayoutParams();
            if (layoutManager instanceof StaggeredGridLayoutManager) {
                StaggeredGridLayoutManager.LayoutParams newLayoutParams = (StaggeredGridLayoutManager.LayoutParams) layoutManager
                        .generateLayoutParams(layoutParams);
                mLogger.debug("viewholder for StaggeredGridLayoutManager, full span");
                itemView.setLayoutParams(newLayoutParams);
            } else if (layoutManager instanceof LinearLayoutManager) {
                RecyclerView.LayoutParams newLayoutParams = layoutManager.generateLayoutParams(layoutParams);
                itemView.setLayoutParams(newLayoutParams);
            } else {
                itemView.setLayoutParams(mParent.getLayoutParams());
            }
        }
    }

    private void bindTo(Object source) {
        if (mViewBinder == null) {
            mLogger.verbose("BindableListItemView bindTo mViewBinder == null");
            return;
        }

        if (source == null) {
            mLogger.verbose("BindableListItemView bindTo source == null");
            return;
        }

        List<IBindingAssociationEngine> bindings = mViewBinder.getViewBindingEngine().getBindingsForView(itemView);

        if (bindings == null || bindings.size() < 1) {
            mLogger.verbose("BindableListItemView bindTo bindings == null or 0, doing lazy binding");
            mViewBinder.getViewBindingEngine().lazyBindView(itemView, source);
        } else {
            mLogger.verbose("BindableListItemView bindTo continue with binding");
            for (IBindingAssociationEngine binding : bindings) {
                binding.setDataContext(source);
            }
        }
    }

    public void unbind() {
        if (mViewBinder == null) {
            return;
        }
        mViewBinder.getViewBindingEngine().clearBindingForViewAndChildren(itemView);
    }
}
