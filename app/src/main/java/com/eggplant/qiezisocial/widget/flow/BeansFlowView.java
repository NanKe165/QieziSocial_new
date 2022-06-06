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
import android.support.v4.util.LruCache;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.view.animation.AccelerateInterpolator;
import android.view.animation.DecelerateInterpolator;
import android.view.animation.Interpolator;
import android.view.animation.LinearInterpolator;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import com.eggplant.qiezisocial.R;
import com.eggplant.qiezisocial.utils.ScreenUtil;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

/**
 * Created by Administrator on 2022/3/24.
 */

public class BeansFlowView extends RelativeLayout {
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
    //数据源
    private List<String> data;
    //点击后---获取到的数据
    private List<String> ownedData;
    private Vibrator vibrator;
    private VibrationEffect vibrationEffect;

    public BeansFlowView(Context context) {
        super(context);
        init(context);
    }

    public BeansFlowView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    public BeansFlowView(Context context, AttributeSet attrs, int defStyleAttr) {
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
        ownedData = new ArrayList<>();
        emojiWidth = (int) context.getResources().getDimension(R.dimen.qb_px_58);
        vibrator = (Vibrator) getContext().getSystemService(Context.VIBRATOR_SERVICE);
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
            vibrationEffect = VibrationEffect.createOneShot(20, 1);
        }

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
//        setBackgroundColor(ContextCompat.getColor(context, R.color.white));
        initDefaultBitmap();

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

    private void initDefaultBitmap() {
        int laugh1 = getContext().getResources().getIdentifier("beans1", "drawable", getContext().getPackageName());
        int laugh2 = getContext().getResources().getIdentifier("beans2", "drawable", getContext().getPackageName());
        int laugh3 = getContext().getResources().getIdentifier("beans3", "drawable", getContext().getPackageName());
        int laugh4 = getContext().getResources().getIdentifier("beans4", "drawable", getContext().getPackageName());
        int laugh5 = getContext().getResources().getIdentifier("beans5", "drawable", getContext().getPackageName());
        int laugh6 = getContext().getResources().getIdentifier("beans6", "drawable", getContext().getPackageName());
        int laugh7 = getContext().getResources().getIdentifier("beans7", "drawable", getContext().getPackageName());
        int laugh8 = getContext().getResources().getIdentifier("beans8", "drawable", getContext().getPackageName());
        int laugh9 = getContext().getResources().getIdentifier("beans9", "drawable", getContext().getPackageName());
        int laugh10 = getContext().getResources().getIdentifier("beans10", "drawable", getContext().getPackageName());
        int laugh11 = getContext().getResources().getIdentifier("beans11", "drawable", getContext().getPackageName());
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
        for (Integer j : laughs) {
            Bitmap bitmap3 = BitmapFactory.decodeResource(getResources(), j);
            addBitmapToMemory(j + "", bitmap3);
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


    private long sleepTime = 300;
    private int addViewSize = 0;

    public void startAnim() {
        pause = false;
        if (animThrad == null) {
            startControl = true;
            animThrad = new Thread(new Runnable() {
                @Override
                public void run() {
                    while (startControl && addViewSize > 0) {
                        try {
                            if (!pause) {
                                BeansFlowView.this.post(new Runnable() {
                                    @Override
                                    public void run() {
                                        addEmojiView(false);
                                        addViewSize--;
                                    }
                                });
                            }
                            Thread.sleep(sleepTime);
                            sleepTime = getRandomNum(200, 350);
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                    }
                    try {
                        Thread.sleep(3500);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                    if (listener != null)
                        listener.onAnimFinish();

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
        removeAllViews();
    }


    public void addEmojiView(boolean init) {
        LayoutParams params = getEmojiLayoutParams(init);
        Bitmap cacheBitmap = getCacheBitmap();

        final ImageView emojiImg = new ImageView(getContext());
        emojiImg.setImageBitmap(cacheBitmap);
        emojiImg.setLayoutParams(params);
        emojiImg.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                v.clearAnimation();
                viabrate();
                if (listener!=null){
                    int[] loca = new int[2];
                    v.getLocationOnScreen(loca);
                    listener.onViewClick(loca);
                }
                removeView(v);
//                Log.i("beansFlow","size---"+data.size());
                if (data != null && data.size() > 0) {
//                    Log.i("beansFlow","data---"+data.get(0));
                    ownedData.add(data.get(0));
                    data.remove(0);
                }
            }
        });
        addView(emojiImg);
        AnimatorSet anim = getEmojiValueAnimator(emojiImg, init);
        anim.addListener(new AnimEndListener(emojiImg));
        anim.setInterpolator(line);
        anim.start();
    }

    private AnimatorSet getEmojiValueAnimator(ImageView emojiImg, boolean init) {
        final AnimatorSet animatorSet = new AnimatorSet();
        int randomNum = 8000;
        if (init) {
            randomNum = getRandomNum(7000, 8000);
        } else {
            randomNum = getRandomNum(2500, 3500);
        }

        if (init) {
            ValueAnimator animator = ObjectAnimator.ofFloat(emojiImg, "translationY", mHeight);
            animator.setDuration(randomNum);
            ObjectAnimator scaleX = ObjectAnimator.ofFloat(emojiImg, "scaleX", 0f, 1f, 1f);
            ObjectAnimator scaleY = ObjectAnimator.ofFloat(emojiImg, "scaleY", 0f, 1f, 1f);
            scaleX.setDuration(500);
            scaleY.setDuration(500);
            animatorSet.play(scaleX).with(scaleY).before(animator);
        } else {
            ValueAnimator animator = ObjectAnimator.ofFloat(emojiImg, "translationY", -300.0f, mHeight);
            animator.setDuration(randomNum);
            animator.setInterpolator(new AccelerateInterpolator());
            animatorSet.play(animator);
        }
        return animatorSet;
    }

    private RelativeLayout.LayoutParams getEmojiLayoutParams(boolean init) {
        int margit = getLeftMargin();
        LayoutParams params = new LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        params.addRule(ALIGN_PARENT_TOP, TRUE);

        if (init) {
            int topMargin = getTopMargin();
            params.setMargins(margit, topMargin, 0, 0);
        } else {
            params.setMargins(margit, 0, 0, 0);
        }
        return params;
    }

    public int getLeftMargin() {
        int margin = emojiWidth;
        margin = getRandomNum(ScreenUtil.getDisplayWidthPixels(getContext()) - emojiWidth, emojiWidth);
        return margin;
    }


    private int getRandomNum(int max, int min) {
        int margin = random.nextInt(max) % (max - min + 1) + min;
        return margin;
    }

    public int getTopMargin() {
        int margin = emojiWidth;
        margin = getRandomNum(ScreenUtil.getDisplayHeightPixels(getContext()) / 2, emojiWidth);
        return margin;
    }

    public void setData(@NotNull List<String> jids) {
        this.data = jids;
        addViewSize = jids.size();
    }

    public List<String> getOwnedData() {
        return ownedData;
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
            if (target.getAnimation() == null)
                removeView((target));
        }
    }

    public interface ViewAnimListener {
        void onAnimFinish();

        void onViewClick(int[] v);
    }

    private ViewAnimListener listener;

    public void setViewAnimListener(ViewAnimListener listener) {
        this.listener = listener;
    }
}
