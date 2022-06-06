package com.eggplant.qiezisocial.socket;

import android.app.Service;
import android.content.Intent;
import android.os.Binder;
import android.os.Handler;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.util.Log;

import com.eggplant.qiezisocial.QzApplication;
import com.eggplant.qiezisocial.socket.event.CustomPingEvent;
import com.eggplant.qiezisocial.socket.event.DisconnectedEvent;
import com.eggplant.qiezisocial.socket.event.WebSocketConnectedEvent;
import com.eggplant.qiezisocial.socket.event.WebSocketConnectionErrorEvent;
import com.eggplant.qiezisocial.socket.event.WebSocketSendDataErrorEvent;
import com.neovisionaries.ws.client.HostnameUnverifiedException;
import com.neovisionaries.ws.client.OpeningHandshakeException;
import com.neovisionaries.ws.client.ProxySettings;
import com.neovisionaries.ws.client.WebSocket;
import com.neovisionaries.ws.client.WebSocketAdapter;
import com.neovisionaries.ws.client.WebSocketException;
import com.neovisionaries.ws.client.WebSocketFactory;
import com.neovisionaries.ws.client.WebSocketFrame;
import com.neovisionaries.ws.client.WebSocketState;

import org.greenrobot.eventbus.EventBus;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.security.KeyManagementException;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.security.cert.X509Certificate;
import java.util.List;
import java.util.Map;

import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSocketFactory;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;

/**
 * Created by Administrator on 2018/11/21.
 * 为了降低代码的耦合度，将与业务逻辑相关的代码（接口地址、数据处理及分发等）与 WebSocket 的连接、发送数据等操作剥离开来
 * 本类用来实现与业务逻辑无关的代码。
 */

public abstract class AbsBaseWebSocketService extends Service implements IWebSocket {
    private static final String TAG = "AbsBaseWebSocketService";
    private static final int TIME_OUT = 15000;
    private static WebSocketFactory factory = new WebSocketFactory().setConnectionTimeout(TIME_OUT);

    private WebSocket webSocket;
    private WebSocketThread webSocketThread;

    private AbsBaseWebSocketService.ServiceBinder serviceBinder = new ServiceBinder();
    private WebSocketAdapter webSocketAdapter;

    public class ServiceBinder extends Binder {
        public AbsBaseWebSocketService getService() {
            return AbsBaseWebSocketService.this;
        }
    }

    private boolean stop = false;
    /**
     * 0-未连接
     * 1-正在连接
     * 2-已连接
     */
    private int connectStatus = 0;//是否已连接

    @Override
    public void onCreate() {
        super.onCreate();
//        Log.d(TAG, "onCreate()");
        ProxySettings proxySettings = factory.getProxySettings();
        proxySettings.addHeader("Content-Type", "text/json");
        proxySettings.setSSLSocketFactory(getSSLSupport());
        connectStatus = 0;
        if (webSocketThread != null && !webSocketThread.isAlive()) {
            webSocketThread.interrupt();
            mHandler.removeCallbacks(heartBeatRunnable);
        }
        webSocketThread = new WebSocketThread();
        webSocketThread.start();

    }

