package com.eggplant.qiezisocial.ui.main;

import android.support.v7.widget.OrientationHelper;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

import static android.support.v7.widget.RecyclerView.HORIZONTAL;
import static android.support.v7.widget.RecyclerView.VERTICAL;

/**
 * Created by Administrator on 2022/2/23.
 */

public class CardLayoutManager extends RecyclerView.LayoutManager {
    private String TAG = "CardLayoutManager";
    private int mOrientation; //滑动方向
    private int mLayoutDirection;
    private int LAYOUT_END = 1;
    private int LAYOUT_START = -1;
    private int mCurrentPosition = 0;
    private int mScrollY = 0;
    private int mConsumed;
    //用于在添加布局的时候，根据它来确定被添加子View的布局位置。
    private int mOffset;
    //在添加一个新view之前，还可以滑动多少空间
    private int mScrollingOffset;
    private int SCROLLING_OFFSET_NaN = Integer.MIN_VALUE;

    private int mItemDirection;
    private int ITEM_DIRECTION_TAIL = 1;
    private int ITEM_DIRECTION_HEAD = -1;
    private Random random;
    private OrientationHelper mOrientationHelper;
    int mAvailable;

    public CardLayoutManager(int mOrientation) {
        mOrientationHelper = OrientationHelper.createOrientationHelper(this, mOrientation);
        this.mOrientation = mOrientation;
        random = new Random();
    }

