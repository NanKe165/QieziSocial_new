package com.eggplant.qiezisocial.utils;

import android.app.Activity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;

import com.eggplant.qiezisocial.R;
import com.eggplant.qiezisocial.widget.guideview.BaseComponent;
import com.eggplant.qiezisocial.widget.guideview.Guide;
import com.eggplant.qiezisocial.widget.guideview.GuideBean;
import com.eggplant.qiezisocial.widget.guideview.GuideBuilder;

import java.util.List;


/**
 * Created by Administrator on 2018/2/28.
 */
public class GuideUtil {
    /**
     * 单独显示一个界面
     * @param activity
     * @param guideBeen
     * @param listener
     */
    public static Guide showGuide(final Activity activity, final GuideBean guideBeen, GuideBuilder.OnVisibilityChangedListener listener){
        GuideBuilder builder = new GuideBuilder();
        builder.setTargetView(guideBeen.target)
                .setAlpha(150)
                .setHighTargetCorner(20)
                .setHighTargetPadding(10)
                .setOverlayTarget(false)
                .setOutsideTouchable(false);

        builder.setOnVisibilityChangedListener(listener);
        builder.addComponent(new BaseComponent(guideBeen));
        Guide guide = builder.createGuide();
        guide.setShouldCheckLocInWindow(false);
        guide.show(activity);
        return guide;
    }

    /**
     * 连续显示一系列界面
     * @param activity
     * @param guideBeens
     */
    public static void showGuide(final Activity activity, final List<GuideBean> guideBeens, final List<Integer> corners, final GuideBuilder.OnVisibilityChangedListener listener){
        GuideBuilder builder = new GuideBuilder();
        builder.setTargetView(guideBeens.get(0).target)
                .setAlpha(150)
                .setHighTargetCorner(0)
                .setHighTargetPadding(0)
                .setOverlayTarget(false)
                .setOutsideTouchable(false);
        if (corners!=null&&corners.size()>0){
            builder.setHighTargetCorner(corners.get(0));
        }

        builder.setOnVisibilityChangedListener(new GuideBuilder.OnVisibilityChangedListener() {
            @Override
            public void onShown() {

            }

            @Override
            public void onDismiss() {
                if(guideBeens.size() > 0){
                    guideBeens.remove(0);
                }
                if (corners!=null&&corners.size()>0){
                    corners.remove(0);
                }
                if(guideBeens.size() > 0){
                    showGuide(activity, guideBeens,corners,listener);
                }else {
                    listener.onDismiss();
                }
            }
        });

        builder.addComponent(new BaseComponent(guideBeens.get(0)));
        Guide guide = builder.createGuide();
        guide.setShouldCheckLocInWindow(false);
        guide.show(activity);
    }

    public static void showGuide2(final Activity activity, final View target, final int imgId){
        final ViewGroup content = (ViewGroup) activity.findViewById(android.R.id.content);
        final View view = activity.getLayoutInflater().inflate(R.layout.guide_layer, null);
        ImageView guideImage = (ImageView) view.findViewById(R.id.guide_image);
        FrameLayout.LayoutParams lps = (FrameLayout.LayoutParams) guideImage.getLayoutParams();
        lps.leftMargin = target.getLeft() - ScreenUtil.dip2px(activity, 45);
        lps.topMargin = target.getTop() - ScreenUtil.dip2px(activity, 80);
        guideImage.setLayoutParams(lps);
        guideImage.setImageResource(imgId);
        view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                content.removeView(view);
            }
        });
        content.addView(view);
    }
}
