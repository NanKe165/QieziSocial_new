package com.eggplant.qiezisocial.widget.round;

/**
 * Created by Administrator on 2019/1/25.
 */

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.RectF;
import android.graphics.Region;
import android.util.AttributeSet;
import android.view.View;
import android.widget.RelativeLayout;

import com.eggplant.qiezisocial.R;
import com.eggplant.qiezisocial.utils.ScreenUtil;

/**
 * 圆角布局
 * 支持自定义属性见<>declare-styleable name="RoundLayout"</>
 * <p>
 * 需要圆角的View作为<>RoundLayout</>的子View即可，设置<>attr_round_corner</>圆角半径
 */
public class RoundLayout extends RelativeLayout {
    public float[] radii = new float[8];   // top-left, top-right, bottom-right, bottom-left
    public Path mClipPath;                 // 剪裁区域路径
    public Paint mPaint;                   // 画笔
    public int mStrokeColor;               // 描边颜色
    public int mStrokeWidth;               // 描边半径
    public boolean mClipBackground;        // 是否剪裁背景
    public Region mAreaRegion;             // 内容区域
    public RectF mLayer;                   // 画布图层大小
    private int mRoundCorner;              // 圆角大小

    public RoundLayout(Context context, int radius) {
        this(context);
        this.mRoundCorner = ScreenUtil.dip2px(context, radius);
    }

    public RoundLayout(Context context) {
        this(context, null);
    }

    public RoundLayout(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public RoundLayout(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initAttrs(context, attrs);
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        mLayer.set(0, 0, w, h);
        refreshRegion(this);
    }

    @Override
    protected void dispatchDraw(Canvas canvas) {
        canvas.clipPath(mClipPath);
        super.dispatchDraw(canvas);
//        canvas.saveLayer(mLayer, null, Canvas.ALL_SAVE_FLAG);
//        super.dispatchDraw(canvas);
//        onClipDraw(canvas);
//        canvas.restore();

    }

//
//        @Override
//    public void draw(Canvas canvas) {
//        refreshRegion(this);
//        if (mClipBackground) {
//            canvas.save();
//            canvas.clipPath(mClipPath);
//            super.draw(canvas);
//            canvas.restore();
//        } else {
//            super.draw(canvas);
//        }
//    }


    public void initAttrs(Context context, AttributeSet attrs) {
        TypedArray ta = context.obtainStyledAttributes(attrs, R.styleable.RoundLayout);
        mStrokeColor = ta.getColor(R.styleable.RoundLayout_attr_stroke_color, Color.WHITE);
        mStrokeWidth = ta.getDimensionPixelSize(R.styleable.RoundLayout_attr_stroke_width, 0);
        mClipBackground = ta.getBoolean(R.styleable.RoundLayout_attr_clip_background, false);
        if (mRoundCorner == 0) {
            mRoundCorner = ta.getDimensionPixelSize(R.styleable.RoundLayout_attr_round_corner, 4);
        }

        int roundCornerTopLeft = ta.getDimensionPixelSize(
                R.styleable.RoundLayout_attr_round_corner_top_left, mRoundCorner);
        int roundCornerTopRight = ta.getDimensionPixelSize(
                R.styleable.RoundLayout_attr_round_corner_top_right, mRoundCorner);
        int roundCornerBottomLeft = ta.getDimensionPixelSize(
                R.styleable.RoundLayout_attr_round_corner_bottom_left, mRoundCorner);
        int roundCornerBottomRight = ta.getDimensionPixelSize(
                R.styleable.RoundLayout_attr_round_corner_bottom_right, mRoundCorner);
        ta.recycle();

        radii[0] = roundCornerTopLeft;
        radii[1] = roundCornerTopLeft;

        radii[2] = roundCornerTopRight;
        radii[3] = roundCornerTopRight;

        radii[4] = roundCornerBottomRight;
        radii[5] = roundCornerBottomRight;

        radii[6] = roundCornerBottomLeft;
        radii[7] = roundCornerBottomLeft;

        mLayer = new RectF();
        mClipPath = new Path();
        mAreaRegion = new Region();
        mPaint = new Paint();
        mPaint.setColor(Color.WHITE);
        mPaint.setAntiAlias(true);

        setLayerType(View.LAYER_TYPE_HARDWARE, null);
    }

    public void refreshRegion(View view) {
        int w = (int) mLayer.width();
        int h = (int) mLayer.height();
        RectF areas = new RectF();
        areas.left = view.getPaddingLeft();
        areas.top = view.getPaddingTop();
        areas.right = w - view.getPaddingRight();
        areas.bottom = h - view.getPaddingBottom();
        mClipPath.reset();

        mClipPath.addRoundRect(areas, radii, Path.Direction.CW);
        Region clip = new Region((int) areas.left, (int) areas.top,
                (int) areas.right, (int) areas.bottom);
        mAreaRegion.setPath(mClipPath, clip);


    }

    public void setRadios(int radios) {
        mRoundCorner = radios;
        int roundCornerTopLeft = radios;
        int roundCornerTopRight = radios;
        int roundCornerBottomLeft = radios;
        int roundCornerBottomRight = radios;

        radii[0] = roundCornerTopLeft;
        radii[1] = roundCornerTopLeft;

        radii[2] = roundCornerTopRight;
        radii[3] = roundCornerTopRight;

        radii[4] = roundCornerBottomRight;
        radii[5] = roundCornerBottomRight;

        radii[6] = roundCornerBottomLeft;
        radii[7] = roundCornerBottomLeft;
        invalidate();
    }

    public void onClipDraw(Canvas canvas) {
        if (mStrokeWidth > 0) {
            // 将与描边区域重叠的内容裁剪掉
            mPaint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.DST_OUT));
            mPaint.setColor(Color.WHITE);
            mPaint.setStrokeWidth(mStrokeWidth * 2);
            mPaint.setStyle(Paint.Style.STROKE);
            canvas.drawPath(mClipPath, mPaint);
            // 绘制描边
            mPaint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.SRC_OVER));
            mPaint.setColor(mStrokeColor);
            mPaint.setStyle(Paint.Style.STROKE);
            canvas.drawPath(mClipPath, mPaint);
        }
        mPaint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.DST_IN));
        mPaint.setColor(Color.RED);
        mPaint.setStyle(Paint.Style.FILL);
//        canvas.drawPath(mClipPath, mPaint);
    }


}
