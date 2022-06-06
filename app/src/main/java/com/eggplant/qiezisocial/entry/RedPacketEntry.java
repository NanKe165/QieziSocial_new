package com.eggplant.qiezisocial.entry;

import java.io.Serializable;
import java.util.List;

/**
 * Created by Administrator on 2022/4/19.
 */

public class RedPacketEntry implements Serializable {

    /**
     * title : 红包来了1
     * content : 蓝月亮赞助
     * start : 1650326400
     * hid : 425
     * jids : ["625e696ebfa75","625e696ec0402","625e696ec1516","625e696ebf69f","625e696ebfd4a","625e696ec05e6","625e696ec184d","625e696ec1c28","625e696ec001b","625e696ebfc5a","625e696ebff28","625e696ec1a2e","625e696ec1f22","625e696ec09cb","625e696ec240d","625e696ec0d01","625e696ec0f4a","625e696ec262d","625e696ec1d1c","625e696ebfe36","625e696ec04f5","625e696ebf9b0","625e696ec08d1","625e696ebfb67","625e696ec2035","625e696ec211c","625e696ec1739","625e696ec2752","625e696ec1b20","625e696ec1421","625e696ec1e2e","625e696ec06f3","625e696ec010d","625e696ec1228","625e696ec0ad6","625e696ec0e27","625e696ec2305","625e696ec0be2","625e696ec163e","625e696ec030a","625e696ebf79e","625e696ec0218","625e696ec1937","625e696ec1051","625e696ec114a","625e696ec1315","625e696ebf8a7","625e696ec07ed","625e696ec2208","625e696ec250b"]
     */
    public String title;
    public String content;
    public long start;
    public String hid;
    public List<String> jids;


}
