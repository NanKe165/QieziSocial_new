package com.eggplant.qiezisocial.entry;

import com.chad.library.adapter.base.entity.MultiItemEntity;

import java.io.Serializable;

/**
 * Created by Administrator on 2020/7/9.
 */

public class AnswGuessMultiEntry<B> implements MultiItemEntity, Serializable {
    public static final int QUESTION = 111;
    public static final int ANSWER = 113;
    public static final int END_TAG=112;
    public  int selectAnsw=0;
    private int type;
    public B bean;

    public AnswGuessMultiEntry(int type, B bean) {
        this.type = type;
        this.bean = bean;
    }

    @Override
    public int getItemType() {
        return type;
    }

}
