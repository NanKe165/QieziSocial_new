package com.eggplant.qiezisocial.event;

/**
 * Created by Administrator on 2019/1/18.
 */

public class LogoutEvent {
   public LogoutEvent(){

   }
    public LogoutEvent(String msg) {
        this.msg = msg;
    }

    public String msg;
}
