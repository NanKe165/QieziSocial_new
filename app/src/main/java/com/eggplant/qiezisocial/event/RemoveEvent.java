package com.eggplant.qiezisocial.event;

import com.eggplant.qiezisocial.greendao.entry.ChatEntry;

/**
 * Created by Administrator on 2021/4/30.
 */

public class RemoveEvent {
    public ChatEntry entry;
    public RemoveEvent(ChatEntry entry) {
        this.entry = entry;
    }
}
