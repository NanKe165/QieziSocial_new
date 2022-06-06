package com.eggplant.qiezisocial.widget.ninegridImage.preview;

import android.animation.Animator;
import android.animation.ValueAnimator;
import android.annotation.SuppressLint;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.RequiresApi;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.text.Editable;
import android.text.InputFilter;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.ViewTreeObserver;
import android.view.inputmethod.EditorInfo;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.eggplant.qiezisocial.R;
import com.eggplant.qiezisocial.entry.BaseEntry;
import com.eggplant.qiezisocial.entry.UserEntry;
import com.eggplant.qiezisocial.model.API;
import com.eggplant.qiezisocial.model.callback.JsonCallback;
import com.eggplant.qiezisocial.utils.DateUtils;
import com.eggplant.qiezisocial.utils.ScreenUtil;
import com.eggplant.qiezisocial.widget.QzEdittext;
import com.eggplant.qiezisocial.widget.QzTextView;
import com.eggplant.qiezisocial.widget.image.CircleImageView;
import com.eggplant.qiezisocial.widget.keyboard.EmojiKeyBoard;
import com.eggplant.qiezisocial.widget.ninegridImage.ImageInfo;
import com.lzy.okgo.OkGo;
import com.lzy.okgo.model.Response;
import com.mabeijianxi.jianxiexpression.ExpressionGridFragment;

import java.util.List;

public class ImagePreviewActivity extends AppCompatActivity implements ViewTreeObserver.OnPreDrawListener, ExpressionGridFragment.ExpressionClickListener, ExpressionGridFragment.ExpressionDeleteClickListener, ExpressionGridFragment.ExpressionaddRecentListener {

    public static final String IMAGE_INFO = "IMAGE_INFO";
    public static final String CURRENT_ITEM = "CURRENT_ITEM";
    public static final int ANIMATE_DURATION = 300;

    private FrameLayout rootView;

    private ImagePreviewAdapter imagePreviewAdapter;
    private List<ImageInfo> imageInfo;
    private int currentItem;
    private int imageHeight;
    private int imageWidth;
    private int screenWidth;
    private int screenHeight;
    private QzEdittext edittext;
    private EmojiKeyBoard keyBoard;
    private boolean showEdit = false;
    private long uid;


    private TextWatcher mTextWatcher = new TextWatcher()

    {

        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {

        }

        @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {
            if (before >= 7) {
                if (edittext.getFilters() != null && edittext.getFilters()[0] instanceof InputFilter.LengthFilter) {
                    int max = ((InputFilter.LengthFilter) edittext.getFilters()[0]).getMax();
                    max -= before - 1;
                    edittext.setFilters(new InputFilter[]{new InputFilter.LengthFilter(max)});
                }
            }
        }

        @Override
        public void afterTextChanged(Editable s) {

        }
    };
    private FrameLayout bottomView;
    private CircleImageView userHead;
    private QzTextView userNick;
    private QzTextView userInfo;

