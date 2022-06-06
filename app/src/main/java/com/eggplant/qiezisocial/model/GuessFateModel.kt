package com.eggplant.qiezisocial.model

import android.text.TextUtils
import com.eggplant.qiezisocial.contract.GuessFateContract
import com.eggplant.qiezisocial.entry.BaseEntry
import com.eggplant.qiezisocial.entry.GuessFateEntry
import com.eggplant.qiezisocial.entry.QuestionEntry
import com.eggplant.qiezisocial.model.callback.JsonCallback
import com.lzy.okgo.OkGo
import java.io.File

/**
 * Created by Administrator on 2020/7/31.
 */

class GuessFateModel : GuessFateContract.Model {
    /**
     *
     */
    fun getData(begin: Int, callback: JsonCallback<BaseEntry<GuessFateEntry>>) {
        OkGo.post<BaseEntry<GuessFateEntry>>(API.MY_QUESTION)
                .params("b", begin)
                .execute(callback)
    }

    /**
     * 获取缘分猜猜猜 问题
     */
    fun getQuestion(callback: JsonCallback<BaseEntry<QuestionEntry>>) {
        OkGo.put<BaseEntry<QuestionEntry>>(API.SYS_QUESTION)
                .execute(callback)
    }

    /**
     * 发布缘分猜猜猜
     */
    fun pubGuess(eligibleQsNumb: Int, picPath: String?, pattern: String?, myQsList: ArrayList<QuestionEntry>, callback: JsonCallback<BaseEntry<GuessFateEntry>>) {
        var params = OkGo.post<BaseEntry<GuessFateEntry>>(API.PUB_GUESS)
                .params("passsum", eligibleQsNumb)
                .params("mode", pattern)
        if (!TextUtils.isEmpty(picPath)) {
            params.params("pic", File(picPath))
        }
        myQsList.forEachIndexed { index, qsEntry ->
            params.params("${index + 1}:title", qsEntry.title)
                    .params("${index + 1}:correct", qsEntry.correct)
                    .params("${index + 1}:select1", qsEntry.select1)
                    .params("${index + 1}:select2", qsEntry.select2)
        }
        params.execute(callback)
    }

    fun answGuess(id: Int, answ: Map<Int, Int>, callback: JsonCallback<BaseEntry<*>>) {
        var params = OkGo.post<BaseEntry<*>>(API.ANSW_GUESS)
                .params("id", id)
        answ.forEach {
            var key = it.key
            var value = it.value
            params.params("${key}:answer",value)
        }
        params.execute(callback)

    }
}
