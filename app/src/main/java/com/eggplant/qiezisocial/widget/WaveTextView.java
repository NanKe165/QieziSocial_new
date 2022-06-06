package com.eggplant.qiezisocial.widget;

import android.content.Context;
import android.graphics.Color;
import android.util.AttributeSet;
import android.view.MotionEvent;

/**
 * Created by Administrator on 2019/6/24.
 */

public class WaveTextView extends android.support.v7.widget.AppCompatTextView {
    private int mStartColor = Color.parseColor("#97c15c");//f5c657
    private int mEndColor = Color.parseColor("#b6d38d");//fceecc
    private WaveDrawable mWaveDrawable = new WaveDrawable(mStartColor, mEndColor, 0, 0);

    public WaveTextView(Context context) {
        this(context, null);
    }

    public WaveTextView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public WaveTextView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
//        mEndColor = ContextCompat.getColor(context, R.color.verify_chat_ad_bg);
//        mStartColor = ContextCompat.getColor(context, R.color.verify_chat_bg);
        setClickable(true);
        setBackground(mWaveDrawable);

    }

    @Override
    public boolean dispatchTouchEvent(MotionEvent event) {
        if (event.getAction() == MotionEvent.ACTION_DOWN) {
            mWaveDrawable.setColors(mStartColor, mEndColor);
            mWaveDrawable.setClickXY(event.getX(), event.getY());
        }

        return super.dispatchTouchEvent(event);
    }

    public void startAnim() {
        mWaveDrawable.start();
    }

    public void stopAnim() {
        mWaveDrawable.stop();
    }
}