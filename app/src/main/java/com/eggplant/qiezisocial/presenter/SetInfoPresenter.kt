package com.eggplant.qiezisocial.presenter

import android.app.Activity
import android.content.Intent
import android.support.v7.app.AppCompatActivity
import android.text.TextUtils
import com.czt.mp3recorder.Log
import com.eggplant.qiezisocial.QzApplication
import com.eggplant.qiezisocial.R
import com.eggplant.qiezisocial.base.BasePresenter
import com.eggplant.qiezisocial.contract.SetInfoContract
import com.eggplant.qiezisocial.entry.AlbumMultiItem
import com.eggplant.qiezisocial.entry.BaseEntry
import com.eggplant.qiezisocial.entry.UserEntry
import com.eggplant.qiezisocial.model.API
import com.eggplant.qiezisocial.model.LoginModel
import com.eggplant.qiezisocial.model.SetInfoModel
import com.eggplant.qiezisocial.model.callback.JsonCallback
import com.eggplant.qiezisocial.ui.main.PrePicActivity
import com.eggplant.qiezisocial.ui.setting.SetInfoActivity
import com.eggplant.qiezisocial.utils.GlideEngine
import com.eggplant.qiezisocial.utils.StorageUtil
import com.eggplant.qiezisocial.utils.TipsUtil
import com.google.gson.Gson
import com.luck.picture.lib.PictureSelector
import com.luck.picture.lib.config.PictureMimeType
import com.luck.picture.lib.entity.LocalMedia
import com.lzy.okgo.model.Response
import java.util.HashMap
import kotlin.collections.ArrayList

/**
 * Created by Administrator on 2020/6/22.
 */

class SetInfoPresenter : BasePresenter<SetInfoContract.View>(), SetInfoContract.Presenter {



    val model = SetInfoModel()
    override fun modifyInfo(tag: AppCompatActivity, key: String, value: String) {
        LoginModel.modifyInfo(tag, key, value, object : JsonCallback<BaseEntry<UserEntry>>() {
            override fun onSuccess(response: Response<BaseEntry<UserEntry>>?) {
                response?.let {
                    if (it.isSuccessful) {
                        if (TextUtils.equals(response.body().stat, "ok")) {
                            QzApplication.get().infoBean = it.body().userinfor
                            setInfo()
                        }
                        it.body().msg?.let { mView?.showTost(it) }
                    }
                }
            }
        })
    }

    override fun setInfo() {
        if (QzApplication.get().infoBean != null) {
            val infoBean = QzApplication.get().infoBean
//            var age = DateUtils.dateDiff(infoBean?.birth, DateUtils.getCurrentTime_Today(), "yyyy-MM-dd").toString() + "岁"
            mView?.setFace(infoBean?.face)
            mView?.setNick(infoBean?.nick)
            mView?.setSign(infoBean?.sign)
            mView?.setLabel(infoBean?.label)
            mView?.setObject(infoBean?.`object`)


            var pic = infoBean?.pic
            if (pic != null && pic.isNotEmpty()) {
                mView?.setPic(getRealPic(pic))
            }
            if (!TextUtils.isEmpty(infoBean?.height)) {
                mView?.setHeight(infoBean?.height + "cm")
            }
            if (!TextUtils.isEmpty(infoBean?.weight)) {
                mView?.setWeight(infoBean?.weight + "kg")
            }
            mView?.setEdu(infoBean?.edu)
            mView?.setCareers(infoBean?.careers)

        }
    }

    /**
     * 相册图片数据转换
     */
    private fun getRealPic(pic: List<String>?): List<AlbumMultiItem<String>>? {
        var data = ArrayList<AlbumMultiItem<String>>()
        pic?.forEach {
            data.add(AlbumMultiItem(AlbumMultiItem.TYPE_ALBUM, API.PIC_PREFIX + it))
        }
        return data
    }

    /**
     * 相册预览
     */
    override fun previewPic(activity: AppCompatActivity, requestcode: Int, position: Int) {
        activity?.startActivityForResult(Intent(activity, PrePicActivity::class.java)
                .putStringArrayListExtra("imgs", QzApplication.get()?.infoBean?.pic as java.util.ArrayList<String>?)
                .putExtra("current", position), requestcode)
        activity?.overridePendingTransition(R.anim.open_enter, R.anim.open_exit)
    }

