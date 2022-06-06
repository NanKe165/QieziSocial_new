package com.eggplant.qiezisocial.widget.ninegridImage.preview;

import android.content.Context;
import android.graphics.Bitmap;
import android.support.annotation.NonNull;
import android.support.v4.view.PagerAdapter;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.ProgressBar;

import com.eggplant.qiezisocial.R;
import com.eggplant.qiezisocial.widget.ninegridImage.ImageInfo;
import com.eggplant.qiezisocial.widget.ninegridImage.NineGridView;
import com.luck.picture.lib.photoview.OnPhotoTapListener;
import com.luck.picture.lib.photoview.OnViewTapListener;
import com.luck.picture.lib.photoview.PhotoView;
import com.luck.picture.lib.widget.longimage.SubsamplingScaleImageView;

import java.util.List;


/**
 * ================================================
 * 作    者：廖子尧
 * 版    本：1.0
 * 创建日期：2016/3/21
 * 描    述：
 * 修订历史：
 * ================================================
 */
public class ImagePreviewAdapter extends PagerAdapter implements OnPhotoTapListener, OnViewTapListener {

    private List<ImageInfo> imageInfo;
    private Context context;
    private View currentView;

    public ImagePreviewAdapter(Context context, @NonNull List<ImageInfo> imageInfo) {
        super();
        this.imageInfo = imageInfo;
        this.context = context;
    }

    @Override
    public int getCount() {
        return imageInfo.size();
    }

    @Override
    public boolean isViewFromObject(View view, Object object) {
        return view == object;
    }

    @Override
    public void setPrimaryItem(ViewGroup container, int position, Object object) {
        super.setPrimaryItem(container, position, object);
        currentView = (View) object;
    }

    public View getPrimaryItem() {
        return currentView;
    }

    public ImageView getPrimaryImageView() {
        return (ImageView) currentView.findViewById(R.id.pv);
    }

    @Override
    public Object instantiateItem(ViewGroup container, int position) {
        FrameLayout view = (FrameLayout) LayoutInflater.from(context).inflate(R.layout.item_photoview, container, false);
        final ProgressBar pb = view.findViewById(R.id.pb);
        final PhotoView imageView = view.findViewById(R.id.pv);
        SubsamplingScaleImageView longImg = view.findViewById(R.id.longImg);

        ImageInfo info = this.imageInfo.get(position);
        imageView.setOnPhotoTapListener(this);
        imageView.setOnViewTapListener(this);
        longImg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ((ImagePreviewActivity) context).onPhotoTap();
            }
        });
        showExcessPic(info, imageView, longImg);
        //如果需要加载的loading,需要自己改写,不能使用这个方法
        NineGridView.getImageLoader().onDisplayImage(view.getContext(), imageView, info.bigImageUrl, longImg);

//        pb.setVisibility(View.VISIBLE);
//        Glide.with(context).load(info.bigImageUrl)//
//                .placeholder(R.drawable.ic_default_image)//
//                .error(R.drawable.ic_default_image)//
//                .diskCacheStrategy(DiskCacheStrategy.ALL)//
//                .listener(new RequestListener<String, GlideDrawable>() {
//                    @Override
//                    public boolean onException(Exception e, String model, Target<GlideDrawable> target, boolean isFirstResource) {
//                        pb.setVisibility(View.GONE);
//                        return false;
//                    }
//
//                    @Override
//                    public boolean onResourceReady(GlideDrawable resource, String model, Target<GlideDrawable> target, boolean isFromMemoryCache, boolean isFirstResource) {
//                        pb.setVisibility(View.GONE);
//                        return false;
//                    }
//                }).into(imageView);
        container.addView(view);
        return view;
    }


    /**
     * 展示过度图片
     */
    private void showExcessPic(ImageInfo imageInfo, PhotoView imageView, SubsamplingScaleImageView longImg) {
        //先获取大图的缓存图片
        Bitmap cacheImage = NineGridView.getImageLoader().getCacheImage(imageInfo.bigImageUrl);
        //如果大图的缓存不存在,在获取小图的缓存
        if (cacheImage == null)
            cacheImage = NineGridView.getImageLoader().getCacheImage(imageInfo.thumbnailUrl);
        //如果没有任何缓存,使用默认图片,否者使用缓存
        if (cacheImage == null) {
            imageView.setImageResource(R.drawable.ic_default_color);
        } else {
            imageView.setImageBitmap(cacheImage);
        }

    }

    @Override
    public void destroyItem(ViewGroup container, int position, Object object) {
        container.removeView((View) object);
    }


    @Override
    public void onPhotoTap(ImageView view, float x, float y) {
        ((ImagePreviewActivity) context).onPhotoTap();
    }

    @Override
    public void onViewTap(View view, float x, float y) {
        ((ImagePreviewActivity) context).onPhotoTap();
    }
}