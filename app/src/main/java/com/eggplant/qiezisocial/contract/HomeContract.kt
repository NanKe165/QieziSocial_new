package com.eggplant.qiezisocial.contract

import android.app.Activity
import android.content.Context
import com.eggplant.qiezisocial.base.BaseView
import com.eggplant.qiezisocial.entry.*
import com.eggplant.qiezisocial.model.callback.DialogCallback
import com.eggplant.qiezisocial.model.callback.JsonCallback

/**
 * Created by Administrator on 2020/4/14.
 */

interface HomeContract{
    interface Model{

        fun getBoxInfo(tag: Any, begin: Int,  sid:String,callback: JsonCallback<BaseEntry<BoxEntry>>)
        fun getQuestion(tag: Any, callback: JsonCallback<BaseEntry<SysQuestionEntry>>)
        fun pubQuestion(tag: Any, txt: String, font: String,   sid:String,callback: DialogCallback<BaseEntry<BoxEntry>>)
        fun joinGroup(id: Int, callback: JsonCallback<BaseEntry<*>>)
        fun costChance(jsonCallback: JsonCallback<BaseEntry<*>>)
        fun getTopic(tag: Any,scens:String, sid:String,jsonCallback: JsonCallback<TopicEntry>)
        fun collectScene(currentSid: String, i: Boolean, jsonCallback: JsonCallback<BaseEntry<*>>)
        fun visitQuestion(id: Int)
        fun getRedPacket(sid: String, jsonCallback: JsonCallback<BaseEntry<RedPacketEntry>>)
    }
    interface View :BaseView{
        /**
         * 设置新数据
         */
        fun setNewData(data: List<BoxEntry>?)

        /**
         * 添加数据
         */
        fun addData(data: List<BoxEntry>?)

        fun setSysQsList(qsList: List<SysQuestionEntry>?)
        fun showTost(s: String?)
        fun continueToAddData()
        fun setTopic(s: String)
        fun collectSceneSuccess()
        fun showMainHintView()
        fun onThreadBeat()
        fun setRedPacket(redPacketEntry: RedPacketEntry)


    }
    interface Presenter{
        /**
         * 刷新数据
         */
        fun refreshData(tag: Any,sid: String)

        fun addData(tag: Any,begin: Int)
        fun pubQuestion(activity: Activity, toString: String, font: String,sid: String)
        fun initData(itemSize: Int, scens: String, sid: String, type: String)
        fun addNewData()

        fun joinGroup(bean: Context, bean1: BoxEntry)
        fun joinQs(mContext: Context, bean: BoxEntry, i: Int, x: Int, y: Int)
        fun continueToAddData(i: Int)
        fun setTopic(activity: Activity,scens:String, sid:String)
        fun setNextTopic(activity: Activity)
        fun setFilter(goal: String,sid: String)
        fun getCurrentGoal(): String
        fun getCurrentSid(): String
        fun collectScene(currentSid: String, i: Boolean)
        fun getCurrentType(): String
        fun getRedPacket(sid: String)

    }

}
