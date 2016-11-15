package solutions.alterego.androidbound.android.ui;

import android.content.Context;
import android.graphics.Rect;
import android.support.annotation.Nullable;
import android.support.v4.view.ViewCompat;
import android.support.v7.widget.RecyclerView;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.ListIterator;
import java.util.Map;

import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;
import solutions.alterego.androidbound.android.adapters.BindableRecyclerViewAdapter;
import solutions.alterego.androidbound.android.interfaces.IBindableView;
import solutions.alterego.androidbound.android.ui.resources.BindingResources;
import solutions.alterego.androidbound.interfaces.ICommand;
import solutions.alterego.androidbound.interfaces.IViewBinder;

@Accessors(prefix = "m")
public class BindableRecyclerView extends RecyclerView implements IBindableView, RecyclerView.OnItemTouchListener {

    private final int mItemTemplate;

    @Getter
    private Map<Class<?>, Integer> mTemplatesForObjects;

    @Getter
    @Setter
    private IViewBinder mViewBinder;

    @Getter
    private BindableRecyclerViewAdapter mAdapter;

    private ICommand mOnItemClickListener = ICommand.empty;

    private final Rect mRect = new Rect();

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

        addOnItemTouchListener(this);
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

    public void setItemsSource(List<?> value) {
        if (mAdapter == null) {
            mAdapter = new BindableRecyclerViewAdapter(getContext(), getViewBinder());
            mAdapter.setItemTemplate(mItemTemplate);
            mAdapter.setTemplatesForObjects(mTemplatesForObjects);
            setAdapter(mAdapter);
        }

        if (getLayoutManager() != null) {
            mAdapter.setLayoutManager(getLayoutManager());
        }

        mAdapter.setItemsSource(value);
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

    public ICommand getOnItemClickListener() {
        return mOnItemClickListener;
    }

    public void setOnItemClickListener(ICommand l) {
        if (l == null) {
            mOnItemClickListener = ICommand.empty;
            return;
        }
        mOnItemClickListener = l;
    }


    @Override
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        removeOnItemTouchListener(this);
    }

    @Override
    public boolean onInterceptTouchEvent(RecyclerView rv, MotionEvent e) {
        if (e.getAction() == MotionEvent.ACTION_UP && getScrollState() == SCROLL_STATE_IDLE) {
            handleClick(e);
        }
        return false;
    }

    private View getChildOfAt(View view, int x, int y) {
        if (!(view instanceof ViewGroup)) {
            return view;
        }
        List<ViewGroup> viewGroups = new ArrayList<>();
        viewGroups.add((ViewGroup) view);
        ListIterator<ViewGroup> iterator = viewGroups.listIterator();

        while (iterator.hasNext() || iterator.hasPrevious()) {
            ViewGroup current;
            if (iterator.hasNext()) {
                current = iterator.next();
            } else {
                current = iterator.previous();
            }

            iterator.remove();
            if (current == null) {
                continue;
            }

            for (int i = 0; i < current.getChildCount(); i++) {
                View childAt = current.getChildAt(i);
                if (childAt instanceof ViewGroup) {
                    iterator.add((ViewGroup) childAt);
                    continue;
                }
                childAt.getHitRect(mRect);
                mRect.set((int) (mRect.left + ViewCompat.getX(view)), (int) (mRect.top + ViewCompat.getY(view)),
                        (int) (mRect.right + ViewCompat.getX(view)), (int) (mRect.bottom + ViewCompat.getY(view)));
                if (mRect.contains(x, y)) {
                    return childAt;
                }
            }
        }

        return null;
    }

    private void handleClick(MotionEvent event) {
        View childView = findChildViewUnder(event.getX(), event.getY());
        if (childView == null) {
            return;
        }

        View childAt = getChildOfAt(childView, (int) event.getX(), (int) event.getY());
        if (childAt != null && ViewCompat.hasOnClickListeners(childAt)) {
            childAt.setPressed(true);
            return;
        }

        final int position = getChildAdapterPosition(childView);
        if (position == RecyclerView.NO_POSITION) {
            return;
        }
        if (getItemsSource() == null) {
            // Adapter is not BindableRecyclerViewAdaper or setItemSource hasn't been called
            return;
        }

        if (mOnItemClickListener.canExecute(getItemsSource().get(position))) {
            mOnItemClickListener.execute(getItemsSource().get(position));
        }
    }


    @Override
    public void onTouchEvent(RecyclerView rv, MotionEvent e) {
    }

    @Override
    public void onRequestDisallowInterceptTouchEvent(boolean disallowIntercept) {
    }
}
