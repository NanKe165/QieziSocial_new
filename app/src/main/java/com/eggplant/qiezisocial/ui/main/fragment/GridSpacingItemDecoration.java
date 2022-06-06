package com.eggplant.qiezisocial.ui.main.fragment;

import android.graphics.Rect;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.View;

/**
 * Created by Administrator on 2021/2/2.
 */

public class GridSpacingItemDecoration extends RecyclerView.ItemDecoration {
    private final String TAG = "GridSpaceItemDecoration";

    private int mSpanCount;//横条目数量
    private int mRowSpacing;//行间距
    private int mColumnSpacing;// 列间距

    /**
     * @param spanCount     列数
     * @param rowSpacing    行间距
     * @param columnSpacing 列间距
     */
    public GridSpacingItemDecoration(int spanCount, int rowSpacing, int columnSpacing) {
        this.mSpanCount = spanCount;
        this.mRowSpacing = rowSpacing;
        this.mColumnSpacing = columnSpacing;
    }

    @Override
    public void getItemOffsets(@NonNull Rect outRect, @NonNull View view, @NonNull RecyclerView parent, @NonNull RecyclerView.State state) {
        int position = parent.getChildAdapterPosition(view); // 获取view 在adapter中的位置。

        int column = position % mSpanCount; // view 所在的列

        if ((hasHeader && position == 0) || (hasFooter && position ==dataSize - 1)){

        }else {
            if (hasHeader){
                position--;
            }
            column = position % mSpanCount; // view 所在的列
            outRect.left = column * mColumnSpacing / mSpanCount; // column * (列间距 * (1f / 列数))
            outRect.right = mColumnSpacing - (column + 1) * mColumnSpacing / mSpanCount; // 列间距 - (column + 1) * (列间距 * (1f /列数))
        }

//        Log.i("gridSp","----p:"+position+"  size:"+parent.getChildCount());
        // 如果position > 行数，说明不是在第一行，则不指定行高，其他行的上间距为 top=mRowSpacing
        if (position >= mSpanCount) {
            outRect.top = mRowSpacing; // item top
        }
    }

    private boolean hasHeader, hasFooter;

    public void setHasHeader(boolean hasHeader) {
        this.hasHeader = hasHeader;
    }


    public void setHasFooter(boolean hasFooter) {
        this.hasFooter = hasFooter;
    }
    private int dataSize;

    public void setDataSize(int dataSize) {
        this.dataSize = dataSize;
    }
}
