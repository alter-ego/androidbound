package solutions.alterego.androidbound.android.adapters;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.util.SparseArray;
import android.view.ViewGroup;

import java.util.List;
import java.util.Map;

import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;
import solutions.alterego.androidbound.ViewBinder;
import solutions.alterego.androidbound.interfaces.IViewBinder;

@Accessors(prefix = "m")
public class BindableRecyclerViewAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private final Context mContext;

    private final IViewBinder mViewBinder;

    @Getter
    @Setter
    private int mItemTemplate;

    @Getter
    private Map<Class<?>, Integer> mTemplatesForObjects;

    @Getter
    private List<?> mItemsSource;

    private SparseArray<Class<?>> mObjectIndex;

    public BindableRecyclerViewAdapter(Context ctx, IViewBinder vb) {
        mContext = ctx;
        mViewBinder = vb;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        Class<?> clazz = mObjectIndex.get(viewType);
        int layoutRes = mTemplatesForObjects.get(clazz);
        ViewBinder.getLogger().verbose(
                "BindableRecyclerViewAdapter creating VH for viewType = " + viewType + " i.e. class = " + clazz.toString() + " using layoutRes = "
                        + layoutRes);
        return new BindableRecyclerViewItemViewHolder(mContext, mViewBinder, parent, layoutRes);
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        if (holder instanceof BindableRecyclerViewItemViewHolder) {
            ((BindableRecyclerViewItemViewHolder) holder).onBindViewHolder(getItemsSource().get(position));
        }
    }

    @Override
    public int getItemCount() {
        return getItemsSource() != null ? getItemsSource().size() : 0;
    }

    @Override
    public int getItemViewType(int position) {
        Object obj = getItemsSource().get(position);
        int viewType = mObjectIndex.indexOfValue(obj.getClass());
        ViewBinder.getLogger().verbose(
                "BindableRecyclerViewAdapter getItemViewType viewType = " + viewType + " i.e. class = " + obj.getClass().toString() + " for position = "
                        + position);
        return viewType;
    }

    public void setItemsSource(List<?> value) {
        mItemsSource = value;
        notifyDataSetChanged();
    }

    public void setTemplatesForObjects(Map<Class<?>, Integer> templatesForObjects) {
        mTemplatesForObjects = templatesForObjects;
        mObjectIndex = new SparseArray<>();
        Class<?>[] classes = mTemplatesForObjects.keySet().toArray(new Class[mTemplatesForObjects.keySet().size()]);

        for (int index = 0; index < classes.length; index++) {
            mObjectIndex.put(index, classes[index]);
        }

        if (mItemsSource != null) {
            notifyDataSetChanged();
        }
    }
}
