/*
 * Copyright (c) 2017 K Sun <jcodeing@gmail.com>
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.jcodeing.kmedia;

import android.Manifest.permission;
import android.content.Intent;
import android.net.Uri;

import com.jcodeing.kmedia.assist.AudioMgrHelper;
import com.jcodeing.kmedia.assist.C;
import com.jcodeing.kmedia.definition.IMediaItem;
import com.jcodeing.kmedia.definition.IMediaQueue;
import com.jcodeing.kmedia.definition.IPositionUnitList;

import java.util.ArrayList;

import androidx.annotation.RequiresPermission;

/**
 * Interface Player
 * 与播放功能相关的间接接口,这些接口方法实现还是调用IMediaPlayer的接口方法
 *
 * @param <P> subClass
 * @see Player
 */
public interface IPlayer<P extends IPlayer> extends IPlayerBase {
    // ============================@Player
    /**
     * added by fee 2019-03-13 扩展框架功能
     * 以下为播放器当前所处在的工作流状态
     */

    /**
     * 播放器工作流：none
     */
    int WORK_FLOW_NONE = 0xf0;

    /**
     * 播放器工作流：设置、初始化(加载资源)
     */
    int WORK_FLOW_SETTING_INIT = WORK_FLOW_NONE + 1;

    /**
     * 播放器工作流：正在缓冲
     */
    int WORK_FLOW_BUFFERING = WORK_FLOW_SETTING_INIT + 1;


    /**
     * 播放器工作流：已加载准备好
     */
    int WORK_FLOW_PREPARED = WORK_FLOW_BUFFERING + 1;
    /**
     * 播放器工作流：被主动暂停了;这个状态下，需要用户主动播放(继续播放)
     */
    int WORK_FLOW_PAUSED_ACTIVE = WORK_FLOW_PREPARED + 1;

    /**
     * 播放器工作流：自动暂停
     */
    int WORK_FLOW_PAUSED_AUTO = WORK_FLOW_PAUSED_ACTIVE + 1;

    /**
     * 播放器工作流：主动播放
     */
    int WORK_FLOW_PLAY_ACTIVE = WORK_FLOW_PAUSED_AUTO + 1;

    /**
     * 播放器工作流：自动播放
     */
    int WORK_FLOW_PLAY_AUTO = WORK_FLOW_PLAY_ACTIVE + 1;

    /**
     * 播放器工作流：停止stop
     */
    int WORK_FLOW_STOP = WORK_FLOW_PLAY_AUTO + 1;

    int WORK_FLOW_PSUSED_INNER_RESULT_IN = WORK_FLOW_STOP + 1;

    /**
     * Get a internal mediaPlayer
     * <pre>
     *   Player run core by internal maintaining mediaPlayer.
     *   In general is not recommended unless you want to operate some special functions.
     *   * note that internalPlayer and Player
     *   * at the same time operating possible when the incompatibilities
     * </pre>
     */
    IMediaPlayer internalPlayer();

    /**
     * Init internal media player
     */
    P init(IMediaPlayer mediaPlayer);

    boolean prepare(Uri uri);

    boolean prepare(IMediaItem mediaItem);

    boolean prepareMediaId(String mediaId);

    /**
     * 暂停(是否主动暂停)
     * @param isActive 是否用户主动暂停
     * @return true: opt ok; false: opt not
     */
    boolean pause(boolean isActive);
    boolean play();

    /**
     * 播放(是否主动播放)
     * @param isActive 是否用户主动播放(继续播放)
     * @return true: opt ok; false: opt not
     */
    boolean play(boolean isActive);

    boolean play(Uri uri);

    boolean play(IMediaItem mediaItem);

    boolean playMediaId(String mediaId);

    /**
     * Shut down player (release all resources)
     */
    void shutdown();

    // ============================@Control

