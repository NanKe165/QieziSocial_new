package com.eggplant.qiezisocial.widget.scrollPicker.provider;

import android.content.Context;
import android.graphics.Color;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.content.ContextCompat;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.eggplant.qiezisocial.R;
import com.eggplant.qiezisocial.widget.scrollPicker.IViewProvider;


public class DefaultItemViewProvider2 implements IViewProvider<String> {
    private Context mContext;
    private int selectColor;

    public void setSelectBg(int selectBg) {
        this.selectColor = selectBg;
    }

    public DefaultItemViewProvider2(Context mContext) {
        this.mContext = mContext;
        selectColor = ContextCompat.getColor(mContext, R.color.tv_gray2);
    }

    @Override
    public int resLayout() {
        return R.layout.scroll_picker_default_item_layout;
    }

    @Override
    public void onBindView(@NonNull View view, @Nullable String text) {

        TextView tv = view.findViewById(R.id.tv_content);
        tv.setPadding(10, 8, 10, 8);
        tv.setText(text);
        view.setTag(text);
        tv.setTextSize(15);

    }

    @Override
    public void updateView(@NonNull View itemView, boolean isSelected) {
        TextView tv = itemView.findViewById(R.id.tv_content);
        LinearLayout tvGp = itemView.findViewById(R.id.tv_gp);
        //根据数据量判别是否开启，因刷新频率太快 数据太多卡顿
//        tv.setTextSize(isSelected ? 18 : 14);
//        tvGp.setBackground(isSelected ? selectBg : null);
        tv.setTextColor(isSelected ? selectColor : Color.parseColor("#999999"));
    }


}
