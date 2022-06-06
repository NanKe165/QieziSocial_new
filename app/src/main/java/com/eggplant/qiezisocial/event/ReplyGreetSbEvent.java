package com.eggplant.qiezisocial.event;

import com.eggplant.qiezisocial.entry.UserEntry;

/**
 * Created by Administrator on 2022/4/14.
 */

public class ReplyGreetSbEvent {

    /**
     * type : message
     * act : greplytxt
     * created : 1649921780328
     * from : 232
     * to : 365
     * id : 6257cec55c85a23298370
     * data : {"created":1649921780329,"txt":"多谢夸奖"}
     * from_userinfor : {"nick":"拳打南山敬老院","sex":"女","weight":"75","height":"133","birth":"1990-01-01","careers":"上班族","edu":"本科","xz":"","topic":"","city":"北京","face":"image/p/t/39QWkN2FIi$KPmFH11jXa9.jpg","stat":"","pic":["image/p/t/3NrSWCKFDlexd2koJa_JXV.jpg"],"card":104086,"uid":232,"spaceback":"6","latitude":"40.025033","longitude":"116.306687","card1":104086,"mood":"孤单","label":"配音 麦霸","object":"快约"}
     */

    public String type;
    public String act;
    public long created;
    public int from;
    public int to;
    public String id;
    public DataBean data;
    public UserEntry from_userinfor;
    public static class DataBean{
        public  long created;
        public String txt;
        public int replyType;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null)
            return false;
        if (getClass() != obj.getClass())
            return false;
        ReplyGreetSbEvent entry = (ReplyGreetSbEvent) obj;
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
