package com.jcodeing.kmedia;

import android.content.Intent;

/**
 * ******************(^_^)***********************<br>
 * User: fee(QQ/WeiXin:1176610771)<br>
 * Date: 2019/4/11<br>
 * Time: 11:15<br>
 * <P>DESC:
 * 带当前播放文件路径信息的播放监听者
 * 或许不是很有意义
 * </p>
 * ******************(^_^)***********************
 */
public abstract class WithMediaDataPathListener implements IPlayer.Listener {
    public String thePlayMediaDataPath;

    @Override
    public void onAdded() {

    }

    /**
     * Some need long term stationed, can be add again when on removed.
     */
    @Override
    public void onRemoved() {

    }

    /**
     * @param intent
     * @return intent != null
     */
    @Override
    public boolean onIntent(Intent intent) {
        return false;
    }


    @Override
    public boolean onPlayProgress(long position, long duration) {
        return false;
    }


    @Override
    public void onPositionUnitProgress(long position, int posUnitIndex, int posUnitState) {

    }


    @Override
    public void onABProgress(long position, long duration, int abState) {

    }

    /**
     * Notification request(by PlayerService and Notifier maintain) <p>If you have a custom
     * condition to control whether or not the request notification -> override</p>
     * <pre>
     * &#64Override
     * public void onNotificationRequired(int order) {
     *    if (request conditions) {
     *      super.onNotificationRequired(order);
     *    } else {
     *      super.onNotificationRequired(0);
     *    }
     * }
     * </pre>
     *
     * @param order <ul> <li>0: stopNotification <li>1: startNotification <li>2:
     *              updateNotification<ul/>
     */
    @Override
    public void onNotificationRequired(int order) {

    }

    @Override
    public boolean onAudioFocusChange(int focusChange) {
        return false;
    }

    @Override
    public void onPrepared() {

    }



    /**
     * 缓冲进度，百分制的0~100
     *
     * @param percent 0~100
     */
    @Override
    public void onBufferingUpdate(int percent) {

    }

    @Override
    public void onSeekComplete() {

    }

    @Override
    public void onVideoSizeChanged(int width, int height, int unappliedRotationDegrees, float pixelWidthHeightRatio) {

    }



    @Override
    public boolean onInfo(int what, int extra) {
        return false;
    }

    /**
     *
     *
     * @param playbackState
     */
    @Override
    public void onStateChanged(int playbackState) {

    }
}
