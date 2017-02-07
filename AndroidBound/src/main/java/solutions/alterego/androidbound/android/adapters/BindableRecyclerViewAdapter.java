package solutions.alterego.androidbound.android.adapters;

import android.os.Handler;
import android.support.v7.util.DiffUtil;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.util.SparseArray;
import android.view.ViewGroup;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;
import rx.Observable;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;
import solutions.alterego.androidbound.interfaces.IViewBinder;

@Accessors(prefix = "m")
public class BindableRecyclerViewAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private final IViewBinder mViewBinder;

    @Getter
    private int mItemTemplate;

    @Getter
    private Map<Class<?>, Integer> mTemplatesForObjects = new HashMap<>();

    @Getter
    private List<?> mItemsSource = new ArrayList<>();

    private SparseArray<Class<?>> mObjectIndex;

    @Getter
    @Setter
    private RecyclerView.LayoutManager mLayoutManager;

    private Handler mHandler = new Handler();

    public BindableRecyclerViewAdapter(IViewBinder vb, int itemTemplate) {
        mViewBinder = vb;
        mItemTemplate = itemTemplate;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        Class<?> clazz = mObjectIndex.get(viewType);
        int layoutRes = mItemTemplate;

        if (clazz != null && mTemplatesForObjects.containsKey(clazz)) {
            layoutRes = mTemplatesForObjects.get(clazz);
            mViewBinder.getLogger().verbose(
                    "BindableRecyclerViewAdapter creating VH for viewType = " + viewType + " i.e. class = " + clazz
                            + " using layoutRes = "
                            + layoutRes);
        } else if (layoutRes != 0) {
            mViewBinder.getLogger().verbose("BindableRecyclerViewAdapter creating VH using layoutRes = " + layoutRes);
        } else {
            mViewBinder.getLogger().error("BindableRecyclerViewAdapter cannot find templates for class = " + clazz
                    + ": did you call setTemplatesForObjects or set itemTemplate in XML?");
        }

        return new BindableRecyclerViewItemViewHolder(parent.getContext(), mViewBinder, parent, layoutRes);
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        if (holder instanceof BindableRecyclerViewItemViewHolder) {
            if (getLayoutManager() instanceof StaggeredGridLayoutManager) {
                ((BindableRecyclerViewItemViewHolder) holder)
                        .onBindViewHolder(getItemsSource().get(position), getLayoutManager());
            } else {
                ((BindableRecyclerViewItemViewHolder) holder).onBindViewHolder(getItemsSource().get(position));
            }
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
        mViewBinder.getLogger().verbose(
                "BindableRecyclerViewAdapter getItemViewType viewType = " + viewType + " i.e. class = " + obj.getClass()
                        .toString()
                        + " for position = " + position);
        return viewType;
    }

    public void setItemsSource(List<?> value) {
        addItemsSource(value);
    }

    public void addItemsSource(List value) {
        if (value == null) {
            if (mItemsSource != null) {
                int size = mItemsSource.size();
                mItemsSource = null;
                postNotifyItemRangeRemoved(0, size);
            }
            return;
        }
        if (mItemsSource == null) {
            mItemsSource = new ArrayList<>();
        }
        final int startIndex = mItemsSource.size();
        mItemsSource.addAll(value);
        notifyItemRangeInserted(startIndex, value.size());
    }

    /* to prevent Cannot call this method in a scroll callback. Scroll callbacks might be run during a measure
    & layout pass where you cannot change the RecyclerView data. Any method call that might change the structure
    of the RecyclerView or the adapter contents should be postponed to the next frame.*/
    private void postNotifyItemRangeRemoved(final int start, final int itemCount) {
        mHandler.post(() -> notifyItemRangeRemoved(start, itemCount));
    }


    public void setTemplatesForObjects(Map<Class<?>, Integer> templatesForObjects) {
        if (mTemplatesForObjects == null) {
            return;
        }

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

    public void removeItems(final List<?> value) {
        if (mItemsSource == null) {
            return;
        }
        List<?> tmp = new ArrayList<>(mItemsSource);
        Observable.just(tmp)
                .subscribeOn(Schedulers.computation())
                .map(list -> {
                    list.removeAll(value);
                    return list;
                })
                .doOnError(throwable -> notifyDataSetChanged())
                .map(list -> DiffUtil.calculateDiff(new ItemSourceDiffCallback(mItemsSource, list), true))
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(diffResult -> diffResult.dispatchUpdatesTo(this));
    }
}
