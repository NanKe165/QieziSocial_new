package com.eggplant.qiezisocial.ui.login.fragment

import android.app.Activity.RESULT_OK
import android.content.Intent
import com.eggplant.qiezisocial.R
import com.eggplant.qiezisocial.base.BaseFragment
import com.eggplant.qiezisocial.ui.login.LoginActivity
import com.eggplant.qiezisocial.utils.GlideEngine
import com.luck.picture.lib.PictureSelector
import com.luck.picture.lib.config.PictureMimeType
import kotlinx.android.synthetic.main.ft_select_head.*

/**
 * Created by Administrator on 2020/4/26.
 */

class SelectHeadFragment : BaseFragment() {
    private var REQUEST_SELECT_HEAD = 110
    override fun getLayoutId(): Int {
        return R.layout.ft_select_head
    }

    override fun initView() {
    }

    override fun initEvent() {
        ft_select_head.setOnClickListener {
            PictureSelector.create(this)
                    .openGallery(PictureMimeType.ofImage())
                    .loadImageEngine(GlideEngine.createGlideEngine())
                    .isCamera(true)//是否显示拍照按钮 true or false
                    .enableCrop(true)// 是否裁剪 true or false
                    .withAspectRatio(1,1)// int 裁剪比例 如16:9 3:2 3:4 1:1 可自定义
                    .circleDimmedLayer(true)// 是否圆形裁剪 true or false
                    .showCropFrame(false)// 是否显示裁剪矩形边框 圆形裁剪时建议设为false   true or false
                    .showCropGrid(false)// 是否显示裁剪矩形网格 圆形裁剪时建议设为false    true or false
                    .compress(true)// 是否压缩 true or false
                    .maxSelectNum(1)
                    .forResult(REQUEST_SELECT_HEAD)
        }
    }

    override fun initData() {

    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode == RESULT_OK && requestCode == REQUEST_SELECT_HEAD) {
            var selectList = PictureSelector.obtainMultipleResult(data)
            selectList?.let {
                var selectPic = it[0]
                if (selectPic.isCompressed){
                    (activity as LoginActivity).setMyHead(selectPic.compressPath)
                }
            }

        }

    }

}
