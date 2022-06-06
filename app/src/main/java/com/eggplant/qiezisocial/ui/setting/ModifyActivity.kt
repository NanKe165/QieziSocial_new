package com.eggplant.qiezisocial.ui.setting

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.text.TextUtils
import com.eggplant.qiezisocial.R
import com.eggplant.qiezisocial.base.BaseActivity
import com.eggplant.qiezisocial.ui.setting.fragment.ModifyLabelFragment
import com.eggplant.qiezisocial.ui.setting.fragment.ModifyNickFragment
import com.eggplant.qiezisocial.ui.setting.fragment.ModifySignFragment
import com.eggplant.qiezisocial.widget.topbar.SimpBarListener
import kotlinx.android.synthetic.main.activity_modify.*

/**
 * Created by Administrator on 2020/6/22.
 */

class ModifyActivity : BaseActivity() {
    override fun getLayoutId(): Int {
        return R.layout.activity_modify
    }

    override fun initView() {
        var type = intent.getStringExtra("type")
        var txt = intent.getStringExtra("txt")
        var argements = Bundle()
        if (!TextUtils.isEmpty(txt)) {
            argements.putString("txt", txt)
        }
        when {
            TextUtils.equals(type, "nick") -> {
                var fragment = ModifyNickFragment()
                fragment.arguments = argements
                modify_bar.setTitle("修改昵称")
                var trans = supportFragmentManager.beginTransaction()
                trans.add(R.id.modify_ft, fragment)
                trans.commit()
            }
            TextUtils.equals(type, "sign") -> {
                var fragment = ModifySignFragment()
                fragment.arguments = argements
                var trans = supportFragmentManager.beginTransaction()
                trans.add(R.id.modify_ft, fragment)
                trans.commit()
                modify_bar.setTitle("修改签名")
            }
            TextUtils.equals(type,"label") -> {
                var fragment=ModifyLabelFragment()
                var trans = supportFragmentManager.beginTransaction()
                trans.add(R.id.modify_ft, fragment)
                trans.commit()
                modify_bar.setTitle("修改标签")

            }
        }
    }

    override fun initData() {

    }

    override fun initEvent() {
        modify_bar.setTbListener(object : SimpBarListener() {
            override fun onReturn() {
                super.onReturn()
                activity.finish()
            }
        })
    }

    fun modify(sign: String) {
        var intent = Intent()
        intent.putExtra("txt", sign)
        setResult(Activity.RESULT_OK,intent)
        finish()
    }

}
