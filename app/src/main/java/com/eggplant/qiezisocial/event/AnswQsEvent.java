package com.eggplant.qiezisocial.event;

/**
 * Created by Administrator on 2021/5/31.
 */

public class AnswQsEvent {
    public AnswQsEvent(boolean startAnim) {
        this.startAnim = startAnim;
    }

    public AnswQsEvent(boolean startAnim, boolean replySuccess) {
        this.startAnim = startAnim;
        this.replySuccess = replySuccess;
    }

    public boolean startAnim=true;
    public boolean replySuccess=false;
}
