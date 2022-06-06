package com.eggplant.qiezisocial.ui.extend

import android.app.Activity
import android.content.Intent
import android.support.v7.widget.LinearLayoutManager
import android.text.TextUtils
import android.view.View
import com.eggplant.qiezisocial.R
import com.eggplant.qiezisocial.base.BaseActivity
import com.eggplant.qiezisocial.entry.AnswGuessMultiEntry
import com.eggplant.qiezisocial.entry.BaseEntry
import com.eggplant.qiezisocial.entry.GuessFateEntry
import com.eggplant.qiezisocial.entry.QuestionEntry
import com.eggplant.qiezisocial.event.PingEvent
import com.eggplant.qiezisocial.model.GuessFateModel
import com.eggplant.qiezisocial.model.callback.JsonCallback
import com.eggplant.qiezisocial.ui.chat.ChatActivity
import com.eggplant.qiezisocial.ui.extend.adapter.AnswerGuessAdapter
import com.eggplant.qiezisocial.utils.TipsUtil
import com.lzy.okgo.model.Response
import kotlinx.android.synthetic.main.activity_answguess.*
import org.greenrobot.eventbus.EventBus

/**
 * Created by Administrator on 2020/7/7.
 */

class AnswerGuessActivity : BaseActivity() {
    var bean: GuessFateEntry? = null

    lateinit var adapter: AnswerGuessAdapter
    lateinit var layoutManager: LinearLayoutManager
    var passsum = 0
    var correctNum = 0
    var currentQsIndex = 0
    var currentQs: QuestionEntry? = null
    var answs = HashMap<Int, Int>()
    override fun getLayoutId(): Int {
        return R.layout.activity_answguess
    }

    override fun initView() {
        adapter = AnswerGuessAdapter(null)
        layoutManager = LinearLayoutManager(mContext)
        answguess_ry.layoutManager = layoutManager
        answguess_ry.adapter = adapter
    }

    override fun initData() {
        bean = intent.getSerializableExtra("bean") as GuessFateEntry
        if (bean == null) {
            finish()
        }
        passsum = bean!!.passsum
        adapter.model = bean!!.mode
        adapter.passsum = passsum
        if (!TextUtils.isEmpty(bean!!.pic)) {
            adapter.face = bean!!.pic
        } else {
            adapter.face = bean!!.userinfor.face
        }

        setNextQuestion(0)

    }


    override fun initEvent() {
        answguess_close.setOnClickListener {
            finish()
        }
        answguess_aw1.setOnClickListener {
            setAnswer(1)
        }
        answguess_aw2.setOnClickListener {
            setAnswer(2)
        }
        answguess_continue.setOnClickListener {
            finish()
        }
        answguess_continue2.setOnClickListener {
            finish()
        }
        answguess_chat.setOnClickListener {
            startActivity(Intent(mContext, ChatActivity::class.java).putExtra("user", bean!!.userinfor))
            finish()
        }
    }

    private fun setNextQuestion(delayMillis: Long) {
        if (currentQsIndex == bean!!.question.size) {
            answQs()

            return
        }

        answguess_ry.postDelayed({
            var qs = bean!!.question[currentQsIndex]
            currentQs = qs
            setQuestion(qs)
            answguess_aw1.text = qs.select1
            answguess_aw2.text = qs.select2
            answguess_aw1.isClickable = true
            answguess_aw2.isClickable = true
            currentQsIndex++
        }, delayMillis)

    }


    private fun setQuestion(qs: QuestionEntry) {
        answguess_aw2.isClickable = false
        answguess_aw1.isClickable = false
        adapter.addData(AnswGuessMultiEntry(AnswGuessMultiEntry.QUESTION, qs))
        scrollToBottom()
    }

    private fun setAnswer(i: Int) {
        if (currentQs != null) {
            var answ = AnswGuessMultiEntry(AnswGuessMultiEntry.ANSWER, currentQs!!)
            answ.selectAnsw = i
            adapter.addData(answ)
            scrollToBottom()
            answs.put(currentQs!!.no, i)
            if (currentQs!!.correct == i) {
                correctNum++
            }
            setNextQuestion(200)
        }
    }

    private fun setEndTag() {
        var endTag = AnswGuessMultiEntry(AnswGuessMultiEntry.END_TAG, currentQs!!)
        adapter.addData(endTag)
        scrollToBottom()
    }

    private fun scrollToBottom() {
        answguess_ry.post({
            //            answguess_ry.scrollToPosition(adapter.data.size-1)
            layoutManager.scrollToPositionWithOffset(adapter.data.size - 1, 0)//先要滚动到这个位置
            val target = layoutManager.findViewByPosition(adapter.data.size - 1)//然后才能拿到这个View
            if (target != null) {
                layoutManager.scrollToPositionWithOffset(adapter.data.size - 1,
                        answguess_ry.measuredHeight - target.measuredHeight)//滚动偏移到底部
            }
        })
    }

    private fun answQs() {
        if (answs.isEmpty()) {
            TipsUtil.showToast(mContext, "error : answer is empty")
            return
        }
        GuessFateModel().answGuess(bean!!.id, answs, object : JsonCallback<BaseEntry<*>>() {
            override fun onSuccess(response: Response<BaseEntry<*>>?) {
                response?.let {
                    if (response.isSuccessful && TextUtils.equals(response.body()!!.stat, "ok")) {
                        finishAnswer()
                    }
                }
            }
        })
    }

    private fun finishAnswer() {
        answguess_aw_gp.visibility = View.GONE
        if (correctNum >= passsum) {
            answguess_next.visibility = View.VISIBLE
        } else {
            answguess_continue2.visibility = View.VISIBLE
        }
        setEndTag()
        EventBus.getDefault().post(PingEvent())
        adapter.correctNum = correctNum
        adapter.showCorrect = true
        setResult(Activity.RESULT_OK, intent)
    }

}
