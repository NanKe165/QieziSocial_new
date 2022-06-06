package com.eggplant.qiezisocial.entry;

import com.chad.library.adapter.base.entity.MultiItemEntity;

import java.io.Serializable;

/**
 * Created by Administrator on 2021/3/12.
 */

public class MsgMultiltem<B> implements MultiItemEntity,Serializable {
    public static final int QUES_MSG=110;
    public static final int CHAT_MSG=114;
    public int type=QUES_MSG;
    public B bean;

    public MsgMultiltem(int type, B bean) {
        this.type = type;
        this.bean = bean;
    }

    @Override
    public int getItemType() {
        return type;
    }
}
