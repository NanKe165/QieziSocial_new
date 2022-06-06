package com.eggplant.qiezisocial.entry;

import java.io.Serializable;
import java.util.List;

/**
 * Created by Administrator on 2019/1/9.
 */

public class LoginEntry implements Serializable {

    /**
     * stat : ok
     * msg : 登录成功
     * step : 1
     * token : 66204eae97c53062ac65143585af7aac
     * userinfor : {"uid":6,"nick":"","sex":"","weight":"","height":"","birth":"1990-01-01","careers":"","edu":"","xz":"","topic":"","city":"","face":"","pic":[],"card":18223}
     */

    public List<String> careerlist;
    public List<String> interest;
    public List<String> objectlist;
    public String stat;
    public String msg;
    public String todayfirst;
    public String token;
    public UserEntry userinfor;
    public int chance;
    public FilterEntry filter;
    public List<ScenesEntry> scenes;


}
