package com.eggplant.qiezisocial.event;

/**
 * Created by Administrator on 2022/4/18.
 */

public class RemoveGreetSbEvent {
    public GreetSbEvent data;

    public RemoveGreetSbEvent(GreetSbEvent data) {
        this.data = data;
    }
}
