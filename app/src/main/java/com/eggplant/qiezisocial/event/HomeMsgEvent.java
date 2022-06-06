package com.eggplant.qiezisocial.event;

import com.eggplant.qiezisocial.greendao.entry.MainInfoBean;

/**
 * Created by Administrator on 2019/3/17.
 */

public class HomeMsgEvent {
    public String title;
    public MainInfoBean bean;

    public HomeMsgEvent(String title, MainInfoBean bean) {
        this.title = title;
        this.bean = bean;
    }
}
