package com.eggplant.qiezisocial.entry;

import java.io.Serializable;
import java.util.List;

/**
 * Created by Administrator on 2020/8/3.
 */

public class VcrEntry implements Serializable {

    /**
     * id : 5994
     * uid : 1573
     * att:false
     * created : 1596113427
     * title : 时尚—美发
     * media : [{"org":"image/p/o/1XYwiMNfkJAqjIQ$1t7C3p.mp4","extra":"","type":"video"},{"org":"image/p/o/2ygW9Tjgdoep78LeZxkg6a.png","extra":"image/p/t/2DGvrYnQf4u0tl$j_CVa78.jpg","type":"pic"}]
     * userinfor : {"uid":1573,"nick":"爪爪","sex":"男生","weight":"","height":"","birth":"1977-06-30","careers":"","edu":"","xz":"巨蟹座","topic":"","city":"北京市","face":"image/p/t/8DcjB4d5N45Ab$JwYaTyP.jpg","object":"","label":"交友 教育 运动 ","level":"1","sign":"","pic":[],"card":2698921,"stat":"","att":"no","friend":"no","spaceback":"0","longitude":"0","latitude":"0","newlevel":1,"nextexp":50,"expper":0,"continued":0,"yjzq":"no","exp":0}
     * like : 1
     * comment : 1
     */

    public int id;
    public String uid;
    public boolean att;
    public boolean mylike;
    public int created;
    public String title;
    public UserEntry userinfor;
    public int like;
    public int comment;
    public List<MediaEntry> media;


}
