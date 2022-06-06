package com.eggplant.qiezisocial.ui.main

import android.content.Intent
import android.support.v7.widget.LinearLayoutManager
import android.text.TextUtils
import android.view.View
import com.bumptech.glide.Glide
import com.eggplant.qiezisocial.R
import com.eggplant.qiezisocial.base.BaseActivity
import com.eggplant.qiezisocial.entry.UserEntry
import com.eggplant.qiezisocial.greendao.utils.MainDBManager
import com.eggplant.qiezisocial.model.API
import com.eggplant.qiezisocial.ui.chat.ChatActivity
import com.eggplant.qiezisocial.ui.main.adapter.OtherAlbumAdapter
import com.eggplant.qiezisocial.utils.DateUtils
import com.eggplant.qiezisocial.utils.PrevUtils
import com.eggplant.qiezisocial.widget.ninegridImage.ImageInfo
import com.eggplant.qiezisocial.widget.topbar.SimpBarListener
import kotlinx.android.synthetic.main.activity_userinfo.*

/**
 * Created by Administrator on 2021/1/5.
 */

class UserInfoActivity : BaseActivity() {
    lateinit var adapter: OtherAlbumAdapter
    var bean: UserEntry? = null
    override fun getLayoutId(): Int {
        return R.layout.activity_userinfo
    }

    override fun initView() {
        adapter= OtherAlbumAdapter(null)
        userinfor_ry.layoutManager=LinearLayoutManager(mContext)
        userinfor_ry.adapter=adapter
    }

    override fun initData() {
        bean = intent.getSerializableExtra("bean") as UserEntry?
        if (bean == null) {
            finish()
        } else {
            setInfoData(bean!!)
        }
    }


    override fun initEvent() {
        userinfor_chat.setOnClickListener {
            var mainBean = MainDBManager.getInstance(mContext).queryMainUser(bean!!.uid.toString())
            startActivity(Intent(mContext, ChatActivity::class.java).putExtra("bean", mainBean).putExtra("user", bean))

        }
        adapter.setOnItemClickListener { _, view, position ->
            var data = ArrayList<ImageInfo>()
            adapter.data.forEach {
                var info = ImageInfo()
                info.bigImageUrl = API.PIC_PREFIX + it
                info.thumbnailUrl = API.PIC_PREFIX + it
                data.add(info)
            }
            PrevUtils.onImageItemClick(mContext, view, position, data)
        }
        userinfor_bar.setTbListener(object : SimpBarListener() {
            override fun onReturn() {
                super.onReturn()
                finish()
            }
        })
    }

    private fun setInfoData(bean: UserEntry) {
        userinfor_name.text = bean.nick
        userinfor_age.text = "${DateUtils.dataToAge(bean.birth)}岁"
        if (bean.sex.contains("男")) {
            userinfor_sex.setImageResource(R.mipmap.sex_boy_big)
        } else {
            userinfor_sex.setImageResource(R.mipmap.sex_girl_big)
        }
        Glide.with(mContext).load(API.PIC_PREFIX + bean.face).into(userinfor_head)
        userinfor_career.text=bean.careers
        userinfor_edu.text=bean.edu
        var wh = ""
        if (!TextUtils.isEmpty(bean.height)) {
            wh += bean.height.replace("cm","").replace("CM","") + "cm"
        }
        if (!TextUtils.isEmpty(bean.weight)) {
            wh += " "+bean.weight.replace("kg","").replace("KG","") + "kg"
        }
        userinfor_wh.text=wh
        userinfor_inster.text=bean.label
        if (bean.pic.isNotEmpty()) {
            userinfor_photo_gp.visibility = View.VISIBLE
            adapter.setNewData(bean.pic)
        }
    }

}
