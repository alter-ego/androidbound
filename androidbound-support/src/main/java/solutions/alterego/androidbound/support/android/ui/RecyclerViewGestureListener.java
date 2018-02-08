package solutions.alterego.androidbound.support.android.ui;

import android.graphics.PointF;
import android.support.v4.view.ViewCompat;
import android.support.v7.widget.RecyclerView;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;

import java.lang.ref.WeakReference;

import solutions.alterego.androidbound.interfaces.ICommand;

public class RecyclerViewGestureListener extends GestureDetector.SimpleOnGestureListener {

    protected WeakReference<BindableRecyclerView> mRecyclerView;

    private ICommand mRecyclerViewClickListener = ICommand.empty;

    public RecyclerViewGestureListener(BindableRecyclerView bindableRecyclerView) {
        mRecyclerView = new WeakReference<>(bindableRecyclerView);
    }

    protected View getChildAt(MotionEvent event) {
        final BindableRecyclerView recyclerView = getRecyclerView();
        return recyclerView == null ? null :  recyclerView.findChildViewUnder(event.getX(), event.getY());
    }

    @Override
    public boolean onSingleTapConfirmed(MotionEvent event) {
        final BindableRecyclerView recyclerView = getRecyclerView();
        if (recyclerView == null) {
            return false;
        }
        View childView = getChildAt(event);
        if (childView == null) {
            return false;
        }

        final int position = recyclerView.getChildAdapterPosition(childView);
        View childAt = getChildOfAt(childView, (int) event.getX(), (int) event.getY());

        if (childAt != null && ViewCompat.hasOnClickListeners(childAt)) {
            return true;
        }

        if (position == RecyclerView.NO_POSITION) {
            return true;
        }

        if (recyclerView.getItemsSource() == null) {
            // Adapter is not BindableRecyclerViewAdapter or setItemSource hasn't been called
            return true;
        }
        
        final Object object = recyclerView.getItemsSource().get(position);
        if (mRecyclerViewClickListener.canExecute(childView, object)) {
            mRecyclerViewClickListener.execute(childView, object);
            return true;
        }

        if (mRecyclerViewClickListener.canExecute(object)) {
            mRecyclerViewClickListener.execute(object);
            return true;
        }
        return false;
    }

    protected View getChildOfAt(View view, int x, int y) {
        return hit(view, x - ViewCompat.getX(view), y - ViewCompat.getY(view));
    }

    protected boolean pointInView(View view, float localX, float localY) {
        return localX >= 0 && localX < (view.getRight() - view.getLeft())
                && localY >= 0 && localY < (view.getBottom() - view.getTop());
    }

    protected boolean isHittable(View view) {
        return view.getVisibility() == View.VISIBLE && ViewCompat.getAlpha(view) >= 0.001f;
    }

    protected boolean isTransformedPointInView(
            ViewGroup parent,
            View child,
            float x,
            float y, PointF outLocalPoint) {

        if (parent == null || child == null) {
            return false;
        }

        float localX = x + parent.getScrollX() - child.getLeft();
        float localY = y + parent.getScrollY() - child.getTop();

        final boolean isInView = pointInView(child, localX, localY);
        if (isInView && outLocalPoint != null) {
            outLocalPoint.set(localX, localY);
        }
        return isInView;
    }

    protected View hit(
            View view,
            float x,
            float y) {

        if (!isHittable(view)) {
            return null;
        }

        if (!pointInView(view, x, y)) {
            return null;
        }

        if (!(view instanceof ViewGroup)) {
            return view;
        }

        final ViewGroup viewGroup = (ViewGroup) view;

        // TODO: get list of Views that are sorted in Z- and draw-order, e.g. buildOrderedChildList()
        if (viewGroup.getChildCount() > 0) {
            PointF localPoint = new PointF();

            for (int i = viewGroup.getChildCount() - 1; i >= 0; --i) {
                final View child = viewGroup.getChildAt(i);

                if (isTransformedPointInView(viewGroup, child, x, y, localPoint)) {
                    View childResult = hit(
                            child,
                            localPoint.x,
                            localPoint.y);

                    if (childResult != null) {
                        return childResult;
                    }
                }
            }
        }

        return viewGroup;
    }

    protected BindableRecyclerView getRecyclerView() {
        if (mRecyclerView == null) {
            return null;
        }
        return mRecyclerView.get();
    }

    public ICommand getRecyclerViewClickListener() {
        return mRecyclerViewClickListener;
    }

    public void setRecyclerViewClickListener(ICommand recyclerViewClickListener) {
        mRecyclerViewClickListener = recyclerViewClickListener;
    }
}