    /**
     * Support custom processing level of seekTo
     *
     * @param processingLevel The greater the level with processing the more things<ul> <li>1:
     *                        Callback {@link Listener#onPlayProgress(long, long)}</li> <li>2: Call {@link
     *                        #calibrateCurrentPositionUnitIndex(long)} </li></ul>
     */
    boolean seekTo(long ms, int processingLevel);

    /**
     * internal call {@link #seekTo(long, int)} Max processing level
     */
    boolean seekTo(long ms);

    /**
     * @param ms fastForwardMs/RewindMs <ul> <li>+ms: fastForward <p /> .fastForwardRewind(1000)->fast
     *           forward 1 second <li>-ms: RewindMs <p /> .fastForwardRewind(-1000)->rewind 1 second<ul/>
     */
    boolean fastForwardRewind(long ms);

    /**
     * Generally used in front of the seekTo...(.) to ensure that seek complete to auto play
     * immediately after
     * <pre>
     *   *{@link #start()} method with shouldAutoPlayWhenSeekComplete(true)
     *   *{@link #pause()} with shouldAutoPlayWhenSeekComplete(false)
     * </pre>
     */
    P shouldAutoPlayWhenSeekComplete(boolean shouldAutoPlayWhenSeekComplete);

    /**
     * set a pending seekTo in before start() dispose
     */
    void seekToPending(long ms);

    /**
     * Seeks to specified progress
     *
     * @param progress    current progress
     * @param progressMax the upper limit of this progress range.
     * @return seekTo position (>=0), or -1 if seekTo Failure.
     */
    long seekToProgress(int progress, int progressMax);

    // ============================@Set/Get/Is

    /**
     * Sets the updatePlayProgress delay. <p /> More accurate to handle progress type work. <ul>
     * <li>{@link #setAB(long, long)} <li>{@link #setABLoop(int, int)} <li>{@link #setAB(long, long,
     * int, int)} <li>{@link #setEnabledPositionUnitLoop(boolean, int, int)} <li>{@link
     * Listener#onPlayProgress(long, long)} <li>{@link Listener#onABProgress(long, long, int)}
     * <li>{@link Listener#onPositionUnitProgress(long, int, int)} <li>...... <ul/>
     *
     * @param updatePlayProgressDelayMs value>=0?value:default[1000 - (position % 1000)]
     */
    P setUpdatePlayProgressDelayMs(long updatePlayProgressDelayMs);

    /**
     * Returns current media id (custom or default[string representation of this play uri])
     */
    String getCurrentMediaId();

    /**
     * Set enabled wifi lock, to hold a Wifi lock, which prevents the player from going to sleep while
     * the media is playing.
     * <p>
     * <p /> RequiresPermission(Manifest.permission.WAKE_LOCK) <p /> AndroidManifest.xml add below
     * <pre>
     * &#60!--Using a WifiLock For KMedia Player-->
     * &#60uses-permission android:name="android.permission.WAKE_LOCK"&#47>
     * </pre>
     */
    @RequiresPermission(permission.WAKE_LOCK)
    P setEnabledWifiLock(boolean enabled);

    P setEnabledAudioFocusManage(boolean enabled);

    AudioMgrHelper getAudioMgrHelper();
    // ============================@PositionUnit

    /**
     * WARNING: must ensure {@link IPositionUnitList#getMediaId()} equals {@link #getCurrentMediaId()}
     * can work normally
     *
     * @param posUnitList [Position Unit] List
     */
    P setPositionUnitList(IPositionUnitList posUnitList);

    /**
     * @return current position unit index OR -1(unset | waiting state)
     */
    int getCurrentPositionUnitIndex();

    /**
     * generally not use(player inside Will automatically maintain current [position unit] index)
     * <p>If you want to calibrate,can call {@link #calibrateCurrentPositionUnitIndex(long)}<p/>
     */
    void setCurrentPositionUnitIndex(int posUnitIndex);

    /**
     * Seeks to specified PositionUnitIndex
     *
     * @return seekTo position (>=0), or -1 if seekTo Failure.
     */
    long seekToPositionUnitIndex(int posUnitIndex);

