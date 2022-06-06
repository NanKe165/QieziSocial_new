package com.xiao.nicevideoplayer;

import android.content.Context;
import android.graphics.Matrix;
import android.util.Log;
import android.view.TextureView;

/**
 * Created by XiaoJianjun on 2017/6/21.
 * 重写TextureView，适配视频的宽高和旋转.
 * （参考自节操播放器 https://github.com/lipangit/JieCaoVideoPlayer）
 */
public class NiceTextureView extends TextureView {

    private int videoHeight;
    private int videoWidth;

    public NiceTextureView(Context context) {
        super(context);
    }


    public void adaptVideoSize(int videoWidth, int videoHeight) {
        if (this.videoWidth != videoWidth && this.videoHeight != videoHeight) {
            this.videoWidth = videoWidth;
            this.videoHeight = videoHeight;
            requestLayout();
        }
    }

    @Override
    public void setRotation(float rotation) {
        if (rotation != getRotation()) {
            super.setRotation(rotation);
            requestLayout();
        }
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {

        float viewRotation = getRotation();

        // 如果判断成立，则说明显示的TextureView和本身的位置是有90度的旋转的，所以需要交换宽高参数。
        if (viewRotation == 90f || viewRotation == 270f) {
            int tempMeasureSpec = widthMeasureSpec;
            widthMeasureSpec = heightMeasureSpec;
            heightMeasureSpec = tempMeasureSpec;
        }

        int width = getDefaultSize(videoWidth, widthMeasureSpec);
        int height = getDefaultSize(videoHeight, heightMeasureSpec);
        if (videoWidth > 0 && videoHeight > 0) {

            int widthSpecMode = MeasureSpec.getMode(widthMeasureSpec);
            int widthSpecSize = MeasureSpec.getSize(widthMeasureSpec);
            int heightSpecMode = MeasureSpec.getMode(heightMeasureSpec);
            int heightSpecSize = MeasureSpec.getSize(heightMeasureSpec);

            if (widthSpecMode == MeasureSpec.EXACTLY && heightSpecMode == MeasureSpec.EXACTLY) {
                // the size is fixed
                width = widthSpecSize;
                height = heightSpecSize;
                // for compatibility, we adjust size based on aspect ratio
                if (videoWidth * height < width * videoHeight) {
                    width = height * videoWidth / videoHeight;
                } else if (videoWidth * height > width * videoHeight) {
                    height = width * videoHeight / videoWidth;
                }
            } else if (widthSpecMode == MeasureSpec.EXACTLY) {
                // only the width is fixed, adjust the height to match aspect ratio if possible
                width = widthSpecSize;
                height = width * videoHeight / videoWidth;
                if (heightSpecMode == MeasureSpec.AT_MOST && height > heightSpecSize) {
                    // couldn't match aspect ratio within the constraints
                    height = heightSpecSize;
                    width = height * videoWidth / videoHeight;
                }
            } else if (heightSpecMode == MeasureSpec.EXACTLY) {
                // only the height is fixed, adjust the width to match aspect ratio if possible
                height = heightSpecSize;
                width = height * videoWidth / videoHeight;
                if (widthSpecMode == MeasureSpec.AT_MOST && width > widthSpecSize) {
                    // couldn't match aspect ratio within the constraints
                    width = widthSpecSize;
                    height = width * videoHeight / videoWidth;
                }
            } else {
                // neither the width nor the height are fixed, try to use actual video size
                width = videoWidth;
                height = videoHeight;
                if (heightSpecMode == MeasureSpec.AT_MOST && height > heightSpecSize) {
                    // too tall, decrease both width and height
                    height = heightSpecSize;
                    width = height * videoWidth / videoHeight;
                }
                if (widthSpecMode == MeasureSpec.AT_MOST && width > widthSpecSize) {
                    // too wide, decrease both width and height
                    width = widthSpecSize;
                    height = width * videoHeight / videoWidth;
                }
            }
        } else {
            // no size yet, just adopt the given spec sizes
        }
        if (isCenterCroop){
            Log.e("social", "onMeasure:   super" );
            super.onMeasure(widthMeasureSpec,heightMeasureSpec);
        }else {
            Log.e("social", "onMeasure:   mine" );
            setMeasuredDimension(width, height);
        }
    }
    boolean isCenterCroop=false;
    //重新计算video的显示位置，裁剪后全屏显示
    public void updateTextureViewSizeCenterCrop(){
        isCenterCroop=true;
        float sx = (float) getWidth() / (float) videoWidth;
        float sy = (float) getHeight() / (float) videoHeight;

        Matrix matrix = new Matrix();
        float maxScale = Math.max(sx, sy);

        //第1步:把视频区移动到View区,使两者中心点重合.
        matrix.preTranslate((getWidth() - videoWidth) / 2, (getHeight() - videoHeight) / 2);

        //第2步:因为默认视频是fitXY的形式显示的,所以首先要缩放还原回来.
        matrix.preScale(videoWidth / (float) getWidth(), videoHeight / (float) getHeight());

        //第3步,等比例放大或缩小,直到视频区的一边超过View一边, 另一边与View的另一边相等. 因为超过的部分超出了View的范围,所以是不会显示的,相当于裁剪了.
        matrix.postScale(maxScale, maxScale, getWidth() / 2, getHeight() / 2);//后两个参数坐标是以整个View的坐标系以参考的

        setTransform(matrix);
        postInvalidate();
    }

}
