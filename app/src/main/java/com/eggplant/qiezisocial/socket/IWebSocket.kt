package com.eggplant.qiezisocial.socket

/**Created by Administrator on 2018/11/21.
 * 创建一个 WebSocket 的接口
 * 定义了 WebSocket 必须提供的几个公开方法
 */

open interface IWebSocket {
    /**
     * 发送数据
     * @param text 需要发送的数据
     */
     fun sendText(text: String)

    /**
     * 0-未连接
     * 1-正在连接
     * 2-已连接
     * @return
     */
     fun getConnectStatus(): Int

    /**
     * 重新连接
     */
     fun reconnect()

    /**
     * 关闭连接
     */
     fun stop()


}