    /**
     * websocket支持wss
     *
     * @return
     */
    private SSLSocketFactory getSSLSupport() {
        SSLContext sslContext = null;
        try {
            sslContext = SSLContext.getInstance("TLS");
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
        try {
            sslContext.init(null, new TrustManager[]{
                    new X509TrustManager() {

                        @Override
                        public void checkClientTrusted(X509Certificate[] chain, String authType) {

                        }

                        @Override
                        public void checkServerTrusted(X509Certificate[] chain, String authType) {

                        }

                        @Override
                        public X509Certificate[] getAcceptedIssuers() {
                            return new X509Certificate[0];
                        }
                    }
            }, new SecureRandom());
        } catch (KeyManagementException e) {
            e.printStackTrace();
        }
        SSLSocketFactory factory = sslContext.getSocketFactory();
        return factory;
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        if (serviceBinder == null) {
            serviceBinder = new ServiceBinder();
        }
        return serviceBinder;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        stop = true;
        if (webSocket != null) {
            webSocket.disconnect();
            webSocket.flush();
            webSocket = null;
        }
        connectStatus = 0;
        QzApplication.Companion.get().setWebLogin(false);
    }

    /**
     * 获取服务器地址
     *
     * @return
     */
    protected abstract String getConnectUrl();

    /**
     * 分发响应数据
     *
     * @param textResponse
     */
    protected abstract void dispathResponse(String textResponse);

    /**
     * 连接成功发送websocketConnectEvent事件
     * 请求成功发送 CommonResponse事件
     * 请求失败发送 WebsocketSendDataErrorEvent事件
     */
    private class WebSocketThread extends Thread {
        @Override
        public void run() {
            super.run();
            setupWebSocket();
        }
    }

    private void setupWebSocket() {
        if (connectStatus != 0)
            return;
        connectStatus = 1;
        try {
//            if (webSocket == null) {
            webSocket = factory.createSocket(getConnectUrl());
//            }
            if (webSocketAdapter == null)
                webSocketAdapter = new WebSocketAdapter() {
                    @Override
                    public void onTextMessage(WebSocket websocket, String text) throws Exception {
                        super.onTextMessage(websocket, text);
                        dispathResponse(text);
                    }

                    @Override
                    public void onTextMessageError(WebSocket websocket, WebSocketException cause, byte[] data) throws Exception {
                        super.onTextMessageError(websocket, cause, data);
                        EventBus.getDefault().post(new WebSocketSendDataErrorEvent("", "onTextMessageError:" + cause.toString()));
                    }

                    @Override
                    public void onDisconnected(WebSocket websocket, WebSocketFrame serverCloseFrame, WebSocketFrame clientCloseFrame, boolean closedByServer) throws Exception {
                        super.onDisconnected(websocket, serverCloseFrame, clientCloseFrame, closedByServer);
                        EventBus.getDefault().post(new DisconnectedEvent());
                        QzApplication.Companion.get().setWebLogin(false);
                        connectStatus = 0;
                        Log.e(TAG, "onDisconnected: " + connectStatus);
                        if (!stop) {
                            mHandler.removeCallbacks(heartBeatRunnable);
                            setupWebSocket();
                        }
                    }

                    @Override
                    public void onConnected(WebSocket websocket, Map<String, List<String>> headers) throws Exception {
                        super.onConnected(websocket, headers);
                        connectStatus = 2;
                        Log.e(TAG, "onConnected: " + connectStatus);
                        EventBus.getDefault().post(new WebSocketConnectedEvent());
                    }

                    @Override
                    public void onError(WebSocket websocket, WebSocketException cause) throws Exception {
                        super.onError(websocket, cause);
                        Log.e(TAG, "onError: " + cause.getMessage());
                        EventBus.getDefault().post(new WebSocketConnectionErrorEvent("onError:" + cause.getMessage()));
                    }

                    @Override
                    public void onStateChanged(WebSocket websocket, WebSocketState newState) throws Exception {
                        super.onStateChanged(websocket, newState);
                        Log.e(TAG, "onStateChanged: " + newState);
                    }

                    @Override
                    public void onConnectError(WebSocket websocket, WebSocketException exception) throws Exception {
                        super.onConnectError(websocket, exception);
                        Log.e(TAG, "onConnectError: " + exception.toString());
                    }
                };
            webSocket.addListener(webSocketAdapter);

            try {
                webSocket.connect();
                mHandler.postDelayed(heartBeatRunnable, HEART_BEAT_RATE);//开启心跳检测
            } catch (NullPointerException e) {
                connectStatus = 0;
//                Log.i(TAG, String.format("NullPointerException()->%s", e.getMessage()));
//                Log.e(TAG, "NullPointerException()", e);
                EventBus.getDefault().post(new WebSocketConnectionErrorEvent("NullPointerException:" + e.getMessage()));
            } catch (OpeningHandshakeException e) {
                connectStatus = 0;
//                Log.i(TAG, String.format("OpeningHandshakeException()->%s", e.getMessage()));
//                Log.e(TAG, "OpeningHandshakeException()", e);
//                StatusLine sl = e.getStatusLine();
//                Log.i(TAG, "=== Status Line ===");
//                Log.e(TAG, "=== Status Line ===");
//                Log.i(TAG, String.format("HTTP Version  = %s\n", sl.getHttpVersion()));
//                Log.e(TAG, String.format("HTTP Version  = %s\n", sl.getHttpVersion()));
//                Log.i(TAG, String.format("Status Code   = %s\n", sl.getStatusCode()));
//                Log.e(TAG, String.format("Status Code   = %s\n", sl.getStatusCode()));
//                Log.i(TAG, String.format("Reason Phrase = %s\n", sl.getReasonPhrase()));
//                Log.e(TAG, String.format("Reason Phrase = %s\n", sl.getReasonPhrase()));
//
//                Map<String, List<String>> headers = e.getHeaders();
//                Log.i(TAG, "=== HTTP Headers ===");
//                Log.e(TAG, "=== HTTP Headers ===");
//                for (Map.Entry<String, List<String>> entry : headers.entrySet()) {
//                    // Header name.
//                    String name = entry.getKey();
//
//                    // Values of the header.
//                    List<String> values = entry.getValue();
//
//                    if (values == null || values.size() == 0) {
//                        // Print the name only.
//                        System.out.println(name);
//                        continue;
//                    }
//
//                    for (String value : values) {
//                        // Print the name and the value.
//                        Log.e(TAG, String.format("%s: %s\n", name, value));
//                        Log.i(TAG, String.format("%s: %s\n", name, value));
//                    }
//                }
                EventBus.getDefault().post(new WebSocketConnectionErrorEvent("OpeningHandshakeException:" + e.getMessage()));
            } catch (HostnameUnverifiedException e) {
                connectStatus = 0;
                // The certificate of the peer does not match the expected hostname.
                EventBus.getDefault().post(new WebSocketConnectionErrorEvent("HostnameUnverifiedException:" + e.getMessage()));
            } catch (WebSocketException e) {
                connectStatus = 0;
                // Failed to establish a WebSocket connection.
                EventBus.getDefault().post(new WebSocketConnectionErrorEvent("WebSocketException:" + e.getMessage()));
            }
        } catch (IOException e) {
            connectStatus = 0;
            EventBus.getDefault().post(new WebSocketConnectionErrorEvent("IOException:" + e.getMessage()));
        }
    }


    private long sendTime = 0L;
    private final long HEART_BEAT_RATE = 20 * 1000;//每隔20秒进行一次长连接的心跳检测
    private Handler mHandler = new Handler();
    private Runnable heartBeatRunnable = new Runnable() {
        @Override
        public void run() {
            if (System.currentTimeMillis() - sendTime > HEART_BEAT_RATE) {
                JSONObject heartJb = new JSONObject();
                try {
                    heartJb.put("type", "system");
                    heartJb.put("act", "gping");
                    heartJb.put("created", System.currentTimeMillis());
                    sendText(heartJb.toString());
                    EventBus.getDefault().post(new CustomPingEvent());
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                sendTime = System.currentTimeMillis();
            }
            mHandler.postDelayed(this, HEART_BEAT_RATE);//每隔一定的时间，对长连接进行一次心跳检测
        }
    };

    @Override
    public void sendText(String text) {
        if (TextUtils.isEmpty(text)) return;
        if (webSocket != null && connectStatus == 2) {
            webSocket.sendText(text);
        }
    }

    @Override
    public int getConnectStatus() {
        return connectStatus;
    }

    @Override
    public void reconnect() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    Thread.sleep(2000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                if (webSocketThread != null && !webSocketThread.isAlive()) {
                    connectStatus = 0;
                    if (webSocketAdapter != null) {
                        webSocket.removeListener(webSocketAdapter);
                    }
                    webSocketThread.interrupt();
                    mHandler.removeCallbacks(heartBeatRunnable);
                    webSocket.disconnect();
                    webSocketThread = new WebSocketThread();
                    webSocketThread.start();
                } else {
                    Log.e(TAG, "reconnect()->start failed: webSocketThread==null || webSocketThread.isAlive()");
                }
            }
        }).start();

    }

    @Override
    public void stop() {
        stop = true;
        if (webSocketThread != null && !webSocketThread.isAlive()) {
            webSocketThread.interrupt();
            mHandler.removeCallbacks(heartBeatRunnable);
        }
        if (webSocketAdapter != null) {
            webSocket.removeListener(webSocketAdapter);
        }
        webSocket.disconnect();

    }



}
