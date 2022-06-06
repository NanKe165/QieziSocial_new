package com.eggplant.qiezisocial.ui.main

import android.content.Intent
import android.support.v4.content.ContextCompat
import android.support.v7.widget.LinearLayoutManager
import com.eggplant.qiezisocial.R
import com.eggplant.qiezisocial.base.BaseActivity
import com.eggplant.qiezisocial.entry.BoxEntry
import com.eggplant.qiezisocial.ui.main.adapter.HomeAdapter
import com.eggplant.qiezisocial.ui.main.fragment.DividerLinearItemDecoration
import com.eggplant.qiezisocial.utils.BitmapUtils
import com.eggplant.qiezisocial.utils.QRCodeUtil
import com.eggplant.qiezisocial.utils.ScreenUtil
import com.umeng.socialize.ShareAction
import com.umeng.socialize.UMShareAPI
import com.umeng.socialize.bean.SHARE_MEDIA
import com.umeng.socialize.media.UMImage
import kotlinx.android.synthetic.main.activity_invite.*


/**
 * Created by Administrator on 2021/11/30.
 */

class InviteActivity : BaseActivity() {
    lateinit var adapter: HomeAdapter
    var bgs = arrayListOf<Int>(R.drawable.homebg1, R.drawable.homebg2, R.drawable.homebg3, R.drawable.homebg4, R.drawable.homebg5,
            R.drawable.homebg6, R.drawable.homebg7, R.drawable.homebg8, R.drawable.homebg9, R.drawable.homebg10)
    var type = ""
    override fun getLayoutId(): Int {
        return R.layout.activity_invite
    }

    override fun initView() {
        val data = intent.getSerializableExtra("data") as ArrayList<BoxEntry>
        type = intent.getStringExtra("type")
        adapter = HomeAdapter(data)
        invite_ry.layoutManager = LinearLayoutManager(mContext)
        invite_ry.adapter = adapter
        invite_ry.addItemDecoration(DividerLinearItemDecoration(ScreenUtil.dip2px(mContext, 10), ContextCompat.getColor(mContext!!, R.color.translate), 0, 0))
        changeHomeBg()
        val bitmap = QRCodeUtil.createQRCodeBitmap("https://a.app.qq.com/o/simple.jsp?pkgname=com.zhaorenwan.social", ScreenUtil.dip2px(mContext, 96), ScreenUtil.dip2px(mContext, 96))
        invite_img.setImageBitmap(bitmap)
        invite_img.postDelayed({
            inviteImg()
//            finish()
        }, 500)

    }

    private fun inviteImg() {
        val gpbitmap = BitmapUtils.createBitmap(invite_root)
//        val thumbBitmap = BitmapUtils.createBitmap(invite_root)
//        Log.i("inviteAct","gpbit is emtpry ${gpbitmap==null}  ${gpbitmap.width}  ${gpbitmap.height}")
        val resizeBitmap = BitmapUtils.resizeBitmap(gpbitmap, 0.5f)
        val resizethBitmap = BitmapUtils.resizeBitmap(gpbitmap, 0.3f)
//        Log.i("inviteAct","resizeBitmap is emtpry ${resizeBitmap==null}  ${resizeBitmap.width}  ${resizeBitmap.height}")
        val image = UMImage(activity, resizeBitmap)

        //UMLog_Social
        val thumb = UMImage(activity, resizethBitmap)
//        val thumb = UMImage(this, R.mipmap.box_iauncher)
        image.setThumb(thumb)
        image.compressStyle = UMImage.CompressStyle.SCALE//大小压缩，默认为大小压缩，适合普通很大的图
        val shareAction = ShareAction(activity).withText("交个朋友").withMedia(image)
//        Log.i("invite", "invite------type:$type")
        when (type) {
            "qq" -> {
                shareAction.platform = SHARE_MEDIA.QQ
                shareAction.share()
            }
            "pyq" -> {
                shareAction.platform = SHARE_MEDIA.WEIXIN_CIRCLE
                shareAction.share()
                finish()
            }
            "wechat" -> {
                shareAction.platform = SHARE_MEDIA.WEIXIN
                shareAction.share()
                finish()
            }
            "sina" -> {
                shareAction.platform = SHARE_MEDIA.SINA
                shareAction.share()
            }
        }
    }

    override fun initData() {

    }

    override fun initEvent() {

    }

    private fun changeHomeBg() {
        var spaceback = ""
        if (application.infoBean != null) {
            spaceback = application.infoBean!!.spaceback
        }
        if (spaceback.isNotEmpty() && spaceback.toInt() < 10) {
            setBackGround(spaceback.toInt())
        } else {
            setBackGround(0)
        }
    }

    private fun setBackGround(p: Int) {
        invite_root.background = ContextCompat.getDrawable(mContext, R.drawable.home_pub_bg)
        invite_root.background = ContextCompat.getDrawable(mContext, bgs[p])


    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        UMShareAPI.get(this).onActivityResult(requestCode, resultCode, data)
        finish()
    }

    override fun finish() {
        super.finish()
        overridePendingTransition(R.anim.empty_anim, R.anim.close_exit2)
    }
}
