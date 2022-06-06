package com.eggplant.qiezisocial.entry;

import java.io.Serializable;
import java.util.List;

/**
 * Created by Administrator on 2020/7/31.
 */

public class GuessFateEntry implements Serializable {


        /**
         * id : 5993
         * uid : 1573
         * created : 1596113207
         * pic : image/p/o/4WluwwUee3vJW52icBeFb.png
         * passsum : 2
         * mode : 真心话大冒险
         * question : {"1":{"title":"这是问题1","correct":0,"select1":"这是选项1","select2":"这是选项2"},"2":{"title":"这是问题2","correct":0,"select1":"这是选项1","select2":"这是选项2"}}
         * passuserno : 0
         * userinfor : {"uid":1573,"nick":"爪爪","sex":"男生","weight":"","height":"","birth":"1977-06-30","careers":"","edu":"","xz":"巨蟹座","topic":"","city":"北京市","face":"image/p/t/8DcjB4d5N45Ab$JwYaTyP.jpg","object":"","label":"交友 教育 运动 ","level":"1","sign":"","pic":[],"card":2698921,"stat":"","att":"yes","friend":"yes","spaceback":"0","longitude":"0","latitude":"0","newlevel":1,"nextexp":50,"expper":0,"continued":0,"yjzq":"no","exp":0}
         */

        public int id;
        public String uid;
        public int created;
        public String pic;
        public int passsum;
        public String mode;
        public List<QuestionEntry> question;
        public int passuserno;
        public UserEntry userinfor;


}
