package com.eggplant.qiezisocial.widget.dialog;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.view.Display;
import android.view.Gravity;
import android.view.Window;
import android.view.WindowManager;

/**
 * Created by Administrator on 2018/11/13.
 */

public class QzProgressDialog extends android.app.ProgressDialog {
    private Context context;
    public QzProgressDialog(Context context) {
        super(context);
        this.context=context;
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Window window = getWindow();
        window.setGravity(Gravity.CENTER); // 此处可以设置dialog显示的位置为居中

        WindowManager windowManager = ((Activity) context).getWindowManager();
        Display display = windowManager.getDefaultDisplay();
        WindowManager.LayoutParams lp = getWindow().getAttributes();
        lp.width = display.getWidth()*4/5; // 设置dialog宽度为屏幕的4/5
        getWindow().setAttributes(lp);
        setCanceledOnTouchOutside(false);// 点击Dialog外部消失

    }
    public void setwidth(){
        WindowManager windowManager = ((Activity) context).getWindowManager();
        Display display = windowManager.getDefaultDisplay();
        WindowManager.LayoutParams lp = getWindow().getAttributes();
        lp.width = display.getWidth()*11/12; // 设置dialog宽度为屏幕的4/5
        getWindow().setAttributes(lp);
    }
}
