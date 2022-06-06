package com.eggplant.qiezisocial.utils.mp3;

import android.content.Context;

import com.czt.mp3recorder.Mp3Recorder;
import com.czt.mp3recorder.Mp3RecorderUtil;
import com.eggplant.qiezisocial.utils.FileUtils;

import java.io.File;

import static com.czt.mp3recorder.Mp3Recorder.State.RECORDING;


/**
 * Created by Administrator on 2018/11/29.
 */

public class Mp3RecorderUtils {
    private static Mp3Recorder mRecorder = null;
    private static String audioFilename;
    private static double fianlDuration;


    public interface RecorderCallback {
        void onStop(String filePath, double duraion);

        void onReset();

        void onStart();

        void onRecording(double volume);
    }

    public static void startRecording(Context context, final RecorderCallback callback) {
//        Toast.makeText(this, "开始录音", Toast.LENGTH_SHORT).show();
        if (null == mRecorder) {
            Mp3RecorderUtil.init(context.getApplicationContext(), true);
            mRecorder = new Mp3Recorder();
        }
        audioFilename = FileUtils.getChatFilePath(context) + System.currentTimeMillis() + ".mp3";
        File file = new File(audioFilename);
        mRecorder.setOutputFile(file.getAbsolutePath())
                .setCallback(new Mp3Recorder.Callback() {
                    @Override
                    public void onStart() {
                        callback.onStart();
                    }

                    @Override
                    public void onPause() {

                    }

                    @Override
                    public void onResume() {

                    }

                    @Override
                    public void onStop(int i) {
                        callback.onStop(audioFilename, fianlDuration);
                    }

                    @Override
                    public void onReset() {
                        callback.onReset();
                    }

                    @Override
                    public void onRecording(double duration, double volume) {
                        //已经录制的时间,实时音量分贝值
                        fianlDuration = duration;
                        callback.onRecording(volume);
                    }

                    @Override
                    public void onMaxDurationReached() {

                    }
                });
        try {
            mRecorder.start();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    public static void stopRecording() {

        if (null != mRecorder && mRecorder.getRecorderState() == RECORDING) {
            mRecorder.stop(Mp3Recorder.ACTION_STOP_ONLY);
        }
    }

    public static void cancleRecording() {

        if (null != mRecorder && mRecorder.getRecorderState() == RECORDING) {
            mRecorder.stop(Mp3Recorder.ACTION_STOP_ONLY);
            //TODO delete file
            FileUtils.deleteFile(new File(audioFilename));
        }

    }
}
