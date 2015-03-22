package solutions.alterego.androidbound.android.ui;



/*
 * HorizontalListView.java v1.5
 *
 * 
 * The MIT License
 * Copyright (c) 2011 Paul Soucy (paul@dev-smart.com)
 * 
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 * 
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 * 
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 *
 */

import java.util.LinkedList;
import java.util.Queue;

import com.alterego.advancedandroidlogger.implementations.NullAndroidLogger;
import com.alterego.advancedandroidlogger.interfaces.IAndroidLogger;

import android.content.Context;
import android.database.DataSetObserver;
import android.graphics.Rect;
import android.util.AttributeSet;
import android.view.GestureDetector;

import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListAdapter;
import android.widget.Scroller;


public class IAAFHorizontalListView extends AdapterView<ListAdapter> {

    public boolean mAlwaysOverrideTouch = true;
    protected ListAdapter mAdapter;
    private int mLeftViewIndex = -1;
    private int mRightViewIndex = 0;
    protected int mCurrentX;
    protected int mNextX;
    private int mMaxX = Integer.MAX_VALUE;
    private int mDisplayOffset = 0;
    protected Scroller mScroller;
    private GestureDetector mGesture;
    private Queue<View> mRemovedViewQueue = new LinkedList<View>();
    private OnItemSelectedListener mOnItemSelected;
    private OnItemClickListener mOnItemClicked;
    private OnItemLongClickListener mOnItemLongClicked;
    private boolean mDataChanged = false;
    private boolean mShouldSelectItem = false;
    private int mShouldSelectItemPosition = 0;
    //private int mRightViewsWidth;
    private int mCurrentViewsWidth;
    IAndroidLogger mLogger = NullAndroidLogger.instance;
    int mChildWith = 0;

    public IAAFHorizontalListView(Context context, AttributeSet attrs) {
        super(context, attrs);
        initView();
    }

