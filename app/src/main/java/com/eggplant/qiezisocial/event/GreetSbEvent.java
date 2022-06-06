package com.eggplant.qiezisocial.event;

import com.eggplant.qiezisocial.entry.UserEntry;

import java.io.Serializable;

/**
 * Created by Administrator on 2022/4/14.
 */

public class GreetSbEvent  implements Serializable{

    /**
     * type : message
     * act : ggreet
     * created : 1649902523229
     * from : 365
     * to : 232
     * id : 6257839477f1a36564691
     * from_userinfor : {"nick":"葫芦娃","sex":"男","weight":"50","height":"176","birth":"2006-01-06","careers":"创业路上","edu":"本科","xz":"摩羯座","topic":"","city":"北京","face":"image/p/t/jzl3B_1tBuWVnlxPcul5t.jpg","stat":"","pic":["image/p/o/2Nf7CKwIAHBvpD3TMZUHhT.jpg"],"card":200777,"uid":365,"spaceback":"8","longitude":"116.306708","latitude":"40.025262","sign":"哦哈哈哈哈哈哈哈哈","label":"二次元 沉迷睡觉 以史为镜 书迷","object":"解寂寞"}
     */

    public String type;
    public String act;
    public long created;
    public int from;
    public int to;
    public String id;
    public UserEntry from_userinfor;

    @Override
    public boolean equals(Object obj) {
        if (obj == null)
            return false;
        if (getClass() != obj.getClass())
            return false;
        GreetSbEvent entry = (GreetSbEvent) obj;
        if (id != null) {
            if (!id.equals(( entry).id)){
                return false;
            }
        } else if (entry.id!=null){
            return false;
        }

        return true;
    }
    
}
