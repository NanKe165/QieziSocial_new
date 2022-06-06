package com.eggplant.qiezisocial.ui.award

import android.content.Intent
import android.support.v4.content.ContextCompat
import android.support.v7.widget.LinearLayoutManager
import com.eggplant.qiezisocial.R
import com.eggplant.qiezisocial.base.BaseActivity
import com.eggplant.qiezisocial.entry.JindouEntry
import com.eggplant.qiezisocial.model.API
import com.eggplant.qiezisocial.model.callback.JsonCallback
import com.eggplant.qiezisocial.utils.TipsUtil
import com.eggplant.qiezisocial.widget.topbar.SimpBarListener
import com.lzy.okgo.OkGo
import com.lzy.okgo.model.Response
import kotlinx.android.synthetic.main.activity_jindou.*

/**
 * Created by Administrator on 2022/1/27.
 */

class JindouActivity : BaseActivity() {
    lateinit var adapter: JindouAdapter
    var imgs = intArrayOf(R.mipmap.icon_gz1, R.mipmap.icon_gz2, R.mipmap.icon_gz3, R.mipmap.icon_gz4, R.mipmap.icon_gz5, R.mipmap.icon_gz6
            , R.mipmap.icon_gz7, R.mipmap.icon_gz8)
    var titles = arrayListOf<String>("完成注册", "完善资料", "一个提问", "一个回复", "一篇日志或动态", "获得1个点赞", "为别人点赞", "成功交友一人")
    var jdc = intArrayOf(50, 20, 5, 5, 5, 5, 1, 20)
    var bgs = arrayListOf<Int>(R.drawable.homebg1, R.drawable.homebg2, R.drawable.homebg3, R.drawable.homebg4, R.drawable.homebg5,
            R.drawable.homebg6, R.drawable.homebg7, R.drawable.homebg8, R.drawable.homebg9, R.drawable.homebg10, R.drawable.homebg11, R.drawable.homebg12)
    var spaceback: String = ""
    override fun getLayoutId(): Int {
        return R.layout.activity_jindou
    }

    override fun initView() {
        changeHomeBg()
        val count = intent.getIntExtra("count", 0)
        jd_count.text = "$count"
        jd_ry.layoutManager = LinearLayoutManager(mContext)
        adapter = JindouAdapter(null)
        jd_ry.adapter = adapter

    }

    override fun initData() {
        imgs.forEachIndexed { index, i ->
            var entry = JindouEntry()
            entry.locaImg = i
            entry.title = titles[index]
            entry.jdcount = "${jdc[index]}"
            adapter.addData(entry)
        }
        getData()

    }
    private fun changeHomeBg() {
        if (application.infoBean != null) {
            spaceback = application.infoBean!!.spaceback
        }
        if (spaceback.isNotEmpty() && spaceback.toInt() < bgs.size) {
            setBackGround(spaceback.toInt())
        } else {
            setBackGround(0)
        }
    }
    private fun getData() {
        OkGo.post<JindouEntry>(API.GET_JINDOU)
                .execute(object :JsonCallback<JindouEntry>(){
                    override fun onSuccess(response: Response<JindouEntry>?) {

                    }
                })
    }

    override fun initEvent() {

        jd_bar.setTbListener(object : SimpBarListener() {
            override fun onReturn() {
                super.onReturn()
                finish()
            }
        })
        jd_award1.setOnClickListener {
            TipsUtil.showToast(mContext,"该功能将于2月15日开启")
        }
        jd_award2.setOnClickListener {
            TipsUtil.showToast(mContext,"该功能将于2月15日开启")
        }
        jd_mx.setOnClickListener {
            startActivity(Intent(mContext, MingxiActivity::class.java))
        }
        jd_guize.setOnClickListener {

        }
    }

    private fun setBackGround(p: Int) {
        jd_rootview.background = ContextCompat.getDrawable(mContext!!, bgs[p])
    }
}