    /**
     * generally not use(player inside Will automatically maintain current [position unit] index
     * calibrate)
     *
     * @param position currentPosition or -1[position < 0 || position > getDuration()] (auto
     *                 getCurrentPosition())
     * @return calibrated PositionUnitIndex or -1(not calibrate or calibrate result not present)
     */
    int calibrateCurrentPositionUnitIndex(long position);

    // =========@Loop

    /**
     * Set the enabled,loopMode,loopInterval of this position unit loop, with reset position unit
     * looped count tag <p /> *param loopMode,loopInterval can use {@link C#PARAM_ORIGINAL} keeping
     * the original values <p /> *param loopMode,loopInterval can use {@link C#PARAM_UNSET}/{@link
     * C#PARAM_RESET} unset/reset values
     *
     * @param enabled      True [position unit] loop is enabled, false otherwise.
     * @param loopMode     [Position unit] loop mode <ul> <li>-1 disable  <li>-8 infinity loop  <li>0++
     *                     specified loop<ul/>
     * @param loopInterval [Position unit] loop interval
     * @see #setUpdatePlayProgressDelayMs(long)
     */
    P setEnabledPositionUnitLoop(boolean enabled, int loopMode, int loopInterval);

    /**
     * WARNING: media source change with specify to loop of [position unit] index list will be reset
     *
     * @param posUnitLoopIndexList specify to loop of [position unit] index list
     */
    P setPositionUnitLoopIndexList(ArrayList<Integer> posUnitLoopIndexList);

    // ============================@AB

    /**
     * Set A-B start position and end position with reset ab all tag
     *
     * @param startPos [A-B] start position (Ms)
     * @param endPos   [A-B] end position (Ms)
     * @return {@link IPlayer} !setAB but does not play. You must call play(?) to play.
     * @see #play()
     * @see #play(Uri)
     */
    P setAB(long startPos, long endPos);

    /**
     * Set A-B loop <p /> *param loopMode,loopInterval can use {@link C#PARAM_ORIGINAL} keeping the
     * original values <p /> *param loopMode,loopInterval can use {@link C#PARAM_UNSET}/{@link
     * C#PARAM_RESET} unset/reset values
     *
     * @param loopMode     [A-B] loop mode <ul> <li>-1 disable  <li>-8 infinity loop  <li>0++ specified
     *                     loop<ul/>
     * @param loopInterval [A-B] loop interval (S)
     * @return {@link IPlayer} !setAB but does not play. You must call play(?) to play.
     * @see #play()
     * @see #play(Uri)
     */
    P setABLoop(int loopMode, int loopInterval);

    /**
     * Set A-B internal call {@link #setAB(long, long)}, {@link #setABLoop(int, int)}
     * <p>
     * <p /> *param loopMode,loopInterval can use {@link C#PARAM_ORIGINAL} keeping the original values
     * <p /> *param loopMode,loopInterval can use {@link C#PARAM_UNSET}/{@link C#PARAM_RESET}
     * unset/reset values
     *
     * @param startPos     [A-B] start position (Ms)
     * @param endPos       [A-B] end position (Ms)
     * @param loopMode     [A-B] loop mode <ul> <li>-1 disable  <li>-8 infinity loop  <li>0++ specified
     *                     loop<ul/>
     * @param loopInterval [A-B] loop interval (S)
     * @return {@link IPlayer} !setAB but does not play. You must call play(?) to play.
     * @see #play()
     * @see #play(Uri)
     */
    P setAB(long startPos, long endPos, int loopMode, int loopInterval);

    /**
     * Set auto clear A-B all tag(when A-B finish) <p /> WARNING: If for special reasons cause A-B is
     * not finish, will don't auto clear.
     *
     * @see #clearAB()
     */
    P setClearAB(boolean autoClear);

    /**
     * Clear A-B all tag <p /> Tips: setClearAB(false) also don't manual invoke clearAB(), will save
     * all tag. invoke again play(?) continue play A-B for specified ab tag.
     *
     * @see #setClearAB(boolean)
     */
    void clearAB();
    // ============================@MediaQueue

