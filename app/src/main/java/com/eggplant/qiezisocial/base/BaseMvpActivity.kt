package com.eggplant.qiezisocial.base

import android.os.Bundle


/**
 * Created by Administrator on 2020/4/13.
 */

abstract class BaseMvpActivity<T : BasePresenter<*>> : BaseActivity(), BaseView {
    protected lateinit var mPresenter: T
    override fun onCreate(savedInstanceState: Bundle?) {
        mPresenter = initPresenter()
        super.onCreate(savedInstanceState)
        if (!mPresenter.isViewAttached()) {
            throw RuntimeException("The MvpActivity must call attachView methods of its Presenter")
        }
    }

    abstract fun initPresenter(): T

    override fun onDestroy() {
        mPresenter.detachView()
        super.onDestroy()
    }
}
