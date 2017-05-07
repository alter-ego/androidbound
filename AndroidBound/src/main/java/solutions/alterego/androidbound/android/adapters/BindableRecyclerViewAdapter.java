package solutions.alterego.androidbound.android.adapters;

import android.support.v4.util.Pair;
import android.support.v7.util.DiffUtil;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.util.SparseArray;
import android.view.ViewGroup;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Queue;

import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;
import rx.Observable;
import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;
import rx.subscriptions.CompositeSubscription;
import rx.subscriptions.Subscriptions;
import solutions.alterego.androidbound.interfaces.IViewBinder;

import static android.support.v7.util.DiffUtil.calculateDiff;

@Accessors(prefix = "m")
public class BindableRecyclerViewAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private final IViewBinder mViewBinder;

    @Getter
    private int mItemTemplate;

    @Getter
    private Map<Class<?>, Integer> mTemplatesForObjects = new HashMap<>();

    @Getter
    private List mItemsSource = new ArrayList<>();

    private SparseArray<Class<?>> mObjectIndex;

    @Getter
    @Setter
    private RecyclerView.LayoutManager mLayoutManager;

    private Subscription mSetValuesSubscription = Subscriptions.unsubscribed();

    private Subscription mRemoveItemsSubscription = Subscriptions.unsubscribed();

    private CompositeSubscription mPageSubscriptions = new CompositeSubscription();

    private Queue<List<?>> pendingUpdates =
            new ArrayDeque<>();

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
        return new BindableRecyclerViewItemViewHolder(
                mViewBinder.inflate(parent.getContext(), null, layoutRes, parent, false),
                mViewBinder, parent);
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

    public void setItemsSource(final List<?> value) {
        final List<?> oldItems = new ArrayList<>(mItemsSource);
        mSetValuesSubscription.unsubscribe();
        mSetValuesSubscription = Observable.just(value)
                .subscribeOn(Schedulers.computation())
                .map(newList -> new Pair<List<?>, DiffUtil.DiffResult>(newList, calculateDiff(new ItemSourceDiffCallback(oldItems, value))))
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(this::applyDiffResult, Throwable::printStackTrace);
    }

    public void addItemsSource(List<?> values) {
        if (values == null) {
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

        Subscription s = Observable.from(values)
                .filter(value -> value != null)
                .subscribe(value -> {
                    boolean contains = mItemsSource.contains(value);
                    if (contains) {
                        int index = mItemsSource.indexOf(value);
                        mItemsSource.set(index, value);
                        notifyItemChanged(index);
                    } else if (mItemsSource.add(value)) {
                        notifyItemInserted(mItemsSource.size() - 1);
                    }
                });
        mPageSubscriptions.add(s);
    }

    private void applyDiffResult(Pair<List<?>, DiffUtil.DiffResult> resultPair) {
        if (!pendingUpdates.isEmpty()) {
            pendingUpdates.remove();
        }
        mItemsSource.clear();
        if (resultPair.first != null) {
            mItemsSource.addAll(new ArrayList<>(resultPair.first));
        }
        resultPair.second.dispatchUpdatesTo(this);
        if (pendingUpdates.size() > 0) {
            setItemsSource(pendingUpdates.peek());
        }
    }

    /* to prevent Cannot call this method in a scroll callback. Scroll callbacks might be run during a measure
    & layout pass where you cannot change the RecyclerView data. Any method call that might change the structure
    of the RecyclerView or the adapter contents should be postponed to the next frame.*/
    private void postNotifyItemRangeRemoved(final int start, final int itemCount) {
        AndroidSchedulers.mainThread().createWorker().schedule(() -> notifyItemRangeRemoved(start, itemCount));
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
        mRemoveItemsSubscription.unsubscribe();
        mRemoveItemsSubscription = Observable.just(tmp)
                .subscribeOn(Schedulers.computation())
                .map(list -> {
                    list.removeAll(value);
                    return list;
                })
                .map(list -> new Pair<List, DiffUtil.DiffResult>(list,
                        calculateDiff(new ItemSourceDiffCallback(mItemsSource, list), true)))
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(pair -> {
                    if (pair.first != null && mItemsSource != null) {
                        mItemsSource.clear();
                        mItemsSource.addAll(pair.first);
                    }
                    pair.second.dispatchUpdatesTo(this);
                }, throwable -> {
                    notifyDataSetChanged();
                });
    }

    @Override
    public void onDetachedFromRecyclerView(RecyclerView recyclerView) {
        super.onDetachedFromRecyclerView(recyclerView);
        mRemoveItemsSubscription.unsubscribe();
        mSetValuesSubscription.unsubscribe();
        mPageSubscriptions.unsubscribe();
    }
}