    /**
     * set a new media queue for player
     *
     * @param mediaQueue {@link IMediaQueue}
     */
    void setMediaQueue(IMediaQueue mediaQueue);

    /**
     * get a media queue, if not {@link #setMediaQueue(IMediaQueue)}, it return default media queue
     *
     * @return {@link IMediaQueue}
     */
    IMediaQueue getMediaQueue();

    // ============================@Listener

    /**
     * @param listener {@link Listener} <ul> <li>new {@link PlayerListener}( ) <li>custom implements
     *                 {@link Listener} <ul/>
     */
    void addListener(Listener listener);

    /**
     * @param listener param==null -> clear()
     */
    void removeListener(Listener listener);

    /**
     * 给设置播放器标识
     * @param playerFlag
     * @return
     */
    P setPlayerFlag(String playerFlag);

    /**
     * 该监听者供外部实现，add进{@link IPlayer}来监听播放的相关功能、状态回调
     * <br>有如下方法:
     * <ul>
     * <li>{@link #onAdded()}</li>
     * <li>{@link #onRemoved()}</li>
     * <li>{@link #onIntent(Intent)}</li>
     * <li>{@link #onPlayProgress(long, long)}</li>
     * <li>{@link #onPositionUnitProgress(long, int, int)}</li>
     * <li>{@link #onABProgress(long, long, int)}</li>
     * <li>{@link #onNotificationRequired(int)}</li>
     * <li>{@link #onAudioFocusChange(int)}</li>
     * </ul>
     */
    interface Listener extends IPlayerBase.Listener {

        /**
         * 当前Listener被增加到集合中的回调
         */
        void onAdded();//Lifecycle

        /**
         * Some need long term stationed, can be add again when on removed.
         */
        void onRemoved();//Lifecycle

        /**
         * @return intent != null
         */
        boolean onIntent(Intent intent);

        /**
         * @param position current play position
         * @param duration play duration
         * @return whether someone was handled
         * @see #setUpdatePlayProgressDelayMs(long)
         */
        boolean onPlayProgress(long position, long duration);

        /**
         * WARNING: use position|posUnitIndex in {@link C#STATE_PROGRESS_POS_UNIT_FINISH}, remember to
         * do the judgment(position>=0) | (posUnitIndex>=0)
         *
         * @param position     current play position
         * @param posUnitIndex current position unit index
         * @param posUnitState <ul> <li>{@link C#STATE_PROGRESS_POS_UNIT_START} <li>{@link
         *                     C#STATE_PROGRESS_POS_UNIT_MID} <li>{@link C#STATE_PROGRESS_POS_UNIT_END} <li>{@link
         *                     C#STATE_PROGRESS_POS_UNIT_FINISH} <ul/>
         * @see #setUpdatePlayProgressDelayMs(long)
         */
        void onPositionUnitProgress(long position, int posUnitIndex, int posUnitState);

        /**
         * WARNING: use position,duration in {@link C#STATE_PROGRESS_AB_FINISH}, remember to do the
         * judgment(position>=0 && duration>position)
         *
         * @param position current play ab position (position - abStartPosition)
         * @param duration ab duration (abEndPosition - abStartPosition)
         * @param abState  <ul> <li>{@link C#STATE_PROGRESS_AB_START} <li>{@link C#STATE_PROGRESS_AB_MID}
         *                 <li>{@link C#STATE_PROGRESS_AB_END} <li>{@link C#STATE_PROGRESS_AB_FINISH} <ul/>
         * @see #setUpdatePlayProgressDelayMs(long)
         */
        void onABProgress(long position, long duration, int abState);

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
        void onNotificationRequired(int order);

        boolean onAudioFocusChange(int focusChange);

        /**
         * 通知监听者当前将要播放的媒体资源路径
         * @param theWillPlayPath 将要播放的媒体资源路径
         */
        void onWillPlayMediaPath(String theWillPlayPath);
    }
}