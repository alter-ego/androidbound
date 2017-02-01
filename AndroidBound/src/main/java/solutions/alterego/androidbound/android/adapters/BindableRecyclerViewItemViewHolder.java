package solutions.alterego.androidbound.android.adapters;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.view.ViewGroup;

import solutions.alterego.androidbound.android.ui.BindableRecyclerViewItemView;
import solutions.alterego.androidbound.interfaces.IViewBinder;

public class BindableRecyclerViewItemViewHolder extends RecyclerView.ViewHolder {

    private final ViewGroup mParent;

    private final IViewBinder mViewBinder;

    public BindableRecyclerViewItemViewHolder(@NonNull Context ctx, @NonNull IViewBinder viewBinder, @NonNull ViewGroup parent,
            @NonNull int layoutResId) {
        super(new BindableRecyclerViewItemView(ctx, viewBinder, layoutResId, null, parent));
        mParent = parent;
        mViewBinder = viewBinder;
    }

    public void onBindViewHolder(@NonNull Object objectForLayout) {
        if (itemView != null) {
            ((BindableRecyclerViewItemView) itemView).bindTo(objectForLayout);

            if (mParent != null) {
                itemView.setLayoutParams(mParent.getLayoutParams());
            }
        }
    }

    public void onBindViewHolder(@NonNull Object objectForLayout, RecyclerView.LayoutManager layoutManager) {
        if (itemView != null) {
            ((BindableRecyclerViewItemView) itemView).bindTo(objectForLayout);

            if (mParent != null) {
                ViewGroup.LayoutParams layoutParams = mParent.getLayoutParams();

                if (layoutManager instanceof StaggeredGridLayoutManager) {
                    StaggeredGridLayoutManager.LayoutParams newLayoutParams = (StaggeredGridLayoutManager.LayoutParams) layoutManager.generateLayoutParams(layoutParams);
                    mViewBinder.getLogger().debug("viewholder for StaggeredGridLayoutManager, full span");
                    itemView.setLayoutParams(layoutParams);
                } else {
                    itemView.setLayoutParams(mParent.getLayoutParams());
                }
            }
        }
    }

}
