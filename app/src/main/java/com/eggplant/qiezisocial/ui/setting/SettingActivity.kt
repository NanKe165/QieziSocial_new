package com.eggplant.qiezisocial.ui.setting

import android.app.ProgressDialog
import android.content.Context
import android.content.Intent
import android.text.TextUtils
import android.view.Window
import com.eggplant.qiezisocial.QzApplication
import com.eggplant.qiezisocial.R
import com.eggplant.qiezisocial.base.BaseActivity
import com.eggplant.qiezisocial.entry.BaseEntry
import com.eggplant.qiezisocial.event.FinishEvent
import com.eggplant.qiezisocial.event.LogoutEvent
import com.eggplant.qiezisocial.event.PingEvent
import com.eggplant.qiezisocial.greendao.entry.ChatEntry
import com.eggplant.qiezisocial.greendao.entry.MainInfoBean
import com.eggplant.qiezisocial.greendao.utils.ChatDBManager
import com.eggplant.qiezisocial.greendao.utils.MainDBManager
import com.eggplant.qiezisocial.model.API
import com.eggplant.qiezisocial.model.SettingModel
import com.eggplant.qiezisocial.model.callback.JsonCallback
import com.eggplant.qiezisocial.ui.login.LoginActivity2
import com.eggplant.qiezisocial.ui.main.dialog.NormalDialog
import com.eggplant.qiezisocial.utils.FileUtils
import com.eggplant.qiezisocial.utils.TipsUtil
import com.eggplant.qiezisocial.widget.dialog.QzProgressDialog
import com.eggplant.qiezisocial.widget.topbar.SimpBarListener
import com.lzy.okgo.OkGo
import com.lzy.okgo.model.Response
import kotlinx.android.synthetic.main.layout_setting.*
import org.greenrobot.eventbus.EventBus

/**
 * Created by Administrator on 2020/6/22.
 */

class SettingActivity : BaseActivity() {
    lateinit var dialog: QzProgressDialog
    lateinit var clearHintDialog: NormalDialog
    override fun getLayoutId(): Int {
        return R.layout.layout_setting
    }

    override fun initView() {
        initDialog()
    }

    private fun initDialog() {
        clearHintDialog = NormalDialog(mContext, intArrayOf(R.id.dlg_normal_cancel, R.id.dlg_normal_delete))

        clearHintDialog.setOnBaseDialogItemClickListener { dialog, view ->
            if (view.id == R.id.dlg_normal_cancel) {

            } else {
                when (clearHintDialog.mode) {
                    5 -> clearMessage()
                    6 -> clearUser()
                    7 -> closeUser()
                }

            }
            dialog.dismiss()
        }
        dialog = QzProgressDialog(mContext)
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
        dialog.setCanceledOnTouchOutside(false)
        dialog.setProgressStyle(ProgressDialog.STYLE_SPINNER)
        dialog.setMessage("正在清理...")

    }

    /**
     * 注销账号
     */
    private fun closeUser() {
        OkGo.post<BaseEntry<*>>(API.DIS_USER)
                .execute(object : JsonCallback<BaseEntry<*>>() {
                    override fun onSuccess(response: Response<BaseEntry<*>>?) {
                        if (response!!.isSuccessful) {
                            EventBus.getDefault().post(LogoutEvent())
                            EventBus.getDefault().post(FinishEvent())
                            mContext.startActivity(Intent(mContext, LoginActivity2::class.java).putExtra("from", "logout"))
                        }
                    }
                })
    }

    /**
     * 清空缓存 退出登录
     */
    private fun clearUser() {
        val head = QzApplication.get().infoBean!!.face
        val sp = mContext.getSharedPreferences("userEntry", Context.MODE_PRIVATE)
        val phone = sp.getString("phone", "")
        EventBus.getDefault().post(LogoutEvent())
        EventBus.getDefault().post(FinishEvent())
        mContext.startActivity(Intent(mContext, LoginActivity2::class.java).putExtra("from", "logout").putExtra("phone", phone).putExtra("head", head))
    }

    override fun initData() {
        val sp = mContext.getSharedPreferences("userEntry", MODE_PRIVATE)
        val phone = sp.getString("phone", "")
        if (!TextUtils.isEmpty(phone)) {
            setting_phone.text = phone
        }
        QzApplication.get().loginEntry?.userinfor?.let {
            setting_id.text = it.card
        }
    }

    override fun initEvent() {
        setting_bar.setTbListener(object : SimpBarListener() {
            override fun onReturn() {
                super.onReturn()
                finish()
            }
        })
        setting_agreement.setOnClickListener {
            startActivity(Intent(mContext, AgreementActivity::class.java).putExtra("from", "agreement"))
        }
        setting_privacy.setOnClickListener {
            startActivity(Intent(mContext, AgreementActivity::class.java).putExtra("from", "privacy"))
        }
        setting_close_account.setOnClickListener {
            clearHintDialog.mode = 7
            clearHintDialog.show()
        }
        setting_blacklist.setOnClickListener {
            mContext.startActivity(Intent(mContext, BlackListActivity::class.java))
        }
        setting_clear.setOnClickListener {
            clearHintDialog.mode = 5
            clearHintDialog.show()
        }
        setting_check_version.setOnClickListener {
            SettingModel.checkVersion(mContext, object : JsonCallback<BaseEntry<*>>() {
                override fun onSuccess(response: Response<BaseEntry<*>>?) {
                    response?.let {
                        if (it.isSuccessful) {
                            TipsUtil.showToast(mContext, it.body().ver!!)
                        }
                    }
                }
            })
        }
        setting_about.setOnClickListener {
            startActivity(Intent(mContext, AboutUsActivity::class.java))
        }
        setting_logout.setOnClickListener {
            clearHintDialog.mode = 6
            clearHintDialog.show()
        }
    }

    private fun clearMessage() {
        dialog.show()
        ChatDBManager.getInstance(mContext).deleteAll(ChatEntry::class.java)

//            val mainInfoBeans = MainDBManager.getInstance(mContext).queryMainUserList()
//            for (i in mainInfoBeans.indices) {
//                val mainInfoBean = mainInfoBeans[i]
//                if (TextUtils.equals(mainInfoBean.type, "gfriendlist") || TextUtils.equals(mainInfoBean.type, "temporal")) {
//                    mainInfoBean.msg = ""
//                    mainInfoBean.`object` = ""
//                    mainInfoBean.msgNum = 0
//                    mainInfoBean.msgType = ""
//                }
//            }
//            MainDBManager.getInstance(mContext).upDataAllUser(mainInfoBeans)
        MainDBManager.getInstance(mContext).deleteAll(MainInfoBean::class.java)
        FileUtils.deleteChatCache(mContext)
        EventBus.getDefault().post(PingEvent())
        setting_clear.postDelayed({
            dialog.dismiss()
            TipsUtil.showToast(mContext, "清理完成")
        }, 1000)
    }
}
