package com.eggplant.qiezisocial.entry;

import java.io.Serializable;
import java.util.List;

/**
 * Created by Administrator on 2020/6/2.
 */

public class BoxEntry implements Serializable {

    /**
     * id : 5751
     * read:false
     * uid : 454
     * created : 2020-06-02
     * text : 你是沙师弟吗？
     * font : miaowu
     * media : [{"org":"image/p/o/2fg_7hvsT_2YgPwGKRccqU.jpg","extra":"image/p/t/3GjB4yyXf40S8ypjTMKBIQ.jpg","type":"pic"},{"org":"image/p/o/1m9LBuzpuvUaqeBlcmHrfH.jpg","extra":"image/p/t/3fMeOQStfW0u0ghOmKUGVs.jpg","type":"pic"},{"org":"image/p/o/2CluLmQkqwZvv1P7YTIztN.jpg","extra":"image/p/t/1sEHm3SrN1ONEDQx8_Rbq6.jpg","type":"pic"}]
     * type : boxquestion
     * userinfor : {"uid":454,"nick":"HQ575850","sex":"男","weight":"","height":"","birth":"1990-01-01","careers":"公关","edu":"","xz":"","topic":"","city":"北京市","face":"face/wwh/gtn2.png","object":"","label":" 仙气十足 只喝露水 ","level":"1","sign":"","pic":["image/p/o/1MZnPVtQ1kNVzvnlUpsH3i.jpg","image/p/o/1Il_$oFVYjz3MA4VCT9oBx.jpg","image/p/o/1DRj__m6b4va5bS_bxK4db.jpg","image/p/o/3lt6x0wSt_rLQwitGydHtK.jpg","image/p/o/2bcHjdEAwgTsgVZzni4o7I.jpg","image/p/o/uM08N3dM9hKYVf4UqFmXB.jpg"],"card":285238,"stat":"","att":"no","friend":"no","spaceback":"0","longitude":"116.433911","latitude":"40.065457","newlevel":1,"nextexp":50,"expper":0,"continued":0,"yjzq":"no","exp":0}
     * answertime : 0
     */

    public int id;
    public boolean read;
    public String uid;
    public String created;
    public String text;
    public String font;
    public String type;
    public String role;
    public String scenes;
    public String sid;
    public UserEntry userinfor;
    public int answertime;
    public List<MediaEntry> media;
    public List<String> person;
    public List<UserEntry> personinfor;
    public int like;
    public  boolean mylike;
    public List<CommentEntry> comment;
    public List<UserEntry> likeuser;
    public String lat;
    public String lng;

    @Override
    public boolean equals(Object obj) {
        if (obj == null)
            return false;
        if (getClass() != obj.getClass()) {
            return false;
        }
        BoxEntry entry = (BoxEntry) obj;
        if (id != 0) {
            if (id != (entry).id) {
                return false;
            }
        } else if (entry.id != 0) {
            return false;
        }

        return true;
    }
}
