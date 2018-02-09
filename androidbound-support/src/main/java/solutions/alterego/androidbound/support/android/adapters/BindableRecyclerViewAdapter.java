package solutions.alterego.androidbound.support.android.adapters;

import android.support.v4.util.Pair;
import android.support.v7.util.DiffUtil;
import android.support.v7.widget.RecyclerView;
import android.util.SparseArray;
import android.view.ViewGroup;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Queue;

import io.reactivex.Observable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.disposables.Disposables;
import io.reactivex.functions.Consumer;
import io.reactivex.functions.Function;
import io.reactivex.functions.Predicate;
import io.reactivex.schedulers.Schedulers;
import solutions.alterego.androidbound.interfaces.IViewBinder;

import static android.support.v7.util.DiffUtil.calculateDiff;

public class BindableRecyclerViewAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private final IViewBinder mViewBinder;

    private int mItemTemplate;

    private boolean mUseParentLayoutParams;

    private Map<Class<?>, Integer> mTemplatesForObjects = new HashMap<>();

    private List mItemsSource = new ArrayList<>();

    private SparseArray<Class<?>> mObjectIndex;

    private RecyclerView.LayoutManager mLayoutManager;

    private Disposable mSetValuesDisposable = Disposables.disposed();

    private Disposable mRemoveItemsDisposable = Disposables.disposed();

    private Queue<List<?>> pendingUpdates =
            new ArrayDeque<>();

    private Disposable mAddValueDisposable = Disposables.disposed();

    public BindableRecyclerViewAdapter(IViewBinder vb, int itemTemplate, boolean useParentLayoutParams) {
        mViewBinder = vb;
        mItemTemplate = itemTemplate;
        mUseParentLayoutParams = useParentLayoutParams;
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
                mViewBinder.inflate(parent.getContext(), null, layoutRes, parent, false), mViewBinder, parent);
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        if (holder instanceof BindableRecyclerViewItemViewHolder) {
            if (getLayoutManager() != null && mUseParentLayoutParams) {
                ((BindableRecyclerViewItemViewHolder) holder)
                        .onBindViewHolderWithParentLayoutParams(getItemsSource().get(position), getLayoutManager());
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
                        .toString() + " for position = " + position);
        return viewType;
    }

    public void setItemsSource(final List<?> value) {
        final List<?> oldItems = new ArrayList<>(mItemsSource);
        mSetValuesDisposable.dispose();
        mSetValuesDisposable = Observable.just(value)
                .subscribeOn(Schedulers.computation())
                .map(new Function<List<?>, Pair<List<?>, DiffUtil.DiffResult>>() {
                    @Override
                    public Pair<List<?>, DiffUtil.DiffResult> apply(List<?> newList) throws Exception {
                        return new Pair<List<?>, DiffUtil.DiffResult>(newList, calculateDiff(new ItemSourceDiffCallback(oldItems, value)));
                    }
                })
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Consumer<Pair<List<?>, DiffUtil.DiffResult>>() {
                    @Override
                    public void accept(Pair<List<?>, DiffUtil.DiffResult> resultPair) throws Exception {
                        applyDiffResult(resultPair);
                    }
                }, new Consumer<Throwable>() {
                    @Override
                    public void accept(Throwable throwable) throws Exception {
                        throwable.printStackTrace();
                    }
                });
    }

    private void applyDiffResult(Pair<List<?>, DiffUtil.DiffResult> resultPair) {
        boolean firstStart = true;

        if (!pendingUpdates.isEmpty()) {
            pendingUpdates.remove();
        }

        if (mItemsSource.size() > 0) {
            mItemsSource.clear();
            firstStart = false;
        }

        if (resultPair.first != null) {
            mItemsSource.addAll(new ArrayList<>(resultPair.first));
        }

        //if we call DiffUtil.DiffResult.dispatchUpdatesTo() on an empty adapter, it will crash - we have to call notifyDataSetChanged()!
        if (firstStart) {
            notifyDataSetChanged();
        } else {
            resultPair.second.dispatchUpdatesTo(this);
        }

        if (pendingUpdates.size() > 0) {
            setItemsSource(pendingUpdates.peek());
        }
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

        mAddValueDisposable.dispose();
        mAddValueDisposable = Observable.fromIterable(values)
                .filter(new Predicate<Object>() {
                    @Override
                    public boolean test(Object value) throws Exception {
                        return value != null;
                    }
                })
                .subscribe(new Consumer<Object>() {
                    @Override
                    public void accept(Object value) throws Exception {
                        boolean contains = mItemsSource.contains(value);
                        if (contains) {
                            int index = mItemsSource.indexOf(value);
                            mItemsSource.set(index, value);
                            notifyItemChanged(index);
                        } else if (mItemsSource.add(value)) {
                            notifyItemInserted(mItemsSource.size() - 1);
                        }
                    }
                });
    }

    /* to prevent Cannot call this method in a scroll callback. Scroll callbacks might be run during a measure
    & layout pass where you cannot change the RecyclerView data. Any method call that might change the structure
    of the RecyclerView or the adapter contents should be postponed to the next frame.*/
    private void postNotifyItemRangeRemoved(final int start, final int itemCount) {
        AndroidSchedulers.mainThread().createWorker().schedule(new Runnable() {
            @Override
            public void run() {
                notifyItemRangeRemoved(start, itemCount);
            }
        });
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
        mRemoveItemsDisposable.dispose();
        mRemoveItemsDisposable = Observable.just(tmp)
                .subscribeOn(Schedulers.computation())
                .map(new Function<List<?>, List<?>>() {
                    @Override
                    public List<?> apply(List<?> list) throws Exception {
                        list.removeAll(value);
                        return list;
                    }
                })
                .map(new Function<List<?>, Pair<List, DiffUtil.DiffResult>>() {
                    @Override
                    public Pair<List, DiffUtil.DiffResult> apply(List<?> list) throws Exception {
                        return new Pair<List, DiffUtil.DiffResult>(list,
                                calculateDiff(new ItemSourceDiffCallback(mItemsSource, list), true));
                    }
                })
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Consumer<Pair<List, DiffUtil.DiffResult>>() {
                    @Override
                    public void accept(Pair<List, DiffUtil.DiffResult> pair) throws Exception {
                        if (pair.first != null && mItemsSource != null) {
                            mItemsSource.clear();
                            mItemsSource.addAll(pair.first);
                        }
                        pair.second.dispatchUpdatesTo(BindableRecyclerViewAdapter.this);
                    }
                }, new Consumer<Throwable>() {
                    @Override
                    public void accept(Throwable throwable) throws Exception {
                        notifyDataSetChanged();
                    }
                });
    }

    @Override
    public void onDetachedFromRecyclerView(RecyclerView recyclerView) {
        super.onDetachedFromRecyclerView(recyclerView);
        mRemoveItemsDisposable.dispose();
        mSetValuesDisposable.dispose();
        mAddValueDisposable.dispose();
    }

    public int getItemTemplate() {
        return mItemTemplate;
    }

    public Map<Class<?>, Integer> getTemplatesForObjects() {
        return mTemplatesForObjects;
    }

    public List getItemsSource() {
        return mItemsSource;
    }

    public RecyclerView.LayoutManager getLayoutManager() {
        return mLayoutManager;
    }

    public void setLayoutManager(RecyclerView.LayoutManager layoutManager) {
        mLayoutManager = layoutManager;
    }
}
