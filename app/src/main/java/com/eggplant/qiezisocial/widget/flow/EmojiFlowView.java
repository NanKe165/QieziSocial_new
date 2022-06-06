package com.eggplant.qiezisocial.widget.flow;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.animation.ValueAnimator;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.support.v4.util.LruCache;
import android.util.AttributeSet;
import android.view.View;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.view.animation.AccelerateInterpolator;
import android.view.animation.DecelerateInterpolator;
import android.view.animation.Interpolator;
import android.view.animation.LinearInterpolator;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import com.eggplant.qiezisocial.R;
import com.eggplant.qiezisocial.utils.ScreenUtil;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

/**
 * Created by Administrator on 2020/4/22.
 */

public class EmojiFlowView extends RelativeLayout {
    private Random random = new Random();
    private int mHeight;//EmojiFlowView高
    private int mWidth;//EmojiFlowView宽
    private int emojiWidth = 180;//表情view宽
    private boolean bigEmojiFlag = false;

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
    private int emojiType;

    public void setBigEmojiFlag(boolean bigEmojiFlag) {
        this.bigEmojiFlag = bigEmojiFlag;
        emojiWidth = (int) getContext().getResources().getDimension(R.dimen.qb_px_35);
    }

    public EmojiFlowView(Context context) {
        super(context);
        init(context);
    }


    public EmojiFlowView(Context context, AttributeSet attrs) {
        super(context, attrs);
        TypedArray ta = context.obtainStyledAttributes(attrs, R.styleable.EmojiFlowView);
        emojiType = ta.getInteger(R.styleable.EmojiFlowView_background_emoji_type, 0);
        ta.recycle();
        init(context);
    }