    @Override
    public RecyclerView.LayoutParams generateDefaultLayoutParams() {
        return new RecyclerView.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT,
                ViewGroup.LayoutParams.WRAP_CONTENT);
    }

    @Override
    public void onLayoutChildren(RecyclerView.Recycler recycler, RecyclerView.State state) {
        if (state.getItemCount() == 0) {
            removeAndRecycleAllViews(recycler);
            Log.i(TAG, "onLayoutChildren---state item ==0");
            return;
        }
        detachAndScrapAttachedViews(recycler);
        mAvailable = mOrientationHelper.getEndAfterPadding();
        mLayoutDirection = LAYOUT_END;
        mItemDirection = ITEM_DIRECTION_TAIL;
        mScrollingOffset = SCROLLING_OFFSET_NaN;
        fill(recycler, state);

    }

    private int fill(RecyclerView.Recycler recycler, RecyclerView.State state) {
        final int start = mAvailable;
        if (mScrollingOffset != SCROLLING_OFFSET_NaN) {
            if (mAvailable < 0) {
                mScrollingOffset += mAvailable;
            }
            recycleByLayoutState(recycler);
        }

        int remainingSpace = mAvailable;

        while (remainingSpace > 0 && hasMore(state)) {

            mConsumed = 0;
            layoutChunk(recycler, state);
            mOffset += mConsumed * mLayoutDirection;
            mAvailable -= mConsumed;
            remainingSpace -= mConsumed;

            if (mScrollingOffset != SCROLLING_OFFSET_NaN) {
                mScrollingOffset += mConsumed;
                if (mAvailable < 0) {
                    mScrollingOffset += mAvailable;
                }
                recycleByLayoutState(recycler);
            }
        }
        return start - mAvailable;
    }

    private void layoutChunk(RecyclerView.Recycler recycler, RecyclerView.State state) {
        View view = next(recycler);
        if (view == null) {
            return;
        }
//        RecyclerView.LayoutParams params = (RecyclerView.LayoutParams) view.getLayoutParams();
        if (mLayoutDirection == LAYOUT_START) {
            addView(view, 0);
        } else {
            addView(view);
        }
        measureChildWithMargins(view, 0, 0);
        mConsumed = mOrientationHelper.getDecoratedMeasurement(view);
        int left, top, right, bottom;
        left = getPaddingLeft();
        right = left + mOrientationHelper.getDecoratedMeasurementInOther(view);
        top = mOffset;
        bottom = mOffset + mConsumed;
        layoutDecoratedWithMargins(view, left, top, right, bottom);

    }

    private View next(RecyclerView.Recycler recycler) {
        final View view = recycler.getViewForPosition(mCurrentPosition);
        mCurrentPosition += mItemDirection;
        return view;
    }

    private boolean hasMore(RecyclerView.State state) {
        return mCurrentPosition >= 0 && mCurrentPosition < state.getItemCount();
    }

    private void recycleByLayoutState(RecyclerView.Recycler recycler) {
        if (mLayoutDirection == LAYOUT_START) {
            recycleViewsFromEnd(recycler);
        } else {
            recycleViewsFromStart(recycler);
        }
    }

    private void recycleViewsFromEnd(RecyclerView.Recycler recycler) {
        final int childCount = getChildCount();
        if (mScrollingOffset < 0) {
            return;
        }
        final int limit = mOrientationHelper.getEnd() - mScrollingOffset;
        for (int i = childCount - 1; i >= 0; i--) {
            View child = getChildAt(i);
            if (mOrientationHelper.getDecoratedStart(child) < limit
                    || mOrientationHelper.getTransformedStartWithDecoration(child) < limit) {
                // stop here
                recycleChildren(recycler, childCount - 1, i);
                return;
            }
        }
    }

    private void recycleViewsFromStart(RecyclerView.Recycler recycler) {
        if (mScrollingOffset < 0) {
            return;
        }
        final int limit = mScrollingOffset;
        final int childCount = getChildCount();
        for (int i = 0; i < childCount; i++) {
            View child = getChildAt(i);
            if (mOrientationHelper.getDecoratedEnd(child) > limit) {// stop here
                recycleChildren(recycler, 0, i);
                return;
            }
        }
    }


    @Override
    public int scrollVerticallyBy(int dy, RecyclerView.Recycler recycler, RecyclerView.State state) {
        if (mOrientation == HORIZONTAL) {
            return 0;
        }
        return scrollBy(dy, recycler, state);
    }

    @Override
    public void collectAdjacentPrefetchPositions(int dx, int dy, RecyclerView.State state,
                                                 LayoutPrefetchRegistry layoutPrefetchRegistry) {
        int delta = (mOrientation == HORIZONTAL) ? dx : dy;
        if (getChildCount() == 0 || delta == 0) {
            // can't support this scroll, so don't bother prefetching
            return;
        }

        final int layoutDirection = delta > 0 ? LAYOUT_END :LAYOUT_START;
        final int absDy = Math.abs(delta);
        int scrollingOffset;
        if (layoutDirection == LAYOUT_END) {
            final View child = getChildClosestToEnd();
            mItemDirection = ITEM_DIRECTION_TAIL;
            mCurrentPosition = getPosition(child) + mItemDirection;
            mOffset = mOrientationHelper.getDecoratedEnd(child);
            scrollingOffset = mOrientationHelper.getDecoratedEnd(child)
                    - mOrientationHelper.getEndAfterPadding();
        } else {
            final View child = getChildClosestToStart();
            mItemDirection = ITEM_DIRECTION_HEAD;
            mCurrentPosition = getPosition(child) + mItemDirection;
            mOffset = mOrientationHelper.getDecoratedStart(child);
            scrollingOffset = -mOrientationHelper.getDecoratedStart(child)
                    + mOrientationHelper.getStartAfterPadding();
        }
        mAvailable=absDy;
        mAvailable -= scrollingOffset;
        mScrollingOffset = scrollingOffset;
        collectPrefetchPositionsForLayoutState(state, layoutPrefetchRegistry);
    }
    void collectPrefetchPositionsForLayoutState(RecyclerView.State state,
                                                LayoutPrefetchRegistry layoutPrefetchRegistry) {
        final int pos = mCurrentPosition;
        if (pos >= 0 && pos < state.getItemCount()) {
            layoutPrefetchRegistry.addPosition(pos, Math.max(0, mScrollingOffset));
        }
    }
    private int scrollBy(int dy, RecyclerView.Recycler recycler, RecyclerView.State state) {
        if (getChildCount() == 0 || dy == 0) {
            return 0;
        }
        final int layoutDirection = dy > 0 ? LAYOUT_END : LAYOUT_START;
        final int absDy = Math.abs(dy);
        mLayoutDirection = layoutDirection;
        int scrollingOffset;
        if (layoutDirection == LAYOUT_END) {
            final View child = getChildClosestToEnd();
            mItemDirection = ITEM_DIRECTION_TAIL;
            mCurrentPosition = getPosition(child) + mItemDirection;
            mOffset = mOrientationHelper.getDecoratedEnd(child);
            scrollingOffset = mOrientationHelper.getDecoratedEnd(child)
                    - mOrientationHelper.getEndAfterPadding();
        } else {
            final View child = getChildClosestToStart();
            mItemDirection = ITEM_DIRECTION_HEAD;
            mCurrentPosition = getPosition(child) + mItemDirection;
            mOffset = mOrientationHelper.getDecoratedStart(child);
            scrollingOffset = -mOrientationHelper.getDecoratedStart(child)
                    + mOrientationHelper.getStartAfterPadding();
        }
        mAvailable = absDy;
        mAvailable -= scrollingOffset;
        mScrollingOffset = scrollingOffset;

        final int consumed = mScrollingOffset
                + fill(recycler, state);
        if (consumed < 0) {
            return 0;
        }
        final int scrolled = absDy > consumed ? layoutDirection * consumed : dy;
        mOrientationHelper.offsetChildren(-scrolled);
        return scrolled;
    }

    @Override
    public boolean canScrollHorizontally() {
        return false;
    }

    @Override
    public boolean canScrollVertically() {
        return mOrientation == VERTICAL;
    }


    private void recycleChildren(RecyclerView.Recycler recycler, int startIndex, int endIndex) {
        if (startIndex == endIndex) {
            return;
        }
        if (endIndex > startIndex) {
            for (int i = endIndex - 1; i >= startIndex; i--) {
                removeAndRecycleViewAt(i, recycler);
            }
        } else {
            for (int i = startIndex; i > endIndex; i--) {
                removeAndRecycleViewAt(i, recycler);
            }
        }
    }


    private View getChildClosestToEnd() {
        return getChildAt(getChildCount() - 1);
    }

    private View getChildClosestToStart() {
        return getChildAt(0);
    }


    private Map<Integer, Float> wes1 = new HashMap();
    private Map<Integer, Float> wes2 = new HashMap();
    private Map<Integer, Integer> rotations = new HashMap();

    private void onLayout(RecyclerView.Recycler recycler) {
        int itemCount = getItemCount();
        int lastY = 0;
        int layoutLoca = 0;
        int x0 = getWidth() / 2;
        int gpWidth = getWidth();

        for (int i = 0; i < itemCount; i++) {
            View item = recycler.getViewForPosition(i);
            addView(item);
            measureChild(item, 0, 0);
            int width = getDecoratedMeasuredWidth(item);
            int height = getDecoratedMeasuredHeight(item);
            float we1;
            if (wes1.containsKey(i)) {
                we1 = wes1.get(i);
            } else {
                we1 = random.nextFloat() * (0.9F - 0.7F) + 0.7F;
                wes1.put(i, we1);
            }
            float we2;
            if (wes2.containsKey(i)) {
                we2 = wes2.get(i);
            } else {
                we2 = random.nextFloat() * (1 - 0.1F) + 0.1F;
                wes2.put(i, we2);
            }

            int rotation;
            if (rotations.containsKey(i)) {
                rotation = rotations.get(i);
            } else {
                rotation = random.nextInt(2);
                rotations.put(i, rotation);
            }
            float heightWeidth = 1.0F;
            int itemX = 0;

            if (layoutLoca == 0) {
                layoutLoca = 1;
                if (width > gpWidth * 2 / 3) {
                    itemX = x0 - width / 2;
                    layoutLoca = 0;
                } else if (width > x0) {
                    itemX = x0 - (int) (width * we1);
                } else if (width < x0 / 2) {
                    itemX = x0 - (int) (width + (x0 - width) * we2);
                    heightWeidth = 0.5F;
                } else {
                    itemX = x0 - (int) (width + (x0 - width) * we2);
                }
            } else if (layoutLoca == 1) {
                layoutLoca = 0;
                if (width > gpWidth * 2 / 3) {
                    itemX = x0 - width / 2;
                    layoutLoca = 0;
                } else if (width > x0) {
                    itemX = x0 + (int) (width * (1.0F - we1));
                } else if (width < x0 / 2) {
                    itemX = x0 + (int) ((x0 - width) * we2);
                    heightWeidth = 0.5F;
                } else {
                    itemX = x0 + (int) ((x0 - width) * we2);
                }
            }

            layoutDecorated(item, itemX, lastY - mScrollY, itemX + width, lastY + height - mScrollY);

            if (rotation == 0) {
                item.setRotation(-10.0F * we2);
            } else {
                item.setRotation(10.0F * we2);
            }
            lastY += height;
//                    * heightWeidth;

        }
    }


    /**
     * 回收屏幕外需回收的Item
     */
    private void recycleChildren(RecyclerView.Recycler recycler) {
        List<RecyclerView.ViewHolder> scrapList = recycler.getScrapList();
        for (int i = 0; i < scrapList.size(); i++) {
            RecyclerView.ViewHolder holder = scrapList.get(i);
            removeView(holder.itemView);
            recycler.recycleView(holder.itemView);
        }
    }

}
