package com.eggplant.qiezisocial.widget.flow;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.animation.ValueAnimator;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Build;
import android.os.VibrationEffect;
import android.os.Vibrator;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.annotation.RequiresApi;
import android.support.v4.util.LruCache;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.view.animation.AccelerateInterpolator;
import android.view.animation.DecelerateInterpolator;
import android.view.animation.Interpolator;
import android.view.animation.LinearInterpolator;
import android.widget.FrameLayout;
import android.widget.ImageView;

import com.eggplant.qiezisocial.R;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

/**
 * Created by Administrator on 2020/10/16.
 * 爆炸效果
 */

public class ExplosiveView extends FrameLayout {
    private Random random = new Random();
    private int mHeight;//EmojiFlowView高
    private int mWidth;//EmojiFlowView宽
    private int emojiWidth = 180;//表情view宽


    private LruCache<String, Bitmap> lruCache;
    //    private List<ValueAnimator> defaultViewAnim;
    private List<String> keys;
    private int currentKeyPosition = 0;

    boolean startControl = true, pause = false;
    private Thread animThrad;

    private Interpolator line = new LinearInterpolator();//线性
    private Interpolator acc = new AccelerateInterpolator();//加速
    private Interpolator dce = new DecelerateInterpolator();//减速
    private Interpolator accdec = new AccelerateDecelerateInterpolator();//先加速后减速
    private Interpolator[] interpolators;
    private int animViewX, animViewY;
    private Vibrator vibrator;
    private VibrationEffect vibrationEffect,vibrationEffect2;
    private ImageView boomView;

    public ExplosiveView(@NonNull Context context) {
        super(context);
        init(context);
    }

    public ExplosiveView(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    public ExplosiveView(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        //注意!!  获取本身的宽高 需要在测量之后才有宽高
        mWidth = getMeasuredWidth();
        mHeight = getMeasuredHeight();

    }

    private void init(Context context) {
        vibrator = (Vibrator) getContext().getSystemService(Context.VIBRATOR_SERVICE);
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
            vibrationEffect = VibrationEffect.createOneShot(20, 1);
        }

        emojiWidth = (int) context.getResources().getDimension(R.dimen.qb_px_28);
        // 初始化插补器
        interpolators = new Interpolator[4];
        interpolators[0] = line;
        interpolators[1] = line;
        interpolators[2] = line;
        interpolators[3] = line;
        keys = new ArrayList<>();
//        defaultViewAnim = new ArrayList<>();
        long maxMemory = Runtime.getRuntime().maxMemory();
        int cacheSize = (int) (maxMemory / 1024);
        lruCache = new LruCache<String, Bitmap>(cacheSize) {
            @Override
            protected int sizeOf(String key, Bitmap value) {
                return value.getRowBytes() * value.getHeight() / 1024;
            }
        };
        initDefaultBitmap();
        initBoomView();
    }

    private void initBoomView() {
        if (boomView == null) {
            boomView = new ImageView(getContext());
            boomView.setImageResource(R.mipmap.icon_boom);
        }
    }

    public void boom(final int viewX, int viewY) {
        animViewX = viewX;
        animViewY = viewY;
        boomView.setLayoutParams(getBoomLayoutParams());
        removeAllViews();
        addView(boomView);
        startBoomAinm();
    }

    private void startBoomAinm() {
        ValueAnimator scaleX = ObjectAnimator.ofFloat(boomView, "scaleX", 0, 1F);
        ValueAnimator scaleY = ObjectAnimator.ofFloat(boomView, "scaleY", 0, 1F);
        AnimatorSet animatorSetsuofang = new AnimatorSet();
        animatorSetsuofang.addListener(new Animator.AnimatorListener() {
            @Override
            public void onAnimationStart(Animator animation) {
                ExplosiveView.this.post(new Runnable() {
                    @Override
                    public void run() {
                        viabrate(50,50);
                    }
                });
            }

            @Override
            public void onAnimationEnd(Animator animation) {
                removeView(boomView);
            }

            @Override
            public void onAnimationCancel(Animator animation) {

            }

            @Override
            public void onAnimationRepeat(Animator animation) {

            }
        });
        animatorSetsuofang.setDuration(400);
        animatorSetsuofang.setInterpolator(new DecelerateInterpolator());
        animatorSetsuofang.play(scaleX).with(scaleY);
        animatorSetsuofang.start();

    }

