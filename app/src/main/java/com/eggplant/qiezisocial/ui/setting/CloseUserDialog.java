package com.eggplant.qiezisocial.ui.setting;

import android.content.Context;

import com.eggplant.qiezisocial.R;
import com.eggplant.qiezisocial.widget.dialog.BaseDialog;

/**
 * Created by Administrator on 2021/10/29.
 */

public class CloseUserDialog extends BaseDialog {
    public CloseUserDialog(Context context, int[] listenedItems) {
        super(context, R.layout.dlg_close_user, listenedItems);
    }
}
