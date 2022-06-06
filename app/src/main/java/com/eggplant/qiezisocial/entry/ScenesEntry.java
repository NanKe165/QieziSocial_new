package com.eggplant.qiezisocial.entry;

import java.io.Serializable;
import java.util.List;

/**
 * Created by Administrator on 2021/8/30.
 */

public class ScenesEntry  implements Serializable{
    public String title;
    public String des;
    public String moment;
    public String sid;
    public String pic;
    public List<String> label;
    public String background;
    public String type;
    public String stat;
    public UserEntry userinfor;
    public String user;
    public String note;
    public String created;

    public String code;

}