    @SuppressLint("StringFormatMatches")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_preview);

        ScreenUtil.hideStatusBar(this);
        ViewPager viewPager = (ViewPager) findViewById(R.id.viewPager);
        final TextView tv_pager = (TextView) findViewById(R.id.tv_pager);
        rootView = (FrameLayout) findViewById(R.id.rootView);
        edittext = findViewById(R.id.preview_edit);
        keyBoard = findViewById(R.id.preview_keyboard);
        bottomView = findViewById(R.id.preview_bottom);
        userHead = findViewById(R.id.userinfor_head);
        userNick = findViewById(R.id.userinfor_name);
        userInfo = findViewById(R.id.user_info);
        keyBoard.setEmojiContent(edittext);


        DisplayMetrics metric = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(metric);
        screenWidth = metric.widthPixels;
        screenHeight = metric.heightPixels;

        final Intent intent = getIntent();
        showEdit = intent.getBooleanExtra("showEdit", false);
        uid = intent.getLongExtra("uid", 0);

        imageInfo = (List<ImageInfo>) intent.getSerializableExtra(IMAGE_INFO);
        if (imageInfo != null && imageInfo.size() == 1) {
            tv_pager.setVisibility(View.GONE);
            if (showEdit) {
                getUserInfo(uid);
                bottomView.setVisibility(View.VISIBLE);
            }
        }
        currentItem = intent.getIntExtra(CURRENT_ITEM, 0);
        imagePreviewAdapter = new ImagePreviewAdapter(this, imageInfo);
        viewPager.setAdapter(imagePreviewAdapter);
        viewPager.setCurrentItem(currentItem);
        viewPager.getViewTreeObserver().addOnPreDrawListener(this);
        viewPager.addOnPageChangeListener(new ViewPager.SimpleOnPageChangeListener() {
            @Override
            public void onPageSelected(int position) {
                currentItem = position;
                tv_pager.setText(String.format(getString(R.string.select), currentItem + 1, imageInfo.size()));
            }
        });
        tv_pager.setText(String.format(getString(R.string.select), currentItem + 1, imageInfo.size()));
        edittext.addTextChangedListener(mTextWatcher);
        edittext.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_SEND) {
                    intent.putExtra("txt", edittext.getText().toString());
                    intent.putExtra("uid", uid);
                    intent.putExtra("img",imageInfo.get(0).getBigImageUrl());
                    setResult(RESULT_OK, intent);
                    edittext.setText("");
                    finish();
                }
                return false;
            }
        });
    }

    private void getUserInfo(long uid) {
        OkGo.<BaseEntry<UserEntry>>post(API.Companion.getGET_USERINFO())
                .tag(this)
                .params("uid", uid)
                .execute(new JsonCallback<BaseEntry<UserEntry>>() {

                    @Override
                    public void onSuccess(Response<BaseEntry<UserEntry>> response) {
                        if (response.isSuccessful()) {
                            UserEntry userinfor = response.body().getUserinfor();
                            if (userinfor != null)
                                setInfo(userinfor);
                        }
                    }
                });
    }

    private void setInfo(UserEntry info) {

        if (userHead != null) {
            Glide.with(getApplicationContext()).load(API.Companion.getPIC_PREFIX() + info.pic).into(userHead);
        }
        if (userNick != null){
            userNick.setText(info.nick);
        }
        if (userInfo!=null){
            StringBuffer txt=new StringBuffer();
            if (!TextUtils.isEmpty(info.birth)){
                txt.append(DateUtils.dataToAge(info.birth));
                txt.append("·");
            }
            if (!TextUtils.isEmpty(info.edu)){
                txt.append(info.edu);
                txt.append("·");
            }
            if (!TextUtils.isEmpty(info.careers)){
                txt.append(info.careers);
                txt.append("·");
            }
            if (!TextUtils.isEmpty(info.height)){
                txt.append(info.height);
                txt.append("cm");
                if (!TextUtils.isEmpty(info.weight)){
                    txt.append(info.weight);
                    txt.append("kg·");
                }else {
                    txt.append("·");
                }
            }
            if (!TextUtils.isEmpty(info.label)){
                String s = info.label.replaceAll(" ", "·");
                txt.append(s);
            }

        }
    }


    @Override
    public void onBackPressed() {
        finishActivityAnim();
    }

    /**
     * 绘制前开始动画
     */
    @Override
    public boolean onPreDraw() {
        rootView.getViewTreeObserver().removeOnPreDrawListener(this);
        final View view = imagePreviewAdapter.getPrimaryItem();
        final ImageView imageView = imagePreviewAdapter.getPrimaryImageView();
        computeImageWidthAndHeight(imageView);

        final ImageInfo imageData = imageInfo.get(currentItem);
        final float vx = imageData.imageViewWidth * 1.0f / imageWidth;
        final float vy = imageData.imageViewHeight * 1.0f / imageHeight;
        ValueAnimator valueAnimator = ValueAnimator.ofFloat(0, 1.0f);
        valueAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                long duration = animation.getDuration();
                long playTime = animation.getCurrentPlayTime();
                float fraction = duration > 0 ? (float) playTime / duration : 1f;
                if (fraction > 1) fraction = 1;
//                Log.i("imgPreAc","imgX: "+imageData.imageViewX+"  imgW:"+imageData.imageViewWidth / 2+"viewW:  "+imageView.getWidth() / 2 +
//                        "      out:"+(imageData.imageViewX + imageData.imageViewWidth / 2 - imageView.getWidth() / 2));
                view.setTranslationX(evaluateInt(fraction, imageData.imageViewX + imageData.imageViewWidth / 2 - imageView.getWidth() / 2, 0));
                view.setTranslationY(evaluateInt(fraction, imageData.imageViewY + imageData.imageViewHeight / 2 - imageView.getHeight() / 2, 0));
                view.setScaleX(evaluateFloat(fraction, vx, 1));
                view.setScaleY(evaluateFloat(fraction, vy, 1));
                view.setAlpha(fraction);
                rootView.setBackgroundColor(evaluateArgb(fraction, Color.TRANSPARENT, Color.BLACK));
            }
        });
        addIntoListener(valueAnimator);
        valueAnimator.setDuration(ANIMATE_DURATION);
        valueAnimator.start();
        return true;
    }

    /**
     * activity的退场动画
     */
    public void finishActivityAnim() {
        final View view = imagePreviewAdapter.getPrimaryItem();
        final ImageView imageView = imagePreviewAdapter.getPrimaryImageView();
        computeImageWidthAndHeight(imageView);

        final ImageInfo imageData = imageInfo.get(currentItem);
        final float vx = imageData.imageViewWidth * 1.0f / imageWidth;
        final float vy = imageData.imageViewHeight * 1.0f / imageHeight;
        final ValueAnimator valueAnimator = ValueAnimator.ofFloat(0, 1.0f);
        valueAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                long duration = animation.getDuration();
                long playTime = animation.getCurrentPlayTime();
                float fraction = duration > 0 ? (float) playTime / duration : 1f;
                if (fraction > 1) fraction = 1;
                view.setTranslationX(evaluateInt(fraction, 0, imageData.imageViewX + imageData.imageViewWidth / 2 - imageView.getWidth() / 2));
                view.setTranslationY(evaluateInt(fraction, 0, imageData.imageViewY + imageData.imageViewHeight / 2 - imageView.getHeight() / 2));
                view.setScaleX(evaluateFloat(fraction, 1, vx));
                view.setScaleY(evaluateFloat(fraction, 1, vy));
                view.setAlpha(1 - fraction);
                rootView.setBackgroundColor(evaluateArgb(fraction, Color.BLACK, Color.TRANSPARENT));
            }
        });
        addOutListener(valueAnimator);
        valueAnimator.setDuration(ANIMATE_DURATION);
        valueAnimator.start();
    }

    /**
     * 计算图片的宽高
     */
    private void computeImageWidthAndHeight(ImageView imageView) {

        // 获取真实大小
        Drawable drawable = imageView.getDrawable();
        if (drawable != null) {
            int intrinsicHeight = drawable.getIntrinsicHeight();
            int intrinsicWidth = drawable.getIntrinsicWidth();
            // 计算出与屏幕的比例，用于比较以宽的比例为准还是高的比例为准，因为很多时候不是高度没充满，就是宽度没充满
            float h = screenHeight * 1.0f / intrinsicHeight;
            float w = screenWidth * 1.0f / intrinsicWidth;
            if (h > w) h = w;
            else w = h;

            // 得出当宽高至少有一个充满的时候图片对应的宽高
            imageHeight = (int) (intrinsicHeight * h);
            imageWidth = (int) (intrinsicWidth * w);
        }
    }

    /**
     * 进场动画过程监听
     */
    private void addIntoListener(ValueAnimator valueAnimator) {
        valueAnimator.addListener(new Animator.AnimatorListener() {
            @Override
            public void onAnimationStart(Animator animation) {
                rootView.setBackgroundColor(0x0);
            }

            @Override
            public void onAnimationEnd(Animator animation) {
            }

            @Override
            public void onAnimationCancel(Animator animation) {
            }

            @Override
            public void onAnimationRepeat(Animator animation) {
            }
        });
    }

    /**
     * 退场动画过程监听
     */
    private void addOutListener(ValueAnimator valueAnimator) {
        valueAnimator.addListener(new Animator.AnimatorListener() {
            @Override
            public void onAnimationStart(Animator animation) {
                rootView.setBackgroundColor(0x0);
            }

            @Override
            public void onAnimationEnd(Animator animation) {
                finish();
                overridePendingTransition(0, 0);
            }

            @Override
            public void onAnimationCancel(Animator animation) {
            }

            @Override
            public void onAnimationRepeat(Animator animation) {
            }
        });
    }

    /**
     * Integer 估值器
     */
    public Integer evaluateInt(float fraction, Integer startValue, Integer endValue) {
        int startInt = startValue;
        return (int) (startInt + fraction * (endValue - startInt));
    }

    /**
     * Float 估值器
     */
    public Float evaluateFloat(float fraction, Number startValue, Number endValue) {
        float startFloat = startValue.floatValue();
        return startFloat + fraction * (endValue.floatValue() - startFloat);
    }

    /**
     * Argb 估值器
     */
    public int evaluateArgb(float fraction, int startValue, int endValue) {
        int startA = (startValue >> 24) & 0xff;
        int startR = (startValue >> 16) & 0xff;
        int startG = (startValue >> 8) & 0xff;
        int startB = startValue & 0xff;

        int endA = (endValue >> 24) & 0xff;
        int endR = (endValue >> 16) & 0xff;
        int endG = (endValue >> 8) & 0xff;
        int endB = endValue & 0xff;

        return (startA + (int) (fraction * (endA - startA))) << 24//
                | (startR + (int) (fraction * (endR - startR))) << 16//
                | (startG + (int) (fraction * (endG - startG))) << 8//
                | (startB + (int) (fraction * (endB - startB)));
    }

    @Override
    public void expressionClick(String str) {
        keyBoard.input(edittext, str);
    }

    @Override
    public void expressionaddRecent(String str) {
        keyBoard.expressionaddRecent(str);
    }

    @Override
    public void expressionDeleteClick(View v) {
        keyBoard.delete(edittext);
    }

    @Override
    protected void onDestroy() {
        edittext.removeTextChangedListener(mTextWatcher);
        super.onDestroy();
    }

    public void onPhotoTap() {
        if (showEdit && keyBoard.isEmojiShow()) {
            keyBoard.reset();
        } else {
            final Intent intent =new Intent();
            intent.putExtra("uid", uid);
            intent.putExtra("img",imageInfo.get(0).getBigImageUrl());
            setResult(RESULT_OK,intent);
            finishActivityAnim();
        }
    }


}
