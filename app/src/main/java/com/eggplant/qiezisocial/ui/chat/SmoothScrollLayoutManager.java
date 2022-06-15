package com.eggplant.qiezisocial.ui.chat;

import android.content.Context;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.LinearSmoothScroller;
import android.support.v7.widget.RecyclerView;
import android.util.DisplayMetrics;

/**
 * Created by Administrator on 2022/6/14.

 * Describe:解决 RecyclerView调用smoothScrollToPosition时没有起到平滑的作用
 * 设置LinerLayoutManager：recyclerview.setLayoutManager(new SmoothScrollLayoutManager(this));
 *recyclerview.smoothScrollToPosition(i);

 */


public class SmoothScrollLayoutManager extends LinearLayoutManager {
    public float speedPer=350f;
    public SmoothScrollLayoutManager(Context context) {
        super(context);
    }

    @Override
    public void smoothScrollToPosition(RecyclerView recyclerView,
                                       RecyclerView.State state, final int position) {

        LinearSmoothScroller smoothScroller =
                new LinearSmoothScroller(recyclerView.getContext()) {
                    // 返回：滑过1px时经历的时间(ms)。
                    @Override
                    protected float calculateSpeedPerPixel(DisplayMetrics displayMetrics) {
                        return speedPer / displayMetrics.densityDpi;
                    }
                };
        smoothScroller.setTargetPosition(position);
        startSmoothScroll(smoothScroller);
    }
}