    public EmojiFlowView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        TypedArray ta = context.obtainStyledAttributes(attrs, R.styleable.EmojiFlowView);
        emojiType = ta.getInteger(R.styleable.EmojiFlowView_background_emoji_type, 0);
        ta.recycle();
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
        if (emojiType==0){

            emojiWidth = (int) context.getResources().getDimension(R.dimen.qb_px_28);
        }else {
            this.bigEmojiFlag = true;
            emojiWidth = (int) context.getResources().getDimension(R.dimen.qb_px_47);
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

    private void initDefaultView() {
        int size = 12;
        if (bigEmojiFlag)
            size = 8;
        if (emojiType!=0)
            size=4;
        for (int i = 0; i < size; i++) {
            addEmojiView(true);
        }
    }


    private void initDefaultBitmap() {
        keys.clear();
        ArrayList<Integer> laughs = new ArrayList<>();
        if (emojiType == 0) {
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
        }else if (emojiType==1){
            int laugh1 = getContext().getResources().getIdentifier("icon_emoji_hellow1", "drawable", getContext().getPackageName());
            int laugh2 = getContext().getResources().getIdentifier("icon_emoji_hellow2", "drawable", getContext().getPackageName());
            int laugh3 = getContext().getResources().getIdentifier("icon_emoji_hellow3", "drawable", getContext().getPackageName());
            int laugh4 = getContext().getResources().getIdentifier("icon_emoji_hellow4", "drawable", getContext().getPackageName());
            int laugh5 = getContext().getResources().getIdentifier("icon_emoji_hellow5", "drawable", getContext().getPackageName());
            laughs.add(laugh1);
            laughs.add(laugh2);
            laughs.add(laugh3);
            laughs.add(laugh4);
            laughs.add(laugh5);
        }else if (emojiType==2){
            int laugh1 = getContext().getResources().getIdentifier("icon_emoji_good1", "drawable", getContext().getPackageName());
            int laugh2 = getContext().getResources().getIdentifier("icon_emoji_good2", "drawable", getContext().getPackageName());
            int laugh3 = getContext().getResources().getIdentifier("icon_emoji_good3", "drawable", getContext().getPackageName());
            laughs.add(laugh1);
            laughs.add(laugh2);
            laughs.add(laugh3);
        }else if (emojiType==3){
            int laugh1 = getContext().getResources().getIdentifier("icon_emoji_comfort1", "drawable", getContext().getPackageName());
            int laugh2 = getContext().getResources().getIdentifier("icon_emoji_comfort2", "drawable", getContext().getPackageName());
            int laugh3 = getContext().getResources().getIdentifier("icon_emoji_comfort3", "drawable", getContext().getPackageName());
            int laugh4 = getContext().getResources().getIdentifier("icon_emoji_comfort4", "drawable", getContext().getPackageName());
            laughs.add(laugh1);
            laughs.add(laugh2);
            laughs.add(laugh3);
            laughs.add(laugh4);
        }
        for (Integer j : laughs) {
            Bitmap bitmap3 = BitmapFactory.decodeResource(getResources(), j);
            addBitmapToMemory(j + "", bitmap3);
        }
//        for (int i = 110; i <= 139; i++) {
//            int result = getContext().getResources().getIdentifier("emoji" + i, "drawable", getContext().getPackageName());
//            int result2 = getContext().getResources().getIdentifier("emoji" + (i - 80), "drawable", getContext().getPackageName());
//            Bitmap bitmap = BitmapFactory.decodeResource(getResources(), result);
//            Bitmap bitmap2 = BitmapFactory.decodeResource(getResources(), result2);
//            addBitmapToMemory(result + "", bitmap);
//            addBitmapToMemory(result2 + "", bitmap2);
//            if (i % 2 == 0) {
//                for (Integer j : laughs) {
//                    Bitmap bitmap3 = BitmapFactory.decodeResource(getResources(), j);
//                    addBitmapToMemory(result + i + j + "", bitmap3);
//                }
//            }
//        }
    }

    public void setEmojiType(Context context,int emojiType) {
        this.emojiType = emojiType;
        if (emojiType==0){
            emojiWidth = (int) context.getResources().getDimension(R.dimen.qb_px_28);
        }else {
            this.bigEmojiFlag = true;
            emojiWidth = (int) context.getResources().getDimension(R.dimen.qb_px_47);
        }
        initDefaultBitmap();
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

    public void startAnim() {
        pause = false;
        if (animThrad == null) {
            startControl = true;
            animThrad = new Thread(new Runnable() {
                @Override
                public void run() {
                    EmojiFlowView.this.post(new Runnable() {
                        @Override
                        public void run() {
                            initDefaultView();
                        }
                    });
                    while (startControl) {
                        try {
                            if (!pause) {
                                EmojiFlowView.this.post(new Runnable() {
                                    @Override
                                    public void run() {
                                        addEmojiView(false);
                                    }
                                });
                            }
                            Thread.sleep(sleepTime);
                            if (emojiType==0) {
                                sleepTime = getRandomNum(200, 350);
                            }else {
                                sleepTime = getRandomNum(1000, 1150);
                            }
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


    public void addEmojiView(boolean init) {
        LayoutParams params = getEmojiLayoutParams(init);
        Bitmap cacheBitmap = getCacheBitmap();

        ImageView emojiImg = new ImageView(getContext());
        if (!bigEmojiFlag)
            emojiImg.setAlpha(0.5f);
        emojiImg.setImageBitmap(cacheBitmap);
        emojiImg.setLayoutParams(params);
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
            randomNum = getRandomNum(5000, 8000);
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
            animatorSet.play(animator);
        }
        return animatorSet;
    }

    private RelativeLayout.LayoutParams getEmojiLayoutParams(boolean init) {
        int margit = getLeftMargin();
        LayoutParams params = new LayoutParams(emojiWidth, emojiWidth);
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
        margin = getRandomNum(ScreenUtil.getDisplayWidthPixels(getContext()) - emojiWidth, 1);
//        int max = emojiWidth * 6;
//        int min = emojiWidth / 2;
//        int randomNum = getRandomNum(max, min);
//
//        if (randomNum % 2 == 0) {
//            margin += randomNum;
//        } else {
//            margin -= randomNum;
//        }
//
//        if (margin > mWidth - emojiWidth) {
//            margin = getRandomNum(mWidth-emojiWidth,mWidth-emojiWidth*2);
//        }else if (margin<0){
//            margin=getRandomNum(1,lastmargin);
//        }
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
}
