package com.eggplant.qiezisocial.ui.main

import android.os.Bundle
import android.util.Log
import android.view.SurfaceHolder
import android.view.View
import android.view.WindowManager
import com.aliyun.player.AliPlayer
import com.aliyun.player.AliPlayerFactory
import com.aliyun.player.IPlayer
import com.aliyun.player.source.VidAuth
import com.eggplant.qiezisocial.R
import com.eggplant.qiezisocial.base.BaseActivity
import com.eggplant.qiezisocial.entry.MovieEntry
import com.eggplant.qiezisocial.model.VideoPlayerModel
import com.eggplant.qiezisocial.model.callback.JsonCallback
import com.eggplant.qiezisocial.ui.main.fragment.GroupChatFragment
import com.lzy.okgo.model.Response
import com.mabeijianxi.jianxiexpression.ExpressionGridFragment
import kotlinx.android.synthetic.main.activity_videoplayer.*


/**
 * Created by Administrator on 2021/9/24.
 */

class VideoPlayerActivity : BaseActivity(), ExpressionGridFragment.ExpressionClickListener, ExpressionGridFragment.ExpressionDeleteClickListener, ExpressionGridFragment.ExpressionaddRecentListener {
    override fun expressionClick(str: String?) {
        fragment?.expressionClick(str)
    }

    override fun expressionaddRecent(str: String?) {
        fragment?.expressionaddRecent(str)
    }

    override fun expressionDeleteClick(v: View?) {
        fragment?.expressionDeleteClick(v)
    }

    private lateinit var aliPlayer: AliPlayer
    var fragment: GroupChatFragment? = null
    private var playPasue = false
    private val VIDEO_FINISH = 0
    private val VIDEO_START = 1
    private val VIDEO_PAUSE = 2
    private var playerState = VIDEO_FINISH
    private var pauseTime = 0L
    private var model: VideoPlayerModel? = null
    private var currentMovie: MovieEntry? = null
    var gid = 0
    override fun getLayoutId(): Int {
        return R.layout.activity_videoplayer
    }

