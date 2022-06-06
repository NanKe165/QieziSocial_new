package com.eggplant.qiezisocial.ui.pub

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v4.content.ContextCompat
import android.text.TextUtils
import android.view.View
import com.eggplant.qiezisocial.R
import com.eggplant.qiezisocial.base.BaseMvpActivity
import com.eggplant.qiezisocial.contract.PubTxtContract
import com.eggplant.qiezisocial.entry.BoxEntry
import com.eggplant.qiezisocial.model.UmSetting
import com.eggplant.qiezisocial.presenter.PubTxtPresenter
import com.eggplant.qiezisocial.ui.main.MainActivity
import com.eggplant.qiezisocial.ui.pub.fragment.PubTxtFragment1
import com.eggplant.qiezisocial.ui.pub.fragment.PubTxtFragment2
import com.eggplant.qiezisocial.utils.TipsUtil
import com.eggplant.qiezisocial.widget.topbar.SimpBarListener
import com.mabeijianxi.jianxiexpression.ExpressionGridFragment
import com.umeng.analytics.MobclickAgent
import kotlinx.android.synthetic.main.activity_pub_txt.*


/**
 * Created by Administrator on 2020/4/22.
 */

class PubTxtActivity : BaseMvpActivity<PubTxtPresenter>(), PubTxtContract.View, ExpressionGridFragment.ExpressionClickListener, ExpressionGridFragment.ExpressionDeleteClickListener, ExpressionGridFragment.ExpressionaddRecentListener {

    private var fragments = HashMap<String, Fragment>()
    private var from: String? = ""
    var bgs = arrayListOf<Int>(R.drawable.homebg1, R.drawable.homebg2, R.drawable.homebg3, R.drawable.homebg4, R.drawable.homebg5
    ,R.drawable.homebg6,R.drawable.homebg7,R.drawable.homebg8,R.drawable.homebg9,R.drawable.homebg10,R.drawable.homebg11,R.drawable.homebg12)


    var goalBgs = arrayListOf<Int>(R.color.homebg1, R.color.homebg2, R.color.homebg3, R.color.homebg4, R.color.homebg5, R.color.homebg6, R.color.homebg7)
    override fun initPresenter(): PubTxtPresenter {
        return PubTxtPresenter()
    }

    override fun getLayoutId(): Int {
        return R.layout.activity_pub_txt
    }

    override fun initView() {
        changeMsgBg()
        pub_txt_emojiview.setBigEmojiFlag(true)
        from = intent.getStringExtra("from")
        var nick = intent.getStringExtra("nick")
        var uid = intent.getIntExtra("uid", 0)
        var bundle = Bundle()
        bundle.putString("from", from)
        bundle.putString("nick", nick)
        bundle.putInt("uid", uid)
        fragments.put(PubTxtFragment1::class.java.name, PubTxtFragment1.newInstance(bundle))
        fragments.put(PubTxtFragment2::class.java.name, PubTxtFragment2.getInstance(bundle))
        mPresenter.attachView(this)
        supportFragmentManager.beginTransaction().add(R.id.pub_txt_ft, fragments[PubTxtFragment1::class.java.name]).commit()
        if (TextUtils.equals(from, "login")) {
            pub_txt_bar.visibility = View.GONE
        }

    }

    override fun initData() {

    }

    override fun initEvent() {
        pub_txt_bar.setTbListener(object : SimpBarListener() {
            override fun onReturn() {
                super.onReturn()
                finish()
            }
        })
    }

    private fun changeMsgBg() {
        val spaceback = application.infoBean!!.spaceback
        if (spaceback.isNotEmpty() && spaceback.toInt() < bgs.size) {
            setBackGround(spaceback.toInt())
        } else if (spaceback.isEmpty()) {
            setBackGround(0)
        }
//
//        if (application.loginEntry!!.filter != null && application.loginEntry!!.filter.goal.isNotEmpty()) {
//            application.loginEntry!!.scenes.forEachIndexed { index, scenesEntry ->
//                if (application.loginEntry!!.filter.goal==scenesEntry.title){
//                    setBg(index)
//                }
//            }
//
//        }
    }

