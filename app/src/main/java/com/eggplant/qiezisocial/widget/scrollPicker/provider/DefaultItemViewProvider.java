package com.eggplant.qiezisocial.widget.scrollPicker.provider;

import android.graphics.Color;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.View;
import android.widget.TextView;

import com.eggplant.qiezisocial.R;
import com.eggplant.qiezisocial.widget.scrollPicker.IViewProvider;


public class DefaultItemViewProvider implements IViewProvider<String> {
    @Override
    public int resLayout() {
        return R.layout.scroll_picker_default_item_layout;
    }

    @Override
    public void onBindView(@NonNull View view, @Nullable String text) {
        TextView tv = view.findViewById(R.id.tv_content);
        tv.setText(text);
        view.setTag(text);
        tv.setTextSize(18);
    }

    @Override
    public void updateView(@NonNull View itemView, boolean isSelected) {
        TextView tv = itemView.findViewById(R.id.tv_content);
        //根据数据量判别是否开启，因刷新频率太快 数据太多卡顿
//        tv.setTextSize(isSelected ? 18 : 14);
        tv.setTextColor(Color.parseColor(isSelected ? "#000000" : "#999999"));
    }
}
