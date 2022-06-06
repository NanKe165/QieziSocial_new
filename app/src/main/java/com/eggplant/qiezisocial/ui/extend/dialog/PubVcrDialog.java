package com.eggplant.qiezisocial.ui.extend.dialog;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.view.Display;
import android.view.WindowManager;

import com.eggplant.qiezisocial.R;
import com.eggplant.qiezisocial.widget.dialog.BaseDialog;

/**
 * Created by Administrator on 2020/7/15.
 */

public class PubVcrDialog extends BaseDialog {
    public PubVcrDialog(Context context, int[] listenedItems) {
        super(context, R.layout.dialog_pub_vcr, listenedItems);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        WindowManager windowManager = ((Activity) context).getWindowManager();
        Display display = windowManager.getDefaultDisplay();
        WindowManager.LayoutParams lp = getWindow().getAttributes();
        lp.width = display.getWidth();
        getWindow().setAttributes(lp);
        setCanceledOnTouchOutside(true);
    }
}
