package com.eggplant.qiezisocial.ui.decorate

import android.content.Intent
import android.support.v7.widget.GridLayoutManager
import com.eggplant.qiezisocial.R
import com.eggplant.qiezisocial.base.BaseActivity
import com.eggplant.qiezisocial.event.ChangeBgEvent
import com.eggplant.qiezisocial.ui.main.fragment.GridSpacingItemDecoration
import com.eggplant.qiezisocial.widget.topbar.SimpBarListener
import kotlinx.android.synthetic.main.activity_decorate.*
import org.greenrobot.eventbus.Subscribe
import org.greenrobot.eventbus.ThreadMode

/**
 * Created by Administrator on 2021/4/21.
 */

class DecorateActivity : BaseActivity() {
    lateinit var adapter: DecorateAdapter
    override fun getLayoutId(): Int {

        return R.layout.activity_decorate
    }

    override fun initView() {
        adapter = DecorateAdapter(null)
        decor_ry.layoutManager = GridLayoutManager(mContext, 2)
        decor_ry.addItemDecoration(GridSpacingItemDecoration(2, mContext.resources.getDimension(R.dimen.qb_px_18).toInt(), mContext.resources.getDimension(R.dimen.qb_px_20).toInt()))
        decor_ry.adapter = adapter
    }

    override fun initData() {
        adapter.setNewData(arrayListOf(R.drawable.decotr_item1, R.drawable.decotr_item2, R.drawable.decotr_item3, R.drawable.decotr_item4,  R.drawable.decotr_item5,
                R.drawable.decotr_item6,R.drawable.decotr_item7,R.drawable.decotr_item8,R.drawable.decotr_item9))
    }

    override fun initEvent() {
        decor_bar.setTbListener(object :SimpBarListener(){
            override fun onReturn() {
                super.onReturn()
                finish()
            }
        })
        adapter.setOnItemClickListener { adapter, view, position ->
            startActivity(Intent(mContext, DecorateDeatilActivity::class.java).putExtra("position",position))
        }
    }
    @Subscribe(threadMode = ThreadMode.MAIN)
    fun changeBg(event: ChangeBgEvent) {
        finish()
    }

}
