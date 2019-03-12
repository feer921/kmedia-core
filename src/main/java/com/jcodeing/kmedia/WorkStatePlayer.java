package com.jcodeing.kmedia;

import android.content.Context;
import android.net.Uri;
import android.util.Log;

import com.jcodeing.kmedia.definition.PlayException;
import com.jcodeing.kmedia.utils.Assert;

/**
 * ******************(^_^)***********************<br>
 * User: fee(QQ/WeiXin:1176610771)<br>
 * Date: 2019/3/11<br>
 * Time: 20:41<br>
 * <P>DESC:
 * 标记工作状态的播放器
 * </p>
 * ******************(^_^)***********************
 */
public class WorkStatePlayer extends APlayer<WorkStatePlayer>{
    /**
     * 播放完了要循环播放的次数
     * -1:无限循环；0:不循环；1：在播放完第一次的基础上再循环1次.
     */
    private int curLoopTime = 0;
    /**
     * 当前播放的媒体资源文件path
     */
    private String curPlayMediaPath = "";

    public WorkStatePlayer(Context context) {
        super(context);
    }
    /**
     * 是否处理音频焦点改变
     * 如果处理，则父类
     */
    private volatile boolean isHandledAudioFocus = true;

    /**
     * 是否在工作状态
     * 包括：加载中，播放中。。。
     */
    private volatile boolean isWorking = false;


    @Override
    public void onStateChanged(int playbackState) {
        isWorking = !(playbackState == STATE_ENDED);
        super.onStateChanged(playbackState);
    }

    @Override
    public boolean onError(int what, int extra, Exception e) {
        isWorking = false;
        boolean isErrorDueToEmptyMediaFile = EMPTY_MEDIA_PATH.equals(curPlayMediaPath);
        PlayException exception = new PlayException(e);
        exception.setErrCode(extra);
        exception.setErrWhat(what);
        exception.setThePlayMediaFilePath(curPlayMediaPath);
        exception.setExtraInfo(PLAYER_FLAG);
        return isErrorDueToEmptyMediaFile | super.onError(what, extra, exception);//如果这里return true,则不会调用onCompletion()
    }

    @Override
    public int onCompletion() {
        Log.i(TAG, "-->onCompletion() " + this);
        if (curLoopTime == 0) {//播放完了一个有效音频并且只需要播放一次的时候，为了避免当界而从后台再恢复到前台后又自动播放，
            //则这里直接让播放一个空音频来冲掉原来的音频资源
            play(EMPTY_MEDIA_PATH, 0);
        }
        else{
            if (curLoopTime > 0) {
                curLoopTime--;
            }
            seekTo(0);
        }
        return super.onCompletion();
    }

    @Override
    public boolean onAudioFocusChange(int focusChange) {
        //处理音频焦点，是否需要压低音量？？？？？
        return isHandledAudioFocus | super.onAudioFocusChange(focusChange);//同为０时为０，否则为１
    }

    @Override
    public void onBufferingUpdate(int percent) {
        isWorking = percent > 0;
        super.onBufferingUpdate(percent);
    }

    public boolean isWorking() {
        return isWorking;
    }

    public void setHandledAudioFocus(boolean handledAudioFocus) {
        this.isHandledAudioFocus = handledAudioFocus;
    }

    public boolean play(String mediaFilePath, int loopTime) {
        if (Assert.isEmpty(mediaFilePath)) {
            return false;
        }
        if (loopTime < -1) {
            loopTime = 0;
        }
        curLoopTime = loopTime;
        curPlayMediaPath = mediaFilePath;
        return play(Uri.parse(mediaFilePath));
    }

    public String getCurPlayMediaPath() {
        return curPlayMediaPath;
    }

    @Override
    public String toString() {
        return "WorkStatePlayer{" +
                "curLoopTime=" + curLoopTime +
                ", curPlayMediaPath='" + curPlayMediaPath + '\'' +
                ", isHandledAudioFocus=" + isHandledAudioFocus +
                ", isWorking=" + isWorking +
                ",PLAYER_FLAG=" + PLAYER_FLAG +
                '}';
    }

}
