package com.eggplant.qiezisocial.widget.keyboard;

import android.app.Activity;
import android.content.Context;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.ViewPager;
import android.util.AttributeSet;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;

import com.eggplant.qiezisocial.R;
import com.mabeijianxi.jianxiexpression.BaseInsideFragment;
import com.mabeijianxi.jianxiexpression.ExpressionInerFragment;
import com.mabeijianxi.jianxiexpression.ExpressionRecentsFragment;
import com.mabeijianxi.jianxiexpression.core.ExpressionCache;
import com.mabeijianxi.jianxiexpression.core.ExpressionTransformEngine;
import com.mabeijianxi.jianxiexpression.widget.ExpressionEditText;
import com.mabeijianxi.jianxiexpression.widget.PagerSlidingTabStrip;

import java.util.ArrayList;

import sj.keyboard.utils.EmoticonsKeyboardUtils;
import sj.keyboard.widget.AutoHeightLayout;
import sj.keyboard.widget.FuncLayout;

/**
 * Created by Administrator on 2020/4/23.
 */

public class EmojiKeyBoard extends AutoHeightLayout implements FuncLayout.OnFuncChangeListener {
    protected LayoutInflater mInflater;
    protected FuncLayout mLyKvml;
    private ImageView emojiFace;
    private ExpressionEditText emojiContent;

    public EmojiKeyBoard(Context context, AttributeSet attrs) {
        super(context, attrs);
        mInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        inflateKeyboardBar();
        initView();
        initEmoticonFuncView();
    }


    private void inflateKeyboardBar() {
        mInflater.inflate(R.layout.layout_emoji_keyboard, this);
    }

