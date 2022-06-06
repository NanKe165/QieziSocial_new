package com.eggplant.qiezisocial.widget.popupwindow;

import android.animation.ValueAnimator;
import android.app.Activity;
import android.content.Context;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.PopupWindow;

import com.eggplant.qiezisocial.R;


/**
 * Created by Administrator on 2017/12/20.
 */
public class BasePopupWindow extends PopupWindow {
    private Context mContext;
    private float mAlpha = 0.5f;
    private Drawable mBackgroundDrawable;
    public int showAnimMode = 0;//0: 定义好的anim，1：自定义的style

    public BasePopupWindow(Context context) {
        mContext = context;
        initBasePopupWindow();
    }

    @Override
    public void setOutsideTouchable(boolean touchable) {
        super.setOutsideTouchable(touchable);
        if (touchable) {
            if (mBackgroundDrawable == null) {
                mBackgroundDrawable = new ColorDrawable(0x00000000);
            }
            super.setBackgroundDrawable(mBackgroundDrawable);
        } else {
            super.setBackgroundDrawable(null);
        }
    }

    @Override
    public void setBackgroundDrawable(Drawable background) {
        mBackgroundDrawable = background;
        setOutsideTouchable(isOutsideTouchable());
    }

    private void initBasePopupWindow() {
        setAnimationStyle(R.style.Animation_AppCompat_Dialog);
        setHeight(ViewGroup.LayoutParams.WRAP_CONTENT);
        setWidth(ViewGroup.LayoutParams.WRAP_CONTENT);
        setFocusable(true);
        setTouchable(true);
        setOutsideTouchable(true);
        setTouchInterceptor(new View.OnTouchListener() {
            private float y;
            private float x;

            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                switch (motionEvent.getAction()) {

                    case MotionEvent.ACTION_DOWN:
                        x = motionEvent.getX();
                        y = motionEvent.getY();
                        break;
                    case MotionEvent.ACTION_MOVE:
                        float currx = motionEvent.getX();
                        float curry = motionEvent.getY();
                        if (Math.abs(currx - x) > 30 || Math.abs(curry - y) > 30)
                            dismiss();

                            break;
                }
                return false;
            }

        });
    }

    public void setWndWidth(int width) {
        setWidth(width);
    }

    public void setWndHeight(int height) {
        setHeight(height);
    }

    public void fullScreen() {
        setHeight(ViewGroup.LayoutParams.MATCH_PARENT);
        setWidth(ViewGroup.LayoutParams.MATCH_PARENT);
    }

    @Override
    public void setContentView(View contentView) {
        if (contentView != null) {
            contentView.measure(View.MeasureSpec.UNSPECIFIED, View.MeasureSpec.UNSPECIFIED);
            super.setContentView(contentView);
            addKeyListener(contentView);
        }
    }

    public Context getContext() {
        return mContext;
    }

    @Override
    public void showAtLocation(View parent, int gravity, int x, int y) {
        super.showAtLocation(parent, gravity, x, y);
        if (showAnimMode == 1) {

        } else {
            showAnimator().start();
        }
    }

    @Override
    public void showAsDropDown(View anchor) {
        super.showAsDropDown(anchor);
        showAnimator().start();
    }

    @Override
    public void showAsDropDown(View anchor, int xoff, int yoff) {
        super.showAsDropDown(anchor, xoff, yoff);
        showAnimator().start();
    }

    @Override
    public void showAsDropDown(View anchor, int xoff, int yoff, int gravity) {
        super.showAsDropDown(anchor, xoff, yoff, gravity);
        showAnimator().start();
    }

    @Override
    public void dismiss() {
        super.dismiss();
        if (showAnimMode == 1) {

        } else {
            dismissAnimator().start();
        }
    }

    private ValueAnimator showAnimator() {
        ValueAnimator animator = ValueAnimator.ofFloat(1.0f, mAlpha);
        animator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                float alpha = (float) animation.getAnimatedValue();
                setWindowBackgroundAlpha(alpha);
            }
        });
        animator.setDuration(360);
        return animator;
    }

    /**
     * 窗口隐藏，窗口背景透明度渐变动画
     */
    private ValueAnimator dismissAnimator() {
        ValueAnimator animator = ValueAnimator.ofFloat(mAlpha, 1.0f);
        animator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {

            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                float alpha = (float) animation.getAnimatedValue();
                setWindowBackgroundAlpha(alpha);
            }
        });
        animator.setDuration(320);
        return animator;
    }

    /**
     * 为窗体添加outside点击事件
     */
    private void addKeyListener(View contentView) {
        if (contentView != null) {
            contentView.setFocusable(true);
            contentView.setFocusableInTouchMode(true);
            contentView.setOnKeyListener(new View.OnKeyListener() {

                @Override
                public boolean onKey(View view, int keyCode, KeyEvent event) {
                    switch (keyCode) {
                        case KeyEvent.KEYCODE_BACK:
                            dismiss();
                            return true;
                        default:
                            break;
                    }
                    return false;
                }
            });
        }
    }

    public void setBgAlpha(float alpha) {
        mAlpha = alpha;
    }

    /**
     * 控制窗口背景的不透明度
     */
    private void setWindowBackgroundAlpha(float alpha) {
        Window window = ((Activity) getContext()).getWindow();
        WindowManager.LayoutParams layoutParams = window.getAttributes();
        layoutParams.alpha = alpha;
        window.setAttributes(layoutParams);
    }
}
