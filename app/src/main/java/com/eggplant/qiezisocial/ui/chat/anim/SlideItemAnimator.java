package com.eggplant.qiezisocial.ui.chat.anim;

import android.support.v4.view.ViewCompat;
import android.support.v4.view.ViewPropertyAnimatorCompat;
import android.support.v7.widget.RecyclerView;

/**
 * Created by Administrator on 2022/2/10.
 * 平移属性动画
 */

public class SlideItemAnimator extends BaseItemAnimator{

    @Override
    public void setRemoveAnimation(RecyclerView.ViewHolder holder, ViewPropertyAnimatorCompat animator) {
        animator.translationX(-holder.itemView.getWidth());
    }

    @Override
    public void removeAnimationEnd(RecyclerView.ViewHolder holder) {
        ViewCompat.setTranslationX(holder.itemView,0);
    }

    @Override
    public void addAnimationInit(RecyclerView.ViewHolder holder) {
        ViewCompat.setTranslationX(holder.itemView,-holder.itemView.getWidth());
    }

    @Override
    public void setAddAnimation(RecyclerView.ViewHolder holder, ViewPropertyAnimatorCompat animator) {
        animator.translationX(0);
    }

    @Override
    public void addAnimationCancel(RecyclerView.ViewHolder holder) {
        ViewCompat.setTranslationX(holder.itemView,0);
    }

    @Override
    public void setOldChangeAnimation(RecyclerView.ViewHolder holder, ViewPropertyAnimatorCompat animator) {
        animator.translationX(-holder.itemView.getWidth());
    }

    @Override
    public void oldChangeAnimationEnd(RecyclerView.ViewHolder holder) {
        ViewCompat.setTranslationX(holder.itemView,0);
    }

    @Override
    public void newChangeAnimationInit(RecyclerView.ViewHolder holder) {
        ViewCompat.setTranslationX(holder.itemView,holder.itemView.getWidth());
    }

    @Override
    public void setNewChangeAnimation(RecyclerView.ViewHolder holder, ViewPropertyAnimatorCompat animator) {
        animator.translationX(0);
    }

    @Override
    public void newChangeAnimationEnd(RecyclerView.ViewHolder holder) {
        ViewCompat.setTranslationX(holder.itemView,0);
    }
}