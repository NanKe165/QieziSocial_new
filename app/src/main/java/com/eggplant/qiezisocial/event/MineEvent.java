package com.eggplant.qiezisocial.event;

import com.eggplant.qiezisocial.entry.UserEntry;

import java.util.List;

/**
 * Created by Administrator on 2019/3/17.
 */

public class MineEvent {
    public MineEvent(String priseNum, List<UserEntry> visitor) {
        this.priseNum = priseNum;
        this.visitor = visitor;
    }

    public String priseNum;
    public List<UserEntry> visitor;
}
