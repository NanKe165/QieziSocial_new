package com.eggplant.qiezisocial.entry;

import com.chad.library.adapter.base.entity.MultiItemEntity;

import java.io.Serializable;

/**
 * Created by Administrator on 2019/1/18.
 */

public class ChatMultiEntry<B> implements MultiItemEntity ,Serializable{
    public static final int CHAT_MINE=110;
    public static final int CHAT_MINE_AUDIO=114;
    public static final int CHAT_MINE_VIDEO=115;
    public static final int CHAT_MINE_QUESTION=112;
    public static final int CHAT_MINE_SHARE_SCENE=121;


    public static final int CHAT_OTHER=111;
    public static final int CHAT_OTHER_AUDIO=113;
    public static final int CHAT_OTHER_VIDEO=116;
    public static final int CHAT_OTHER_QUESTION =117;
    public static final int CHAT_OTHER_SHARE_SCENE=122;


    public static final int CHAT_QUESTION_TITLE=118;
//    public static final int CHAT_MINE_GIFT=117;
//    public static final int CHAT_OTHER_GIFT=118;

    private int type;
    public B bean;
    public ChatMultiEntry(int type, B bean){
        this.type=type;
        this.bean=bean;
    }
    @Override
    public int getItemType() {
        return type;
    }

}
