package com.eggplant.qiezisocial.widget.guideview;

import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;

import com.eggplant.qiezisocial.R;


/**
 * Created by Administrator on 2018/2/28.
 */
public class BaseComponent implements Component {
    private ImageView guideImage;
    private int imgId;
    private int xOffset;
    private int yOffset;
    private int anchorType;
    private int fitType;

    public BaseComponent(GuideBean guideBean){
        this.imgId = guideBean.imgId;
        this.xOffset = guideBean.xOffset;
        this.yOffset = guideBean.yOffset;
        this.anchorType = guideBean.anchorType;
        this.fitType = guideBean.fitType;
    }

    public BaseComponent(int imgId){
        this(imgId, 0, 0, Component.ANCHOR_TOP, Component.FIT_CENTER);
    }

    public BaseComponent(int imgId, int xOffset, int yOffset){
        this(imgId, xOffset, yOffset, Component.ANCHOR_TOP, Component.FIT_CENTER);
    }

    public BaseComponent(int imgId, int xOffset, int yOffset, int anchorType, int fitType){
        this.imgId = imgId;
        this.xOffset = xOffset;
        this.yOffset = yOffset;
        this.anchorType = anchorType;
        this.fitType = fitType;
    }

    @Override
    public View getView(LayoutInflater inflater) {
        View view = inflater.inflate(R.layout.guide_layer, null);
        view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
//                Toast.makeText(view.getContext(), "引导层被点击了", Toast.LENGTH_SHORT).show();
            }
        });
        guideImage = (ImageView) view.findViewById(R.id.guide_image);
        guideImage.setImageResource(imgId);
        return view;
    }

    @Override
    public int getAnchor() {
        return anchorType;
    }

    @Override
    public int getFitPosition() {
        return fitType;
    }

    @Override
    public int getXOffset() {
        return xOffset;
    }

    @Override
    public int getYOffset() {
        return yOffset;
    }
}
