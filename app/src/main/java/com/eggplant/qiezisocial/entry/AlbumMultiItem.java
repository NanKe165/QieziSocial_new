package com.eggplant.qiezisocial.entry;

import com.chad.library.adapter.base.entity.MultiItemEntity;

import java.io.Serializable;

/**
 * Created by Administrator on 2020/4/20.
 */

public class AlbumMultiItem<B> implements MultiItemEntity ,Serializable{
    public static final int TYPE_ALBUM=110;
    public static final int TYPE_TAKEPHOTO=114;
    public int type=TYPE_ALBUM;
    public B bean;

    public AlbumMultiItem(int type, B bean) {
        this.type = type;
        this.bean = bean;
    }

    @Override
    public int getItemType() {
        return type;
    }
}
