package com.eggplant.qiezisocial.widget.flow;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ObjectAnimator;
import android.animation.ValueAnimator;
import android.content.Context;
import android.support.v4.content.ContextCompat;
import android.util.AttributeSet;
import android.view.View;
import android.view.animation.Interpolator;
import android.view.animation.LinearInterpolator;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import com.bumptech.glide.Glide;
import com.eggplant.qiezisocial.R;
import com.eggplant.qiezisocial.utils.ScreenUtil;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

/**
 * Created by Administrator on 2020/12/31.
 */

public class FallsImageView extends RelativeLayout {
    private int emojiWidth = 180;//表情view宽
    private int mHeight;
    private Interpolator line = new LinearInterpolator();//线性
    private List<String> imagsPath = new ArrayList<>();
    boolean startControl = true, pause = false;
    private Thread animThrad;
    private Random random = new Random();

    public FallsImageView(Context context) {
        super(context);
        init(context);
    }

    public FallsImageView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    public FallsImageView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        //注意!!  获取本身的宽高 需要在测量之后才有宽高
        mHeight = getMeasuredHeight();

    }

    private void init(Context context) {
        emojiWidth = (int) context.getResources().getDimension(R.dimen.qb_px_28);
        setBackgroundColor(ContextCompat.getColor(context, R.color.chat_bg));
    }

    public void addImagePath(String path) {
        if (path != null) {
            imagsPath.add(path);
        }
    }

    private long sleepTime = 300;
    //每个表情持续动画时间
    private long animTime = 6000;

    public void startFallsAnim() {
        pause = false;
        if (animThrad == null) {
            startControl = true;
            animThrad = new Thread(new Runnable() {
                @Override
                public void run() {
                    while (startControl) {
                        if (imagsPath.size() == 0) {
                            stopAnim();
                            return;
                        }
                        if (animTime <= 0) {
                            imagsPath.remove(0);
                            animTime = 6000;
                        }
                        if (imagsPath.size() == 0) {
                            stopAnim();
                            return;
                        }
                        final String path = imagsPath.get(0);

                        try {
                            if (!pause) {
                                FallsImageView.this.post(new Runnable() {
                                    @Override
                                    public void run() {
                                        addImageView(path);
                                    }
                                });
                            }
                            Thread.sleep(sleepTime);
                            animTime -= sleepTime;
//                            sleepTime = getRandomNum(200, 300);
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                    }
                }
            });
            animThrad.start();
        }
    }

    private void addImageView(String path) {
        LayoutParams params = getEmojiLayoutParams();
        ImageView emojiImg = new ImageView(getContext());
        emojiImg.setLayoutParams(params);
        Glide.with(getContext()).load(path).into(emojiImg);
        addView(emojiImg);
        ValueAnimator anim = getEmojiValueAnimator(emojiImg);
        anim.addListener(new AnimEndListener(emojiImg));
        anim.setInterpolator(line);
        anim.start();
    }

    public void pauseAnim() {
        pause = true;
    }

    public void stopAnim() {
        startControl = false;
        animTime = 5000;
        if (animThrad != null) {
            animThrad.interrupt();
            animThrad = null;
        }
    }

    private int getRandomNum(int max, int min) {
        int margin = random.nextInt(max) % (max - min + 1) + min;
        lastMargin = margin;
        return margin;
    }

    public LayoutParams getEmojiLayoutParams() {
        int margin = getLeftMargin();
        LayoutParams params = new LayoutParams(emojiWidth, emojiWidth);
        params.addRule(ALIGN_PARENT_TOP, TRUE);
        params.setMargins(margin, 0, 0, 0);
        return params;
    }

    int lastMargin = 10;

    public int getLeftMargin() {
        int margin = emojiWidth;
        margin = getRandomNum(ScreenUtil.getDisplayWidthPixels(getContext()) - emojiWidth - 10, 10);
        if ((margin > lastMargin && margin < lastMargin + emojiWidth) || (margin < lastMargin && margin < lastMargin - emojiWidth)) {
            margin = getLeftMargin();
        }
        return margin;
    }

    private ValueAnimator getEmojiValueAnimator(ImageView emojiImg) {

        int randomNum = 5000;
//        randomNum = getRandomNum(5000, 8000);
        ValueAnimator animator = ObjectAnimator.ofFloat(emojiImg, "translationY", -300.0f, mHeight);
        animator.setDuration(randomNum);
        return animator;
    }
    private class AnimEndListener extends AnimatorListenerAdapter {
        private View target;

        public AnimEndListener(View target) {
            this.target = target;
        }

        @Override
        public void onAnimationEnd(Animator animation) {
            super.onAnimationEnd(animation);
            //因为不停的add 导致子view数量只增不减,所以在view动画结束后remove掉
            removeView((target));
        }
    }
}
