package com.eggplant.qiezisocial.widget.guideview;

import android.view.View;

public class GuideBean {
    public View target;
    public int imgId;
    public int xOffset;
    public int yOffset;
    public int anchorType;
    public int fitType;

    public GuideBean(View target, int imgId) {
        this(target, imgId, 0, 0);
    }

    public GuideBean(View target, int imgId, int xOffset, int yOffset) {
        this(target, imgId, xOffset, yOffset, Component.ANCHOR_TOP, Component.FIT_CENTER);
    }

    public GuideBean(View target, int imgId, int xOffset, int yOffset, int anchorType, int fitType) {
        this.target = target;
        this.imgId = imgId;
        this.xOffset = xOffset;
        this.yOffset = yOffset;
        this.anchorType = anchorType;
        this.fitType = fitType;
    }
}
