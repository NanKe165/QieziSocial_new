package com.eggplant.qiezisocial.ui.extend.fragment

import android.os.Bundle
import com.eggplant.qiezisocial.R
import com.eggplant.qiezisocial.base.BaseFragment
import com.eggplant.qiezisocial.entry.BaseEntry
import com.eggplant.qiezisocial.entry.QuestionEntry
import com.eggplant.qiezisocial.model.GuessFateModel
import com.eggplant.qiezisocial.model.callback.JsonCallback
import com.eggplant.qiezisocial.ui.extend.PubGuessActivity
import com.eggplant.qiezisocial.utils.TipsUtil
import com.lzy.okgo.model.Response
import kotlinx.android.synthetic.main.fragment_set_guess.*

/**
 * Created by Administrator on 2020/7/14.
 */

class SetGuessFragment : BaseFragment() {
    companion object {

        fun instanceFragment(bundle: Bundle?): SetGuessFragment {
            var fragment = SetGuessFragment()
            if (bundle != null) {
                fragment.arguments = bundle
            }
            return fragment
        }

    }

    var qsNumb = 0


    //当前问题在问题库中位置下标
    var currentSysQsIndex = 0
    //问题库
    var sysQsList = ArrayList<QuestionEntry>()
    //选择的问题
    var myQsList = ArrayList<QuestionEntry>()
    //当前问题
    var currentQs: QuestionEntry? = null
    //当前问题的正确选项
    var currentQsSelect = -1

    override fun getLayoutId(): Int {
        return R.layout.fragment_set_guess
    }

    override fun initView() {

    }

    override fun initEvent() {
        ft_set_guess_answ1.setOnClickListener {
            currentQsSelect = 1
            ft_set_guess_answ1.setCompoundDrawablesWithIntrinsicBounds(R.mipmap.answer_select, 0, 0, 0)
            ft_set_guess_answ2.setCompoundDrawablesRelativeWithIntrinsicBounds(R.mipmap.answer_unselect, 0, 0, 0)
            currentQs?.correct = currentQsSelect
        }
        ft_set_guess_answ2.setOnClickListener {
            currentQsSelect = 2
            ft_set_guess_answ2.setCompoundDrawablesWithIntrinsicBounds(R.mipmap.answer_select, 0, 0, 0)
            ft_set_guess_answ1.setCompoundDrawablesRelativeWithIntrinsicBounds(R.mipmap.answer_unselect, 0, 0, 0)
            currentQs?.correct = currentQsSelect
        }
        ft_set_guess_nextqs.setOnClickListener {
            changeQuestion()
        }
        ft_set_guess_next.setOnClickListener {

            if (currentQs == null)
                return@setOnClickListener
            if (currentQsSelect == -1) {
                TipsUtil.showToast(mContext, "请先选择一个答案")
                return@setOnClickListener
            }
            setNextQs()

        }
    }


    override fun initData() {
        qsNumb = arguments?.getInt("qsNumb")!!
        set_guess_numbview.setSum(qsNumb)
        GuessFateModel().getQuestion(object : JsonCallback<BaseEntry<QuestionEntry>>() {
            override fun onSuccess(response: Response<BaseEntry<QuestionEntry>>?) {
                response?.let {
                    if (response.isSuccessful) {
                        sysQsList = response.body().list as ArrayList<QuestionEntry>
                        setQs()
                    }
                }
            }
        })

    }

    private fun setQs() {
        ft_set_guess_answ1.setCompoundDrawablesWithIntrinsicBounds(R.mipmap.answer_unselect, 0, 0, 0)
        ft_set_guess_answ2.setCompoundDrawablesWithIntrinsicBounds(R.mipmap.answer_unselect, 0, 0, 0)
        if (currentSysQsIndex < sysQsList.size) {
            currentQs = sysQsList[currentSysQsIndex]
            ft_set_guess_question.text = currentQs!!.title
            ft_set_guess_answ1.text = currentQs!!.select1
            ft_set_guess_answ2.text = currentQs!!.select2
        } else {
            TipsUtil.showToast(mContext, "error")
        }
    }

    private fun changeQuestion() {
        currentSysQsIndex++
        if (currentSysQsIndex >= sysQsList.size)
            currentSysQsIndex = 0
        setQs()
    }

    private fun setNextQs() {
        myQsList.add(currentQs!!)
        sysQsList.remove(currentQs!!)
        currentQsSelect = -1
        if (myQsList.size == qsNumb - 1) {
            ft_set_guess_next.text = "发布"
        }
        if (myQsList.size == qsNumb) {
            (activity as PubGuessActivity).pubGuess(myQsList)
            return
        }
        changeQuestion()
        set_guess_numbview.nextNumb()


    }

}
