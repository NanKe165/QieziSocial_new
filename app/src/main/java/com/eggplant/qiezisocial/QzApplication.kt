package com.eggplant.qiezisocial

import android.app.Application
import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.PointF
import android.support.multidex.MultiDex
import android.view.View
import android.widget.ImageView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.bumptech.glide.request.RequestOptions
import com.bumptech.glide.request.target.SimpleTarget
import com.bumptech.glide.request.transition.Transition
import com.czt.mp3recorder.Mp3RecorderUtil
import com.eggplant.qiezisocial.entry.*
import com.eggplant.qiezisocial.ui.login.LoginActivity2
import com.eggplant.qiezisocial.utils.CrashHandler
import com.eggplant.qiezisocial.utils.push.PushHelper
import com.eggplant.qiezisocial.widget.ninegridImage.NineGridView
import com.luck.picture.lib.tools.MediaUtils
import com.luck.picture.lib.widget.longimage.ImageSource
import com.luck.picture.lib.widget.longimage.ImageViewState
import com.luck.picture.lib.widget.longimage.SubsamplingScaleImageView
import com.lzy.okgo.OkGo
import com.lzy.okgo.cookie.CookieJarImpl
import com.lzy.okgo.cookie.store.SPCookieStore
import com.lzy.okgo.interceptor.HttpLoggingInterceptor
import com.scwang.smartrefresh.layout.SmartRefreshLayout
import com.scwang.smartrefresh.layout.footer.ClassicsFooter
import com.scwang.smartrefresh.layout.header.ClassicsHeader
import com.umeng.commonsdk.UMConfigure
import com.umeng.commonsdk.utils.UMUtils
import com.umeng.socialize.PlatformConfig
import okhttp3.OkHttpClient
import java.util.concurrent.TimeUnit
import java.util.logging.Level

/**
 * Created by Administrator on 2020/4/9.
 */
class QzApplication : Application() {
    override fun attachBaseContext(base: Context?) {
        super.attachBaseContext(base)
        MultiDex.install(this)
    }

    /**
     * websocket是否登录
     */
    var isWebLogin = false

    //是否登录
    var isLogin = false

    //记录当聊天chatid
    var chatUid: Long = -1
    //记录当前聊天群id
    var chatGsId: Long = -1
    //发送消息唯一id
    var msgUUID: String = "0"
    var scenePosition=0
    //筛选器信息
    var filterData: FilterEntry? = null
    var infoBean: UserEntry? = null
    var loginEntry: LoginEntry? = null
    var emojiList: List<EmojiEntry>? = null
    var qsList: List<SysQuestionEntry>? = null

    companion object {
        //静态代码段可以防止内存泄露
        init {
            //设置全局的Header构建器
            SmartRefreshLayout.setDefaultRefreshHeaderCreator { context, layout ->
                layout.setPrimaryColorsId(R.color.translate)//全局设置主题颜色
                ClassicsHeader(context)//.setTimeFormat(new DynamicTimeFormat("更新于 %s"));//指定为经典Header，默认是 贝塞尔雷达Header
            }
            //设置全局的Footer构建器
            SmartRefreshLayout.setDefaultRefreshFooterCreator { context, _ ->
                //指定为经典Footer，默认是 BallPulseFooter
                ClassicsFooter(context).setDrawableSize(20f)
            }
        }

        //单利application
        private var application: QzApplication? = null
            get() {
                if (field == null) {
                    field = QzApplication()
                }
                return field
            }

        fun get(): QzApplication {
            return application as QzApplication
        }

    }

    fun isLogin(context: Context?): Boolean {
        if (!isLogin || infoBean == null) {
            context?.startActivity(Intent(context, LoginActivity2::class.java).putExtra("from", "app"))
        }
        return isLogin && infoBean != null
    }

    override fun onCreate() {
        super.onCreate()
        val crashHandler = CrashHandler.getInstance()
        crashHandler.init(applicationContext)
        initOkGo()
        Mp3RecorderUtil.init(this, true)
        initNineGridView()
        closeAndroidPDialog()
        initUm()
    }

    private fun initUm() {
        val notice = getSharedPreferences("permissionAgreement", Context.MODE_PRIVATE)
        val isFirst = notice.getBoolean("isFirst", true)
        if (isFirst)
            return
        /**
         *设置组件化的Log开关
         *参数: boolean 默认为false，如需查看LOG设置为true
         */
        UMConfigure.setLogEnabled(true)
//        UMConfigure.preInit(this, UmSetting.appkey, UmSetting.channel)
        //预初始化
        PushHelper.preInit(this)
        // QQ设置
        PlatformConfig.setQQZone("1108880284", "EBbAT0869ltj3wRX")
        PlatformConfig.setQQFileProvider("com.eggplant.qiezisocial.fileprovider")
        //QQ官方sdk授权
//        Tencent.setIsPermissionGranted(true)
        //微信
        PlatformConfig.setWeixin("wxa0bbcce01a1c57f3", "1c1d0137795ee7e595cc2636bd9243db")
        PlatformConfig.setWXFileProvider("com.eggplant.qiezisocial.fileprovider")
        //新浪微博(第三个参数为回调地址)
        PlatformConfig.setSinaWeibo("1975914947", "6ebbb4d78852669a8fcc2d10bf3c0bb2", "http://sns.whalecloud.com/sina2/callback")
        PlatformConfig.setSinaFileProvider("com.eggplant.qiezisocial.fileprovider")

        val isMainProcess = UMUtils.isMainProgress(this)
        if (isMainProcess) {
            //启动优化：建议在子线程中执行初始化
            Thread(Runnable {
                PushHelper.init(applicationContext)
            }).start()
        } else {
            //若不是主进程（":channel"结尾的进程），直接初始化sdk，不可在子线程中执行
            PushHelper.init(applicationContext)
        }

    }

