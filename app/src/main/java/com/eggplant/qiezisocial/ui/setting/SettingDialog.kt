package com.eggplant.qiezisocial.ui.setting

import android.app.Activity
import android.app.ProgressDialog
import android.content.Context
import android.content.Context.MODE_PRIVATE
import android.content.Intent
import android.os.Bundle
import android.text.TextUtils
import android.view.Gravity
import android.view.Window
import com.eggplant.qiezisocial.QzApplication
import com.eggplant.qiezisocial.R
import com.eggplant.qiezisocial.entry.BaseEntry
import com.eggplant.qiezisocial.event.FinishEvent
import com.eggplant.qiezisocial.event.LogoutEvent
import com.eggplant.qiezisocial.greendao.entry.ChatEntry
import com.eggplant.qiezisocial.greendao.utils.ChatDBManager
import com.eggplant.qiezisocial.greendao.utils.MainDBManager
import com.eggplant.qiezisocial.model.SettingModel.checkVersion
import com.eggplant.qiezisocial.model.callback.JsonCallback
import com.eggplant.qiezisocial.ui.login.LoginActivity2
import com.eggplant.qiezisocial.utils.FileUtils
import com.eggplant.qiezisocial.utils.TipsUtil
import com.eggplant.qiezisocial.widget.dialog.BaseDialog
import com.eggplant.qiezisocial.widget.dialog.QzProgressDialog
import com.lzy.okgo.model.Response
import kotlinx.android.synthetic.main.layout_setting.*
import org.greenrobot.eventbus.EventBus

/**
 * Created by Administrator on 2020/5/27.
 */

class SettingDialog(context: Context) : BaseDialog(context, R.layout.layout_setting, null) {
    lateinit var dialog: QzProgressDialog

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val window = window
        window!!.setGravity(Gravity.BOTTOM) // 此处可以设置dialog显示的位置为底部
        val windowManager = (context as Activity).windowManager
        val display = windowManager.defaultDisplay
        val lp = getWindow()!!.attributes
        lp.width = display.width
        getWindow()!!.attributes = lp

        initDialog()
        val sp = getContext().getSharedPreferences("userEntry", MODE_PRIVATE)
        val phone = sp.getString("phone", "")
        if (!TextUtils.isEmpty(phone)) {
            setting_phone.text = phone
        }
        QzApplication.get().infoBean?.let {
            setting_id.text = it.card
        }
//        setting_close.setOnClickListener {
//            dismiss()
//        }
        setting_blacklist.setOnClickListener {
            context.startActivity(Intent(context, BlackListActivity::class.java))
        }
        setting_clear.setOnClickListener {
            dialog.show()
            ChatDBManager.getInstance(context).deleteAll(ChatEntry::class.java)
            val mainInfoBeans = MainDBManager.getInstance(context).queryMainUserList()
            for (i in mainInfoBeans.indices) {
                val mainInfoBean = mainInfoBeans[i]
                if (TextUtils.equals(mainInfoBean.type, "gfriendlist") || TextUtils.equals(mainInfoBean.type, "temporal")) {
                    mainInfoBean.msg = ""
                    mainInfoBean.`object` = ""
                    mainInfoBean.msgNum = 0
                    mainInfoBean.msgType = ""
                }
            }
            MainDBManager.getInstance(context).upDataAllUser(mainInfoBeans)
            FileUtils.deleteChatCache(context)
            setting_clear.postDelayed({
                dialog.dismiss()
                TipsUtil.showToast(context, "清理完成")
            }, 1000)
        }
        setting_check_version.setOnClickListener {
            checkVersion(context, object : JsonCallback<BaseEntry<*>>() {
                override fun onSuccess(response: Response<BaseEntry<*>>?) {
                    response?.let {
                        if (it.isSuccessful) {
                            TipsUtil.showToast(context, it.body().ver!!)
                        }
                    }

                }

            })
        }
        setting_about.setOnClickListener {

        }
        setting_logout.setOnClickListener {
            val head = QzApplication.get().infoBean!!.face
            val sp = context.getSharedPreferences("userEntry", Context.MODE_PRIVATE)
            val phone = sp.getString("phone", "")
            EventBus.getDefault().post(LogoutEvent())
            EventBus.getDefault().post(FinishEvent())
            context.startActivity(Intent(context, LoginActivity2::class.java).putExtra("from", "logout").putExtra("phone", phone).putExtra("head", head))
        }
    }

    private fun initDialog() {
        dialog = QzProgressDialog(context)
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
        dialog.setCanceledOnTouchOutside(false)
        dialog.setProgressStyle(ProgressDialog.STYLE_SPINNER)
        dialog.setMessage("正在清理...")


    }
}
