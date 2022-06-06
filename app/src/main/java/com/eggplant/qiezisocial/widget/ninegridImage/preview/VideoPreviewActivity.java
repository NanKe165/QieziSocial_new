package com.eggplant.qiezisocial.widget.ninegridImage.preview;

import android.animation.Animator;
import android.animation.ValueAnimator;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.util.DisplayMetrics;
import android.view.View;
import android.view.ViewTreeObserver;
import android.widget.FrameLayout;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.DataSource;
import com.bumptech.glide.load.engine.GlideException;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.target.Target;
import com.eggplant.qiezisocial.R;
import com.eggplant.qiezisocial.model.API;
import com.eggplant.qiezisocial.utils.FileUtils;
import com.eggplant.qiezisocial.widget.QzTextView;
import com.eggplant.qiezisocial.widget.ninegridImage.ImageInfo;
import com.xiao.nicevideoplayer.NiceVideoPlayer;
import com.xiao.nicevideoplayer.NiceVideoPlayerManager;
import com.xiao.nicevideoplayer.TxVideoPlayerController;

import java.io.File;
import java.util.List;

public class VideoPreviewActivity extends AppCompatActivity implements ViewTreeObserver.OnPreDrawListener {

    public static final String IMAGE_INFO = "IMAGE_INFO";
    public static final String PLAYER_RESOURCE = "PLAYER_RESOURCE";
    public static final String OPEN_DOWNLOAD = "OPEN_THE_DOWNLOAD";
    public static final String DOWNLOAD_FILE_NAME="DOWNLOAD_FILE_NAME";
    public static final int ANIMATE_DURATION = 200;

    private FrameLayout rootView;


    private List<ImageInfo> imageInfo;
    //    private int currentItem;
    private int imageHeight;
    private int imageWidth;
    private int screenWidth;
    private int screenHeight;
    private NiceVideoPlayer player;
    private ImageView imageView;
    private FrameLayout viewGp;
    private QzTextView loadTxt;
    private TxVideoPlayerController controller;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_preview_video);
