package com.eggplant.qiezisocial.entry;

import java.io.Serializable;
import java.util.List;

/**
 * Created by Administrator on 2020/10/16.
 */

public class DiaryEntry implements Serializable{
    /**
     * id : 6190
     * uid : 454
     * created : 1602736798
     * text : 你在人际交往方面吃过哪些亏？
     * font : miaowu
     * media : []
     * type : diary
     * userinfor : {"uid":454,"nick":"HQ575850","sex":"男","weight":"","height":"","birth":"1990-01-01","careers":"美妆达人","edu":"","xz":"","topic":"","city":"北京市","face":"face/wwh/gtn1.png","object":"打发寂寞","label":"IT狂","level":"1","sign":"","pic":["image/p/o/1MZnPVtQ1kNVzvnlUpsH3i.jpg","image/p/o/1Il_$oFVYjz3MA4VCT9oBx.jpg","image/p/o/1DRj__m6b4va5bS_bxK4db.jpg","image/p/o/3lt6x0wSt_rLQwitGydHtK.jpg","image/p/o/2bcHjdEAwgTsgVZzni4o7I.jpg","image/p/o/uM08N3dM9hKYVf4UqFmXB.jpg"],"card":285238,"stat":"","att":"no","friend":"no","spaceback":"0","longitude":"116.433911","latitude":"40.065457","newlevel":1,"nextexp":50,"expper":0,"continued":0,"yjzq":"no","exp":0}
     * like : 1
     * comment : 1
     * mylike : false
     */

    public int id;
    public String uid;
    public int created;
    public String text;
    public String font;
    public String type;
    public UserEntry userinfor;
    public int like;
    public int comment;
    public boolean mylike;
    public List<MediaEntry> media;
    public List<CommentEntry> commentlist;
    public  boolean hasMoreComment=true;

    @Override
    public boolean equals(Object obj) {
        if (obj == null)
            return false;
        if (getClass() != obj.getClass())
            return false;
        DiaryEntry entry = (DiaryEntry) obj;
        if (id != 0) {
            if (id!=( entry).id){
                return false;
            }
        } else if (entry.id!=0){
            return false;
        }

        return true;
    }
}
