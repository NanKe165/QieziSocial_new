package com.eggplant.qiezisocial.contract

import android.content.Context
import android.support.v7.app.AppCompatActivity
import com.eggplant.qiezisocial.base.BaseView
import com.eggplant.qiezisocial.entry.BaseEntry
import com.eggplant.qiezisocial.entry.CommentEntry
import com.eggplant.qiezisocial.entry.DiaryEntry
import com.eggplant.qiezisocial.model.callback.JsonCallback
import com.luck.picture.lib.entity.LocalMedia

/**
 * Created by Administrator on 2020/10/15.
 */

interface DiaryContract{
    interface  Model {
        fun getDiary(i: Int, jsonCallback: JsonCallback<BaseEntry<DiaryEntry>>)
        fun likDiary(id: Int, jsonCallback: JsonCallback<BaseEntry<*>>)
        fun commentDiary(id: Int, toid: Int, content: String, jsonCallback: JsonCallback<BaseEntry<CommentEntry>>)
        fun boomDiary(id: Int,callback: JsonCallback<BaseEntry<*>>)
        fun pubDiary(context: String, jsonCallback: JsonCallback<BaseEntry<DiaryEntry>>)
        fun loadMoreComment(id: Int,cPosition: Int, jsonCallback: JsonCallback<BaseEntry<CommentEntry>>)
    }

    interface View:BaseView {
        fun setNewData(data: List<DiaryEntry>?)
        fun loadMoreDataComplete()
        fun loadMoreDataFinish()
        fun addData(data: List<DiaryEntry>):Boolean
        fun likDiarySuccess(msg: String?, position: Int)
        fun commentSuccess(record: CommentEntry?, position: Int)
        fun loadMoreCommentSuccess(aPosition: Int, list: List<CommentEntry>)
        fun loadmoreCommentError(aPosition: Int)
    }

    interface Presenter {
        fun setNewData(uid: Int)
        fun loadMoreData(size: Int, uid: Int)
        fun likeDiary(mContext: Context, id: Int, position: Int)
        fun commentDiary(postion: Int, id: Int, toid: Int, content: String)
        fun boomDiary(id: Int)
        fun loadMoreComment(aPosition: Int, cPosition: Int, cPosition1: Int)
        fun recordVideo(activity: AppCompatActivity, requesT_CODE_RECORD: Int)
        fun selectVideo(activity: AppCompatActivity, requesT_CODE_SELECT: Int)
        fun selectVideoSuccess(mContext: Context, imgs: ArrayList<LocalMedia>)
    }
}
