package com.eggplant.qiezisocial.ui.extend.dialog;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.view.Display;
import android.view.Gravity;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;

import com.eggplant.qiezisocial.R;
import com.eggplant.qiezisocial.widget.dialog.BaseDialog;

/**
 * Created by Administrator on 2020/7/16.
 */

public class ShareDialog extends BaseDialog {
    public ShareDialog(Context context,  int[] listenedItems) {
        super(context, R.layout.dialog_share, listenedItems);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Window window = getWindow();
        window.setGravity(Gravity.BOTTOM); // 此处可以设置dialog显示的位置为居中
        window.setWindowAnimations(R.style.bottom_menu_animation); // 添加动画效果


        WindowManager windowManager = ((Activity) context).getWindowManager();
        Display display = windowManager.getDefaultDisplay();
        WindowManager.LayoutParams lp = getWindow().getAttributes();
        lp.width = display.getWidth(); // 设置dialog宽度为屏幕的4/5
        getWindow().setAttributes(lp);
        setCanceledOnTouchOutside(true);// 点击Dialog外部消失
    }

    @Override
    public void onClick(View view) {
        super.onClick(view);
    }
}