    private FrameLayout.LayoutParams getBoomLayoutParams() {
        FrameLayout.LayoutParams params = new FrameLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        params.setMargins(animViewX - 70, animViewY - 70, 0, 0);
        return params;
    }

    private boolean viabrate;

    public void startAnim(final int viewX, int viewY) {
        animViewX = viewX;
        animViewY = viewY;
        pause = false;
        if (animThrad == null) {
            startControl = true;
            animThrad = new Thread(new Runnable() {
                @Override
                public void run() {
                    while (startControl) {
                        try {
                            if (!pause) {
                                ExplosiveView.this.post(new Runnable() {
                                    @Override
                                    public void run() {
                                        if (viabrate)
                                            viabrate();
                                        viabrate = !viabrate;
                                        addEmojiView();
                                    }
                                });
                            }
                            Thread.sleep(50);
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                    }
                }
            });
            animThrad.start();
        }

    }

    public void pauseAnim() {
        pause = true;
    }

    public void stopAnim() {
        startControl = false;
        if (animThrad != null) {
            animThrad.interrupt();
            animThrad = null;
        }
    }


    public void addEmojiView() {
        FrameLayout.LayoutParams params = getEmojiLayoutParams();
        Bitmap cacheBitmap = getCacheBitmap();

        ImageView emojiImg = new ImageView(getContext());
        emojiImg.setAlpha(0.8f);
        emojiImg.setImageBitmap(cacheBitmap);
        emojiImg.setLayoutParams(params);
        addView(emojiImg);
        AnimatorSet anim = getEmojiValueAnimator(emojiImg);
        anim.addListener(new AnimEndListener(emojiImg));
        anim.setInterpolator(line);
        anim.start();
    }

    private FrameLayout.LayoutParams getEmojiLayoutParams() {
        FrameLayout.LayoutParams params = new FrameLayout.LayoutParams(emojiWidth, emojiWidth);
        params.setMargins(animViewX, animViewY, 0, 0);
        return params;
    }


    private AnimatorSet getEmojiValueAnimator(ImageView emojiImg) {

        //(float) (animViewX+ getRandomNum(800, 500) * Math.cos(Math.toRadians(30 * i))
        int i = getI();
        int randomNum = 500;
        int x = (int) Math.round(Math.sin(Math.toRadians(10 * i)) * 400);
        int y = (int) Math.round(Math.cos(Math.toRadians(10 * i)) * 400);
        ValueAnimator animatorX = ObjectAnimator.ofFloat(emojiImg, "translationX", 0, x);
        ValueAnimator animatorY = ObjectAnimator.ofFloat(emojiImg, "translationY", 0, y);
        AnimatorSet animator = new AnimatorSet();
        animator.playTogether(animatorX, animatorY);
        animator.setDuration(randomNum);


        return animator;
    }

    int lastI = 0;

    private int getI() {
        int i = getRandomNum(36, 1);
        if (Math.abs(i - lastI) < 3) {
            i = getI();
        }
        lastI = i;
        return i;
    }

    private int getRandomNum(int max, int min) {
        int margin = random.nextInt(max) % (max - min + 1) + min;
        return margin;
    }


    private class AnimEndListener extends AnimatorListenerAdapter {
        private View target;

        public AnimEndListener(View target) {
            this.target = target;
        }

        @RequiresApi(api = Build.VERSION_CODES.O)
        @Override
        public void onAnimationStart(Animator animation) {

            super.onAnimationStart(animation);
        }

        @Override
        public void onAnimationEnd(Animator animation) {
            super.onAnimationEnd(animation);
            //因为不停的add 导致子view数量只增不减,所以在view动画结束后remove掉
            removeView((target));
        }

    }

    private void viabrate() {
        if (vibrator.hasVibrator()) {
            if (vibrationEffect != null && Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                vibrator.vibrate(vibrationEffect);
            } else {
                vibrator.vibrate(20);
            }
        }
    }
    private void viabrate(long millisec,int amplitude){
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
            vibrationEffect2 = VibrationEffect.createOneShot(millisec, amplitude);
            vibrator.vibrate(vibrationEffect2);
        }else {
            vibrator.vibrate(millisec);
        }
    }


