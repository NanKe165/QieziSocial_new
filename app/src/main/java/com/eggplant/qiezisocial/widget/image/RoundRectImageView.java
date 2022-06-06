package com.eggplant.qiezisocial.widget.image;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.Rect;
import android.graphics.RectF;
import android.util.AttributeSet;

import com.eggplant.qiezisocial.R;
import com.eggplant.qiezisocial.utils.ScreenUtil;


/**
 * Created by Administrator on 2018/6/27.
 */

public class RoundRectImageView extends android.support.v7.widget.AppCompatImageView {

    private Paint paint;
    private int radian;
    private boolean isOnlyTop;

    public RoundRectImageView(Context context) {
        this(context, null);
    }

    public RoundRectImageView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public RoundRectImageView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        TypedArray a = context.obtainStyledAttributes(attrs, R.styleable.RoundRectImageView);
        radian = ScreenUtil.dip2px(context, a.getInt(R.styleable.RoundRectImageView_round_radian, 5));
        isOnlyTop = a.getBoolean(R.styleable.RoundRectImageView_uesTop, false);
        a.recycle();
        paint = new Paint();
    }

    float width, height;

    @Override
    protected void onLayout(boolean changed, int left, int top, int right, int bottom) {
        super.onLayout(changed, left, top, right, bottom);
        width = getWidth();
        height = getHeight() < getMaxHeight() ? getHeight() : getMaxHeight();
    }

    /**
     * 绘制圆角矩形图片
     *
     * @author caizhiming
     */
    @Override
    protected void onDraw(Canvas canvas) {

        if (width > radian && height > radian) {
            Path path = new Path();
            path.moveTo(radian, 0);
            path.lineTo(width - radian, 0);
            path.quadTo(width, 0, width, radian);
            if (isOnlyTop) {
                path.lineTo(width, height);
                path.lineTo(0, height);
            } else {
                path.lineTo(width, height - radian);
                path.quadTo(width, height, width - radian, height);
                path.lineTo(radian, height);
                path.quadTo(0, height, 0, height - radian);
            }

            path.lineTo(0, radian);
            path.quadTo(0, 0, radian, 0);
            canvas.clipPath(path);
        }

        super.onDraw(canvas);
    }


    /**
     * 获取圆角矩形图片方法
     *
     * @param bitmap
     * @param roundPx,圆角的弧度
     * @return Bitmap
     * @author caizhiming
     */
    private Bitmap getRoundBitmap(Bitmap bitmap, int roundPx) {

        Bitmap output = Bitmap.createBitmap(bitmap.getWidth(),
                bitmap.getHeight(), Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(output);

        final int color = 0xff424242;
        final Rect rect = new Rect(0, 0, bitmap.getWidth(), bitmap.getHeight());
        final RectF rectF = new RectF(rect);
        paint.setAntiAlias(true);
        canvas.drawARGB(0, 0, 0, 0);
        paint.setColor(color);
        canvas.drawRoundRect(rectF, roundPx, roundPx, paint);
        paint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.SRC_IN));
        canvas.drawBitmap(bitmap, rect, rect, paint);
        return output;
    }
}