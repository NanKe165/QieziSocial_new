package com.eggplant.qiezisocial.ui.main

import android.os.Bundle
import android.os.Handler
import android.os.Message
import android.support.v7.app.AppCompatActivity
import android.util.Log
import android.view.SurfaceHolder
import android.view.View
import android.view.WindowManager
import com.aliyun.player.AliPlayer
import com.aliyun.player.AliPlayerFactory
import com.aliyun.player.IPlayer
import com.aliyun.player.source.VidAuth
import com.bumptech.glide.Glide
import com.eggplant.qiezisocial.R
import com.eggplant.qiezisocial.base.BaseActivity
import com.eggplant.qiezisocial.entry.MovieEntry
import com.eggplant.qiezisocial.model.VideoPlayerModel
import com.eggplant.qiezisocial.model.callback.JsonCallback
import com.eggplant.qiezisocial.ui.main.fragment.GroupChatFragment
import com.eggplant.qiezisocial.utils.DateUtils
import com.lzy.okgo.model.Response
import com.mabeijianxi.jianxiexpression.ExpressionGridFragment
import kotlinx.android.synthetic.main.activity_audiolayer.*
import java.lang.ref.WeakReference

/**
 * Created by Administrator on 2021/11/26.
 */

class AudioPlayerActivity : BaseActivity(), ExpressionGridFragment.ExpressionClickListener, ExpressionGridFragment.ExpressionDeleteClickListener, ExpressionGridFragment.ExpressionaddRecentListener {
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
    private var thread: Thread? = null
    private lateinit var handler: Handler
    private val MSG_UPDATA_PROGRESS = 101
    private var gid = 0
    private var maxDura = -1
    private var currentTime = -1
    private var playCompletion=false
    @Volatile
    private var exit = false

    override fun getLayoutId(): Int {
        return R.layout.activity_audiolayer
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
        initHandler()
        //url 方式
        setMovie()

    }

    private fun initHandler() {
        handler = MyHandler()
        thread = Thread(Runnable {
            Log.i("alip", "thread  -- start")
            while (!exit && !activity.isDestroyed) {
                handler.sendEmptyMessage(MSG_UPDATA_PROGRESS)
                Thread.sleep(1000)
                if (!playPasue)
                    currentTime += 1
            }
        })
        thread?.start()
    }


    private fun setMovie() {
        if (model == null)
            model = VideoPlayerModel()
        Log.i("alip", "setmove")
        model!!.getMovie("$gid", object : JsonCallback<MovieEntry>() {
            override fun onSuccess(response: Response<MovieEntry>?) {
                if (response!!.isSuccessful) {
                    Log.i("alip", "onSuccess ${response.body()}")
                    currentMovie = response.body()
                    val urlSource = VidAuth()
                    urlSource.playAuth = currentMovie!!.PlayAuth
                    urlSource.vid = currentMovie!!.VideoMeta.VideoId
                    aliPlayer.setDataSource(urlSource)
                    aliPlayer.prepare()
                    aliPlayer.isLoop = false
                }
            }

            override fun onError(response: Response<MovieEntry>?) {
                super.onError(response)
                Log.i("alip", "onError ${response?.exception}  ${response?.code()}")
            }
        })
    }


    private fun initChatView() {
        var bundle = Bundle()
        bundle.putInt("gid", gid)
        bundle.putString("from", "aplayer")
        val tran = supportFragmentManager.beginTransaction()
        fragment = GroupChatFragment.instanceFragment(bundle)
        tran.add(R.id.aplayer_ft, fragment)
        tran.commit()

    }

