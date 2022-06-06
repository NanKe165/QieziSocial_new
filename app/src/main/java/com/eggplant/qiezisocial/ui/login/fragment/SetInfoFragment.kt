package com.eggplant.qiezisocial.ui.login.fragment

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.support.v4.content.ContextCompat
import com.bumptech.glide.Glide
import com.eggplant.qiezisocial.QzApplication
import com.eggplant.qiezisocial.R
import com.eggplant.qiezisocial.base.BaseFragment
import com.eggplant.qiezisocial.entry.BaseEntry
import com.eggplant.qiezisocial.entry.UserEntry
import com.eggplant.qiezisocial.model.LoginModel
import com.eggplant.qiezisocial.model.callback.DialogCallback
import com.eggplant.qiezisocial.ui.login.LoginActivity2
import com.eggplant.qiezisocial.utils.GlideEngine
import com.eggplant.qiezisocial.utils.TipsUtil
import com.eggplant.qiezisocial.widget.topbar.SimpBarListener
import com.luck.picture.lib.PictureSelector
import com.luck.picture.lib.config.PictureMimeType
import com.lzy.okgo.model.Response
import kotlinx.android.synthetic.main.fragment_setinfo.*

/**
 * Created by Administrator on 2021/4/25.
 */

class SetInfoFragment : BaseFragment() {
    var sex = ""
    var head = ""
    private var REQUEST_SELECT_HEAD = 110
    override fun getLayoutId(): Int {
        return R.layout.fragment_setinfo
    }

    override fun initView() {

    }

    override fun initEvent() {
        ft_setinfo_bar.setTbListener(object : SimpBarListener() {
            override fun onReturn() {
                super.onReturn()
                head = ""
                ft_setinfo_head.setImageResource(R.mipmap.login_normal_head)
                changeSex("")
                ft_setinfo_nick.setText("")
                (activity as LoginActivity2).setFragment1()
            }
        })
        ft_setinfo_head.setOnClickListener {
            PictureSelector.create(this)
                    .openGallery(PictureMimeType.ofImage())
                    .loadImageEngine(GlideEngine.createGlideEngine())
                    .isCamera(true)//是否显示拍照按钮 true or false
                    .enableCrop(true)// 是否裁剪 true or false
                    .withAspectRatio(1, 1)// int 裁剪比例 如16:9 3:2 3:4 1:1 可自定义
                    .circleDimmedLayer(true)// 是否圆形裁剪 true or false
                    .showCropFrame(false)// 是否显示裁剪矩形边框 圆形裁剪时建议设为false   true or false
                    .showCropGrid(false)// 是否显示裁剪矩形网格 圆形裁剪时建议设为false    true or false
                    .compress(true)// 是否压缩 true or false
                    .maxSelectNum(1)
                    .forResult(REQUEST_SELECT_HEAD)
        }
        ft_setinfo_boy.setOnClickListener {
            changeSex("男")
        }
        ft_setinfo_girl.setOnClickListener {
            changeSex("女")
        }
        ft_setinfo_sure.setOnClickListener {
            val nick = ft_setinfo_nick.text.toString().trim()
            when {
//                head.isEmpty() -> TipsUtil.showToast(mContext, "请添加头像")
                sex.isEmpty() -> TipsUtil.showToast(mContext, "请选择性别")
                nick.isEmpty() -> TipsUtil.showToast(mContext, "请完善昵称")
                else -> {
                    ft_setinfo_sure.isClickable=false
                    setinfo(mContext!!, nick, sex, head)
                }
            }
        }

    }

    private fun changeSex(sex: String) {
        this.sex = sex
        when (sex) {
            "男" -> {
                ft_setinfo_boy.background = ContextCompat.getDrawable(mContext!!, R.drawable.login_boy_select_bg)
                ft_setinfo_girl.background = ContextCompat.getDrawable(mContext!!, R.drawable.login_girl_bg)
            }
            "女" -> {
                ft_setinfo_boy.background = ContextCompat.getDrawable(mContext!!, R.drawable.login_boy_bg)
                ft_setinfo_girl.background = ContextCompat.getDrawable(mContext!!, R.drawable.login_girl_select_bg)
            }
            else -> {
                ft_setinfo_boy.background = ContextCompat.getDrawable(mContext!!, R.drawable.login_boy_bg)
                ft_setinfo_girl.background = ContextCompat.getDrawable(mContext!!, R.drawable.login_girl_bg)
            }
        }
    }

    override fun initData() {

    }


    private fun setinfo(context: Context, nick: String, sex: String, head: String) {
        LoginModel.register(context, nick, sex, head, object : DialogCallback<BaseEntry<UserEntry>>(activity,"登录中...") {
            override fun onSuccess(response: Response<BaseEntry<UserEntry>>?) {
                if (response!!.isSuccessful) {
                    QzApplication.get().infoBean = response.body().userinfor
                    (activity as LoginActivity2).goPubActivity()
                }
            }
        })
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode == Activity.RESULT_OK && requestCode == REQUEST_SELECT_HEAD) {
            var selectList = PictureSelector.obtainMultipleResult(data)
            selectList?.let {
                var selectPic = it[0]
                if (selectPic.isCompressed) {
                    head = selectPic.compressPath
                    Glide.with(mContext!!).load(head).into(ft_setinfo_head)
                }
            }

        }
    }
}
