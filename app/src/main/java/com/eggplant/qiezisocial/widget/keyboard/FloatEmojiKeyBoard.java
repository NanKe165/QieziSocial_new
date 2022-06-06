package com.eggplant.qiezisocial.widget.keyboard;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.ViewPager;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.AttributeSet;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

import com.eggplant.qiezisocial.R;
import com.eggplant.qiezisocial.utils.mp3.Mp3RecorderUtils;
import com.eggplant.qiezisocial.utils.mp3.RecorderListener;
import com.mabeijianxi.jianxiexpression.BaseInsideFragment;
import com.mabeijianxi.jianxiexpression.ExpressionInerFragment;
import com.mabeijianxi.jianxiexpression.ExpressionRecentsFragment;
import com.mabeijianxi.jianxiexpression.core.ExpressionCache;
import com.mabeijianxi.jianxiexpression.core.ExpressionTransformEngine;
import com.mabeijianxi.jianxiexpression.widget.ExpressionEditText;
import com.mabeijianxi.jianxiexpression.widget.PagerSlidingTabStrip;
import com.tbruyelle.rxpermissions2.RxPermissions;

import java.util.ArrayList;

import io.reactivex.functions.Consumer;
import sj.keyboard.utils.EmoticonsKeyboardUtils;
import sj.keyboard.widget.AutoHeightLayout;
import sj.keyboard.widget.FuncLayout;

/**
 * Created by Administrator on 2019/1/7.
 */

public class FloatEmojiKeyBoard extends AutoHeightLayout implements FuncLayout.OnFuncChangeListener, View.OnClickListener, ExpressionEditText.OnBackKeyClickListener {
    protected LayoutInflater mInflater;
    protected FuncLayout mLyKvml;
    private ImageView emojiAudio;
    private ExpressionEditText emojiContent;
    private ImageView emojiFace;
    private FrameLayout emojiAdd;
    private TextView emojiSend;

    public FloatEmojiKeyBoard(Context context, AttributeSet attrs) {
        super(context, attrs);
        mInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        inflateKeyboardBar();
        initView();
        initFuncView();
    }


    private void inflateKeyboardBar() {
        mInflater.inflate(R.layout.layout_float_emoji_panel, this);
    }

    private void initView() {
        mLyKvml = ((FuncLayout) findViewById(R.id.ly_kvml));
        emojiAudio = (ImageView) findViewById(R.id.emoji_audio);
        emojiContent = (ExpressionEditText) findViewById(R.id.emoji_content);
        emojiFace = (ImageView) findViewById(R.id.emoji_face);
//        emojiAdd = (ImageView) findViewById(R.id.emoji_add);
        emojiAdd = (FrameLayout) findViewById(R.id.emoji_add_gp);
        emojiSend = (TextView) findViewById(R.id.emoji_send);

        emojiAdd.setOnClickListener(this);
        emojiAudio.setOnClickListener(this);
        emojiFace.setOnClickListener(this);
        emojiSend.setOnClickListener(this);
        emojiContent.setOnBackKeyClickListener(this);
        mLyKvml.setOnFuncChangeListener(this);
    }

    private void initFuncView() {
        initAudioView();
        initEmoticonFuncView();
        initEditView();
    }