    private fun initAliPlayer() {
        aliPlayer = AliPlayerFactory.createAliPlayer(mContext)
        aliPlayer.scaleMode = IPlayer.ScaleMode.SCALE_ASPECT_FILL
        aliPlayer.setOnErrorListener { errorInfo ->
            //此回调会在使用播放器的过程中，出现了任何错误，都会回调此接口。
            val errorCode = errorInfo.code //错误码
            val errorMsg = errorInfo.msg //错误描述
            errorInfo.extra
            Log.i("alip", "error $errorCode  $errorMsg ")
            //出错后需要停止掉播放器
            aliPlayer.stop()
            currentMovie = null
            currentTime = -1
            maxDura = -1
            aplayer_seekbar.progress = 0
        }
        aliPlayer.setOnPreparedListener {
            //一般调用start开始播放视频
            Log.i("alip", "onprepare")
            aliPlayer.start()
            playerState = VIDEO_START
            setMusicInfo()
            maxDura = currentMovie!!.infor.Video.Duration.toInt()
            if (!playCompletion) {
                aliPlayer.seekTo(currentMovie!!.time * 1000L, IPlayer.SeekMode.Accurate)
                currentTime = currentMovie!!.time.toInt()
                //网络请求到的当前播放时间较快，这里减去5s
                currentTime -= 5
            } else {
                currentTime = 0
            }
            playCompletion=false
        }
        aliPlayer.setOnCompletionListener {
            //播放完成通知.   一般调用stop停止播放视频
            Log.i("alip", "stop")
            aliPlayer.stop()
            currentMovie = null
            aplayer_seekbar.progress = 0
            currentTime = -1
            maxDura = -1
            playCompletion=true
            setMovie()
        }
        aliPlayer.setOnStateChangedListener { state ->
            //播放器状态变化通知
            Log.i("alip", "state $state")
            when (state) {
                IPlayer.paused -> {
                    pauseTime = System.currentTimeMillis()
                }
                IPlayer.started -> {
                    if (pauseTime != 0L && System.currentTimeMillis() - pauseTime > 30 * 1000) {
                        aliPlayer.stop()
                        currentMovie = null
                        aplayer_buffering.visibility = View.VISIBLE
                        setMovie()
                    }
                    pauseTime = 0L
                }
            }
        }

        aliPlayer.setOnLoadingStatusListener(object : IPlayer.OnLoadingStatusListener {
            //播放器的加载状态, 网络不佳时，用于展示加载画面。

            override fun onLoadingBegin() {
                aplayer_buffering.visibility = View.VISIBLE
                Log.i("alip", "onLoadingBegin")
                //开始加载。画面和声音不足以播放。
                //一般在此处显示圆形加载
            }

            override fun onLoadingProgress(percent: Int, netSpeed: Float) {
                //加载进度。百分比和网速。
                Log.i("alip", "onLoadingProgress  ${aliPlayer.duration} $percent  $netSpeed")
            }

            override fun onLoadingEnd() {
                aplayer_buffering.visibility = View.GONE
                //结束加载。画面和声音可以播放。
                //一般在此处隐藏圆形加载
                Log.i("alip", "onLoadingEnd")
            }
        })

        aliPlayer.setOnInfoListener { infoBean ->
            //播放器中的一些信息，包括：当前进度、缓存位置等等
            val code = infoBean.code //信息码
            val msg = infoBean.extraMsg//信息内容
            val value = infoBean.extraValue //信息值
//            Log.i("alip","info  $code  $msg $value")
            //当前进度：InfoCode.CurrentPosition
            //当前缓存位置：InfoCode.BufferedPosition
        }

        aplayer_surfaceview.holder.addCallback(object : SurfaceHolder.Callback {
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

    private fun setMusicInfo() {
        aplayer_songname.text = currentMovie!!.VideoMeta.Title
        aplayer_singer.text = currentMovie!!.infor.Video.Tags
        aplayer_time.text = "${DateUtils.videoTime(currentMovie!!.infor.Video.Duration.toInt() * 1000)}"
        Glide.with(mContext).load(currentMovie!!.VideoMeta.CoverURL).into(aplyer_cover)


    }


    override fun initData() {

    }

    override fun initEvent() {
        aplayer_close.setOnClickListener {
            finish()
        }
        aplayer_rootview.setOnClickListener {
            finish()
        }
    }

    inner class MyHandler : Handler() {
        var mWeakReference: WeakReference<AppCompatActivity>? = null

        init {
            mWeakReference = WeakReference(activity)
        }

        override fun handleMessage(msg: Message?) {
            super.handleMessage(msg)
            Log.i("alip", "handlermsg   $maxDura  $currentTime")
            if (!activity.isDestroyed && maxDura != -1 && currentTime != -1) {
                aplayer_seekbar.max = maxDura
                aplayer_seekbar.progress = currentTime
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
        exit = true
        thread?.join()
        handler.removeMessages(MSG_UPDATA_PROGRESS)
        super.onDestroy()
    }

    override fun finish() {
        aliPlayer.stop()
        aliPlayer.release()
        currentTime = -1
        maxDura = -1
        aplayer_shadow.visibility = View.VISIBLE
        super.finish()
        overridePendingTransition(R.anim.close_enter, R.anim.close_exit)
    }
}
