package com.eggplant.qiezisocial.widget.topbar;

import android.content.Context;
import android.os.Build;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.annotation.RequiresApi;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.ViewPager;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewTreeObserver;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.LinearInterpolator;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.eggplant.qiezisocial.QzApplication;
import com.eggplant.qiezisocial.R;
import com.eggplant.qiezisocial.entry.UserEntry;
import com.eggplant.qiezisocial.model.API;
import com.eggplant.qiezisocial.ui.setting.SettingDialog;
import com.eggplant.qiezisocial.widget.QzTextView;
import com.eggplant.qiezisocial.widget.image.CircleImageView;

/**
 * Created by Administrator on 2020/4/13.
 */

public class MainTopBar extends FrameLayout {

    private FrameLayout indicator;
    private QzTextView titleTv;
    private ImageView mineImg;
    private ImageView friendlistImg;
    private ImageView msgImg;
    private ImageView homeImg;
//    private boolean showClickmeAble = true;

    private ViewPager mVp;

    private String[] mTitleData = {"信息盒", "对话", "好友"};
    private int[] imageData = {0, R.mipmap.main_bar_msg_txt,
//            R.mipmap.main_bar_square_txt,
            R.mipmap.main_bar_fmsg_txt};
    private int[] switchImgData = {R.mipmap.main_home_black_icon, R.mipmap.main_msg_black_icon,
            R.mipmap.main_gchat_black_icon,
            R.mipmap.main_fmsg_black_icon};
    private int jumpLength;
    private ImageView setinfoImg;
    private ImageView settingImg;
    private TextView msgHint;
    private FrameLayout msgHintBg;
    private ImageView barHome;
    private ImageView barMsg;
    private ImageView barFriendList;
    private ImageView barMine;
    private SettingDialog settingDialog;
    private ImageView clickMe;
    private QzTextView funcImg;
    private FrameLayout msgDot;
    private CircleImageView leftmineImg;
    private FrameLayout applyDot;
    private FrameLayout leftHint;
    //    private Integer questionNumb = 0;

    public MainTopBar(@NonNull Context context) {
        this(context, null);
    }

    public MainTopBar(@NonNull Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }


    public MainTopBar(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        View rootView = LayoutInflater.from(context).inflate(R.layout.main_top_bar, null, false);
        homeImg = rootView.findViewById(R.id.bar_home);
        msgImg = rootView.findViewById(R.id.bar_msg);
        friendlistImg = rootView.findViewById(R.id.bar_friendlist);
        mineImg = rootView.findViewById(R.id.bar_mine);
        setinfoImg = rootView.findViewById(R.id.bar_setinfo);
        settingImg = rootView.findViewById(R.id.bar_setting);
        titleTv = rootView.findViewById(R.id.bar_title_tv);
        indicator = rootView.findViewById(R.id.bar_indicator);
        msgHint = rootView.findViewById(R.id.bar_msg_hint);
        clickMe = rootView.findViewById(R.id.bar_clickme);
        msgHintBg = rootView.findViewById(R.id.bar_msg_hint_bg);
        funcImg = rootView.findViewById(R.id.bar_title_func);
        msgDot = rootView.findViewById(R.id.bar_msg_dot);
        applyDot = rootView.findViewById(R.id.bar_apply_dot);
        leftmineImg = rootView.findViewById(R.id.bar_mine_img);
        leftHint = rootView.findViewById(R.id.bar_left_hint);

        barHome = rootView.findViewById(R.id.bar_home);
        barMsg = rootView.findViewById(R.id.bar_msg);
        barFriendList = rootView.findViewById(R.id.bar_friendlist);
        barMine = rootView.findViewById(R.id.bar_mine);

        addView(rootView);
        indicator.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN)
            @Override
            public void onGlobalLayout() {
                jumpLength = msgImg.getLeft() - homeImg.getLeft();
                indicator.getViewTreeObserver().removeOnGlobalLayoutListener(this);
            }
        });
        homeImg.setOnClickListener(new BarClickListener());
        msgImg.setOnClickListener(new BarClickListener());
        friendlistImg.setOnClickListener(new BarClickListener());
        mineImg.setOnClickListener(new BarClickListener());
        settingDialog = new SettingDialog(getContext());
        setinfoImg.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });

