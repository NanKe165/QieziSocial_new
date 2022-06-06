package com.eggplant.qiezisocial.utils;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.View;

import com.eggplant.qiezisocial.ui.main.TxtPreviewActivity;
import com.eggplant.qiezisocial.widget.ninegridImage.ImageInfo;
import com.eggplant.qiezisocial.widget.ninegridImage.preview.ImagePreviewActivity;
import com.eggplant.qiezisocial.widget.ninegridImage.preview.VideoPreviewActivity;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Administrator on 2019/1/24.
 */

public class PrevUtils {
    public static void onImageItemClick(Context context, View imageView, String content, String extra) {
        ImageInfo info = new ImageInfo();
        info.setThumbnailUrl(extra);
        info.setBigImageUrl(content);
        info.imageViewWidth = imageView.getWidth();
        info.imageViewHeight = imageView.getHeight();
        int[] points = new int[2];
        imageView.getLocationInWindow(points);
        info.imageViewX = points[0];
        info.imageViewY = points[1] - ScreenUtil.getStatusBarHeight(context);
        ArrayList<ImageInfo> infos = new ArrayList<>();
        infos.add(info);
        Intent intent = new Intent(context, ImagePreviewActivity.class);
        Bundle bundle = new Bundle();
        bundle.putSerializable(ImagePreviewActivity.IMAGE_INFO, infos);
        bundle.putInt(ImagePreviewActivity.CURRENT_ITEM, 0);
        intent.putExtras(bundle);
        context.startActivity(intent);
        ((Activity) context).overridePendingTransition(0, 0);
    }

    public static void onImageItemClick(Context context, View imageView, String content, String extra, int requestid, Long uid) {
        ImageInfo info = new ImageInfo();
        info.setThumbnailUrl(extra);
        info.setBigImageUrl(content);
        info.imageViewWidth = imageView.getWidth();
        info.imageViewHeight = imageView.getHeight();
        int[] points = new int[2];
        imageView.getLocationInWindow(points);
        info.imageViewX = points[0];
        info.imageViewY = points[1] - ScreenUtil.getStatusBarHeight(context);
        ArrayList<ImageInfo> infos = new ArrayList<>();
        infos.add(info);
        Intent intent = new Intent(context, ImagePreviewActivity.class);
        Bundle bundle = new Bundle();
        bundle.putSerializable(ImagePreviewActivity.IMAGE_INFO, infos);
        bundle.putInt(ImagePreviewActivity.CURRENT_ITEM, 0);
        bundle.putBoolean("showEdit", true);
        bundle.putLong("uid", uid);
        intent.putExtras(bundle);
        ((Activity) context).startActivityForResult(intent, requestid);
        ((Activity) context).overridePendingTransition(0, 0);
    }

    public static void onImageItemClick(Context context, Fragment fragment, View imageView, String content, String extra, int requestid, Long uid) {
        ImageInfo info = new ImageInfo();
        info.setThumbnailUrl(extra);
        info.setBigImageUrl(content);
        info.imageViewWidth = imageView.getWidth();
        info.imageViewHeight = imageView.getHeight();
        int[] points = new int[2];
        imageView.getLocationInWindow(points);
        info.imageViewX = points[0];
        info.imageViewY = points[1] - ScreenUtil.getStatusBarHeight(context);
        ArrayList<ImageInfo> infos = new ArrayList<>();
        infos.add(info);
        Intent intent = new Intent(context, ImagePreviewActivity.class);
        Bundle bundle = new Bundle();
        bundle.putSerializable(ImagePreviewActivity.IMAGE_INFO, infos);
        bundle.putInt(ImagePreviewActivity.CURRENT_ITEM, 0);
        bundle.putBoolean("showEdit", true);
        bundle.putLong("uid", uid);
        intent.putExtras(bundle);
        fragment.startActivityForResult(intent, requestid);
        ((Activity) context).overridePendingTransition(0, 0);
    }

