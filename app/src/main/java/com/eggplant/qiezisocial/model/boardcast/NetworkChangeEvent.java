package com.eggplant.qiezisocial.model.boardcast;

/**
 * Created by Administrator on 2018/11/14.
 */

public class NetworkChangeEvent {
    public boolean isConnected; //是否存在网络

    public NetworkChangeEvent(boolean isConnected) {
        this.isConnected = isConnected;
    }
}