//        Eyes.setStatusBarColor(this, ContextCompat.getColor(this, R.color.tv_black2));
        rootView = (FrameLayout) findViewById(R.id.rootView);
        viewGp = (FrameLayout) findViewById(R.id.preview_gp);
        player = (NiceVideoPlayer) findViewById(R.id.preview_player);
        imageView = (ImageView) findViewById(R.id.preview_img);
        loadTxt = findViewById(R.id.preview_load);

        int w = View.MeasureSpec.makeMeasureSpec(0,
                View.MeasureSpec.UNSPECIFIED);
        int h = View.MeasureSpec.makeMeasureSpec(0,
                View.MeasureSpec.UNSPECIFIED);
        imageView.measure(w, h);
        DisplayMetrics metric = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(metric);
        screenWidth = metric.widthPixels;
        screenHeight = metric.heightPixels;

        Intent intent = getIntent();
        imageInfo = (List<ImageInfo>) intent.getSerializableExtra(IMAGE_INFO);
        controller = new TxVideoPlayerController(this);
        if (imageInfo != null && imageInfo.size() == 1) {

            Glide.with(this)
                    .load(imageInfo.get(0).thumbnailUrl)
                    .listener(new RequestListener<Drawable>() {
                        @Override
                        public boolean onLoadFailed(@Nullable GlideException e, Object model, Target<Drawable> target, boolean isFirstResource) {
                            return false;
                        }

                        @Override
                        public boolean onResourceReady(Drawable resource, Object model, Target<Drawable> target, DataSource dataSource, boolean isFirstResource) {
                            viewGp.getViewTreeObserver().addOnPreDrawListener(VideoPreviewActivity.this);
                            return false;
                        }
                    })
                    .into(imageView);
            Glide.with(this).load(imageInfo.get(0).thumbnailUrl).into(controller.imageView());
        }

        String reource = intent.getStringExtra(PLAYER_RESOURCE);
        boolean openDownload = intent.getBooleanExtra(OPEN_DOWNLOAD, false);
        if (openDownload) {
            String fileName = intent.getStringExtra(DOWNLOAD_FILE_NAME);
            downLoadFile(reource,fileName);
        } else {
            if (reource != null) {
                player.setUp(reource, null);
            }
            player.setController(controller);
            player.start();
        }

        findViewById(R.id.preview_close).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finishActivityAnim();
            }
        });


    }

    private void downLoadFile(final String fileUrl,String fileName) {
        String mFileRelativeUrl = fileUrl.replace(API.Companion.getPIC_PREFIX(), "");
        int i = mFileRelativeUrl.lastIndexOf(".");
        if (i==-1){
            return;
        }
        final String mDestFileName = fileName + mFileRelativeUrl.substring(i, mFileRelativeUrl.length());
        String downloadFilePath = FileUtils.getTempFilePath(this);
        File file = new File(mDestFileName+downloadFilePath);
        if (file.exists()){
            player.setUp(downloadFilePath, null);
            player.setController(controller);
            player.start();
            return;
        }
        FileUtils.downloadFile(this, fileUrl, fileName, mFileRelativeUrl, downloadFilePath,new FileUtils.DownloadFileCallback() {
            @Override
            public void onError(String message) {
                loadTxt.setText("");
                player.setUp(fileUrl, null);
                player.setController(controller);
                player.start();
            }

            @Override
            public void onSuccess(String filePath) {
                loadTxt.setText("");
                if (filePath != null) {
                    player.setUp(filePath, null);
                }
                player.setController(controller);
                player.start();
            }

            @Override
            public void onProgress(Float progress) {
                loadTxt.setText("正在加载" + (int)(progress*100)+"%");
            }

        });
    }

    @Override
    public void onBackPressed() {
        if (NiceVideoPlayerManager.instance().onBackPressd()) return;
        finishActivityAnim();
    }

    /**
     * 绘制前开始动画
     */
    @Override
    public boolean onPreDraw() {
        rootView.getViewTreeObserver().removeOnPreDrawListener(this);

        computeImageWidthAndHeight(imageView);

        final ImageInfo imageData = imageInfo.get(0);
        final float vx = imageData.imageViewWidth * 1.0f / imageWidth;
        final float vy = imageData.imageViewHeight * 1.0f / imageHeight;
        ValueAnimator valueAnimator = ValueAnimator.ofFloat(0, 1.0f);
        valueAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                long duration = animation.getDuration();
                long playTime = animation.getCurrentPlayTime();
                float fraction = duration > 0 ? (float) playTime / duration : 1f;
                if (fraction > 1) fraction = 1;
                viewGp.setTranslationX(evaluateInt(fraction, imageData.imageViewX + imageData.imageViewWidth / 2 - imageView.getMeasuredWidth() / 2, 0));
                viewGp.setTranslationY(evaluateInt(fraction, imageData.imageViewY + imageData.imageViewHeight / 2 - imageView.getMeasuredHeight() / 2, 0));
                viewGp.setScaleX(evaluateFloat(fraction, vx, 1));
                viewGp.setScaleY(evaluateFloat(fraction, vy, 1));
                viewGp.setAlpha(fraction);
                rootView.setBackgroundColor(evaluateArgb(fraction, Color.TRANSPARENT, Color.BLACK));
            }
        });
        addIntoListener(valueAnimator);
        valueAnimator.setDuration(ANIMATE_DURATION);
        valueAnimator.start();
        return true;
    }

    private static final String TAG = "VideoPreviewActivity";

    /**
     * activity的退场动画
     */
    public void finishActivityAnim() {
//
        computeImageWidthAndHeight(imageView);

        final ImageInfo imageData = imageInfo.get(0);
        final float vx = imageData.imageViewWidth * 1.0f / imageWidth;
        final float vy = imageData.imageViewHeight * 1.0f / imageHeight;
        final ValueAnimator valueAnimator = ValueAnimator.ofFloat(0, 1.0f);
        valueAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                long duration = animation.getDuration();
                long playTime = animation.getCurrentPlayTime();
                float fraction = duration > 0 ? (float) playTime / duration : 1f;
                if (fraction > 1) fraction = 1;

                viewGp.setTranslationX(evaluateInt(fraction, 0, imageData.imageViewX + imageData.imageViewWidth / 2 - imageView.getMeasuredWidth() / 2));
                viewGp.setTranslationY(evaluateInt(fraction, 0, imageData.imageViewY + imageData.imageViewHeight / 2 - imageView.getMeasuredHeight() / 2));
                viewGp.setScaleX(evaluateFloat(fraction, 1, vx));
                viewGp.setScaleY(evaluateFloat(fraction, 1, vy));
                viewGp.setAlpha(1 - fraction);
                rootView.setBackgroundColor(evaluateArgb(fraction, Color.BLACK, Color.TRANSPARENT));
            }
        });
        addOutListener(valueAnimator);
        valueAnimator.setDuration(ANIMATE_DURATION);
        valueAnimator.start();
    }

    /**
     * 计算图片的宽高
     */
    private void computeImageWidthAndHeight(ImageView imageView) {

        // 获取真实大小
        Drawable drawable = imageView.getDrawable();
        if (drawable != null) {
            int intrinsicHeight = drawable.getIntrinsicHeight();
            int intrinsicWidth = drawable.getIntrinsicWidth();
            // 计算出与屏幕的比例，用于比较以宽的比例为准还是高的比例为准，因为很多时候不是高度没充满，就是宽度没充满
            float h = screenHeight * 1.0f / intrinsicHeight;
            float w = screenWidth * 1.0f / intrinsicWidth;
            if (h > w) h = w;
            else w = h;

            // 得出当宽高至少有一个充满的时候图片对应的宽高
            imageHeight = (int) (intrinsicHeight * h);
            imageWidth = (int) (intrinsicWidth * w);
        }
    }

    /**
     * 进场动画过程监听
     */
    private void addIntoListener(ValueAnimator valueAnimator) {
        valueAnimator.addListener(new Animator.AnimatorListener() {
            @Override
            public void onAnimationStart(Animator animation) {
                rootView.setBackgroundColor(0x0);
            }

            @Override
            public void onAnimationEnd(Animator animation) {
            }

            @Override
            public void onAnimationCancel(Animator animation) {
            }

            @Override
            public void onAnimationRepeat(Animator animation) {
            }
        });
    }

    /**
     * 退场动画过程监听
     */
    private void addOutListener(ValueAnimator valueAnimator) {
        valueAnimator.addListener(new Animator.AnimatorListener() {
            @Override
            public void onAnimationStart(Animator animation) {
                rootView.setBackgroundColor(0x0);
            }

            @Override
            public void onAnimationEnd(Animator animation) {
                finish();
                overridePendingTransition(0, 0);
            }

            @Override
            public void onAnimationCancel(Animator animation) {
            }

            @Override
            public void onAnimationRepeat(Animator animation) {
            }
        });
    }

    /**
     * Integer 估值器
     */
    public Integer evaluateInt(float fraction, Integer startValue, Integer endValue) {
        int startInt = startValue;
        return (int) (startInt + fraction * (endValue - startInt));
    }

    /**
     * Float 估值器
     */
    public Float evaluateFloat(float fraction, Number startValue, Number endValue) {
        float startFloat = startValue.floatValue();
        return startFloat + fraction * (endValue.floatValue() - startFloat);
    }

    /**
     * Argb 估值器
     */
    public int evaluateArgb(float fraction, int startValue, int endValue) {
        int startA = (startValue >> 24) & 0xff;
        int startR = (startValue >> 16) & 0xff;
        int startG = (startValue >> 8) & 0xff;
        int startB = startValue & 0xff;

        int endA = (endValue >> 24) & 0xff;
        int endR = (endValue >> 16) & 0xff;
        int endG = (endValue >> 8) & 0xff;
        int endB = endValue & 0xff;

        return (startA + (int) (fraction * (endA - startA))) << 24//
                | (startR + (int) (fraction * (endR - startR))) << 16//
                | (startG + (int) (fraction * (endG - startG))) << 8//
                | (startB + (int) (fraction * (endB - startB)));
    }

    @Override
    protected void onStop() {
        NiceVideoPlayerManager.instance().releaseNiceVideoPlayer();
        super.onStop();
    }

}
