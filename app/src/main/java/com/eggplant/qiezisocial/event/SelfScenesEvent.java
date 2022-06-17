package com.eggplant.qiezisocial.event;

import com.eggplant.qiezisocial.entry.ScenesEntry;

import java.io.Serializable;

/**
 * Created by Administrator on 2022/6/17.
 */

public class SelfScenesEvent implements Serializable {
    public SelfScenesEvent(ScenesEntry entry) {
        this.entry = entry;
    }

    public ScenesEntry entry;
}
