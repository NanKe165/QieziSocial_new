package com.eggplant.qiezisocial.ui.extend

import android.app.Activity
import android.content.Intent
import android.support.v7.widget.LinearLayoutManager
import com.eggplant.qiezisocial.R
import com.eggplant.qiezisocial.base.BaseMvpActivity
import com.eggplant.qiezisocial.contract.GuessFateContract
import com.eggplant.qiezisocial.entry.GuessFateEntry
import com.eggplant.qiezisocial.presenter.GuessFatePresenter
import com.eggplant.qiezisocial.ui.extend.adapter.GuessFateAdapter
import com.eggplant.qiezisocial.widget.topbar.TopBarListener
import kotlinx.android.synthetic.main.activity_guess_fate.*

/**
 * Created by Administrator on 2020/7/7.
 * 缘分猜猜猜
 */

class GuessFateActivity : BaseMvpActivity<GuessFatePresenter>(), GuessFateContract.View {
    val REQUEST_ANSW = 121
    override fun initPresenter(): GuessFatePresenter {
        return GuessFatePresenter()
    }

    lateinit var adapter: GuessFateAdapter
    override fun getLayoutId(): Int {
        return R.layout.activity_guess_fate
    }

    override fun initView() {
        mPresenter.attachView(this)
        adapter = GuessFateAdapter(null)
        guess_fate_ry.layoutManager = LinearLayoutManager(mContext)
        guess_fate_ry.adapter = adapter
    }

    override fun initData() {
        mPresenter.initData()
    }

    override fun initEvent() {
        guess_fate_bar.setTbListener(object : TopBarListener {
            override fun onTxtClick() {
                var intent = Intent(mContext, PubGuessActivity::class.java)
                startActivity(intent)
            }

            override fun onReturn() {
                finish()
            }
        })
        adapter.setOnLoadMoreListener({
            mPresenter.loadMore(adapter.data.size)
        }, guess_fate_ry)
        adapter.setOnItemClickListener { _, view, position ->
            var bean = adapter.data[position]
            var intent = Intent(mContext, AnswerGuessActivity::class.java).putExtra("bean", bean).putExtra("answPosition", position)
            startActivityForResult(intent, REQUEST_ANSW)
        }
    }

    override fun setNewData(list: List<GuessFateEntry>?) {
        adapter.setNewData(list)
    }

    override fun addData(it: List<GuessFateEntry>) {
        if (it.isNotEmpty()) {
            adapter.addData(it)
            adapter.loadMoreComplete()
        } else {
            if (adapter.data.size>5) {
                adapter.loadMoreEnd()
            }else{
                adapter.loadMoreEnd(true)
            }
        }

    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode == Activity.RESULT_OK) {
            if (requestCode == REQUEST_ANSW) {
                data?.let {
                    var answPosition = it.getIntExtra("answPosition", -1)
                    if (answPosition != -1 && answPosition < adapter.data.size) {
                        adapter.remove(answPosition)
                    }
                }

            }
        }
    }
}
