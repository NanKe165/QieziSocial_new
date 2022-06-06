package com.eggplant.qiezisocial.ui.main.fragment;

import android.util.Log;

import com.eggplant.qiezisocial.entry.BaseEntry;
import com.eggplant.qiezisocial.entry.BoxEntry;
import com.eggplant.qiezisocial.entry.SealEvent;
import com.eggplant.qiezisocial.event.MomentCountEvent;
import com.eggplant.qiezisocial.model.HomeModel;
import com.eggplant.qiezisocial.model.callback.JsonCallback;
import com.lzy.okgo.model.Response;

import org.greenrobot.eventbus.EventBus;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Administrator on 2020/9/9.
 */

public class GetHomeDataController {
    //    private HandlerThread handlerThread;
//    private Handler mHandler;
    private List<BoxEntry> datas;
    private HomeModel model;
    //    private final static int GET_HOME_DATA = 101;
    private GetDataSuccessListener listener;
    private int maxSize = 4;
    private String currentScenes = "";
    private String currentSid="";
    private String currentType="0";

    public String getCurrentType() {
        return currentType;
    }

    public void setCurrentType(String currentType) {
        this.currentType = currentType;
    }

    public void setCurrentScenes(String currentScenes) {
        this.currentScenes = currentScenes;
    }

    public void setCurrentSid(String currentSid) {
        this.currentSid = currentSid;
    }

    public String getCurrentScenes() {
        return currentScenes;
    }

    public String getCurrentSid() {
        return currentSid;
    }

    public GetHomeDataController() {
        datas = new ArrayList<>();
        model = new HomeModel();
//        initHandler();

    }

//    private void initHandler() {
//        if (handlerThread == null) {
//            handlerThread = new HandlerThread("getHomeData");
//            handlerThread.start();
//        }
//        if (mHandler == null) {
//            mHandler = new Handler(handlerThread.getLooper()) {
//                @Override
//                public void handleMessage(Message msg) {
//                    super.handleMessage(msg);
//                    Log.i("continueToAddData","TGET_HOME_DATA");
//                    if (msg.what == GET_HOME_DATA) {
//                        getBoxEntryData();
//                    }
//                }
//            };
//            queueMessage(GET_HOME_DATA);
//        }
//    }

    public List<BoxEntry> getData(int size) {
//        if (handlerThread == null || mHandler == null) {
//            initHandler();
//        }
        List<BoxEntry> res = new ArrayList<>();
        if (datas.size() < size || datas.size() < 3) {
//            queueMessage(GET_HOME_DATA);
            if (datas.size()>0&&listener!=null){
                res.addAll(datas);
                datas.clear();
                return res;
            }
            getBoxEntryData(currentScenes,currentSid);

        }
        if (datas.size() < size) {
            Log.i("continueToAddData", "The data size is less than the fetched size");
            res.addAll(datas);
            datas.clear();
            return res;
        } else {
            for (int i = 0; i < size; i++) {
                res.add(datas.get(0));
                datas.remove(0);
            }
            return res;
        }
    }


    public void getData(int size, String scenes, String sid,String type,GetDataSuccessListener listener) {
        begin=0;
        datas.clear();
        currentScenes = scenes;
        currentSid=sid;
        currentType=type;
        maxSize = size;
        this.listener = listener;
        getData(size);
//        if (data.size() > 0) {
//            listener.onSuccess(data);
//        } else {
//
//        }
    }

//    private void queueMessage(int msgWhat, Bundle obj) {
//        if (mHandler == null) {
//            Log.i("continueToAddData","queuemessage  :  mHandler is null");
//            return;
//        }
//        Message message = mHandler.obtainMessage();
//        message.what = msgWhat;
//        message.obj = obj;
//        mHandler.sendMessage(message);
//    }
//
//    private void queueMessage(int msgWhat) {
//        queueMessage(msgWhat, null);
//    }

    private int begin = 0;

    private void getBoxEntryData(String scens,String sid) {
        Log.i("continueToAddData", "getboxEntryData: begin" + begin);
        model.getBoxInfo(begin, scens,sid ,new JsonCallback<BaseEntry<BoxEntry>>() {
                    @Override
                    public void onSuccess(Response<BaseEntry<BoxEntry>> response) {
                        if (response.isSuccessful()) {
                            List<BoxEntry> list = response.body().getList();
                            Integer momentcount = response.body().getMomentcount();
                            String msg = response.body().getMsg();
                            if (msg!=null&&msg.equals("你被封号了")){
                                EventBus.getDefault().post(new SealEvent());
                                return;
                            }
                            Log.i("continueToAddData", "momentcount: " + momentcount);
                            if (momentcount!=null){
                                EventBus.getDefault().post(new MomentCountEvent(momentcount));
                            }
                            if (list != null && list.size() > 0) {
                                if (listener != null) {
                                    datas.addAll(list);
                                    listener.onSuccess(getData(maxSize));
                                    listener = null;
                                } else {
                                    datas.addAll(list);
                                }
                                begin += list.size();
                            } else {
                                begin = 0;
//                                queueMessage(GET_HOME_DATA);
                            }
                        }
                    }
                }
        );
    }

    public void desroyController() {
//        handlerThread.quit();
//        mHandler = null;
//        handlerThread = null;
    }

    public interface GetDataSuccessListener {
        void onSuccess(List<BoxEntry> data);
    }
}
