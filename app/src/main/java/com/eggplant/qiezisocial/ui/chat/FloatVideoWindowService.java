package com.eggplant.qiezisocial.ui.chat;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.opengl.GLSurfaceView;
import android.os.Binder;
import android.os.Build;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.widget.RelativeLayout;

import com.eggplant.qiezisocial.R;

/**
 * Created by Administrator on 2020/6/15.
 */

public class FloatVideoWindowService extends Service {

    private WindowManager mWindowManager;
    private WindowManager.LayoutParams wmParams;
    private LayoutInflater inflater;
    //浮动布局view
    private View mFloatingLayout;
    private RelativeLayout videoGp;

    @Override
    public void onCreate() {
        super.onCreate();
        initWindow();//设置悬浮窗基本参数（位置、宽高等）
    }


    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        initFloating();//悬浮框点击事件的处理
        return new MyBinder();
    }

    public class MyBinder extends Binder {
        public FloatVideoWindowService getService() {
            return FloatVideoWindowService.this;
        }
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        return super.onStartCommand(intent, flags, startId);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (mFloatingLayout != null) {
            mWindowManager.removeView(mFloatingLayout);
            mFloatingLayout = null;
            ChatConst.isShowFloatWindow=false;
        }
    }

    private void initWindow() {
        mWindowManager = (WindowManager) getApplicationContext().getSystemService(Context.WINDOW_SERVICE);
        wmParams = getParams();
        //得到容器，通过这个inflater来获得悬浮窗控件
        inflater = LayoutInflater.from(getApplicationContext());
        // 获取浮动窗口视图所在布局
        mFloatingLayout = inflater.inflate(R.layout.layout_float_video_layout, null);
        // 添加悬浮窗的视图
        mWindowManager.addView(mFloatingLayout, wmParams);
    }

    private WindowManager.LayoutParams getParams() {
        wmParams = new WindowManager.LayoutParams();
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            // android 8.0及以后使用
            wmParams.type = WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY;
        } else {
            // android 8.0以前使用
            wmParams.type = WindowManager.LayoutParams.TYPE_PHONE;
        }
        wmParams.gravity = Gravity.LEFT | Gravity.TOP;
        //该flags描述的是窗口的模式，是否可以触摸，可以聚焦等
        wmParams.flags = WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE | WindowManager.LayoutParams.FLAG_NOT_TOUCH_MODAL |
                WindowManager.LayoutParams.FLAG_LAYOUT_IN_SCREEN | WindowManager.LayoutParams.FLAG_LAYOUT_INSET_DECOR |
                WindowManager.LayoutParams.FLAG_WATCH_OUTSIDE_TOUCH;

        // 设置视频的播放窗口大小
        //设置悬浮窗口长宽数据
        wmParams.width = WindowManager.LayoutParams.WRAP_CONTENT;
        wmParams.height = WindowManager.LayoutParams.WRAP_CONTENT;
        wmParams.x = 70;
        wmParams.y = 210;

        return wmParams;
    }

    private void initFloating() {
        videoGp = mFloatingLayout.findViewById(R.id.float_videoView);
        GLSurfaceView mLocalsurface=  mFloatingLayout.findViewById(R.id.float_glsurface);
        GLSurfaceView mGLSurfaceView = ChatConst.mVideoViewLayout;
//        Surface surface = mGLSurfaceView.getHolder().getSurface();
//        mLocalsurface.surfaceCreated(mGLSurfaceView.getHolder());

//        if (mGLSurfaceView != null && mGLSurfaceView.getParent() != null) {
//            ((ViewGroup) mGLSurfaceView.getParent()).removeView(mGLSurfaceView);
//            videoGp.addView(mGLSurfaceView);
//        }

        ChatConst.isShowFloatWindow=true;
        //悬浮框触摸事件，设置悬浮框可拖动
        videoGp.setOnTouchListener(new FloatingListener());
        //悬浮框点击事件
        videoGp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //在这里实现点击重新回到Activity
                Intent intent = new Intent(FloatVideoWindowService.this, ChatAVActivity.class);
                startActivity(intent);
            }
        });

    }


    //开始触控的坐标，移动时的坐标（相对于屏幕左上角的坐标）
    private int mTouchStartX, mTouchStartY, mTouchCurrentX, mTouchCurrentY;
    //开始时的坐标和结束时的坐标（相对于自身控件的坐标）
    private int mStartX, mStartY, mStopX, mStopY;
    //判断悬浮窗口是否移动，这里做个标记，防止移动后松手触发了点击事件
    private boolean isMove;

    private class FloatingListener implements View.OnTouchListener {

        @Override
        public boolean onTouch(View v, MotionEvent event) {
            int action = event.getAction();
            switch (action) {
                case MotionEvent.ACTION_DOWN:
                    isMove = false;
                    mTouchStartX = (int) event.getRawX();
                    mTouchStartY = (int) event.getRawY();
                    mStartX = (int) event.getX();
                    mStartY = (int) event.getY();
                    break;
                case MotionEvent.ACTION_MOVE:
                    mTouchCurrentX = (int) event.getRawX();
                    mTouchCurrentY = (int) event.getRawY();
                    wmParams.x += mTouchCurrentX - mTouchStartX;
                    wmParams.y += mTouchCurrentY - mTouchStartY;
                    mWindowManager.updateViewLayout(mFloatingLayout, wmParams);

                    mTouchStartX = mTouchCurrentX;
                    mTouchStartY = mTouchCurrentY;
                    break;
                case MotionEvent.ACTION_UP:
                    mStopX = (int) event.getX();
                    mStopY = (int) event.getY();
                    if (Math.abs(mStartX - mStopX) >= 1 || Math.abs(mStartY - mStopY) >= 1) {
                        isMove = true;
                    }
                    break;
                default:
                    break;
            }
            //如果是移动事件不触发OnClick事件，防止移动的时候一放手形成点击事件
            return isMove;
        }
    }


}
