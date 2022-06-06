package com.eggplant.qiezisocial.ui.main.adapter

import android.graphics.drawable.Drawable
import android.support.v7.app.AppCompatActivity
import android.text.TextUtils
import android.view.View
import android.widget.ImageView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.DataSource
import com.bumptech.glide.load.engine.GlideException
import com.bumptech.glide.request.RequestListener
import com.bumptech.glide.request.target.Target
import com.chad.library.adapter.base.BaseMultiItemQuickAdapter
import com.chad.library.adapter.base.BaseViewHolder
import com.eggplant.qiezisocial.R
import com.eggplant.qiezisocial.entry.ChatMultiEntry
import com.eggplant.qiezisocial.greendao.entry.ChatEntry
import com.eggplant.qiezisocial.ui.main.MultMediaActivity
import com.xiao.nicevideoplayer.TxVideoPlayerController
import kotlinx.android.synthetic.main.ap_media_img.view.*
import kotlinx.android.synthetic.main.ap_media_video.view.*

/**
 * Created by Administrator on 2021/5/6.
 */

class MulMediaAdapter(activity: AppCompatActivity, data: List<ChatMultiEntry<ChatEntry>>?) : BaseMultiItemQuickAdapter<ChatMultiEntry<ChatEntry>, BaseViewHolder>(data) {
    var acv = activity
    var firstPosition=0
    var primaryItem: View? = null
    var primaryItemImageView: ImageView? = null

    init {
        addItemType(ChatMultiEntry.CHAT_OTHER, R.layout.ap_media_img)
        addItemType(ChatMultiEntry.CHAT_MINE, R.layout.ap_media_img)
        addItemType(ChatMultiEntry.CHAT_OTHER_VIDEO, R.layout.ap_media_video)
        addItemType(ChatMultiEntry.CHAT_MINE_VIDEO, R.layout.ap_media_video)
    }

    override fun convert(helper: BaseViewHolder, item: ChatMultiEntry<ChatEntry>) {
        helper.itemView.tag = helper.itemViewType
        helper.itemView.setTag(R.id.tag_meida, helper.adapterPosition)


        if (helper.itemViewType == ChatMultiEntry.CHAT_OTHER||helper.itemViewType == ChatMultiEntry.CHAT_MINE) {
            val content = item.bean.content
            if (helper.adapterPosition == firstPosition) {
                Glide.with(mContext)
                        .load(content)
                        .listener(object : RequestListener<Drawable> {
                            override fun onLoadFailed(e: GlideException?, model: Any, target: Target<Drawable>, isFirstResource: Boolean): Boolean {
                                return false
                            }

                            override fun onResourceReady(resource: Drawable, model: Any, target: Target<Drawable>, dataSource: DataSource, isFirstResource: Boolean): Boolean {
                                addPreDrawListener?.invoke(0)
                                return false
                            }
                        })
                        .into(helper.itemView.ap_media_pv)
            } else {
                Glide.with(mContext).load(content).into(helper.itemView.ap_media_pv)
            }
//            helper.itemView.ap_media_pv.onPhotoTapListener = PhotoViewAttacher.OnPhotoTapListener { view, x, y ->
//                Log.i("mulmediaadapter"," OnPhotoTapListener ")
//                (acv as MultMediaActivity).onPhotoTap()
//            }
//            helper.itemView.ap_media_pv.onViewTapListener = PhotoViewAttacher.OnViewTapListener { view, x, y ->
//                Log.i("mulmediaadapter"," OnViewTapListener ")
//                (acv as MultMediaActivity).onViewTap()
//            }
        } else if (helper.itemViewType == ChatMultiEntry.CHAT_OTHER_VIDEO||helper.itemViewType == ChatMultiEntry.CHAT_MINE_VIDEO) {
            val player = helper.itemView.ap_media_player
            helper.itemView.ap_media_close.setOnClickListener {
                (acv as MultMediaActivity).onViewTap()
            }
            val content = item.bean.content
            player.setUp(content, null)
            val controller = TxVideoPlayerController(mContext)
            val extra = item.bean.extra
            if (!TextUtils.isEmpty(extra)) {
                val split = extra?.split("&&".toRegex())?.dropLastWhile({ it.isEmpty() })?.toTypedArray()
                if (split!!.isNotEmpty()) {
                    val img = split[0]
                    if (helper.adapterPosition ==firstPosition) {
                        Glide.with(mContext)
                                .load(img)
                                .listener(object : RequestListener<Drawable> {
                                    override fun onLoadFailed(e: GlideException?, model: Any, target: Target<Drawable>, isFirstResource: Boolean): Boolean {
                                        return false
                                    }

                                    override fun onResourceReady(resource: Drawable, model: Any, target: Target<Drawable>, dataSource: DataSource, isFirstResource: Boolean): Boolean {
                                        addPreDrawListener?.invoke(0)
                                        return false
                                    }
                                })
                                .into(controller.imageView())
                    } else {
                        Glide.with(mContext).load(img).into(controller.imageView())
                    }
                }
            }
            player.controller = controller
            player.start()
        }

    }

    var addPreDrawListener: ((Int) -> Unit)? = null

}
