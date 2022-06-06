package com.eggplant.qiezisocial.greendao.entry;


import com.eggplant.qiezisocial.entry.UserEntry;

import java.util.List;

/**
 * Created by Administrator on 2019/1/11.
 */

public class RecomListEvent {
    public RecomListEvent(List<UserEntry> entries) {
        this.entries = entries;
    }
    public List<UserEntry> entries;
}