    /**
     * 添加相册图片
     */
    override fun addPic(setInfoActivity: SetInfoActivity, maxNum: Int, requestcode: Int) {
        PictureSelector.create(setInfoActivity)
                .openGallery(PictureMimeType.ofImage())
                .loadImageEngine(GlideEngine.createGlideEngine())
                .isCamera(true)//是否显示拍照按钮 true or false
                .enableCrop(false)// 是否裁剪 true or false
                .compress(true)// 是否压缩 true or false
                .maxSelectNum(maxNum)
                .forResult(requestcode)
    }

    override fun setAlbum(infoBean: UserEntry?) {
        mView?.setPic(getRealPic(infoBean?.pic))
    }

    /**
     * 上传最新相册图片
     */

    fun upNewPic(context: Activity?, selectList: List<LocalMedia>) {
        var updata = ArrayList<String>()
        selectList.forEach {
            if (it.isCompressed) {
                updata.add(it.compressPath)
            }
        }
        model.addPic(context, updata, object : JsonCallback<BaseEntry<UserEntry>>() {
            override fun onSuccess(response: Response<BaseEntry<UserEntry>>?) {
                Log.i("minePresenter", "sueccess")
                if (response != null && response.isSuccessful) {
                    val body = response.body()
                    TipsUtil.showToast(context, body.msg!!)
                    if (TextUtils.equals(body.stat, "ok")) {
                        val user = body.userinfor
                        QzApplication.get().infoBean = user
                        val map = HashMap<String, Any>()
                        map.put("nick", user!!.nick)
                        map.put("birth", user.birth)
                        map.put("sex", user.sex)
                        map.put("card", user.card)
                        map.put("careers", user.careers)
                        map.put("face", user.face)
                        map.put("uid", user.uid)
                        map.put("question", user.topic)
                        map.put("city", user.city)
                        map.put("edu", user.edu)
                        map.put("weight", user.weight)
                        map.put("height", user.height)
                        map.put("xz", user.xz)
                        map.put("latitude", user.latitude)
                        map.put("longitude", user.longitude)
                        val gson = Gson()
                        val strJson = gson.toJson(user.pic)
                        map.put("pic", strJson)
                        StorageUtil.SPSave(context, "userEntry", map)
                        mView?.setPic(getRealPic(user.pic))
                    }
                }

            }
        })
    }


    fun modifyHead(tag: Any, head: String) {
        LoginModel.modifyHead(tag, head, object : JsonCallback<BaseEntry<UserEntry>>() {
            override fun onSuccess(response: Response<BaseEntry<UserEntry>>?) {
                response?.let {
                    if (it.isSuccessful) {
                        if (TextUtils.equals(response.body().stat, "ok")) {
                            QzApplication.get().infoBean = it.body().userinfor
                            mView?.setFace(it.body().userinfor?.face)
                        }
                        it.body().msg?.let { mView?.showTost(it) }
                    }
                }
            }

        })
    }

    fun changeFace(activity: AppCompatActivity, REQUEST_SELECT_HEAD: Int) {
        PictureSelector.create(activity)
                .openGallery(PictureMimeType.ofImage())
                .loadImageEngine(GlideEngine.createGlideEngine())
                .isCamera(true)//是否显示拍照按钮 true or false
                .enableCrop(true)// 是否裁剪 true or false
                .withAspectRatio(1, 1)// int 裁剪比例 如16:9 3:2 3:4 1:1 可自定义
                .circleDimmedLayer(true)// 是否圆形裁剪 true or false
                .showCropFrame(false)// 是否显示裁剪矩形边框 圆形裁剪时建议设为false   true or false
                .showCropGrid(false)// 是否显示裁剪矩形网格 圆形裁剪时建议设为false    true or false
                .compress(true)// 是否压缩 true or false
                .maxSelectNum(1)
                .forResult(REQUEST_SELECT_HEAD)
    }
}
