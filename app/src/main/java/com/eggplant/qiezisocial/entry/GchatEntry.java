package com.eggplant.qiezisocial.entry;

import java.io.Serializable;

/**
 * Created by Administrator on 2021/4/6.
 */

public class GchatEntry implements Serializable{
    public String title;
    public String font;
    public MediaEntry media;
    public int usercount;
    public int topiccount;
    public long created;
    public int id;
    public int uid;

}