//        titleTv.setOnClickListener(new OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                if (listener != null && mVp != null && mVp.getCurrentItem() == 0) {
////                    showClickmeAble = false;
////                    hideClickHint();
//                    listener.onHomeBarClick();
//                }
//            }
//        });
        funcImg.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                if (listener != null && mVp != null && mVp.getCurrentItem() == 0) {
                    listener.onHomeBarClick();
                }
            }
        });
        settingImg.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                settingDialog.show();
            }
        });

        switchBar(0);
        changeTitleBar(0);
    }


    class BarClickListener implements OnClickListener {
        @Override
        public void onClick(View v) {
            if (mVp == null)
                return;
            int position = 0;
            if (v == homeImg) {

            } else {
                if (QzApplication.Companion.get().isLogin(getContext())) {
                    if (v == msgImg) {
                        position = 1;
                    } else if (v == friendlistImg) {
                        position = 2;
                    } else if (v == mineImg) {
                        position = 2;
                    }
                }
            }
            mVp.setCurrentItem(position);
            changeTitleBar(position);
//            scroolIndicator(0, position);
        }
    }


    public void setViewPager(ViewPager vp) {
        this.mVp = vp;
        mVp.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
//                scroolIndicator(positionOffset, position);
            }

            @Override
            public void onPageSelected(int position) {
                changeTitleBar(position);
                if (pageSelectListener != null)
                    pageSelectListener.onPageSelect(position);
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });
    }

    /**
     * 切换titlebar
     *
     * @param position 当前vp position
     */
    private void changeTitleBar(int position) {
        if (titleTv != null) {
            titleTv.setCompoundDrawablesWithIntrinsicBounds(imageData[position], 0, 0, 0);
//            if (position == 0 && questionNumb > 0) {
//                titleTv.setText("(" + questionNumb + ")");
//            } else {
//                titleTv.setText("");
//            }
            if (position == 0) {
                leftmineImg.setVisibility(VISIBLE);
                leftHint.setVisibility(VISIBLE);
            } else {
                leftmineImg.setVisibility(GONE);
                leftHint.setVisibility(INVISIBLE);
            }
            switchBar(position);

        }
    }

    private void switchBar(int position) {
        if (position == 0) {
            switchImgData[0] = R.mipmap.main_home_black_icon;
            switchImgData[1] = R.mipmap.main_msg_white_icon;
            switchImgData[2] = R.mipmap.main_gchat_white_icon;
            switchImgData[3] = R.mipmap.main_flist_white_icon;
        } else if (position == 1) {
            switchImgData[0] = R.mipmap.main_home_white_icon;
            switchImgData[1] = R.mipmap.main_msg_black_icon;
            switchImgData[2] = R.mipmap.main_gchat_white_icon;
            switchImgData[3] = R.mipmap.main_flist_white_icon;
        } else {
            switchImgData[0] = R.mipmap.main_home_white_icon;
            switchImgData[1] = R.mipmap.main_msg_white_icon;
            switchImgData[2] = R.mipmap.main_gchat_black_icon;
            switchImgData[3] = R.mipmap.main_flist_black_icon;
        }
        barHome.setImageResource(switchImgData[0]);
        barMsg.setImageResource(switchImgData[1]);
        barFriendList.setImageResource(switchImgData[2]);
        barMine.setImageResource(switchImgData[3]);
        barFriendList.setBackground(ContextCompat.getDrawable(getContext(), R.drawable.mian_bar_normal_bg));
        barHome.setBackground(ContextCompat.getDrawable(getContext(), R.drawable.mian_bar_normal_bg));
        barMine.setBackground(ContextCompat.getDrawable(getContext(), R.drawable.mian_bar_normal_bg));
        barMsg.setBackground(ContextCompat.getDrawable(getContext(), R.drawable.mian_bar_normal_bg));
        setMineHead();
        switch (position) {
            case 0:
                barHome.setBackground(ContextCompat.getDrawable(getContext(), R.drawable.mian_bar_bg));
                funcImg.setVisibility(VISIBLE);
                break;
            case 1:
                barMsg.setBackground(ContextCompat.getDrawable(getContext(), R.drawable.mian_bar_bg));
                funcImg.setVisibility(GONE);
//                hideClickHint();
                break;
            case 2:
//                barFriendList.setBackground(ContextCompat.getDrawable(getContext(), R.drawable.mian_bar_bg));
//                funcImg.setVisibility(GONE);
////                hideClickHint();
//                break;
            case 3:
                barMine.setBackground(ContextCompat.getDrawable(getContext(), R.drawable.mian_bar_bg));
                funcImg.setVisibility(GONE);
//                hideClickHint();
                break;
        }
    }
    public void setMineHead(){
        UserEntry infoBean = QzApplication.Companion.get().getInfoBean();
        if (infoBean != null && !TextUtils.isEmpty(infoBean.face)) {
            Glide.with(getContext()).load(API.Companion.getPIC_PREFIX() + infoBean.face).into(leftmineImg);
        }
    }

    public void setMsgDotVisiable(boolean visiable) {
        if (visiable) {
            msgDot.setVisibility(View.VISIBLE);
        } else {
            msgDot.setVisibility(View.GONE);
        }
    }

    public void setApplyDotVisiable(boolean visiable) {
        if (visiable) {
            applyDot.setVisibility(View.VISIBLE);
        } else {
            applyDot.setVisibility(View.GONE);
        }
    }

    public void setFuncText(String txt) {
        funcImg.setText(txt);
    }

    //    /**
