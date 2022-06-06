package com.eggplant.qiezisocial.widget;

import android.content.Context;
import android.graphics.drawable.AnimationDrawable;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.bumptech.glide.Glide;
import com.eggplant.qiezisocial.R;
import com.eggplant.qiezisocial.entry.ChatMultiEntry;
import com.eggplant.qiezisocial.greendao.entry.ChatEntry;
import com.eggplant.qiezisocial.model.API;
import com.eggplant.qiezisocial.utils.DateUtils;
import com.eggplant.qiezisocial.widget.image.CircleImageView;
import com.xiao.nicevideoplayer.NiceVideoPlayer;
import com.xiao.nicevideoplayer.TxVideoPlayerController;

/**
 * Created by Administrator on 2021/6/29.
 */

public class AuVideoTxtView extends FrameLayout {

    private ImageView aPlay;
    private LinearLayout aPlayGp;
    private QzTextView aPlayTime;
    private CircleImageView head;
    private QzTextView nick;
    private ImageView sex;
    private ImageView img;
    private ImageView vPlay;
    private QzTextView time;
    private QzTextView cTxt;
    private NiceVideoPlayer player;
    private TxVideoPlayerController controller;
    private ImageView aPlayAnim;

    public AuVideoTxtView(@NonNull Context context) {
        super(context);
        initView();
    }

    public AuVideoTxtView(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        initView();
    }

    public AuVideoTxtView(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initView();
    }

    private void initView() {
        View v = LayoutInflater.from(getContext()).inflate(R.layout.layout_avtxt, null);
        aPlay = v.findViewById(R.id.avt_aplay);
        aPlayAnim = v.findViewById(R.id.avt_aplay_anim);
        aPlayGp = v.findViewById(R.id.avt_aplay_gp);
        aPlayTime = v.findViewById(R.id.avt_aplay_time);
        head = v.findViewById(R.id.avt_head);
        nick = v.findViewById(R.id.avt_nick);
        sex = v.findViewById(R.id.avt_sex);
        img = v.findViewById(R.id.avt_img);
        vPlay = v.findViewById(R.id.avt_vplay);
        time = v.findViewById(R.id.avt_time);
        cTxt = v.findViewById(R.id.avt_txt);
        player = v.findViewById(R.id.avt_player);
        controller = new TxVideoPlayerController(getContext());
        controller.setOnCompleteListener(new TxVideoPlayerController.OnCompleteListener() {
            @Override
            public void onComplete() {
                AnimationDrawable ad = (AnimationDrawable) aPlayAnim.getDrawable();
                aPlayAnim.setVisibility(GONE);
                aPlay.setVisibility(VISIBLE);
                ad.stop();
                player.release();
            }

            @Override
            public void onReset() {
                AnimationDrawable ad = (AnimationDrawable) aPlayAnim.getDrawable();
                aPlayAnim.setVisibility(GONE);
                aPlay.setVisibility(VISIBLE);
                ad.stop();
            }

            @Override
            public void onPrepared() {

            }
        });
        aPlay.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                AnimationDrawable ad = (AnimationDrawable) aPlayAnim.getDrawable();
                aPlayAnim.setVisibility(VISIBLE);
                aPlay.setVisibility(GONE);
                ad.start();
                player.start();
            }
        });
        aPlayAnim.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                AnimationDrawable ad = (AnimationDrawable) aPlayAnim.getDrawable();
                aPlayAnim.setVisibility(GONE);
                aPlay.setVisibility(VISIBLE);
                ad.stop();
                player.release();
            }
        });
