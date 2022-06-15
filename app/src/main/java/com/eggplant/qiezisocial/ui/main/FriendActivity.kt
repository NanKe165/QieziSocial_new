package com.eggplant.qiezisocial.ui.main

import android.content.Intent
import android.os.Bundle
import android.util.Log
import com.eggplant.qiezisocial.R
import com.eggplant.qiezisocial.base.BaseActivity
import com.eggplant.qiezisocial.ui.main.fragment.FriendMsgFragment
import com.eggplant.qiezisocial.utils.NotifycationUtils
import com.eggplant.qiezisocial.widget.topbar.TopBarListener
import kotlinx.android.synthetic.main.activity_friend.*

/**
 * Created by Administrator on 2022/2/9.
 */

class FriendActivity : BaseActivity() {
    override fun getLayoutId(): Int {
        return R.layout.activity_friend
    }

    override fun initView() {
        NotifycationUtils.getInstance(mContext).cancelNotify(2)
    }

    override fun initView(savedInstanceState: Bundle?) {
        super.initView(savedInstanceState)
        val from = intent.getStringExtra("from")
        if (from == null) {
            finish()
            return
        }
        val bundle = Bundle()
        bundle.putString("from", from)
        if (from == "apply") {
            friend_bar.setTitle("好友申请")
        }else{
            friend_bar.setRightDrawable(R.mipmap.ic_add_friend)
        }
        if (savedInstanceState != null && savedInstanceState.getBoolean("isFriendDestroy", false)) {
//            Log.i("friendAc","---------0")
            supportFragmentManager.fragments
                    .filter { it::class.java.simpleName == "FriendMsgFragment" }
                    .forEach {
//                        Log.i("friendAc","---------0---${it.arguments?.getString("from")}")
                        supportFragmentManager.beginTransaction().add(R.id.friend_ft, it).commit() }
        }else {
            Log.i("friendAc","---------$from")
            val ft = FriendMsgFragment.newInstance(bundle)
            supportFragmentManager.beginTransaction().add(R.id.friend_ft, ft).commit()
        }
    }
    override fun initData() {

    }
    override fun onSaveInstanceState(outState: Bundle?) {
        outState?.putBoolean("isFriendDestroy", true)
        super.onSaveInstanceState(outState)
    }

    override fun initEvent() {
        friend_bar.setTbListener(object : TopBarListener{
            override fun onReturn() {
                finish()
            }

            override fun onTxtClick() {
                startActivity(Intent(mContext,FindFirendActivity::class.java))
            }

        })
    }
}
