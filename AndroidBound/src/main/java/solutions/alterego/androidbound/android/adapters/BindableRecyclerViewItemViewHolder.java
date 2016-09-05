package solutions.alterego.androidbound.android.adapters;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.ViewGroup;

import solutions.alterego.androidbound.android.ui.BindableRecyclerViewItemView;
import solutions.alterego.androidbound.interfaces.IViewBinder;

public class BindableRecyclerViewItemViewHolder extends RecyclerView.ViewHolder {

    private final ViewGroup mParent;

    public BindableRecyclerViewItemViewHolder(@NonNull Context ctx, @NonNull IViewBinder viewBinder, @NonNull ViewGroup parent,
            @NonNull int layoutResId) {
        super(new BindableRecyclerViewItemView(ctx, viewBinder, layoutResId, null, parent));
        mParent = parent;
    }

    public void onBindViewHolder(@NonNull Object objectForLayout) {
        if (itemView != null) {
            ((BindableRecyclerViewItemView) itemView).bindTo(objectForLayout);

            if (mParent != null) {
                itemView.setLayoutParams(mParent.getLayoutParams());
            }
        }
    }

}
