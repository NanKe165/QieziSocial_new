package com.eggplant.qiezisocial.utils.mp3;

/**
 * Created by Administrator on 2019/1/7.
 */

public interface RecorderListener {
    void onRecorderStart();

    void onRecording(double volume);

    void onStop(String filePath, double duration);

    void onHideVoiceGp();

    void onShowVoice();

    void onShoCancle();
}
