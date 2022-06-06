package com.eggplant.qiezisocial.widget;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.text.TextPaint;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.view.MotionEvent;
import android.view.View;
import android.widget.TextView;

import java.util.ArrayList;

/**
 * Created by Administrator on 2020/10/26.
 */

public class CustomSeekbar extends View {
    private final String TAG = "CustomSeekbar";
    private int width;
    private int height;
    //    private int downX = 0;
//    private int downY = 0;
    private int upX = 0;
    private int upY = 0;
    private int moveX = 0;
    private int moveY = 0;

    private int perWidth = 0;
    private Paint mPaint;
    private Paint mTextPaint;
    private Paint buttonPaint;
    private Canvas canvas;
    private Bitmap bitmap;
    private int hotarea = 100;//点击的热区
    private int cur_sections = 2;
    private ResponseOnTouch responseOnTouch;
    private int bitMapHeight = 5;//第一个点的起始位置起始，图片的长宽是10，所以取一半的距离
    private int textMove = 60;//字与下方点的距离，因为字体字体是40px，再加上10的间隔
    private int[] colors = new int[]{0xfffffe53, 0xff999999};//大圆颜色， 线颜色
    private int textSize;
    private int circleRadius;
    private ArrayList<String> section_title;
    private int linePadding = 30;
    private TextPaint measurePain;
    private boolean moveing = false;

    public CustomSeekbar(Context context) {
        super(context);
    }

    public CustomSeekbar(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public CustomSeekbar(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        cur_sections = 0;
        bitmap = Bitmap.createBitmap(900, 900, Bitmap.Config.ARGB_8888);
        canvas = new Canvas();
        canvas.setBitmap(bitmap);

        textMove = bitMapHeight + 22;
        textSize = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_SP, 12, getResources().getDisplayMetrics());
        circleRadius = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 3, getResources().getDisplayMetrics());
        linePadding = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 30, getResources().getDisplayMetrics());
        mPaint = new Paint(Paint.DITHER_FLAG);
        mPaint.setAntiAlias(true);//锯齿不显示
        mPaint.setStrokeWidth(3);
        mTextPaint = new Paint(Paint.DITHER_FLAG);
        mTextPaint.setAntiAlias(true);
        mTextPaint.setTextSize(textSize);
        mTextPaint.setColor(0xff2A3342);
        buttonPaint = new Paint(Paint.DITHER_FLAG);
        buttonPaint.setAntiAlias(true);
        TextView textView = new TextView(context);
        measurePain = textView.getPaint();
        //initData(null);
    }

    /**
     * 实例化后调用，设置bar的段数和文字
     */
    public void initData(ArrayList<String> section) {
        if (section != null) {
            section_title = section;
        } else {
            String[] str = new String[]{"低", "中", "高"};
            section_title = new ArrayList<String>();
            for (int i = 0; i < str.length; i++) {
                section_title.add(str[i]);
            }
        }
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);

        int widthMode = MeasureSpec.getMode(widthMeasureSpec);
        int widthSize = MeasureSpec.getSize(widthMeasureSpec);
        int heightMode = MeasureSpec.getMode(heightMeasureSpec);
        int heightSize = MeasureSpec.getSize(heightMeasureSpec);
        width = widthSize;
        //控件的高度
        //        height = 185;
        height = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 72, getResources().getDisplayMetrics());
        setMeasuredDimension(width, height);
        //当前宽度-点高度/2
//        width = width - bitMapHeight / 2;
        //(当前宽度-标题数*spot宽-点高度/2)/(标题数-1)=每段线段长度
        perWidth = (width - linePadding * 2) / (section_title.size() - 1);
        //每段线段长度一半
        hotarea = perWidth / 2;
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        mPaint.setColor(Color.WHITE);
        mPaint.setStyle(Paint.Style.FILL);
        mPaint.setAlpha(0);
        canvas.drawRect(0, 0, getMeasuredWidth(), getMeasuredHeight(), mPaint);
        //画出白色背景
        canvas.drawBitmap(bitmap, 0, 0, null);
        mPaint.setAlpha(255);
        mPaint.setColor(colors[1]);
        //画出灰色进度线
        canvas.drawLine(linePadding, height * 2 / 3 - textMove, width - linePadding, height * 2 / 3 - textMove, mPaint);
        int section = 0;
        while (section < section_title.size()) {
            float cx = linePadding + section * perWidth - circleRadius / 2;
            float cy = height * 2 / 3 - textMove;


            mPaint.setColor(colors[1]);
            canvas.drawCircle(cx, cy, circleRadius, mPaint);
            float textWidth = measurePain.measureText(section_title.get(section));
            float txtX = linePadding + section * perWidth - textWidth / 2;
            float txtY = height * 2 / 3 + textSize;
            canvas.drawText(section_title.get(section), txtX, txtY + 10, mTextPaint);
            section++;
        }
        float cx = linePadding + cur_sections * perWidth - circleRadius / 2;
        float cy = height * 2 / 3 - textMove;
        if (moveing) {
            if (cur_sections == 0 && moveX < cx || (cur_sections == section_title.size() - 1 && moveX > cx)) {
                moveX = (int) cx;
            }
            mPaint.setColor(colors[0]);
            canvas.drawCircle(moveX, cy, circleRadius * 4, mPaint);
            mPaint.setColor(Color.BLACK);
            canvas.drawCircle(moveX, cy, circleRadius, mPaint);
        } else {

            mPaint.setColor(colors[0]);
            canvas.drawCircle(cx, cy, circleRadius * 4, mPaint);


            mPaint.setColor(Color.BLACK);

            canvas.drawCircle(cx, cy, circleRadius, mPaint);
        }

    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        super.onTouchEvent(event);
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                moveing = true;
//                thumb = BitmapFactory.decodeResource(getResources(), R.drawable.set_button_0);
                moveX = (int) event.getX();
                moveY = (int) event.getY();
                responseTouch(moveX, moveY);
                break;
            case MotionEvent.ACTION_MOVE:
                moveing = true;
//                thumb = BitmapFactory.decodeResource(getResources(), R.drawable.set_button_0);
                moveX = (int) event.getX();
                moveY = (int) event.getY();
                responseTouch(moveX, moveY);
                break;
            case MotionEvent.ACTION_UP:
                moveing = false;
//                thumb = BitmapFactory.decodeResource(getResources(), R.drawable.set_button_0);
                upX = (int) event.getX();
                upY = (int) event.getY();
                responseTouch(upX, upY);
                if (responseOnTouch != null)
                    responseOnTouch.onTouchResponse(cur_sections);
                break;
        }
        return true;
    }

    private void responseTouch(int x, int y) {
        if (x <= width - bitMapHeight / 2) {
            cur_sections = (x + perWidth / 3) / perWidth;
        } else {
            cur_sections = section_title.size() - 1;
        }
        if (cur_sections > section_title.size() - 1)
            cur_sections = section_title.size() - 1;
        invalidate();
    }

    //设置监听
    public void setResponseOnTouch(ResponseOnTouch response) {
        responseOnTouch = response;
    }

    public String getSelectData(){
        return section_title.get(cur_sections);
    }
    //设置进度
    public void setProgress(int progress) {
        cur_sections = progress;
        invalidate();
    }

    public interface ResponseOnTouch {
        void onTouchResponse(int sections);
    }
}
