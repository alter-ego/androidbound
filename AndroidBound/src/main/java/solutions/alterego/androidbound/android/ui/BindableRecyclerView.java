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
import solutions.alterego.androidbound.android.adapters.PageDescriptor;
import solutions.alterego.androidbound.android.interfaces.IBindableView;
import solutions.alterego.androidbound.android.ui.resources.BindingResources;
import solutions.alterego.androidbound.binding.interfaces.INotifyPropertyChanged;
import solutions.alterego.androidbound.interfaces.IViewBinder;

@Accessors(prefix = "m")
public class BindableRecyclerView extends RecyclerView implements IBindableView, INotifyPropertyChanged {

    @Accessors(prefix = "m")
    private final class PageScrollListener extends OnScrollListener {

        private int[] mVisiblePosition;

        @Getter
        @Setter
        private PageDescriptor mPageDescriptor;

        @Getter
        @Setter
        private int mPage = 1;


        PageScrollListener(PageDescriptor pageDescriptor) {
            mPageDescriptor = pageDescriptor;
            mPage = mPageDescriptor.getStartPage();
        }

        @Override
        public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
            if (disposed || recyclerView.getAdapter() == null) {
                return;
            }
            final LayoutManager layoutManager = recyclerView.getLayoutManager();
            int totalItemCount = layoutManager.getItemCount();
            int lastVisibleItem = getLastVisibleItemPosition(layoutManager);
            if ((totalItemCount - lastVisibleItem) <= mPageDescriptor.getThreshold()) {
                if (mPageDescriptor.getCurrentPage() < (1 + (totalItemCount / mPageDescriptor.getPageSize()))) {
                    mPageDescriptor.setCurrentPage(1 + (totalItemCount / mPageDescriptor.getPageSize()));
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

    protected PublishSubject<String> propertyChanged = PublishSubject.create();

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

    private PageDescriptor mDefaultPageDescriptor;

    public BindableRecyclerView(Context context) {
        this(context, null);
    }

    public BindableRecyclerView(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public BindableRecyclerView(Context context, @Nullable AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);

        mDefaultPageDescriptor =
                new PageDescriptor.PageDescriptorBuilder()
                        .setPageSize(20)
                        .setStartPage(1)
                        .setThreshold(5).build();

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

    public PageDescriptor getNextPage() {
        return mPageScrollListener != null ? mPageScrollListener.getPageDescriptor() : null;
    }

    public void setNextPage(PageDescriptor pageDescriptor) {
        if (mPageScrollListener != null) {
            mPageScrollListener.setPageDescriptor(pageDescriptor);
        }
    }

    private void createPageInternal(PageDescriptor pageDescriptor) {
        if (mPageScrollListener != null) {
            removeOnScrollListener(mPageScrollListener);
            mPageScrollListener = null;
        }
        mPageScrollListener = new PageScrollListener(pageDescriptor);
        addOnScrollListener(mPageScrollListener);
    }

    public PageDescriptor getPageDescriptor() {
        return mDefaultPageDescriptor;
    }

    public void setPageDescriptor(PageDescriptor pageDescritor) {
        if (mPageScrollListener != null) {
            removeOnScrollListener(mPageScrollListener);
        }
        mPageScrollListener = new PageScrollListener(pageDescritor);
        addOnScrollListener(mPageScrollListener);
        propertyChanged.onNext("NextPage");
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