    public IAAFHorizontalListView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        initView();
    }

    private synchronized void initView() {
        mLeftViewIndex = -1;
        mRightViewIndex = 0;
        mDisplayOffset = 0;
        mCurrentX = 0;
        mNextX = 0;
        mMaxX = Integer.MAX_VALUE;
        //mRightViewsWidth = 0;
        mCurrentViewsWidth = 0;
        mScroller = new Scroller(getContext());
        mGesture = new GestureDetector(getContext(), mOnGesture);
    }

    @Override
    public void setOnItemSelectedListener(AdapterView.OnItemSelectedListener listener) {
        mOnItemSelected = listener;
    }

    @Override
    public void setOnItemClickListener(AdapterView.OnItemClickListener listener) {
        mOnItemClicked = listener;
    }

    @Override
    public void setOnItemLongClickListener(AdapterView.OnItemLongClickListener listener) {
        mOnItemLongClicked = listener;
    }

    private DataSetObserver mDataObserver = new DataSetObserver() {

        @Override
        public void onChanged() {
            synchronized (IAAFHorizontalListView.this) {
                mDataChanged = true;
            }
            setEmptyView(getEmptyView());
            invalidate();
            requestLayout();
        }

        @Override
        public void onInvalidated() {
            reset();
            invalidate();
            requestLayout();
        }

    };

    public void setLogger(IAndroidLogger logger) {
        mLogger = logger;
    }

    public IAndroidLogger getLogger() {
        return mLogger;
    }

    @Override
    public ListAdapter getAdapter() {
        return mAdapter;
    }

    @Override
    public View getSelectedView() {
        //TODO: implement
        return null;
    }

    @Override
    public void setAdapter(ListAdapter adapter) {
        if (mAdapter != null) {
            mAdapter.unregisterDataSetObserver(mDataObserver);
        }
        mAdapter = adapter;
        mAdapter.registerDataSetObserver(mDataObserver);
        reset();
    }

    private synchronized void reset() {
        initView();
        removeAllViewsInLayout();
        requestLayout();
    }

    @Override
    public void setSelection(int position) {
        mShouldSelectItem = true;
        mShouldSelectItemPosition = position;
        mLogger.warning("HorizontalListView setSelection position = " + position);
    }

    private void addAndMeasureChild(final View child, int viewPos) {
        LayoutParams params = child.getLayoutParams();
        if (params == null) {
            params = new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT);
        }

        addViewInLayout(child, viewPos, params, true);
        child.measure(MeasureSpec.makeMeasureSpec(getWidth(), MeasureSpec.AT_MOST), MeasureSpec.makeMeasureSpec(getHeight(), MeasureSpec.AT_MOST));
        mChildWith = child.getMeasuredWidth();
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);

        int widthMode = MeasureSpec.getMode(widthMeasureSpec);
        int heightMode = MeasureSpec.getMode(heightMeasureSpec);
        int widthSize = MeasureSpec.getSize(widthMeasureSpec);
        int heightSize = MeasureSpec.getSize(heightMeasureSpec);

        int childWidth = 0;
        int childHeight = 0;
        int childState = 0;

        if (widthMode == MeasureSpec.UNSPECIFIED) {
            widthSize = this.getPaddingLeft() + this.getPaddingRight() + childWidth + getVerticalScrollbarWidth();
        } else {
            widthSize |= (childState & MEASURED_STATE_MASK);
        }

        if (heightMode == MeasureSpec.UNSPECIFIED) {
            heightSize = this.getPaddingTop() + this.getPaddingBottom() + childHeight + getVerticalFadingEdgeLength() * 2;
        }

        if (widthMode == MeasureSpec.AT_MOST) {
            int w = this.getPaddingLeft() + this.getPaddingRight();

            if (mAdapter != null) {
                for (int i = 0; i < this.mAdapter.getCount(); i++) {
                    // TODO only for visible children?
                    View child = this.mAdapter.getView(i, null, this);

                    measureScrapChild(child, i, widthMeasureSpec);

                    w += child.getMeasuredWidth();

                    if (w > widthSize) {
                        w = widthSize;
                        break;
                    }
                }
            }

            widthSize = w;
        }

        setMeasuredDimension(widthSize, heightSize);
    }

    @Override
    protected synchronized void onLayout(boolean changed, int left, int top, int right, int bottom) {
        super.onLayout(changed, left, top, right, bottom);

        if (mAdapter == null) {
            return;
        }

        if (mDataChanged) {
            int oldCurrentX = mCurrentX;
            initView();
            removeAllViewsInLayout();
            mNextX = oldCurrentX;
            mDataChanged = false;
        }

        if (mScroller.computeScrollOffset()) {
            int scrollx = mScroller.getCurrX();
            mNextX = scrollx;
        }

        if (mNextX <= 0) {
            mNextX = 0;
            mScroller.forceFinished(true);
        }
        if (mNextX >= mMaxX) {
            mNextX = mMaxX;
            mScroller.forceFinished(true);
        }

        int dx = mCurrentX - mNextX;
        mLogger.info("HorizontalListView onLayout dx = " + dx + ", mNextX = " + mNextX + ", mScroller.getCurrX() = " + mScroller.getCurrX()
                + ", mCurrentX = " + mCurrentX);

        //        removeNonVisibleItems(dx);
        fillList(dx);
        positionItems(dx);

        mCurrentX = mNextX;

        if (!mScroller.isFinished()) {
            post(new Runnable() {
                @Override
                public void run() {
                    requestLayout();
                }
            });

        }
    }

    private void fillList(final int dx) {
        int edge = 0;
        View child = getChildAt(getChildCount() - 1);

        //        if (mShouldSelectItem) {
        //            //if we have an item selected, we should start drawing layout from that one first (if it exists)
        //            child = getChildAt(mShouldSelectItemPosition - mLeftViewIndex);
        //        } else {
        //            child = getChildAt(getChildCount() - 1);
        //        }

        if (child != null) {
            edge = child.getRight();
            //        } else {
            //            //we set this because that's the one used in fillListRight when there are no existing child views
            //            mRightViewIndex = mShouldSelectItemPosition;
            //            mLeftViewIndex = mShouldSelectItemPosition - 1;
        }
        fillListRight(edge, dx);

        edge = 0;
        child = getChildAt(0);
        if (child != null) {
            edge = child.getLeft();
        }
        fillListLeft(edge, dx);

        mShouldSelectItem = false;
        mShouldSelectItemPosition = 0;

    }

    private void fillListRight(int rightEdge, final int dx) {

        mLogger.debug("HorizontalListView fillListRight start rightEdge = " + rightEdge + ", dx = " + dx + ", mRightViewIndex = " + mRightViewIndex
                + ", mDisplayOffset = " + mDisplayOffset + ", mCurrentViewsWidth = " + mCurrentViewsWidth);

        while (rightEdge + dx < getWidth() && mRightViewIndex < mAdapter.getCount()) {

            View child = mAdapter.getView(mRightViewIndex, mRemovedViewQueue.poll(), this);
            addAndMeasureChild(child, -1);
            rightEdge += child.getMeasuredWidth();
            mCurrentViewsWidth += child.getMeasuredWidth();

            if (mRightViewIndex == mAdapter.getCount() - 1) {
                mMaxX = mCurrentX + rightEdge - getWidth();

            }

            if (mMaxX < 0) {
                mMaxX = 0;
            }
            mRightViewIndex++;

            mLogger.info("HorizontalListView fillListRight NEW CHILD rightEdge = " + rightEdge + ", dx = " + dx + ", mRightViewIndex = "
                    + mRightViewIndex + ", mDisplayOffset = " + mDisplayOffset + ", mCurrentViewsWidth = " + mCurrentViewsWidth);
        }

        //        if (mShouldSelectItem)
        //            mDisplayOffset += getWidth() - mCurrentViewsWidth;

    }

    private void fillListLeft(int leftEdge, final int dx) {
        mLogger.debug("HorizontalListView fillListLeft start leftEdge = " + leftEdge + ", dx = " + dx + ", mLeftViewIndex = " + mLeftViewIndex
                + ", mDisplayOffset = " + mDisplayOffset + ", mCurrentViewsWidth = " + mCurrentViewsWidth);

        while (leftEdge + dx > 0 && mLeftViewIndex >= 0) {
            //while ((mCurrentViewsWidth < getWidth()) && mLeftViewIndex >= 0) {
            View child = mAdapter.getView(mLeftViewIndex, mRemovedViewQueue.poll(), this);
            addAndMeasureChild(child, 0);
            leftEdge -= child.getMeasuredWidth();
            mLeftViewIndex--;
            mDisplayOffset -= child.getMeasuredWidth();
            mCurrentViewsWidth += child.getMeasuredWidth();
            mLogger.info("HorizontalListView fillListLeft NEW CHILD leftEdge = " + leftEdge + ", dx = " + dx + ", mLeftViewIndex = " + mLeftViewIndex
                    + ", mDisplayOffset = " + mDisplayOffset + ", mCurrentViewsWidth = " + mCurrentViewsWidth);

        }
    }

    private void removeNonVisibleItems(final int dx) {
        mLogger.debug("HorizontalListView removeNonVisibleItems dx = " + dx);
        View child = getChildAt(0);
        while (child != null && child.getRight() + dx <= 0) {
            mDisplayOffset += child.getMeasuredWidth();
            mCurrentViewsWidth -= child.getMeasuredWidth();
            mRemovedViewQueue.offer(child);
            removeViewInLayout(child);
            mLeftViewIndex++;
            child = getChildAt(0);
            mLogger.info("HorizontalListView removeNonVisibleItems from left mLeftViewIndex = " + mLeftViewIndex + ", mCurrentViewsWidth = "
                    + mCurrentViewsWidth);
        }

        child = getChildAt(getChildCount() - 1);
        while (child != null && child.getLeft() + dx >= getWidth()) {
            mRemovedViewQueue.offer(child);
            removeViewInLayout(child);
            mCurrentViewsWidth -= child.getMeasuredWidth();
            mRightViewIndex--;
            child = getChildAt(getChildCount() - 1);
            mLogger.info("HorizontalListView removeNonVisibleItems from right mRightViewIndex = " + mRightViewIndex + ", mCurrentViewsWidth = "
                    + mCurrentViewsWidth);
        }
    }

    private void positionItems(final int dx) {
        if (getChildCount() > 0) {
            mLogger.debug("HorizontalListView positionItems dx = " + dx + ", mDisplayOffset = " + mDisplayOffset + ", mCurrentViewsWidth = "
                    + mCurrentViewsWidth);
            mDisplayOffset += dx;
            int left = mDisplayOffset;
            mLogger.debug("HorizontalListView positionItems before for loop left = " + left);
            for (int i = 0; i < getChildCount(); i++) {
                View child = getChildAt(i);
                int childWidth = child.getMeasuredWidth();
                child.layout(left, 0, left + childWidth, child.getMeasuredHeight());
                left += childWidth + child.getPaddingRight();
                mLogger.debug("HorizontalListView positionItems for loop end left = " + left);
            }
        }
    }

    private void measureScrapChild(View child, int position, int widthMeasureSpec) {
        LayoutParams p = (LayoutParams) child.getLayoutParams();
        if (p == null) {
            p = generateDefaultLayoutParams();
            child.setLayoutParams(p);
        }
        int childWidthSpec = ViewGroup.getChildMeasureSpec(widthMeasureSpec, this.getPaddingLeft() + this.getPaddingRight(), p.width);
        int lpHeight = p.height;
        int childHeightSpec;
        if (lpHeight > 0) {
            childHeightSpec = MeasureSpec.makeMeasureSpec(lpHeight, MeasureSpec.EXACTLY);
        } else {
            childHeightSpec = MeasureSpec.makeMeasureSpec(0, MeasureSpec.UNSPECIFIED);
        }
        child.measure(childWidthSpec, childHeightSpec);
    }

    public synchronized void scrollTo(int x) {
        int width = getWidth();
        //      int half_screen_items = (((mRightViewIndex * mChildWith) - (mLeftViewIndex * mChildWith)) / 2);
        int scroll_x = x * mChildWith - (width / 2);
        mScroller.startScroll(0, 0, scroll_x, 0);
        //        mScroller.abortAnimation();
        requestLayout();
        //        mScroller.startScroll(mNextX, 0, x - mNextX, 0);
        //        requestLayout();
    }

    public synchronized void scrollToChild(int position, int numOfItems) {
        //      measure(
        //              MeasureSpec.makeMeasureSpec(getLayoutParams().width, MeasureSpec.AT_MOST),
        //              MeasureSpec.makeMeasureSpec(getLayoutParams().height, MeasureSpec.AT_MOST));
        int width = getWidth();
        int max_scroll_x = (numOfItems * mChildWith) - width;
        int scroll_x = 0;
        //      int half_screen_items = (((mRightViewIndex * mChildWith) - (mLeftViewIndex * mChildWith)) / 2);

        scroll_x = position * mChildWith;
        if (scroll_x > max_scroll_x)
            scroll_x = max_scroll_x;
        mScroller.startScroll(0, 0, scroll_x, 0, 50);
        requestLayout();
    }

    @Override
    public boolean dispatchTouchEvent(MotionEvent ev) {
        if (ev.getAction() == MotionEvent.ACTION_DOWN || ev.getAction() == MotionEvent.ACTION_HOVER_ENTER) {
            int index = indexForView((int) ev.getX(), (int) ev.getY());
            if (index > -1)
                getChildAt(index).setPressed(true);
        } else if (ev.getAction() == MotionEvent.ACTION_UP || ev.getAction() == MotionEvent.ACTION_CANCEL
                || ev.getAction() == MotionEvent.ACTION_MOVE || ev.getAction() == MotionEvent.ACTION_SCROLL
                || ev.getAction() == MotionEvent.ACTION_HOVER_EXIT) {
            int index = indexForView((int) ev.getX(), (int) ev.getY());
            if (index > -1)
                getChildAt(index).setPressed(false);
        }
        boolean handled = mGesture.onTouchEvent(ev);
        return handled;
    }

    protected boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX, float velocityY) {
        synchronized (IAAFHorizontalListView.this) {
            mScroller.fling(mNextX, 0, (int) -velocityX, 0, 0, mMaxX, 0, 0);
        }
        requestLayout();

        return true;
    }

    protected boolean onDown(MotionEvent e) {
        mScroller.forceFinished(true);
        return true;
    }

    private int indexForView(int x, int y) {
        Rect viewRect = new Rect();
        for (int i = 0; i < getChildCount(); i++) {
            View child = getChildAt(i);
            int left = child.getLeft();
            int right = child.getRight();
            int top = child.getTop();
            int bottom = child.getBottom();
            viewRect.set(left, top, right, bottom);
            if (viewRect.contains(x, y)) {
                return i;
            }
        }
        return -1;
    }

    private GestureDetector.OnGestureListener mOnGesture = new GestureDetector.SimpleOnGestureListener() {

        @Override
        public boolean onDown(MotionEvent e) {
            return IAAFHorizontalListView.this.onDown(e);
        }

        @Override
        public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX, float velocityY) {
            return IAAFHorizontalListView.this.onFling(e1, e2, velocityX, velocityY);
        }

        @Override
        public boolean onScroll(MotionEvent e1, MotionEvent e2, float distanceX, float distanceY) {

            getParent().requestDisallowInterceptTouchEvent(true);

            synchronized (IAAFHorizontalListView.this) {
                mNextX += (int) distanceX;
            }
            requestLayout();

            return true;
        }

        @Override
        public boolean onSingleTapConfirmed(MotionEvent e) {
            for (int i = 0; i < getChildCount(); i++) {
                View child = getChildAt(i);
                if (isEventWithinView(e, child)) {
                    if (mOnItemClicked != null) {
                        mOnItemClicked
                                .onItemClick(IAAFHorizontalListView.this, child, mLeftViewIndex + 1 + i, mAdapter.getItemId(mLeftViewIndex + 1 + i));
                    }
                    if (mOnItemSelected != null) {
                        mOnItemSelected.onItemSelected(IAAFHorizontalListView.this, child, mLeftViewIndex + 1 + i,
                                mAdapter.getItemId(mLeftViewIndex + 1 + i));
                    }
                    break;
                }

            }
            return true;
        }

        @Override
        public void onLongPress(MotionEvent e) {
            int childCount = getChildCount();
            for (int i = 0; i < childCount; i++) {
                View child = getChildAt(i);
                if (isEventWithinView(e, child)) {
                    if (mOnItemLongClicked != null) {
                        mOnItemLongClicked.onItemLongClick(IAAFHorizontalListView.this, child, mLeftViewIndex + 1 + i,
                                mAdapter.getItemId(mLeftViewIndex + 1 + i));
                    }
                    break;
                }

            }
        }

        private boolean isEventWithinView(MotionEvent e, View child) {
            Rect viewRect = new Rect();
            int[] childPosition = new int[2];
            child.getLocationOnScreen(childPosition);
            int left = childPosition[0];
            int right = left + child.getWidth();
            int top = childPosition[1];
            int bottom = top + child.getHeight();
            viewRect.set(left, top, right, bottom);
            return viewRect.contains((int) e.getRawX(), (int) e.getRawY());
        }
    };
}
