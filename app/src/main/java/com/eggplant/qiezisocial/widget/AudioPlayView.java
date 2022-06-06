package com.eggplant.qiezisocial.widget;

import android.animation.Animator;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.animation.ValueAnimator;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.drawable.AnimationDrawable;
import android.os.Build;
import android.os.VibrationEffect;
import android.os.Vibrator;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.annotation.RequiresApi;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.eggplant.qiezisocial.R;
import com.luck.picture.lib.tools.ScreenUtils;

/**
 * Created by Administrator on 2019/6/21.
 */

public class AudioPlayView extends FrameLayout {

    private FrameLayout playGp;
    private ImageView close;
    private TextView duraTv;
    private ImageView animImg;
    private WaveTextView recordTv;
    private int minWidth, minRightMargin;
    boolean playingStart = false;
    private int displayType;
    private int mViewWidth;

    private Vibrator vibrator;
    private VibrationEffect vibrationEffect;
    public AudioPlayView(@NonNull Context context) {
        this(context, null);
    }

    public AudioPlayView(@NonNull Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public AudioPlayView(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        TypedArray typedArray = context.obtainStyledAttributes(attrs, R.styleable.AudioPlayView);
        displayType = typedArray.getInt(R.styleable.AudioPlayView_displayStatus, 0);
        typedArray.recycle();
        init();
        LayoutInflater.from(context).inflate(R.layout.layout_aduio_play, this);
        playGp = findViewById(R.id.play_gp);
        close = findViewById(R.id.close);
        duraTv = findViewById(R.id.dura);
        animImg = findViewById(R.id.anim_img);
        recordTv = findViewById(R.id.record);

        setDisplayType(displayType);
        close.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                close.setClickable(false);
                stopPlayingAnim();
                if (listener != null)
                    listener.releaseVoice();
                showRecord();
            }
        });
        playGp.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                if (!playingStart) {
                    if (listener != null)
                        listener.review();
                    startPlayingAnim();
                } else {
                    if (listener != null)
                        listener.stopVoice();
                    stopPlayingAnim();
                }
            }
        });
    }


    private void init() {
        minWidth = ScreenUtils.dip2px(getContext(), 110);
        minRightMargin = ScreenUtils.dip2px(getContext(), 60);

        vibrator = (Vibrator) getContext().getSystemService(Context.VIBRATOR_SERVICE);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            vibrationEffect = VibrationEffect.createOneShot(40, 1);
        }
    }

    /**
     * 播放转录制的转场动画
     */
    private void showRecord() {
        final int measuredWidth = playGp.getMeasuredWidth();
        ObjectAnimator rotateAnim = ObjectAnimator.ofFloat(close, "rotation", 0f, 90f);
        rotateAnim.setDuration(200);
        rotateAnim.addListener(new Animator.AnimatorListener() {
            @Override
            public void onAnimationStart(Animator animator) {

            }

            @Override
            public void onAnimationEnd(Animator animator) {
                close.setVisibility(GONE);
                close.setClickable(true);
            }

            @Override
            public void onAnimationCancel(Animator animator) {
                close.setVisibility(GONE);
                close.setClickable(true);
            }

            @Override
            public void onAnimationRepeat(Animator animator) {

            }
        });
        int maxWidth  = this.getMeasuredWidth();
        ValueAnimator paramAnim = ValueAnimator.ofInt(measuredWidth, maxWidth);
        paramAnim.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator valueAnimator) {
                float animatedFraction = valueAnimator.getAnimatedFraction();
                int animatedValue = (int) valueAnimator.getAnimatedValue();
                playGp.setLayoutParams(new LinearLayout.LayoutParams(animatedValue, playGp.getHeight()));
                recordTv.setAlpha(animatedFraction);
            }
        });
        paramAnim.addListener(new Animator.AnimatorListener() {
            @Override
            public void onAnimationStart(Animator animator) {
                duraTv.setVisibility(GONE);
                animImg.setVisibility(GONE);
                recordTv.setVisibility(VISIBLE);
            }

            @Override
            public void onAnimationEnd(Animator animator) {

            }

            @Override
            public void onAnimationCancel(Animator animator) {

            }

            @Override
            public void onAnimationRepeat(Animator animator) {

            }
        });
        paramAnim.setDuration(300);

        AnimatorSet animatorSet = new AnimatorSet();
        animatorSet.playSequentially(rotateAnim, paramAnim);
        animatorSet.start();
    }

    public void initAudioPlayview() {
        close.setVisibility(GONE);
        close.setClickable(true);
        duraTv.setVisibility(GONE);
        animImg.setVisibility(GONE);
        recordTv.setVisibility(VISIBLE);
        if (mViewWidth != 0) {
            playGp.setLayoutParams(new LinearLayout.LayoutParams(mViewWidth, playGp.getHeight()));
        }
    }

    /**
     * 设置语音时间，开始录制转播放的转场动画
     *
     * @param dura 时间 S
     */
    public void setDuraTv(int dura) {
        if (dura <= 0) {
            dura = 1;
        }
        duraTv.setText(dura + "\"");
        int maxWidth = this.getMeasuredWidth();
        int finalWidth = minWidth + dura * 5 > maxWidth - minRightMargin ? maxWidth - minRightMargin : minWidth + dura * 5;
        ValueAnimator paramAnim = ValueAnimator.ofInt(maxWidth, finalWidth);
        paramAnim.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator valueAnimator) {
                float animatedFraction = valueAnimator.getAnimatedFraction();
                int animatedValue = (int) valueAnimator.getAnimatedValue();
                playGp.setLayoutParams(new LinearLayout.LayoutParams(animatedValue, playGp.getHeight()));
                animImg.setAlpha(animatedFraction);
                duraTv.setAlpha(animatedFraction);
            }
        });
        paramAnim.addListener(new Animator.AnimatorListener() {
            @Override
            public void onAnimationStart(Animator animator) {
                duraTv.setVisibility(VISIBLE);
                animImg.setVisibility(VISIBLE);
                close.setVisibility(VISIBLE);
                recordTv.setVisibility(GONE);
            }

            @Override
            public void onAnimationEnd(Animator animator) {

            }

            @Override
            public void onAnimationCancel(Animator animator) {

            }

            @Override
            public void onAnimationRepeat(Animator animator) {

            }
        });
        paramAnim.setDuration(300);
        paramAnim.start();
    }

    /**
     * 设置语音时间，不展示关闭按钮
     *
     * @param dura
     */
    public void setDuraTvWithoutClose(int dura) {
        setDuraTvWithoutMeasure(dura);
        int maxWidth = this.getMeasuredWidth();
        int finalWidth = minWidth + dura * 5 > maxWidth - minRightMargin ? maxWidth - minRightMargin : minWidth + dura * 5;
        playGp.setLayoutParams(new LinearLayout.LayoutParams(finalWidth, playGp.getHeight()));
    }

    /**
     * 设置语音时间，不重新测量宽度
     *
     * @param dura
     */
    public void setDuraTvWithoutMeasure(int dura) {
        if (dura <= 0) {
            dura = 1;
        }
        duraTv.setText(dura + "\"");
        duraTv.setVisibility(VISIBLE);
        animImg.setVisibility(VISIBLE);
        recordTv.setVisibility(GONE);
    }

    public void setCloseVisiable(int visiable) {
        close.setVisibility(visiable);
    }

    /**
     * 开始播放语音动画
     */
    public void startPlayingAnim() {
        AnimationDrawable anim = (AnimationDrawable) animImg.getDrawable();
        anim.start();
        playingStart = true;
    }

    /**
     * 停止播放语音动画
     */
    public void stopPlayingAnim() {
        AnimationDrawable anim = (AnimationDrawable) animImg.getDrawable();
        anim.stop();
        playingStart = false;
    }

    /**
     * 开始录音动画
     */
    @RequiresApi(api = Build.VERSION_CODES.O)
    public void statRecordAinm() {
        if (vibrator.hasVibrator()) {
            if (vibrationEffect != null) {
                vibrator.vibrate(vibrationEffect);
            } else {
                vibrator.vibrate(40);
            }
        }
        recordTv.startAnim();
    }

    /**
     * 停止录音动画
     */
    public void stopRecordAnim() {
        recordTv.stopAnim();
    }

    boolean clickable = true;

    public void setClick(boolean clickable) {
        this.clickable = clickable;
        close.setClickable(clickable);
    }

    public boolean getClickable() {
        return clickable;
    }

    public TextView getRecordTv() {
        return recordTv;
    }

    AudioPlayListener listener;

    public void setDisplayType(int displayType) {
        if (displayType == 0) {

        } else {
            duraTv.setVisibility(VISIBLE);
            animImg.setVisibility(VISIBLE);
            recordTv.setVisibility(GONE);
            setCloseVisiable(View.GONE);
            setDuraTvWithoutMeasure(0);
        }
    }

    public interface AudioPlayListener {
        //释放语音
        void releaseVoice();

        //播放语音
        void review();

        //停止语音
        void stopVoice();
    }

    public void setAudioPlayListener(AudioPlayListener listener) {
        this.listener = listener;
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        mViewWidth = MeasureSpec.getSize(widthMeasureSpec);
    }
}