//     * 滑动indicator
//     *
//     * @param positionOffset 偏移量
//     * @param position       当前vp position
//     */
//    private void scroolIndicator(float positionOffset, int position) {
//        if (indicator != null) {
//            RelativeLayout.LayoutParams params = (RelativeLayout.LayoutParams) indicator.getLayoutParams();
//            int topMargin = (int) getContext().getResources().getDimension(R.dimen.qb_px_48);
//            int rightMargin = (int) getContext().getResources().getDimension(R.dimen.qb_px_143) - (int) (jumpLength * positionOffset + position * jumpLength);
//            params.setMargins(0, topMargin, rightMargin, 0);
//            indicator.setLayoutParams(params);
//        }
//    }
//
    public void setMsgHint(int msgNum) {
        if (msgNum > 0) {
            msgHint.setText(msgNum + "");
            msgHintBg.setVisibility(VISIBLE);
        } else {
            msgHint.setText("");
            msgHintBg.setVisibility(GONE);
        }
    }

//    public void setQuestionNumb(int numb) {
//        this.questionNumb = numb;
//        changeTitleBar(mVp.getCurrentItem());
//    }

//    private void showClickHint() {
//        if (showClickmeAble && mVp.getCurrentItem() == 0) {
//            clickMe.setVisibility(VISIBLE);
//        }
//    }

//    private void hideClickHint() {
//        clickMe.setVisibility(GONE);
//    }

//    public boolean changeClickHint() {
//        if (clickMe.getVisibility() == VISIBLE) {
//            hideClickHint();
//        } else {
//            showClickHint();
//        }
//        return showClickmeAble;
//    }


    public interface onMainBarClickListener {
        void onHomeBarClick();
    }

    public interface OnPageSelectListener {
        void onPageSelect(int position);
    }

    OnPageSelectListener pageSelectListener;
    onMainBarClickListener listener;

    public void setOnPageSelectListener(OnPageSelectListener listener) {
        pageSelectListener = listener;
    }

    public void setOnMainBarClickListener(onMainBarClickListener listener) {
        this.listener = listener;
    }


    public void setMineClickListener(OnClickListener listener) {
        leftmineImg.setOnClickListener(listener);
    }

    public void msgImgAnim() {
        final Animation animation = new AlphaAnimation(1, 0); // Change alpha from fully visible to invisible
        animation.setDuration(400); // duration - half a second
        animation.setInterpolator(new LinearInterpolator()); // do not alter animation rate
        animation.setRepeatCount(3);
        animation.setRepeatMode(Animation.REVERSE); //
        msgImg.setAnimation(animation);
    }

    public int[] getMsgImgLoca() {
        int[] loca = new int[2];
        msgImg.getLocationOnScreen(loca);
        return loca;
    }
    public int [] getFuncViewLoca(){
        int[] loca = new int[2];
        funcImg.getLocationOnScreen(loca);
        return loca;
    }

}
