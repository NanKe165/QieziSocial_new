package com.eggplant.qiezisocial.widget.dialog;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.os.Bundle;
import android.view.Display;
import android.view.Gravity;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;

import com.eggplant.qiezisocial.R;


/**
 * Created by Administrator on 2017/12/20.
 */
public class BaseDialog extends AlertDialog implements View.OnClickListener{
    protected Context context;      // 上下文
    private int layoutResID;      // 布局文件id
    private int[] listenedItems;  // 要监听的控件id

    public BaseDialog(Context context, int layoutResID, int[] listenedItems) {
        super(context, R.style.dialog_custom);
        this.context = context;
        this.layoutResID = layoutResID;
        if(listenedItems == null){
            this.listenedItems = new int[]{};
        }else{
            this.listenedItems = listenedItems;
        }
    }
    public BaseDialog(Context context, int style,int layoutResID, int[] listenedItems){
        super(context,style);
        this.context = context;
        this.layoutResID = layoutResID;
        if(listenedItems == null){
            this.listenedItems = new int[]{};
        }else{
            this.listenedItems = listenedItems;
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Window window = getWindow();
        window.setGravity(Gravity.CENTER); // 此处可以设置dialog显示的位置为居中
        window.setWindowAnimations(R.style.bottom_menu_animation); // 添加动画效果
        setContentView(layoutResID);

        WindowManager windowManager = ((Activity) context).getWindowManager();
        Display display = windowManager.getDefaultDisplay();
        WindowManager.LayoutParams lp = getWindow().getAttributes();
        lp.width = display.getWidth()*4/5; // 设置dialog宽度为屏幕的4/5
        getWindow().setAttributes(lp);
        setCanceledOnTouchOutside(true);// 点击Dialog外部消失
        //遍历控件id,添加点击事件
        for (int id : listenedItems) {
            findViewById(id).setOnClickListener(this);
        }

    }
    public void setwidth(float scale){
        WindowManager windowManager = ((Activity) context).getWindowManager();
        Display display = windowManager.getDefaultDisplay();
        WindowManager.LayoutParams lp = getWindow().getAttributes();
        lp.width = (int) (display.getWidth()*scale);
        getWindow().setAttributes(lp);
    }

    private OnBaseDialogItemClickListener listener;
    public interface OnBaseDialogItemClickListener {
        void OnItemClick(BaseDialog dialog, View view);
    }
    public void setOnBaseDialogItemClickListener(OnBaseDialogItemClickListener listener) {
        this.listener = listener;
    }

    @Override
    public void onClick(View view) {
//        dismiss();//注意：我在这里加了这句话，表示只要按任何一个控件的id,弹窗都会消失，不管是确定还是取消。
        if(listener != null){
            listener.OnItemClick(this, view);
        }
    }


}
