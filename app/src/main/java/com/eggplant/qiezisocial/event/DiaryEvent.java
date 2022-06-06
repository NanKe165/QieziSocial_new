package com.eggplant.qiezisocial.event;

/**
 * Created by Administrator on 2021/11/4.
 */

public class DiaryEvent {
    public DiaryEvent() {

    }
    public DiaryEvent(boolean deleteDiary) {
        this.deleteDiary = deleteDiary;
    }

    public boolean deleteDiary;
}
