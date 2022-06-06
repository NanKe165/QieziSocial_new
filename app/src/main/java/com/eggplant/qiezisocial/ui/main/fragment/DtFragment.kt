package com.eggplant.qiezisocial.ui.main.fragment

import android.os.Bundle
import com.eggplant.qiezisocial.R
import com.eggplant.qiezisocial.base.BaseMvpFragment
import com.eggplant.qiezisocial.contract.DynamicContract
import com.eggplant.qiezisocial.entry.BoxEntry
import com.eggplant.qiezisocial.entry.CommentEntry
import com.eggplant.qiezisocial.presenter.DynamicPresenter

/**
 * Created by Administrator on 2022/2/9.
 */

class DtFragment : BaseMvpFragment<DynamicPresenter>(), DynamicContract.View {


    companion object {

        private var fragment: DtFragment? = null
            get() {
                if (field == null)
                    field = DtFragment()
                return field
            }

        fun instanceFragment(bundle: Bundle?): DtFragment {
            if (bundle != null)
                fragment?.arguments = bundle
            return fragment!!
        }
    }
    override fun initPresenter(): DynamicPresenter {
        return DynamicPresenter()
    }

    override fun getLayoutId(): Int {
        return R.layout.ft_dynamc
    }

    override fun initView() {
        mPresenter.attachView(this)
    }

    override fun initEvent() {
    }

    override fun initData() {
    }

    override fun showCompressView(progress: String) {
    }

    override fun hideCompressView() {
    }

    override fun setNewData(list: List<BoxEntry>) {
    }

    override fun addData(list: List<BoxEntry>) {
    }

    override fun loadMoreEnd(b: Boolean) {
    }

    override fun addPubData(record: BoxEntry) {
    }

    override fun notifyLikeView(position: Int) {
    }

    override fun commentSuccess(record: CommentEntry?, position: Int) {
    }

    override fun showUploadView(progress: String) {
    }
}
