package com.eggplant.qiezisocial.base

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup


/**
 * Created by Administrator on 2020/4/13.
 */

abstract class BaseMvpFragment<T : BasePresenter<*>> : BaseFragment(), BaseView {
    protected lateinit var mPresenter: T

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        mPresenter = initPresenter()
        return super.onCreateView(inflater, container, savedInstanceState)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        if (!mPresenter.isViewAttached()) {
            throw RuntimeException("The MvpFragment must call attachView methods of its Presenter")
        }
    }


    abstract fun initPresenter(): T

    override fun onDestroyView() {
        super.onDestroyView()
        mPresenter.detachView()
    }

    override fun onDestroy() {
        super.onDestroy()
    }
}
