package com.eggplant.qiezisocial.widget;

import android.content.Context;
import android.support.annotation.Nullable;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.LinearLayoutCompat;
import android.util.AttributeSet;
import android.view.Gravity;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.LinearLayout;

import com.eggplant.qiezisocial.R;

/**
 * Created by Administrator on 2020/7/14.
 */

public class NumbsView extends LinearLayout {
    private int sum = 1;
    private int currentNumb = 1;
    private int numbViewWidth = (int) getResources().getDimension(R.dimen.qb_px_30);
    private int lineMargin = (int) getResources().getDimension(R.dimen.qb_px_5);
    private LinearLayout currentGp ;

    public NumbsView(Context context) {
        this(context, null);
    }

    public NumbsView(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public NumbsView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    private void init() {
        setOrientation(LinearLayout.VERTICAL);
        setGravity(Gravity.CENTER_HORIZONTAL);
        createViews();
    }

    public void setSum(int sum) {
        if (sum >= 0)
            this.sum = sum;
        createViews();
    }

    public void setCurrentNumb(int currentNumb) {
        this.currentNumb = currentNumb;
        createViews();
    }

    public void nextNumb() {
        currentNumb++;
        createViews();
    }

    private void createViews() {
        removeAllViews();
        for (int i = 1; i <= sum; i++) {
            createNumbView(i);
        }
    }

    private void createNumbView(int i) {
        if (i%4==1) {
            currentGp=new LinearLayout(getContext());
            currentGp.setOrientation(LinearLayout.HORIZONTAL);
            LayoutParams layoutParams = new LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
            layoutParams.setMargins(0, lineMargin * 2, 0, 0);
            currentGp.setLayoutParams(layoutParams);
            addView(currentGp);
        }
        QzTextView numbView = new QzTextView(getContext());
        numbView.setTextSize(17);
        numbView.setGravity(Gravity.CENTER);
        numbView.setText(i + "");
        numbView.setLayoutParams(new LinearLayoutCompat.LayoutParams(numbViewWidth, numbViewWidth));
        if (i == currentNumb) {
            numbView.setTextColor(ContextCompat.getColor(getContext(), R.color.tv_black));
            numbView.setBackground(ContextCompat.getDrawable(getContext(), R.drawable.numb_select));
        } else {
            numbView.setTextColor(ContextCompat.getColor(getContext(), R.color.tv_black2));
            numbView.setBackground(ContextCompat.getDrawable(getContext(), R.drawable.numb_unselect));
        }
        currentGp.addView(numbView);
        if (i != sum && i % 4 != 0) {
            FrameLayout lineView = new FrameLayout(getContext());
            LayoutParams layoutParams = new LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
            layoutParams.setMargins(lineMargin, 0, lineMargin, 0);
            layoutParams.gravity = Gravity.CENTER_VERTICAL;
            lineView.setLayoutParams(layoutParams);
            lineView.setBackground(ContextCompat.getDrawable(getContext(), R.drawable.lineview_bg));
            currentGp.addView(lineView);
        }
    }

}