    private fun setBg(index: Int) {
        if (index<6) {
            rootView.setBackgroundColor(ContextCompat.getColor(mContext, goalBgs[index]))
        }else{
            rootView.setBackgroundColor(ContextCompat.getColor(mContext, goalBgs[6]))
        }
    }

    private fun setBackGround(p: Int) {
        rootView.background = ContextCompat.getDrawable(mContext, bgs[p])
    }

    override fun expressionClick(str: String?) {
        (fragments[PubTxtFragment1::class.java.name] as PubTxtFragment1).expressionClick(str)
    }

    override fun expressionaddRecent(str: String?) {
        (fragments[PubTxtFragment1::class.java.name] as PubTxtFragment1).expressionaddRecent(str)
    }

    override fun expressionDeleteClick(v: View?) {
        (fragments[PubTxtFragment1::class.java.name] as PubTxtFragment1)?.expressionDeleteClick()
    }


    override fun onResume() {
        super.onResume()
        pub_txt_emojiview.postDelayed({
            pub_txt_emojiview.startAnim()
        }, 1000)

    }

    override fun onPause() {
        super.onPause()
        pub_txt_emojiview.pauseAnim()
    }

    override fun onDestroy() {
        pub_txt_emojiview.stopAnim()
        super.onDestroy()
    }


    fun onNext(context: String,font: String,uid:Int,data:ArrayList<String> ){

        mPresenter.pubPrivateQs(context,font,uid,data)

        val map = HashMap<String, Any>()
        map.put("action_type", "private_pub")
        map.put("user_info", "${application.infoBean!!.uid} ${application.infoBean!!.nick}")
        MobclickAgent.onEventObject(this, UmSetting.EVENT_HOME_PUB, map)
    }

    fun onNext(label: String, context: String, font: String, data: ArrayList<String>, destory: String, model: Int,broadCaost :Int) {
        var scenes = ""
        var sid=""
        if (application.filterData != null) {
            scenes = application.filterData!!.goal
            sid = application.filterData!!.sid
        }
        mPresenter.pubQuestion(activity, scenes, label, context, font, data, destory, model,broadCaost,sid)
        //设置友盟自定义事件统计
        val map = HashMap<String, Any>()
        map.put("action_type", "home_pub")
        map.put("user_info", "${application.infoBean!!.uid} ${application.infoBean!!.nick}")
        MobclickAgent.onEventObject(this, UmSetting.EVENT_HOME_PUB, map)
    }

    fun onLast() {
        pub_txt_bar.visibility = View.VISIBLE
        supportFragmentManager.beginTransaction().replace(R.id.pub_txt_ft, fragments[PubTxtFragment1::class.java.name]).commit()
    }

    var record: BoxEntry? = null
    override fun pubQuestionSuccess(record: BoxEntry) {
        this.record = record
//        var intent=Intent()
//        intent.putExtra("pubBox",record)
//        setResult(Activity.RESULT_OK, intent)
//        finish()

        supportFragmentManager.beginTransaction().replace(R.id.pub_txt_ft, fragments[PubTxtFragment2::class.java.name]).commit()
        pub_txt_bar.visibility = View.GONE
    }

    override fun showTost(msg: String?) {
        msg?.let { TipsUtil.showToast(mContext, msg) }
    }

    fun endPub() {
        when (from) {
            "login" -> startActivity(Intent(mContext, MainActivity::class.java).putExtra("newPub", true).putExtra("pubBox", record))
            "space" -> finish()
            else -> {
                var intent = Intent()
                intent.putExtra("pubBox", record)
                setResult(Activity.RESULT_OK, intent)
                finish()
            }
        }
//        setResult(Activity.RESULT_OK)
//        finish()
//        overridePendingTransition(R.anim.close_enter,R.anim.close_exit)
    }

    override fun finish() {
        super.finish()
        overridePendingTransition(R.anim.close_enter3, R.anim.close_exit3)
    }


}
