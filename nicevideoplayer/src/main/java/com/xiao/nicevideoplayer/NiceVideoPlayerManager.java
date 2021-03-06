package com.xiao.nicevideoplayer;

/**
 * Created by XiaoJianjun on 2017/5/5.
 * 视频播放器管理器.
 */
public class NiceVideoPlayerManager {

    private NiceVideoPlayer mVideoPlayer;

    private HomeVideoPlayer mHomeVideoPlayer;

    private NiceVideoPlayerManager() {
    }

    private static NiceVideoPlayerManager sInstance;

    public static synchronized NiceVideoPlayerManager instance() {
        if (sInstance == null) {
            sInstance = new NiceVideoPlayerManager();
        }
        return sInstance;
    }

    public NiceVideoPlayer getCurrentNiceVideoPlayer() {
        return mVideoPlayer;
    }
    public HomeVideoPlayer getHomeNiceVideoPlayer(){
        return mHomeVideoPlayer;
    }

    public void setCurrentNiceVideoPlayer(NiceVideoPlayer videoPlayer) {
        if (mVideoPlayer != videoPlayer) {
            releaseNiceVideoPlayer();
            mVideoPlayer = videoPlayer;
        }
    }
    public void setHomeNiceVideoPlayer(HomeVideoPlayer videoPlayer) {
        if (mHomeVideoPlayer != videoPlayer) {
            releaseHomeVideoPlayer();
            mHomeVideoPlayer = videoPlayer;
        }

    }

    public void initCurrentNiceVideoPlayer(NiceVideoPlayer videoPlayer) {
        if (mVideoPlayer != videoPlayer) {
            if (mVideoPlayer != null) {
                mVideoPlayer.removeTextureView();
                mVideoPlayer = null;
            }
            mVideoPlayer = videoPlayer;
        }
    }



    public void suspendNiceVideoPlayer() {
        if (mVideoPlayer != null && (mVideoPlayer.isPlaying() || mVideoPlayer.isBufferingPlaying())) {
            mVideoPlayer.pause();
        }
    }

    public void resumeNiceVideoPlayer() {
        if (mVideoPlayer != null && (mVideoPlayer.isPaused() || mVideoPlayer.isBufferingPaused())) {
            mVideoPlayer.restart();
        }
    }

    public void releaseNiceVideoPlayer() {
        if (mVideoPlayer != null) {
            mVideoPlayer.release();
            mVideoPlayer = null;
        }
    }

    public void releaseHomeVideoPlayer() {
        if (mHomeVideoPlayer != null) {
            mHomeVideoPlayer.release();
            mHomeVideoPlayer = null;
        }
    }

    public boolean onBackPressd() {
        if (mVideoPlayer != null) {
            if (mVideoPlayer.isFullScreen()) {
                return mVideoPlayer.exitFullScreen();
            } else if (mVideoPlayer.isTinyWindow()) {
                return mVideoPlayer.exitTinyWindow();
            }
        }
        return false;
    }
}
