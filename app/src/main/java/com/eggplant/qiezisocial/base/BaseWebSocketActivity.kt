package com.eggplant.qiezisocial.base

import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.ServiceConnection
import android.os.Bundle
import android.os.IBinder
import android.util.Log
import com.eggplant.qiezisocial.event.LogoutEvent
import com.eggplant.qiezisocial.socket.AbsBaseWebSocketService
import com.eggplant.qiezisocial.socket.IWebSocket
import com.eggplant.qiezisocial.socket.event.CommonResponse
import com.eggplant.qiezisocial.socket.event.WebSocketConnectedEvent
import com.eggplant.qiezisocial.socket.event.WebSocketSendDataErrorEvent
import org.greenrobot.eventbus.Subscribe
import org.greenrobot.eventbus.ThreadMode
import org.json.JSONException
import org.json.JSONObject
import java.util.*

/**
 * Created by Administrator on 2019/1/18.
 */

abstract class BaseWebSocketActivity<T :BasePresenter<*>> : BaseMvpActivity<T>() {
    /**
     * 服务重连次数
     * 这里指的是绑定websocket 服务失败时使用的重连次数，一般来说不会出现绑定失败的情况
     */
    private val RECONNECT_TIME = 5

    protected var webSocketService: IWebSocket? = null
        private set
    /**
     * 连接时机
     * 0-刚进入界面，如果websocket还未连接，会继续连接，或者由于某些原因websocket 断开，会自动连接，从而出发连接/失败事件
     * 1-onResume()方法回调判断websocket是否连接，如果未连接，则进行连接，从而触发连接成功/失败事件
     * 2-sendText()方法会判断websocket是否已经连接，如果未连接，则进行连接，从而出发连接成功/失败事件，此时连接成功后应继续调用sendText()方法发送数据
     *
     *
     * 另外，当connectType!=0时，每次使用完之后应设置为0.    因为0的状态是无法预知的，随时可能调用
     *
     */
    private var connectType = 0
    /**
     * 需要发送的数据，当connectType==2 时会使用
     */
    private val needSendText = ArrayList<String>()

    private var isConnected = false
    private val networkReceiverIsRegister = false
    private var connectTime = 0

    protected var mWebSocketServiceConnection: ServiceConnection = object : ServiceConnection {
        override fun onServiceConnected(name: ComponentName, service: IBinder) {
            webSocketService = (service as AbsBaseWebSocketService.ServiceBinder).service
            //此处假设要不就已经连接，要不就未连接，未连接就等着接收连接成功/失败的广播即可
            if (webSocketService!!.getConnectStatus() == 2) {
                onServiceBindSuccess()
            } else {
                if (webSocketService!!.getConnectStatus() == 0) {
                    Log.e("socket_reconnect", "onServiceConnected: websocketServer status==0 ")
                    webSocketService!!.reconnect()
                }
            }
        }

        override fun onServiceDisconnected(name: ComponentName) {
            webSocketService = null
            if (connectTime < RECONNECT_TIME) {
                bindWebSocketService()
            }
        }
    }

    //获取 WebSocketService 类，这里传入 WebSocketService.class 既可
    protected abstract val webSocketClass: Class<out AbsBaseWebSocketService>
    override fun onRestoreInstanceState(savedInstanceState: Bundle?) {
        super.onRestoreInstanceState(savedInstanceState)
        bindWebSocketService()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        initBind()
    }

    protected fun initBind() {
        bindWebSocketService()
    }

    /**
     * 从后台返回时，判断服务是否已断开
     * 断开则调用reconnect方法重连
     */
    override fun onResume() {
        super.onResume()
        if (webSocketService != null && webSocketService!!.getConnectStatus() != 2) {
            if (webSocketService!!.getConnectStatus() == 0) {
                Log.e("socket_reconnect", " activity_onResume: websocketServer status==0 ")
                webSocketService!!.reconnect()
            } else {
                Log.e(TAG, "onResume()->WebSocket 正在连接")
            }
            connectType = 1
        }
    }

    protected fun bindWebSocketService() {
        val intent = Intent(this, webSocketClass)
        bindService(intent, mWebSocketServiceConnection, Context.BIND_AUTO_CREATE)
        connectTime++
    }

    //当有接收到数据时会回调此方法
    protected abstract fun onCommonResponse(response: CommonResponse)

    //当有发送数据失败时会回调此方法
    protected abstract fun onErrorResponse(response: WebSocketSendDataErrorEvent)

    /**
     * 连接失败
     */
    protected fun onConnectFailed() {

    }

    /**
     * 服务绑定成功后回调该方法，可以在此方法中加载一些初始化数据
     */
    open protected fun onServiceBindSuccess() {

    }

    /**
     * 发送数据包
     *
     * @param text
     */
    protected fun sendText(text: String) {
        if (webSocketService != null && webSocketService!!.getConnectStatus() == 2) {
            webSocketService!!.sendText(text)
        } else {
            connectType = 2
            needSendText.add(text)
            if (webSocketService != null && webSocketService!!.getConnectStatus() == 0) {
                Log.e("socket_reconnect", "sentText: websocketServer status==0 ")
                webSocketService!!.reconnect()
            }
        }
    }

    fun sendSocketMessage(msg: String): Boolean {
        return if (webSocketService != null && webSocketService!!.getConnectStatus() == 2) {
            webSocketService!!.sendText(msg)
            true
        } else {
            false
        }
    }

    /**
     * 连接成功
     */
    @Subscribe(threadMode = ThreadMode.MAIN)
    fun onEventMainThread(event: WebSocketConnectedEvent) {
        isConnected = true
        connectType = 2
        //        Log.e(TAG, "onEventMainThread:   type:" + connectType + " needSendText:" + needSendText);
        if (connectType == 2 && needSendText.size > 0) {
            for (i in needSendText.indices) {
                sendText(needSendText[i])
                needSendText.removeAt(i)
            }
        } else if (connectType == 0) {
            onServiceBindSuccess()
        }
        connectType = 0
    }

    protected fun reconnected() {
        webSocketService!!.reconnect()
    }

    fun stopSocket() {
        if (webSocketService != null) {
            webSocketService!!.stop()
        }
    }


    /**
     * 接收消息
     *
     * @param event
     */
    @Subscribe(threadMode = ThreadMode.MAIN)
    fun onEventMainThread(event: CommonResponse) {
        onCommonResponse(event)
    }

    /**
     * 发送数据失败或者数据返回不合规（code >=2000等）
     */
    @Subscribe(threadMode = ThreadMode.MAIN)
    fun onEventMainThread(event: WebSocketSendDataErrorEvent) {
        onErrorResponse(event)
    }

    override fun onDestroy() {
        unbindService(mWebSocketServiceConnection)
        super.onDestroy()
    }

    override fun logout(event: LogoutEvent) {
        val `object` = JSONObject()
        try {
            `object`.put("type", "system")
            `object`.put("act", "glogout")
            `object`.put("created", System.currentTimeMillis())
        } catch (e: JSONException) {
            e.printStackTrace()
        }

        sendSocketMessage(`object`.toString())

        stopSocket()
        super.logout(event)
    }

    fun ping() {
        val heartJb = JSONObject()
        try {
            heartJb.put("type", "system")
            heartJb.put("act", "gping")
            heartJb.put("created", System.currentTimeMillis())
            sendText(heartJb.toString())
        } catch (e: JSONException) {
            e.printStackTrace()
        }

    }

    companion object {
        private val TAG = "BaseWebSocketActivity"
    }
}
