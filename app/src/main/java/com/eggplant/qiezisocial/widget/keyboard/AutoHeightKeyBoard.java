package com.eggplant.qiezisocial.widget.keyboard;

import android.content.Context;
import android.util.AttributeSet;
import android.view.KeyEvent;
import android.view.LayoutInflater;

import com.eggplant.qiezisocial.R;

import sj.keyboard.utils.EmoticonsKeyboardUtils;
import sj.keyboard.widget.AutoHeightLayout;
import sj.keyboard.widget.FuncLayout;

/**
 * Created by Administrator on 2020/6/30.
 */

public class AutoHeightKeyBoard extends AutoHeightLayout {
    protected LayoutInflater mInflater;
    protected FuncLayout mLyKvml;

    public AutoHeightKeyBoard(Context context, AttributeSet attrs) {
        super(context, attrs);
        mInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        inflateKeyboardBar();
        initView();
    }

    private void inflateKeyboardBar() {
        mInflater.inflate(R.layout.layout_autoheight_keyboard, this);
    }

    private void initView() {
        mLyKvml = findViewById(R.id.ly_kvml);
    }

    @Override
    public void onSoftKeyboardHeightChanged(int height) {
        mLyKvml.updateHeight(height);
    }

    @Override
    public void OnSoftPop(int height) {
        super.OnSoftPop(height);
        mLyKvml.setVisibility(true);
    }

    @Override
    public void OnSoftClose() {
        super.OnSoftClose();
        reset();
    }

    public void reset() {
        EmoticonsKeyboardUtils.closeSoftKeyboard(this);
        mLyKvml.hideAllFuncView();
    }


    @Override
    public boolean dispatchKeyEvent(KeyEvent event) {
        switch (event.getKeyCode()) {
            case KeyEvent.KEYCODE_BACK:
                if (mLyKvml.isShown()) {
                    reset();
                    return true;
                } else {
                    return super.dispatchKeyEvent(event);
                }
        }
        return super.dispatchKeyEvent(event);
    }

}
