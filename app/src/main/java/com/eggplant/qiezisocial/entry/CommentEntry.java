package com.eggplant.qiezisocial.entry;

import java.io.Serializable;

/**
 * Created by Administrator on 2020/10/27.
 */

public class CommentEntry implements Serializable{
    public long created;
    public String diaryid;
    public String momentid;
    public int id;
    public String text;
    public String toid;
    public UserEntry touserinfor;
    public String uid;
    public UserEntry userinfor;
}
