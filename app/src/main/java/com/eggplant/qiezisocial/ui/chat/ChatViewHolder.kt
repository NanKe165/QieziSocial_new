package com.eggplant.qiezisocial.ui.chat

import android.view.View
import com.chad.library.adapter.base.BaseViewHolder


import com.eggplant.qiezisocial.R
import com.xiao.nicevideoplayer.NiceVideoPlayer
import com.xiao.nicevideoplayer.TxVideoPlayerController

/**
 * Created by Administrator on 2018/11/29.
 */

class ChatViewHolder(view: View) : BaseViewHolder(view) {
    lateinit var controller: TxVideoPlayerController

    init {
        val videoPlayer: NiceVideoPlayer = getView(R.id.ap_chat_player)
        if (videoPlayer != null) {
            controller = TxVideoPlayerController(view.context)
            videoPlayer!!.controller = controller
        }
    }
}