    public static void onVideoItemClick(Context context, View imageView, String content, String extra) {
        ImageInfo info = new ImageInfo();
        info.setThumbnailUrl(extra);
        info.setBigImageUrl(extra);
        info.imageViewWidth = imageView.getWidth();
        info.imageViewHeight = imageView.getHeight();
        int[] points = new int[2];
        imageView.getLocationInWindow(points);
        info.imageViewX = points[0];
        info.imageViewY = points[1] - ScreenUtil.getStatusBarHeight(context);
        ArrayList<ImageInfo> infos = new ArrayList<>();
        infos.add(info);
        Intent intent = new Intent(context, VideoPreviewActivity.class);
        Bundle bundle = new Bundle();
        bundle.putSerializable(VideoPreviewActivity.IMAGE_INFO, (Serializable) infos);
        bundle.putString(VideoPreviewActivity.PLAYER_RESOURCE, content);
        intent.putExtras(bundle);
        context.startActivity(intent);
        ((Activity) context).overridePendingTransition(0, 0);
    }
    public static void onVideoItemClick(Context context, View imageView, String content, String extra,boolean openDownload,String fileName) {
        ImageInfo info = new ImageInfo();
        info.setThumbnailUrl(extra);
        info.setBigImageUrl(extra);
        info.imageViewWidth = imageView.getWidth();
        info.imageViewHeight = imageView.getHeight();
        int[] points = new int[2];
        imageView.getLocationInWindow(points);
        info.imageViewX = points[0];
        info.imageViewY = points[1] - ScreenUtil.getStatusBarHeight(context);
        ArrayList<ImageInfo> infos = new ArrayList<>();
        infos.add(info);
        Intent intent = new Intent(context, VideoPreviewActivity.class);
        Bundle bundle = new Bundle();
        bundle.putSerializable(VideoPreviewActivity.IMAGE_INFO, (Serializable) infos);
        bundle.putString(VideoPreviewActivity.PLAYER_RESOURCE, content);
        bundle.putBoolean(VideoPreviewActivity.OPEN_DOWNLOAD,openDownload);
        bundle.putString(VideoPreviewActivity.DOWNLOAD_FILE_NAME,fileName);
        intent.putExtras(bundle);
        context.startActivity(intent);
        ((Activity) context).overridePendingTransition(0, 0);
    }

    public static void onTxtItemClick(Context context, int position,View view, String txt) {
        int[] points = new int[2];
        view.getLocationInWindow(points);
        int height = view.getHeight();
        int width = view.getWidth();
        Intent intent = new Intent(context, TxtPreviewActivity.class);
        intent.putExtra("txt",txt);
        intent.putExtra("w",width);
        intent.putExtra("h",height);
        intent.putExtra("viewX",points[0]);
        intent.putExtra("viewY",points[1]);
        intent.putExtra("p",position);
        context.startActivity(intent);
        ((Activity) context).overridePendingTransition(0, 0);
    }


    public static void onImageItemClick(Context context, View view, int index, List<ImageInfo> imageInfo) {

        for (int i = 0; i < imageInfo.size(); i++) {
            ImageInfo info = imageInfo.get(i);
            info.imageViewWidth = view.getWidth();
            info.imageViewHeight = view.getHeight();
            int[] points = new int[2];
            view.getLocationInWindow(points);
            info.imageViewX = points[0];
            info.imageViewY = points[1] - ScreenUtil.getStatusBarHeight(context);
        }

        Intent intent = new Intent(context, ImagePreviewActivity.class);
        Bundle bundle = new Bundle();
        bundle.putSerializable(ImagePreviewActivity.IMAGE_INFO, (Serializable) imageInfo);
        bundle.putInt(ImagePreviewActivity.CURRENT_ITEM, index);
        intent.putExtras(bundle);
        context.startActivity(intent);
        ((Activity) context).overridePendingTransition(0, 0);
    }
}
