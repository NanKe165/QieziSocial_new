package com.eggplant.qiezisocial.widget.topbar;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.drawable.Drawable;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.content.ContextCompat;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

import com.eggplant.qiezisocial.R;

/**
 * Created by Administrator on 2019/1/14.
 */

public class Topbar extends FrameLayout {
    private TopBarListener tbListener;
    private FrameLayout tbGp;
    private ImageView leftReturn;
    private ImageView rightReturn;
    private TextView tbTitle;
    private TextView rightTv;
    private String rightTxt;
    private View lineView;


    public Topbar(@NonNull Context context) {
        this(context, null);
    }

    public Topbar(@NonNull Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public Topbar(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        TypedArray typedArray = context.obtainStyledAttributes(attrs, R.styleable.Topbar);
        boolean isrightReturn = typedArray.getBoolean(R.styleable.Topbar_returnToright, false);
        Drawable drawable = typedArray.getDrawable(R.styleable.Topbar_returnDrawable);
        String title = typedArray.getString(R.styleable.Topbar_tbTitle);
        float titleSize = typedArray.getDimensionPixelSize(R.styleable.Topbar_tbTitleSize, 0);
        rightTxt = typedArray.getString(R.styleable.Topbar_tbRightTxt);
        Drawable txtBg = typedArray.getDrawable(R.styleable.Topbar_rightTxtBg);
        boolean showLine = typedArray.getBoolean(R.styleable.Topbar_showLine, false);
        int rightTxtColor = typedArray.getColor(R.styleable.Topbar_rightTxtColor, ContextCompat.getColor(context, R.color.tv_topbar));
        int titleColor = typedArray.getColor(R.styleable.Topbar_tbTitleColor, ContextCompat.getColor(context, R.color.tv_topbar));
        int bgColor = typedArray.getColor(R.styleable.Topbar_bgColor, ContextCompat.getColor(context, R.color.topbar));
        int rightPadding = typedArray.getDimensionPixelOffset(R.styleable.Topbar_rightDrawablePadding, 10);
        typedArray.recycle();


        LayoutInflater.from(context).inflate(R.layout.top_bar, this, true);
        tbGp = findViewById(R.id.tb_gp);
        leftReturn = findViewById(R.id.tb_left_return);
        rightReturn = findViewById(R.id.tb_right_return);
        tbTitle = findViewById(R.id.tb_title);

        rightTv = findViewById(R.id.tb_right_txt);
        lineView = findViewById(R.id.tb_line);




        showReturn(isrightReturn);
        setRightTxt(rightTxt);
        setRightTxtBg(txtBg);
        setRightTxtColor(rightTxtColor);
        setLine(showLine);
        setReturnDrawable(drawable);
        setTbBackground(bgColor);
        setTitle(title, titleColor,titleSize);
        setRightDrawablePadding(rightPadding);
    }

    /**
     * 显示return按钮
     *
     * @param isrightReturn 是否显示在右方
     */
    public void showReturn(boolean isrightReturn) {
        if (isrightReturn && TextUtils.isEmpty(rightTxt)) {
            leftReturn.setVisibility(GONE);
            rightReturn.setVisibility(VISIBLE);
            rightReturn.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (tbListener != null)
                        tbListener.onReturn();
                }
            });
        } else {
            leftReturn.setVisibility(VISIBLE);
            rightReturn.setVisibility(GONE);
            leftReturn.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (tbListener != null)
                        tbListener.onReturn();
                }
            });
        }

    }

    public void setRightTxt(String rightTxt) {
        if (!TextUtils.isEmpty(rightTxt)) {
            rightTv.setVisibility(VISIBLE);
            leftReturn.setVisibility(VISIBLE);
            rightReturn.setVisibility(GONE);
            rightTv.setText(rightTxt);
            rightTv.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (tbListener != null)
                        tbListener.onTxtClick();
                }
            });
        } else {
            rightTv.setVisibility(GONE);
        }
    }
    public void  setRightDrawable(int res){
        if (res!=0){
            rightTv.setVisibility(VISIBLE);
            leftReturn.setVisibility(VISIBLE);
            rightReturn.setVisibility(GONE);
            rightTv.setCompoundDrawablesRelativeWithIntrinsicBounds(res,0,0,0);
            rightTv.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (tbListener != null)
                        tbListener.onTxtClick();
                }
            });
        }else {
            rightTv.setVisibility(GONE);
        }
    }

    public void setRightTetWithStyle(String rightTxt, int color, int drawable) {
        setRightTxt(rightTxt);
        if (color != 0)
            rightTv.setTextColor(ContextCompat.getColor(getContext(), color));
        if (drawable != 0)
            rightTv.setBackground(ContextCompat.getDrawable(getContext(), drawable));
    }

    /**
     * 设置返回按钮
     *
     * @param drawable 资源文件
     */

    public void setReturnDrawable(Drawable drawable) {
        if (drawable != null) {
            rightReturn.setImageDrawable(drawable);
            leftReturn.setImageDrawable(drawable);
        }
    }

    /**
     * 设置topbar背景色
     *
     * @param bgColor
     */
    public void setTbBackground(int bgColor) {
        tbGp.setBackgroundColor(bgColor);
    }

    public void setTbAlpha(float alpha) {
        tbGp.setAlpha(alpha);
    }

    /**
     * 设置 标题内容和字体颜色
     *
     * @param title
     * @param titleColor
     */
    public void setTitle(String title, int titleColor,float titleSize) {
        if (!TextUtils.isEmpty(title))
            tbTitle.setText(title);

        tbTitle.setTextColor(titleColor);
        if (titleSize!=0) {
            tbTitle.setTextSize(TypedValue.COMPLEX_UNIT_PX, titleSize);
        }
    }

    public void setTitle(String title) {
        if (!TextUtils.isEmpty(title))
            tbTitle.setText(title);
        else
            tbTitle.setText("");
    }

    public void setTbListener(TopBarListener tbListener) {
        this.tbListener = tbListener;
    }

    public void setRightDrawablePadding(int rightDrawablePadding) {
        rightReturn.setPadding(rightDrawablePadding, rightDrawablePadding, rightDrawablePadding, rightDrawablePadding);
    }

    public void setRightTxtBg(Drawable txtBg) {
        if (txtBg != null)
            rightTv.setBackground(txtBg);
    }

    public void setRightTxtColor(int rightTxtColor) {
        if (rightTxtColor != 0)
            rightTv.setTextColor(rightTxtColor);
    }

    public void setLine(boolean showLine) {
        if (showLine){
            lineView.setVisibility(VISIBLE);
        }else {
            lineView.setVisibility(GONE);
        }
    }


}