    private fun initNineGridView() {
        NineGridView.setImageLoader(object : NineGridView.ImageLoader {
            override fun onDisplayImage(context: Context, imageView: ImageView, url: String, longImageView: SubsamplingScaleImageView?) {
                var options = RequestOptions()
                        .placeholder(R.drawable.ic_default_color)
                        .error(R.drawable.ic_default_color)
                        .diskCacheStrategy(DiskCacheStrategy.RESOURCE)
                if (url.contains(".gif")) {
                    longImageView?.visibility = View.GONE
                    Glide.with(context)
                            .asGif()
                            .load(url)//
                            .apply(options)
                            .into(imageView)
                } else {
                    options.diskCacheStrategy(DiskCacheStrategy.ALL)
                    Glide.with(context)
                            .asBitmap()
                            .load(url)//
                            .apply(options)
                            .into(object : SimpleTarget<Bitmap>() {
                                override fun onResourceReady(resource: Bitmap, transition: Transition<in Bitmap>?) {
                                    val eqLongImage = MediaUtils.isLongImg(resource.width,
                                            resource.height)
                                    longImageView?.visibility = if (eqLongImage) View.VISIBLE else View.GONE
                                    imageView.visibility = if (eqLongImage) View.GONE else View.VISIBLE
//                                    Log.i("qzapp","islong:$eqLongImage")
                                    if (eqLongImage) {
                                        // 加载长图
                                        longImageView?.isQuickScaleEnabled = true
                                        longImageView?.isZoomEnabled = true
                                        longImageView?.isPanEnabled = true
                                        longImageView?.setDoubleTapZoomDuration(100)
                                        longImageView?.setMinimumScaleType(SubsamplingScaleImageView.SCALE_TYPE_CENTER_CROP)
                                        longImageView?.setDoubleTapZoomDpi(SubsamplingScaleImageView.ZOOM_FOCUS_CENTER)
                                        longImageView?.setImage(ImageSource.bitmap(resource),
                                                ImageViewState(0f, PointF(0f, 0f), 0))
                                    } else {
                                        // 普通图片
                                        imageView.setImageBitmap(resource)
                                    }
                                }
                            })
                }
            }

            override fun getCacheImage(url: String): Bitmap? {
                return null
            }
        })
    }


    private fun initOkGo() {
        val builder = OkHttpClient.Builder()
//        try {
//            val sslParams = HttpsUtils.getSslSocketFactory(assets.open("box.cer"))
//            builder.sslSocketFactory(sslParams.sSLSocketFactory, sslParams.trustManager)
//        } catch (e: IOException) {
//            e.printStackTrace()
//        }

        val loggingInterceptor = HttpLoggingInterceptor("OkGo_social")
        loggingInterceptor.setPrintLevel(HttpLoggingInterceptor.Level.BODY)
        loggingInterceptor.setColorLevel(Level.INFO)
        builder.addInterceptor(loggingInterceptor)
        builder.connectTimeout(1, TimeUnit.MINUTES)
        builder.readTimeout(1, TimeUnit.MINUTES)
        builder.writeTimeout(1, TimeUnit.MINUTES)
        builder.cookieJar(CookieJarImpl(SPCookieStore(this)))
//        builder.protocols(Collections.singletonList(Protocol.HTTP_1_1))
        OkGo.getInstance()
                .init(this).okHttpClient = builder.build()
    }

    /**
     * 关闭androidP 弹窗
     *
     *
     * Android P 后谷歌限制了开发者调用非官方公开API 方法或接口，也就是说，你用反射直接调用源码就会有(Detected problems with API compatibility)提示弹窗出现，
     * 非 SDK 接口指的是 Android 系统内部使用、并未提供在 SDK 中的接口，开发者可能通过 Java 反射、JNI 等技术来调用这些接口。
     */
    private fun closeAndroidPDialog() {
        try {
            val aClass = Class.forName("android.content.pm.PackageParser\$Package")
            val declaredConstructor = aClass.getDeclaredConstructor(String::class.java)
            declaredConstructor.isAccessible = true
        } catch (e: Exception) {
            e.printStackTrace()
        }

        try {
            val cls = Class.forName("android.app.ActivityThread")
            val declaredMethod = cls.getDeclaredMethod("currentActivityThread")
            declaredMethod.isAccessible = true
            val activityThread = declaredMethod.invoke(null)
            val mHiddenApiWarningShown = cls.getDeclaredField("mHiddenApiWarningShown")
            mHiddenApiWarningShown.isAccessible = true
            mHiddenApiWarningShown.setBoolean(activityThread, true)
        } catch (e: Exception) {
            e.printStackTrace()
        }

    }

}