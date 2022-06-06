package com.eggplant.qiezisocial.ui.main

import com.eggplant.qiezisocial.R
import com.eggplant.qiezisocial.base.BaseActivity
import com.eggplant.qiezisocial.ui.main.fragment.FriendListFragment
import com.eggplant.qiezisocial.widget.topbar.SimpBarListener
import kotlinx.android.synthetic.main.activity_friendlist.*

class FriendListActivity : BaseActivity() {
    override fun getLayoutId(): Int {
        return R.layout.activity_friendlist
    }

    override fun initView() {
        var trans = supportFragmentManager.beginTransaction()
        trans.add(R.id.ft_friendlist_view, FriendListFragment.instanceFragment(null))
        trans.commit()
        initSmartSwipe()
    }

    override fun initData() {

    }

    override fun initEvent() {
        frientlist_bar.setTbListener(object : SimpBarListener() {
            override fun onReturn() {
                super.onReturn()
                finish()
            }
        })
    }
}