//        aPlayGp.setOnClickListener(new OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                AnimationDrawable ad = (AnimationDrawable) aPlayAnim.getDrawable();
//                Log.i("avtView","running:"+ad.isRunning());
//                if (ad.isRunning()) {
//                    aPlayAnim.setVisibility(GONE);
//                    aPlay.setVisibility(VISIBLE);
//                    ad.stop();
//                    player.release();
//                } else {
//                    aPlayAnim.setVisibility(VISIBLE);
//                    aPlay.setVisibility(GONE);
//                    ad.start();
//                    player.start();
//                }
//            }
//        });
        addView(v);
    }


    public void setInfo(ChatMultiEntry<ChatEntry> entry) {
        final ChatEntry bean = entry.bean;
        String created = bean.getCreated();
        String face = bean.getFace();
        String name = bean.getNick();
        String s = bean.getSex();
        setTime(created);
        if (!TextUtils.isEmpty(face)) {
            Glide.with(getContext()).load(API.Companion.getPIC_PREFIX() + face).into(head);
        }
        if (TextUtils.equals(s, "男")) {
            sex.setImageResource(R.mipmap.sex_boy);
        } else {
            sex.setImageResource(R.mipmap.sex_girl);
        }
        if (!TextUtils.isEmpty(name))
            nick.setText(name);
        switch (entry.getItemType()) {
            case ChatMultiEntry.CHAT_MINE:
            case ChatMultiEntry.CHAT_OTHER:
                setTxtData(entry);
                break;

            case ChatMultiEntry.CHAT_OTHER_AUDIO:
            case ChatMultiEntry.CHAT_MINE_AUDIO:
                setAudioData(entry);
                break;

            case ChatMultiEntry.CHAT_OTHER_VIDEO:
            case ChatMultiEntry.CHAT_MINE_VIDEO:
                setVideoData(entry);
                break;
        }
    }

    private void setVideoData(ChatMultiEntry<ChatEntry> item) {
        img.setVisibility(VISIBLE);
        vPlay.setVisibility(VISIBLE);
        aPlayGp.setVisibility(GONE);
        cTxt.setVisibility(GONE);
        String content = item.bean.getContent();
        String extra = item.bean.getExtra();
        if (!TextUtils.isEmpty(extra)) {
            String[] split = extra.split("&&");
            if (split.length > 0) {
                String imgres = split[0];
                Glide.with(getContext()).load(imgres).into(img);
            }
        }
    }

    private void setAudioData(ChatMultiEntry<ChatEntry> item) {
        String extra = item.bean.getExtra();
        String content = item.bean.getContent();
        img.setVisibility(GONE);
        vPlay.setVisibility(GONE);
        aPlayGp.setVisibility(VISIBLE);
        cTxt.setVisibility(GONE);
        if (!TextUtils.isEmpty(extra)) {
            aPlayTime.setText(extra + "''");

        }
        if (!TextUtils.isEmpty(content)) {
            player.release();
            player.setController(controller);
            player.setUp(content, null);
        }
    }

    private void setTxtData(ChatMultiEntry<ChatEntry> item) {
        String content = item.bean.getContent();
        String type = item.bean.getType();
        vPlay.setVisibility(View.GONE);
        aPlayGp.setVisibility(View.GONE);
        if (TextUtils.equals(type, "gtxt")) {
            img.setVisibility(View.GONE);
            cTxt.setVisibility(View.VISIBLE);
            if (!TextUtils.isEmpty(content)) {
                cTxt.setText(content);
            }
        } else if (TextUtils.equals(type, "gpic")) {
            img.setVisibility(View.VISIBLE);
            cTxt.setVisibility(View.GONE);
            if (!TextUtils.isEmpty(content)) {
                Glide.with(getContext()).load(content).into(img);
            }
        }


    }

    private void setTime(String created) {

        if (!TextUtils.isEmpty(created) && !TextUtils.equals(created, "0")) {
            long t = Long.parseLong(created);
            if (DateUtils.isToday(created)) {
                time.setText(DateUtils.timeMinute(created));
            } else if (DateUtils.isSameWeek(created)) {
                time.setText(DateUtils.getWeek(t) + " " + DateUtils.timeMinute(created));
            } else if (DateUtils.IsYesterday(t)) {
                time.setText("昨天 " + DateUtils.timeMinute(created));
            } else {
                time.setText(DateUtils.timeM(created));
            }
        } else {
            time.setText("");
        }
    }

    public CircleImageView getHead() {
        return head;
    }

    public QzTextView getcTxt(){
        return cTxt;
    }
    public void setContentLine(int line){
        cTxt.setLines(line);
    }
}