    private void initDefaultBitmap() {
        int laugh1 = getContext().getResources().getIdentifier("emoji1", "drawable", getContext().getPackageName());
        int laugh2 = getContext().getResources().getIdentifier("emoji2", "drawable", getContext().getPackageName());
        int laugh3 = getContext().getResources().getIdentifier("emoji3", "drawable", getContext().getPackageName());
        int laugh4 = getContext().getResources().getIdentifier("emoji4", "drawable", getContext().getPackageName());
        int laugh5 = getContext().getResources().getIdentifier("emoji17", "drawable", getContext().getPackageName());
        int laugh6 = getContext().getResources().getIdentifier("emoji18", "drawable", getContext().getPackageName());
        int laugh7 = getContext().getResources().getIdentifier("emoji19", "drawable", getContext().getPackageName());
        int laugh8 = getContext().getResources().getIdentifier("emoji20", "drawable", getContext().getPackageName());
        int laugh9 = getContext().getResources().getIdentifier("emoji3", "drawable", getContext().getPackageName());
        int laugh10 = getContext().getResources().getIdentifier("emoji3", "drawable", getContext().getPackageName());
        int laugh11 = getContext().getResources().getIdentifier("emoji3", "drawable", getContext().getPackageName());
        int laugh12 = getContext().getResources().getIdentifier("emoji3", "drawable", getContext().getPackageName());
        ArrayList<Integer> laughs = new ArrayList<>();
        laughs.add(laugh9);
        laughs.add(laugh1);
        laughs.add(laugh2);
        laughs.add(laugh3);
        laughs.add(laugh4);
        laughs.add(laugh5);
        laughs.add(laugh10);
        laughs.add(laugh6);
        laughs.add(laugh7);
        laughs.add(laugh11);
        laughs.add(laugh8);
        laughs.add(laugh12);
        for (int i = 110; i <= 139; i++) {
            int result = getContext().getResources().getIdentifier("emoji" + i, "drawable", getContext().getPackageName());
            int result2 = getContext().getResources().getIdentifier("emoji" + (i - 80), "drawable", getContext().getPackageName());
            Bitmap bitmap = BitmapFactory.decodeResource(getResources(), result);
            Bitmap bitmap2 = BitmapFactory.decodeResource(getResources(), result2);
            addBitmapToMemory(result + "", bitmap);
            addBitmapToMemory(result2 + "", bitmap2);
            if (i % 2 == 0) {
                for (Integer j : laughs) {
                    Bitmap bitmap3 = BitmapFactory.decodeResource(getResources(), j);
                    addBitmapToMemory(result + i + j + "", bitmap3);
                }
            }
        }
    }

    // 把Bitmap对象加入到缓存中
    public void addBitmapToMemory(String key, Bitmap bitmap) {
        if (getBitmapFromMemCache(key) == null) {
            if (!keys.contains(key))
                keys.add(key);
            lruCache.put(key, bitmap);
        }
    }

    // 从缓存中得到Bitmap对象
    public Bitmap getBitmapFromMemCache(String key) {
        return lruCache.get(key);
    }

    // 从缓存中删除指定的Bitmap
    public void removeBitmapFromMemory(String key) {
        lruCache.remove(key);
        keys.remove(key);
    }

    public Bitmap getCacheBitmap() {
        Bitmap bitmap = null;
        if (keys != null) {
            if (currentKeyPosition >= keys.size()) {
                currentKeyPosition = 0;
            }
            for (int i = 0; i < keys.size(); i++) {
                if (i == currentKeyPosition) {
                    bitmap = getBitmapFromMemCache(keys.get(i));
                    currentKeyPosition++;
                    return bitmap;
                }
            }
        }
        if (bitmap == null && lruCache.size() > 0) {
            bitmap = getCacheBitmap();
        }
        return bitmap;
    }

}
