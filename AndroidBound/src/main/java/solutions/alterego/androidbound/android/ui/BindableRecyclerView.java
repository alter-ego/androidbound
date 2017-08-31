package solutions.alterego.androidbound.android.ui;

import android.content.Context;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.OrientationHelper;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.view.GestureDetector;
import android.view.MotionEvent;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import io.reactivex.Observable;
import io.reactivex.subjects.PublishSubject;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;
import solutions.alterego.androidbound.android.adapters.BindableRecyclerViewAdapter;
import solutions.alterego.androidbound.android.adapters.PageDescriptor;
import solutions.alterego.androidbound.android.interfaces.IBindableView;
import solutions.alterego.androidbound.android.ui.resources.BindingResources;
import solutions.alterego.androidbound.binding.interfaces.INotifyPropertyChanged;
import solutions.alterego.androidbound.interfaces.ICommand;
import solutions.alterego.androidbound.interfaces.IViewBinder;

@Accessors(prefix = "m")
public class BindableRecyclerView extends RecyclerView implements IBindableView, INotifyPropertyChanged, RecyclerView.OnItemTouchListener {

    public static final String LAYOUTMANAGER_LINEAR = "linear";

    public static final String LAYOUTMANAGER_STAGGERED = "staggered";

    public static final String LAYOUTMANAGER_ORIENTATION_HORIZONTAL = "horizontal";

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

    @Getter
    @Setter
    private boolean mUseParentLayoutParams = true;

    private PageScrollListener mPageScrollListener;

    private PageDescriptor mDefaultPageDescriptor;

    private GestureDetector mGestureDetector;

    private RecyclerViewGestureListener mRecyclerViewGestureListener;

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

        mItemTemplate = getItemTemplate(attrs);
        mTemplatesForObjects = new HashMap<>();

        processAttrs(attrs);
        LayoutManager layoutManagerFromXml = getLayoutManager(attrs);
        if (layoutManagerFromXml != null) {
            setLayoutManager(layoutManagerFromXml);
        }
    }

    private int getItemTemplate(AttributeSet attrs) {
        return attrs.getAttributeResourceValue(null, BindingResources.attr.BindableListView.itemTemplate, 0);
    }

    private void processAttrs(AttributeSet attrs) {
        if (attrs != null) {
            mUseParentLayoutParams = attrs.getAttributeBooleanValue(null, BindingResources.attr.BindableRecyclerView.useParentLayoutParams, true);
        }
    }

    private LayoutManager getLayoutManager(AttributeSet attrs) {
        LayoutManager layoutManager = null;
        String managerType = null;

        if (attrs != null) {
            managerType = attrs.getAttributeValue(null, BindingResources.attr.BindableRecyclerView.layoutManager);
        }

        if (!TextUtils.isEmpty(managerType)) {
            String managerOrientationString = attrs.getAttributeValue(null, BindingResources.attr.BindableRecyclerView.layoutManagerOrientation);
            boolean reverseLayout = attrs.getAttributeBooleanValue(null, BindingResources.attr.BindableRecyclerView.layoutManagerReverse, false);
            int spanCount = attrs.getAttributeIntValue(null, BindingResources.attr.BindableRecyclerView.layoutManagerSpanCount, 1);
            int initialPrefetchCount = attrs.getAttributeIntValue(null, BindingResources.attr.BindableRecyclerView.initialPrefetchCount, 0);

            int managerOrientation = OrientationHelper.VERTICAL;
            if ((LAYOUTMANAGER_ORIENTATION_HORIZONTAL).equalsIgnoreCase(managerOrientationString)) {
                managerOrientation = OrientationHelper.HORIZONTAL;
            }

            if ((LAYOUTMANAGER_LINEAR).equalsIgnoreCase(managerType)) {
                layoutManager = new LinearLayoutManager(getContext(), managerOrientation, reverseLayout);
                if (initialPrefetchCount != 0) {
                    ((LinearLayoutManager) layoutManager).setInitialPrefetchItemCount(initialPrefetchCount);
                }
            } else if ((LAYOUTMANAGER_STAGGERED).equalsIgnoreCase(managerType)) {
                layoutManager = new StaggeredGridLayoutManager(spanCount, managerOrientation);
            }
        }

        return layoutManager;
    }

    @Override
    public void setLayoutManager(LayoutManager layout) {
        super.setLayoutManager(layout);
        if (mAdapter != null) {
            mAdapter.setLayoutManager(getLayoutManager());
        }
    }

    public List<?> getItemsSource() {
        if (mAdapter != null) {
            return mAdapter.getItemsSource();
        }
        return null;
    }

    private void createAdapterChecked() {
        if (mAdapter == null && getViewBinder() != null) {
            mAdapter = new BindableRecyclerViewAdapter(getViewBinder(), mItemTemplate, mUseParentLayoutParams);
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

    public void removeRemoveItems(List<?> value) {
        if (mAdapter != null) {
            mAdapter.removeItems(value);
        }
    }

    public List<?> getRemoveItems() {
        return getItems();
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

    public void setPageDescriptor(PageDescriptor pageDescriptor) {
        if (mPageScrollListener != null) {
            removeOnScrollListener(mPageScrollListener);
        }
        mPageScrollListener = new PageScrollListener(pageDescriptor);
        addOnScrollListener(mPageScrollListener);
        propertyChanged.onNext("NextPage");
    }

    @Override
    public boolean onInterceptTouchEvent(RecyclerView rv, MotionEvent e) {
        return mGestureDetector != null && mGestureDetector.onTouchEvent(e);
    }

    @Override
    public void onTouchEvent(RecyclerView rv, MotionEvent e) {

    }

    @Override
    public void onRequestDisallowInterceptTouchEvent(boolean disallowIntercept) {

    }

    public ICommand getOnItemClickListener() {
        return mRecyclerViewGestureListener == null ? null : mRecyclerViewGestureListener.getRecyclerViewClickListener();
    }

    public void setOnItemClickListener(ICommand l) {
        if (l == null) {
            return;
        }
        mGestureDetector = new GestureDetector(getContext(),
                mRecyclerViewGestureListener = getRecyclerViewGestureListener());
        mRecyclerViewGestureListener.setRecyclerViewClickListener(l);
    }

    protected RecyclerViewGestureListener getRecyclerViewGestureListener() {
        return new RecyclerViewGestureListener(this);
    }

    @Override
    public void dispose() {
        if (disposed) {
            return;
        }

        disposed = true;
        if (propertyChanged != null) {
            propertyChanged.onComplete();
        }

        propertyChanged = null;
        mViewBinder = null;
        mAdapter = null;
        mTemplatesForObjects = new HashMap<>();
    }

    @Override
    public Observable<String> onPropertyChanged() {
        if (propertyChanged == null) {
            propertyChanged = PublishSubject.create();
        }

        return propertyChanged;
    }

    @Override
    protected void onAttachedToWindow() {
        super.onAttachedToWindow();
        addOnItemTouchListener(this);
    }

    @Override
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        removeOnItemTouchListener(this);
    }
}