    private void initView() {
        mLyKvml = findViewById(R.id.ly_kvml);
        emojiFace = findViewById(R.id.emoji_face);
        emojiFace.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                if (emojiContent==null){
                    throw new IllegalArgumentException("需要设置emojiContent  调用setEmojiContent(*)方法");
                }

                mLyKvml.toggleFuncView(FUNC_TYPE_EMOJI, isSoftKeyboardPop(), emojiContent);
            }
        });
        mLyKvml.setOnFuncChangeListener(this);
    }


    private ViewPager vp_expression;
    private PagerSlidingTabStrip psts_expression;
    private ExpressionRecentsFragment expressionRecentsFragment;
    protected int FUNC_TYPE_EMOJI = 6;

    private void initEmoticonFuncView() {
        View EmoticoView = mInflater.inflate(R.layout.expression_fragment, null);
        mLyKvml.addFuncView(FUNC_TYPE_EMOJI, EmoticoView);
        vp_expression = (ViewPager) EmoticoView.findViewById(com.mabeijianxi.jianxiexpression.R.id.vp_expression);
        psts_expression = (PagerSlidingTabStrip) EmoticoView.findViewById(com.mabeijianxi.jianxiexpression.R.id.psts_expression);
        ArrayList<BaseInsideFragment> expressionInerFragments = new ArrayList<>();

        expressionRecentsFragment = new ExpressionRecentsFragment();
//        最近使用
        expressionInerFragments.add(expressionRecentsFragment);
//        表情一
//        expressionInerFragments.add(ExpressionInerFragment.newInstance(new String[][]{ExpressionCache.page_1, ExpressionCache.page_2, ExpressionCache.page_3, ExpressionCache.page_4, ExpressionCache.page_5}));
        expressionInerFragments.add(ExpressionInerFragment.newInstance(new String[][]{ExpressionCache.page_6, ExpressionCache.page_7,ExpressionCache.page_8, ExpressionCache.page_9}));
        //   表情一
        expressionInerFragments.add(ExpressionInerFragment.newInstance(new String[][]{ExpressionCache.page_10, ExpressionCache.page_11, ExpressionCache.page_12}));
//      TODO 自己拓展
        vp_expression.setOffscreenPageLimit(2);
        vp_expression.setAdapter(new ExpressionShowApadater(((FragmentActivity) getContext()).getSupportFragmentManager(), expressionInerFragments));
        psts_expression.setViewPager(vp_expression);
        vp_expression.setCurrentItem(1, false);
    }

    private static class ExpressionShowApadater extends FragmentStatePagerAdapter {

        private final ArrayList<BaseInsideFragment> expressionInerFragments;

        public ExpressionShowApadater(FragmentManager fm, ArrayList<BaseInsideFragment> expressionInerFragments) {
            super(fm);
            this.expressionInerFragments = expressionInerFragments;
        }

        @Override
        public CharSequence getPageTitle(int position) {
            return ExpressionCache.getPageTitle()[position];
        }

        @Override
        public Fragment getItem(int position) {
            return expressionInerFragments.get(position);
        }

        @Override
        public int getCount() {
            return expressionInerFragments == null ? 0 : expressionInerFragments.size();
        }
    }

    public void input(EditText editText, String str) {
        ExpressionTransformEngine.input(editText, str);
    }

    public void delete(EditText editText) {
        ExpressionTransformEngine.delete(editText);
    }

    public void expressionaddRecent(String str) {
        if (expressionRecentsFragment != null) {
            expressionRecentsFragment.expressionaddRecent(str);
        }
    }


    @Override
    public void onSoftKeyboardHeightChanged(int height) {
        mLyKvml.updateHeight(height);
    }


    @Override
    public void OnSoftPop(int height) {
        super.OnSoftPop(height);
        Log.i("EmojiKeyboard"," onsoftpop  "+isSoftKeyboardPop()+" height"+height);
        mLyKvml.setVisibility(true);
        onFuncChange(mLyKvml.DEF_KEY);
    }

    @Override
    public void OnSoftClose() {
        super.OnSoftClose();
        if (mLyKvml.isOnlyShowSoftKeyboard()) {
            reset();
        } else {
            onFuncChange(mLyKvml.getCurrentFuncKey());
        }
    }

    public void reset() {
        EmoticonsKeyboardUtils.closeSoftKeyboard(this);
        mLyKvml.hideAllFuncView();
        emojiFace.setImageResource(R.mipmap.emoji_face);
        emojiFace.setVisibility(GONE);
    }
    public boolean isEmojiShow(){
        return emojiFace.getVisibility()==View.VISIBLE;
    }

    @Override
    public void onFuncChange(int key) {
        emojiFace.setVisibility(VISIBLE);
        if (FUNC_TYPE_EMOJI == key) {
            emojiFace.setImageResource(R.mipmap.emoji_keyboard);
        } else {
            emojiFace.setImageResource(R.mipmap.emoji_face);
        }
    }

    protected boolean mDispatchKeyEventPreImeLock = false;

    public void setEmojiContent(ExpressionEditText emojiContent) {
        this.emojiContent = emojiContent;
        emojiContent.setOnBackKeyClickListener(new ExpressionEditText.OnBackKeyClickListener() {
            @Override
            public void onBackKeyClick() {
                if (mLyKvml.isShown()) {
                    mDispatchKeyEventPreImeLock = true;
                    reset();
                }
            }
        });
    }

    @Override
    public boolean dispatchKeyEvent(KeyEvent event) {
        switch (event.getKeyCode()) {
            case KeyEvent.KEYCODE_BACK:
                if (mDispatchKeyEventPreImeLock) {
                    mDispatchKeyEventPreImeLock = false;
                    return true;
                }
                if (mLyKvml.isShown()) {
                    reset();
                    return true;
                } else {
                    return super.dispatchKeyEvent(event);
                }
        }
        return super.dispatchKeyEvent(event);
    }

    public boolean dispatchKeyEventInFullScreen(KeyEvent event) {
        if(event == null){
            return false;
        }
        switch (event.getKeyCode()) {
            case KeyEvent.KEYCODE_BACK:
                if (EmoticonsKeyboardUtils.isFullScreen((Activity) getContext()) && mLyKvml.isShown()) {
                    reset();
                    return true;
                }
            default:
                if(event.getAction() == KeyEvent.ACTION_DOWN&&emojiContent!=null){
                    boolean isFocused;
                    if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.LOLLIPOP) {
                        isFocused = emojiContent.getShowSoftInputOnFocus();
                    } else {
                        isFocused = emojiContent.isFocused();
                    }
                    if(isFocused){
                        emojiContent.onKeyDown(event.getKeyCode(), event);
                    }
                }
                return false;
        }
    }

}
