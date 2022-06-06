package com.eggplant.qiezisocial.base

import java.io.Serializable


/**
 * Created by Administrator on 2020/4/13.
 */
open class BasePresenter<V : BaseView>: Serializable {
    protected var mView: V? = null


    /**
     * 绑定view，一般在初始化中调用该方法
     *
     * @param view view
     */
    fun attachView(view: V) {
        this.mView = view
    }

    /**
     * 解除绑定view，一般在onDestroy中调用
     */

   open fun detachView() {
        this.mView = null
    }

    /**
     * View是否绑定
     *
     * @return
     */
    fun isViewAttached(): Boolean {
        return mView != null
    }
}