    override fun initView() {
        gid = intent.getIntExtra("gid", 0)
        if (gid == 0) {
            finish()
            return
        }
        window.addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON)
        initAliPlayer()
        initChatView()
        //url ??????
        setMovie()

    }

    private fun setMovie() {
        if (model == null)
            model = VideoPlayerModel()
        Log.i("alip", "setmove")
        model!!.getMovie("$gid", object : JsonCallback<MovieEntry>() {
            override fun onSuccess(response: Response<MovieEntry>?) {
                if (response!!.isSuccessful) {
                    Log.i("alip", "onSuccess time:${response.body()?.time}")
                    currentMovie = response.body()
                    val urlSource = VidAuth()
                    urlSource.playAuth = currentMovie!!.PlayAuth
                    urlSource.vid = currentMovie!!.VideoMeta.VideoId
                    aliPlayer.setDataSource(urlSource)
                    aliPlayer.prepare()
                    aliPlayer.isLoop = false
                }
            }
        })
    }


    private fun initChatView() {
        var bundle = Bundle()
        bundle.putInt("gid", gid)
        val tran = supportFragmentManager.beginTransaction()
        fragment = GroupChatFragment.instanceFragment(bundle)
        tran.add(R.id.vplayer_ft, fragment)
        tran.commit()

    }

    private fun initAliPlayer() {
        aliPlayer = AliPlayerFactory.createAliPlayer(mContext)
        aliPlayer.scaleMode = IPlayer.ScaleMode.SCALE_ASPECT_FILL
        aliPlayer.setOnErrorListener { errorInfo ->
            //?????????????????????????????????????????????????????????????????????????????????????????????
            val errorCode = errorInfo.code //?????????
            val errorMsg = errorInfo.msg //????????????
            errorInfo.extra
            Log.i("alip", "error $errorCode  $errorMsg ")
            //?????????????????????????????????
            aliPlayer.stop()
            currentMovie = null
        }
        aliPlayer.setOnPreparedListener {
            //????????????start??????????????????
            Log.i("alip", "onprepare")
            aliPlayer.start()
            playerState = VIDEO_START
            refreshVideoState()
//            vplayer_nick.text = currentMovie!!.videoMeta.title
            setNote()
            if (currentMovie!!.time > 10)
                aliPlayer.seekTo(currentMovie!!.time * 1000L, IPlayer.SeekMode.Accurate)
        }
        aliPlayer.setOnCompletionListener {
            //??????????????????.   ????????????stop??????????????????
            Log.i("alip", "stop")
            aliPlayer.stop()
            currentMovie = null
            setMovie()
        }
        aliPlayer.setOnStateChangedListener { state ->
            //???????????????????????????
            Log.i("alip", "state $state")
            when (state) {
                IPlayer.paused -> {
                    pauseTime = System.currentTimeMillis()
                }
                IPlayer.started -> {
                    if (pauseTime != 0L && System.currentTimeMillis() - pauseTime > 30 * 1000) {
                        aliPlayer.stop()
                        currentMovie = null
                        vplayer_buffering.visibility = View.VISIBLE
                        setMovie()
                    }
                    pauseTime = 0L
                }
            }
        }

        aliPlayer.setOnLoadingStatusListener(object : IPlayer.OnLoadingStatusListener {
            //????????????????????????, ?????????????????????????????????????????????

            override fun onLoadingBegin() {
                vplayer_buffering.visibility = View.VISIBLE
                Log.i("alip", "onLoadingBegin")
                //????????????????????????????????????????????????
                //?????????????????????????????????
            }

            override fun onLoadingProgress(percent: Int, netSpeed: Float) {
                //????????????????????????????????????
                Log.i("alip", "onLoadingProgress  ${aliPlayer.duration} $percent  $netSpeed")
            }

            override fun onLoadingEnd() {
                vplayer_buffering.visibility = View.GONE
                //?????????????????????????????????????????????
                //?????????????????????????????????
                Log.i("alip", "onLoadingEnd")
            }
        })

        aliPlayer.setOnInfoListener { infoBean ->
            //????????????????????????????????????????????????????????????????????????
            val code = infoBean.code //?????????
            val msg = infoBean.extraMsg//????????????
            val value = infoBean.extraValue //?????????
//            Log.i("alip","info  $code  $msg $value")
            //???????????????InfoCode.CurrentPosition
            //?????????????????????InfoCode.BufferedPosition
        }

        vplayer_surfaceview.holder.addCallback(object : SurfaceHolder.Callback {
            override fun surfaceChanged(holder: SurfaceHolder, format: Int, width: Int, height: Int) {
                aliPlayer.surfaceChanged()
            }

            override fun surfaceDestroyed(holder: SurfaceHolder) {
                aliPlayer.setSurface(null)
            }

            override fun surfaceCreated(holder: SurfaceHolder) {
                aliPlayer.setSurface(holder.surface)
            }

        })
    }

    private fun setNote() {
        if (fragment != null && currentMovie != null) {
//            fragment!!.setNewMsg(currentMovie!!.note)
            currentMovie!!.note.trimEnd()
            if (currentMovie!!.note != null) {
                val note = currentMovie!!.note.trimEnd()
                if (note.isNotEmpty())
                    vplayer_qs.text = note
            }

            vplayer_qs_gp.visibility = View.VISIBLE
        } else {
            vplayer_qs_gp.visibility = View.GONE
        }

    }

    private fun refreshVideoState() {
        if (playerState == VIDEO_START) {
            vplayer_func.setImageResource(R.mipmap.icon_vplayer_pause)
        } else {
            vplayer_func.setImageResource(R.mipmap.icon_vplayer_start)
        }
    }

    override fun initData() {

    }

    override fun initEvent() {
        vplayer_close.setOnClickListener {
            finish()
        }
        vplayer_rootview.setOnClickListener {
            finish()
        }
        vplayer_func.setOnClickListener {
            if (playerState == VIDEO_START) {
                aliPlayer.pause()
                playerState = VIDEO_PAUSE
                refreshVideoState()
            } else {
                aliPlayer.start()
                playerState = VIDEO_START
                refreshVideoState()
            }
        }
    }


    override fun onPause() {
        super.onPause()
        playPasue = true
        aliPlayer.pause()
    }

    override fun onResume() {
        super.onResume()
        if (playPasue && playerState == VIDEO_START)
            aliPlayer.start()
        playPasue = false
    }

    override fun onDestroy() {
//        aliPlayer.stop()
//        aliPlayer.release()
        super.onDestroy()
    }

    override fun finish() {
        aliPlayer.stop()
        aliPlayer.release()
        vplayer_shadow.visibility = View.VISIBLE
        super.finish()
        overridePendingTransition(R.anim.close_enter, R.anim.close_exit)
    }

}