    protected int FUNC_TYPE_EMOJI = 6;
    protected int FUNC_TYPE_AUDIO = 8;
    private boolean isCancel = false;
    private void initAudioView() {
        View audioView = mInflater.inflate(R.layout.layout_audio_record, null);
        mLyKvml.addFuncView(FUNC_TYPE_AUDIO, audioView);
        ImageView audioImg = (ImageView) audioView.findViewById(R.id.audio_ps_gp);
        final TextView audio_hint = (TextView) audioView.findViewById(R.id.audio_txt);
        audioImg.setOnTouchListener(new OnTouchListener() {

            private float lastY;
            private float lastX;
            private float startY;
            private float startX;

            @Override
            public boolean onTouch(final View v, MotionEvent event) {
                switch (event.getAction()) {
                    case MotionEvent.ACTION_DOWN:
                        startX = event.getRawX();
                        startY = event.getRawY();
                        new RxPermissions((FragmentActivity) getContext())
                                .request(new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.RECORD_AUDIO})
                                .subscribe(new Consumer<Boolean>() {
                                    @Override
                                    public void accept(Boolean aBoolean) throws Exception {
                                        if (aBoolean) {
                                            Mp3RecorderUtils.startRecording(getContext(),new Mp3RecorderUtils.RecorderCallback() {
                                                @Override
                                                public void onStart() {
                                                    isCancel = false;
                                                    audio_hint.setText("上滑取消");
                                                    if (listener != null) {
                                                        listener.onRecorderStart();
                                                    }
                                                }

                                                @Override
                                                public void onRecording(double volume) {
                                                    if (listener != null) {
                                                        listener.onRecording(volume);
                                                    }
//                                                    TipsUtil.showToast(mContext, volume + "");
                                                }

                                                @Override
                                                public void onStop(final String filePath, double duration) {
                                                    if (isCancel) {
                                                        return;
                                                    }
                                                    if (listener != null) {
                                                        listener.onStop(filePath, duration);
                                                    }
                                                }

                                                @Override
                                                public void onReset() {

                                                }
                                            });
                                        }
                                    }
                                });
                        break;
                    case MotionEvent.ACTION_UP:
                        if (listener != null) {
                            listener.onHideVoiceGp();
                        }
                        audio_hint.setText("按着说话");
                        float deltaX = startX - lastX;
                        float deltaY = startY - lastY;
                        if (deltaY > deltaX && deltaY > 100) {
                            isCancel = true;
                            Mp3RecorderUtils.cancleRecording();
                        } else {
                            Mp3RecorderUtils.stopRecording();
                        }


                        break;
                    case MotionEvent.ACTION_MOVE:
                        lastX = event.getRawX();
                        lastY = event.getRawY();
                        float deltaX1 = startX - lastX;
                        float deltaY1 = startY - lastY;
                        if (deltaY1 > deltaX1 && deltaY1 > 100) {
                            if (listener!=null)
                            listener.onShoCancle();
                        } else {
                            if (listener!=null)
                            listener.onShowVoice();
                        }

                        break;
                }
                return true;
            }
        });
    }

    RecorderListener listener;
    public void setRecorderListener(RecorderListener listener){
        this.listener=listener;
    }


    private ViewPager vp_expression;
    private PagerSlidingTabStrip psts_expression;
    private ExpressionRecentsFragment expressionRecentsFragment;

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

    public  void input(EditText editText, String str) {
        ExpressionTransformEngine.input(editText, str);
    }

    public  void delete(EditText editText) {
        ExpressionTransformEngine.delete(editText);
    }

    public  void expressionaddRecent(String str){
        if (expressionRecentsFragment != null) {
            expressionRecentsFragment.expressionaddRecent(str);
        }
    }

    protected void initEditView() {
        emojiContent.setOnTouchListener(new OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if (!emojiContent.isFocused()) {
                    emojiContent.setFocusable(true);
                    emojiContent.setFocusableInTouchMode(true);
                }
                return false;
            }
        });

        emojiContent.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
            }

            @Override
            public void afterTextChanged(Editable s) {
                if (!TextUtils.isEmpty(s)) {
                    emojiSend.setVisibility(VISIBLE);
                    emojiAdd.setVisibility(GONE);
//                    mBtnSend.setBackgroundResource(com.keyboard.view.R.drawable.btn_send_bg);
                } else {
                    emojiAdd.setVisibility(VISIBLE);
                    emojiSend.setVisibility(GONE);
                }
            }
        });
    }


    @Override
    public void onSoftKeyboardHeightChanged(int height) {
        mLyKvml.updateHeight(height);
    }

    @Override
    public void OnSoftPop(int height) {
        super.OnSoftPop(height);
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


    protected boolean mDispatchKeyEventPreImeLock = false;
    @Override
    public void onBackKeyClick() {
        if (mLyKvml.isShown()) {
            mDispatchKeyEventPreImeLock = true;
            reset();
        }
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
                if(event.getAction() == KeyEvent.ACTION_DOWN){
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

    public void reset() {
        EmoticonsKeyboardUtils.closeSoftKeyboard(this);
        mLyKvml.hideAllFuncView();
        emojiFace.setImageResource(R.mipmap.emoji_face);
        emojiAudio.setImageResource(R.mipmap.emoji_audio);
    }


    @Override
    public void onFuncChange(int key) {
        if (FUNC_TYPE_EMOJI == key) {
            emojiFace.setImageResource(R.mipmap.emoji_keyboard);
            emojiAudio.setImageResource(R.mipmap.emoji_audio);
        } else if (FUNC_TYPE_AUDIO == key){
            emojiAudio.setImageResource(R.mipmap.emoji_keyboard);
            emojiFace.setImageResource(R.mipmap.emoji_face);
        }else {
            emojiFace.setImageResource(R.mipmap.emoji_face);
            emojiAudio.setImageResource(R.mipmap.emoji_audio);
        }
    }


    @Override
    public void onClick(View v) {
        int i = v.getId();
        if (i == R.id.emoji_add_gp) {
            if (funcClickListener!=null)
                funcClickListener.onAddClick(v);
        } else if (i== R.id.emoji_send){
            if (funcClickListener!=null){
                String str = emojiContent.getText().toString();
                funcClickListener.onSendClick(str);
                emojiContent.setText("");
            }
        }else if (i == R.id.emoji_audio) {
            toggleFuncView(FUNC_TYPE_AUDIO);
        } else if (i == R.id.emoji_face) {
            toggleFuncView(FUNC_TYPE_EMOJI);
        }
    }
    OnFuncClickListener funcClickListener;
    public void setOnFuncClickListener(OnFuncClickListener funcClickListener){
        this.funcClickListener=funcClickListener;
    }
    public interface OnFuncClickListener{
        void onAddClick(View v);
        void onSendClick(String str);
    }

    protected void toggleFuncView(int key) {
        mLyKvml.toggleFuncView(key, isSoftKeyboardPop(), emojiContent);
    }


    public EditText getEdit() {
        return emojiContent;
    }


}
