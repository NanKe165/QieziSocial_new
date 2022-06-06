package com.eggplant.qiezisocial.ui.extend.adapter

import android.text.TextUtils
import android.view.View
import com.bumptech.glide.Glide
import com.chad.library.adapter.base.BaseMultiItemQuickAdapter
import com.chad.library.adapter.base.BaseViewHolder
import com.eggplant.qiezisocial.R
import com.eggplant.qiezisocial.entry.AnswGuessMultiEntry
import com.eggplant.qiezisocial.entry.QuestionEntry
import com.eggplant.qiezisocial.model.API
import com.eggplant.qiezisocial.utils.NumbUtils
import kotlinx.android.synthetic.main.ap_answguess_answer.view.*
import kotlinx.android.synthetic.main.ap_answguess_question.view.*

/**
 * Created by Administrator on 2020/7/9.
 */

class AnswerGuessAdapter(data: List<AnswGuessMultiEntry<QuestionEntry>>?) : BaseMultiItemQuickAdapter<AnswGuessMultiEntry<QuestionEntry>, BaseViewHolder>(data) {
    var passsum = 0
    var correctNum = 0
    var model: String = ""
    var showCorrect = false
        set(value) {
            field = value
            currentQs = 1
            notifyDataSetChanged()
        }
    var face: String? = null
    private var currentQs = 1

    init {
        addItemType(AnswGuessMultiEntry.QUESTION, R.layout.ap_answguess_question)
        addItemType(AnswGuessMultiEntry.END_TAG, R.layout.ap_answguess_question)
        addItemType(AnswGuessMultiEntry.ANSWER, R.layout.ap_answguess_answer)
    }

    override fun convert(helper: BaseViewHolder, item: AnswGuessMultiEntry<QuestionEntry>) {
        if (item.itemType == AnswGuessMultiEntry.QUESTION) {
            helper.itemView.ap_ag_qs_title.text = item.bean.title
            if (!TextUtils.isEmpty(face)) {
                Glide.with(mContext).load(API.PIC_PREFIX + face).into(helper.itemView.ap_ag_qs_head)
            }
            helper.itemView.ap_ag_qs_num.text = "第${NumbUtils.numberToChinese(currentQs)}题"
            currentQs++
        } else if (item.itemType == AnswGuessMultiEntry.ANSWER) {
            if (item.selectAnsw == 1) {
                helper.itemView.ap_ag_as_content.text = item.bean.select1
            } else if (item.selectAnsw == 2) {
                helper.itemView.ap_ag_as_content.text = item.bean.select2
            }
            if (showCorrect) {
                helper.itemView.ap_ag_as_hint.visibility = View.VISIBLE
                if (item.selectAnsw == item.bean.correct) {
                    helper.itemView.ap_ag_as_hint.setImageResource(R.mipmap.answer_right)
                    helper.itemView.ap_ag_as_correct.visibility = View.GONE
                } else {
                    helper.itemView.ap_ag_as_hint.setImageResource(R.mipmap.answ_error)
                    helper.itemView.ap_ag_as_correct.visibility = View.VISIBLE
                    helper.itemView.ap_ag_as_correct.text = if (item.bean.correct == 1) item.bean.select1 else item.bean.select2
                }
            } else {
                helper.itemView.ap_ag_as_hint.visibility = View.GONE
                helper.itemView.ap_ag_as_correct.visibility = View.GONE
            }
        } else if (item.itemType == AnswGuessMultiEntry.END_TAG) {

            if (!TextUtils.isEmpty(face)) {
                Glide.with(mContext).load(API.PIC_PREFIX + face).into(helper.itemView.ap_ag_qs_head)
            }
            if (correctNum >= passsum) {
                helper.itemView.ap_ag_qs_num.text = "通过考验你们已经成功添加为好友"
                helper.itemView.ap_ag_qs_title.text = "[qzxs1]太棒了答对了${correctNum}道题，你已经通过了我的考验已经成为好友并奖励你一次${model}"
            } else {
                helper.itemView.ap_ag_qs_num.text = "未通过考验"
                helper.itemView.ap_ag_qs_title.text = "[qzxs13]太遗憾了至少答对${passsum}道题才能通过考验呢"
            }
        }
    }


}
