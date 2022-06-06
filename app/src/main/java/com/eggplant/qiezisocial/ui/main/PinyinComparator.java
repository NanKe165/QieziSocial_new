package com.eggplant.qiezisocial.ui.main;

import com.eggplant.qiezisocial.widget.azlist.AZItemEntity;

import java.util.Comparator;

/**
 * Created by Administrator on 2021/12/20.
 */


public class PinyinComparator implements Comparator<AZItemEntity> {

    public int compare(AZItemEntity o1, AZItemEntity o2) {
        if (o1.getSortLetters().equals("@")
                || o2.getSortLetters().equals("#")) {
            return 1;
        } else if (o1.getSortLetters().equals("#")
                || o2.getSortLetters().equals("@")) {
            return -1;
        } else {
            return o1.getSortLetters().compareTo(o2.getSortLetters());
        }
    }



}