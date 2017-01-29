package solutions.alterego.androidbound.android.ui;

import android.content.Context;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.util.AttributeSet;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;
import rx.Observable;
import rx.subjects.PublishSubject;
import solutions.alterego.androidbound.android.adapters.BindableRecyclerViewAdapter;
import solutions.alterego.androidbound.android.interfaces.IBindableView;
import solutions.alterego.androidbound.android.ui.resources.BindingResources;
import solutions.alterego.androidbound.binding.interfaces.INotifyPropertyChanged;
import solutions.alterego.androidbound.interfaces.IViewBinder;

@Accessors(prefix = "m")
public class BindableRecyclerView extends RecyclerView implements IBindableView, INotifyPropertyChanged {

    @Accessors(prefix = "m")
    private final class PageScrollListener extends OnScrollListener {

        private int[] mVisiblePosition;

        @Setter
        @Getter
        private int mPage = 1;

        @Setter
        @Getter
        private int mThreshold = 5;

        @Setter
        @Getter
        private int mPageSize = 20;

        @Override
        public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
            if (disposed || recyclerView.getAdapter() == null) {
                return;
            }
            final LayoutManager layoutManager = recyclerView.getLayoutManager();
            int totalItemCount = layoutManager.getItemCount();
            int lastVisibleItem = getLastVisibleItemPosition(layoutManager);
            if ((totalItemCount - lastVisibleItem) <= mThreshold) {
                if (mPage < (1 + (totalItemCount / mPageSize))) {
                    mPage = 1 + (totalItemCount / mPageSize);
                    propertyChanged.onNext("NextPage");
                }
            }
        }

        private int getLastVisibleItemPosition(LayoutManager layoutManager) {
            if (layoutManager instanceof LinearLayoutManager) {
                return ((LinearLayoutManager) layoutManager).findLastCompletelyVisibleItemPosition();
            } else if (layoutManager instanceof StaggeredGridLayoutManager) {
                if (mVisiblePosition == null) {
                    mVisiblePosition = new int[((StaggeredGridLayoutManager) layoutManager).getSpanCount()];
                }
                return ((StaggeredGridLayoutManager) layoutManager)
                        .findLastCompletelyVisibleItemPositions(mVisiblePosition)[0];
            }
            return 0;
        }

    }

    private PublishSubject<String> propertyChanged = PublishSubject.create();

    private boolean disposed;

    private final int mItemTemplate;

    @Getter
    private Map<Class<?>, Integer> mTemplatesForObjects;

    @Getter
    @Setter
    private IViewBinder mViewBinder;

    @Getter
    private BindableRecyclerViewAdapter mAdapter;

    private PageScrollListener mPageScrollListener;

    public BindableRecyclerView(Context context) {
        this(context, null);
    }

    public BindableRecyclerView(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public BindableRecyclerView(Context context, @Nullable AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);

        mItemTemplate = getItemTemplate(context, attrs);
        mTemplatesForObjects = new HashMap<>();
    }

    private int getItemTemplate(Context context, AttributeSet attrs) {
        return attrs.getAttributeResourceValue(null, BindingResources.attr.BindableListView.itemTemplate, 0);
    }

    public List<?> getItemsSource() {
        if (mAdapter != null) {
            return mAdapter.getItemsSource();
        }
        return null;
    }

    private void createAdapterChecked() {
        if (mAdapter == null) {
            mAdapter = new BindableRecyclerViewAdapter(getViewBinder(), mItemTemplate);
            mAdapter.setTemplatesForObjects(mTemplatesForObjects);
            setAdapter(mAdapter);
        }
    }

    public void setItemsSource(List<?> value) {
        createAdapterChecked();

        if (getLayoutManager() != null) {
            mAdapter.setLayoutManager(getLayoutManager());
        }

        mAdapter.setItemsSource(value);
    }

    public void addItems(List<?> value) {
        createAdapterChecked();
        if (getLayoutManager() != null) {
            mAdapter.setLayoutManager(getLayoutManager());
        }
        mAdapter.addItemsSource(value);
    }

    public List<?> getItems() {
        return mAdapter != null ? mAdapter.getItemsSource() : null;
    }

    @Override
    public void setAdapter(Adapter adapter) {
        super.setAdapter(adapter);

        if (adapter instanceof BindableRecyclerViewAdapter) {
            mAdapter = (BindableRecyclerViewAdapter) adapter;
            if (getLayoutManager() != null) {
                mAdapter.setLayoutManager(getLayoutManager());
            }
        }
    }

    public void setTemplatesForObjects(Map<Class<?>, Integer> map) {
        mTemplatesForObjects = map;
        if (mAdapter != null) {
            mAdapter.setTemplatesForObjects(mTemplatesForObjects);
        }
    }

    public int getNextPage() {
        int page = mPageScrollListener != null ? mPageScrollListener.getPage() : 0;
        if (mPageScrollListener == null) {
            createPageInternal(1);
        }
        return page;
    }

    public void setNextPage(int page) {
        if (mPageScrollListener != null) {
            mPageScrollListener.setPage(page);
        }
    }

    private void createPageInternal(int page) {
        if (mPageScrollListener != null) {
            removeOnScrollListener(mPageScrollListener);
            mPageScrollListener = null;
        }
        mPageScrollListener = new PageScrollListener();
        mPageScrollListener.setPage(page);
        addOnScrollListener(mPageScrollListener);
    }

    @Override
    public void dispose() {
        if (disposed) {
            return;
        }

        disposed = true;
        if (propertyChanged != null) {
            propertyChanged.onCompleted();
            propertyChanged = null;
        }

        propertyChanged = null;

    }

    @Override
    public Observable<String> onPropertyChanged() {
        if (propertyChanged == null) {
            propertyChanged = PublishSubject.create();
        }

        return propertyChanged;
    }
}
