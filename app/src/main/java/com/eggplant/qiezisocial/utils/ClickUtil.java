package com.eggplant.qiezisocial.utils;

/**
 * Created by Administrator on 2020/6/16.
 */

public class ClickUtil {
    public static final int DELAY = 1000;
    private static long lastClickTime = 0;
    private static long lastClickTimeWithCustom = 0;

    public static boolean isNotFastClick() {
        long currentTime = System.currentTimeMillis();
        if (currentTime - lastClickTime > DELAY) {
            lastClickTime = currentTime;
            return true;
        } else {
            return false;
        }
    }

    public static boolean isNotFastClick(int delay) {
        long currentTime = System.currentTimeMillis();
        if (currentTime - lastClickTimeWithCustom > delay) {
            lastClickTimeWithCustom = currentTime;
            return true;
        } else {
            return false;
        }
    }

}
