package solutions.alterego.androidbound.support.android.ui;

import android.annotation.SuppressLint;
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
import solutions.alterego.androidbound.android.adapters.PageDescriptor;
import solutions.alterego.androidbound.android.interfaces.IBindableView;
import solutions.alterego.androidbound.android.ui.resources.BindingResources;
import solutions.alterego.androidbound.binding.interfaces.INotifyPropertyChanged;
import solutions.alterego.androidbound.interfaces.ICommand;
import solutions.alterego.androidbound.interfaces.IViewBinder;
import solutions.alterego.androidbound.support.android.adapters.BindableRecyclerViewAdapter;
import solutions.alterego.androidbound.support.android.ui.resources.SupportBindingResources;

public class BindableRecyclerView extends RecyclerView implements IBindableView, INotifyPropertyChanged, RecyclerView.OnItemTouchListener {

    public static final String LAYOUTMANAGER_LINEAR = "linear";

    public static final String LAYOUTMANAGER_STAGGERED = "staggered";

    public static final String LAYOUTMANAGER_ORIENTATION_HORIZONTAL = "horizontal";

    public static final String LAYOUTMANAGER_ORIENTATION_VERTICAL = "vertical";

    public Map<Class<?>, Integer> getTemplatesForObjects() {
        return this.mTemplatesForObjects;
    }

    public IViewBinder getViewBinder() {
        return mViewBinder;
    }

    public BindableRecyclerViewAdapter getAdapter() {
        return mAdapter;
    }

    public boolean isUseParentLayoutParams() {
        return mUseParentLayoutParams;
    }

    public boolean isRtlLayout() {
        return mRtlLayout;
    }

    public void setViewBinder(IViewBinder viewBinder) {
        mViewBinder = viewBinder;
    }

    public void setUseParentLayoutParams(boolean useParentLayoutParams) {
        mUseParentLayoutParams = useParentLayoutParams;
    }

    private final class PageScrollListener extends OnScrollListener {

        private int[] mVisiblePosition;

        private PageDescriptor mPageDescriptor;

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

        public PageDescriptor getPageDescriptor() {
            return mPageDescriptor;
        }

        public int getPage() {
            return mPage;
        }

        public void setPageDescriptor(PageDescriptor pageDescriptor) {
            this.mPageDescriptor = pageDescriptor;
        }

        public void setPage(int page) {
            this.mPage = page;
        }
    }

    protected PublishSubject<String> propertyChanged = PublishSubject.create();

    private boolean disposed;

    private final int mItemTemplate;

    private Map<Class<?>, Integer> mTemplatesForObjects;

    private IViewBinder mViewBinder;

    private BindableRecyclerViewAdapter mAdapter;

    private boolean mUseParentLayoutParams = false;

    private boolean mRtlLayout = false;

    private String mLayoutManagerOrientationString = LAYOUTMANAGER_ORIENTATION_VERTICAL;

    private String mLayoutManagerType = null;

    private int mSpanCount = 1;

    private int mInitialPrefetchCount = 0;

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
        createLayoutManagerFromXmlParams();
    }

    public void setRtlLayout(boolean isRtlLayout) {
        if (isRtlLayout != mRtlLayout) {
            mRtlLayout = isRtlLayout;
            createLayoutManagerFromXmlParams();
        }
    }

    private int getItemTemplate(AttributeSet attrs) {
        return attrs.getAttributeResourceValue(null, BindingResources.attr.BindableListView.itemTemplate, 0);
    }

    private void processAttrs(AttributeSet attrs) {
        if (attrs != null) {
            mUseParentLayoutParams = attrs.getAttributeBooleanValue(null, SupportBindingResources.attr.BindableRecyclerView.useParentLayoutParams, true);
            mLayoutManagerOrientationString = attrs.getAttributeValue(null, SupportBindingResources.attr.BindableRecyclerView.layoutManagerOrientation);
            mRtlLayout = attrs.getAttributeBooleanValue(null, SupportBindingResources.attr.BindableRecyclerView.layoutManagerReverse, mRtlLayout);
            mSpanCount = attrs.getAttributeIntValue(null, SupportBindingResources.attr.BindableRecyclerView.layoutManagerSpanCount, 1);
            mInitialPrefetchCount = attrs.getAttributeIntValue(null, SupportBindingResources.attr.BindableRecyclerView.initialPrefetchCount, 0);
            mLayoutManagerType = attrs.getAttributeValue(null, SupportBindingResources.attr.BindableRecyclerView.layoutManager);

            boolean nestedScrollingEnabled = attrs
                    .getAttributeBooleanValue(null, SupportBindingResources.attr.BindableRecyclerView.nestedScrollingEnabled, isNestedScrollingEnabled());
            if (nestedScrollingEnabled != isNestedScrollingEnabled()) {
                setNestedScrollingEnabled(nestedScrollingEnabled);
            }
        }
    }

    private void createLayoutManagerFromXmlParams() {
        LayoutManager layoutManager = null;

        if (!TextUtils.isEmpty(mLayoutManagerType)) {
            int managerOrientation = OrientationHelper.VERTICAL;
            if ((LAYOUTMANAGER_ORIENTATION_HORIZONTAL).equalsIgnoreCase(mLayoutManagerOrientationString)) {
                managerOrientation = OrientationHelper.HORIZONTAL;
            }

            if ((LAYOUTMANAGER_LINEAR).equalsIgnoreCase(mLayoutManagerType)) {
                layoutManager = new LinearLayoutManager(getContext(), managerOrientation, mRtlLayout);
                if (mInitialPrefetchCount != 0) {
                    ((LinearLayoutManager) layoutManager).setInitialPrefetchItemCount(mInitialPrefetchCount);
                }
            } else if ((LAYOUTMANAGER_STAGGERED).equalsIgnoreCase(mLayoutManagerType)) {
                layoutManager = new StaggeredGridLayoutManager(mSpanCount, managerOrientation);
            }
        }

        if (layoutManager != null) {
            setLayoutManager(layoutManager);
        }
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

    @SuppressLint("ClickableViewAccessibility")
    @Override
    public boolean onTouchEvent(MotionEvent ev) {
        return mAdapter == null || super.onTouchEvent(ev);
    }
}
