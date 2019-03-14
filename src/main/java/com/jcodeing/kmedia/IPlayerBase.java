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

import android.view.SurfaceView;
import android.view.TextureView;

import com.jcodeing.kmedia.assist.C;

/**
 * {@link IMediaPlayer} And {@link IPlayer} Common Interface
 * 基础的对播放功能的操作
 * <p>有如下操作</p>
 * <ul>
 * <li>{@link #start()}</li>
 * <li>{@link #pause()}</li>
 * <li>{@link #seekTo(long)}</li>
 * <li>{@link #stop()}</li>
 * <li>{@link #reset()}</li>
 * <li>{@link #release()}</li>
 * <li>{@link #setVideo(SurfaceView)}</li>
 * <li>{@link #setVideo(TextureView)}</li>
 * <li>{@link #clearVideo()}</li>
 * <li>{@link #setVolume(float)}</li>
 * <li>{@link #getVolume()}</li>
 * <li>{@link #getCurrentPosition()}</li>
 * <li>{@link #getDuration()}</li>
 * <li>{@link #isPlaying()}</li>
 * <li>{@link #getPlaybackSpeed()}</li>
 * <li>{@link #setPlaybackSpeed(float speed)}</li>
 * <li>{@link #getPlaybackState()}</li>
 * <li>{@link #isPlayable()}</li>
 * </ul>
 */
public interface IPlayerBase {
    String EMPTY_MEDIA_PATH = "null.mp3";

    // ============================@Control
    boolean start();

    boolean pause();

    boolean seekTo(long ms);

    void stop();

    void reset();

    void release();

    // ============================@Video
    void setVideo(SurfaceView surfaceView);

    void setVideo(TextureView textureView);

    void clearVideo();

    // ============================@Set/Get/Is

    /**
     * Sets the volume, with 0 being silence and 1 being unity gain.
     */
    void setVolume(float volume);

    /**
     * Returns the volume, with 0 being silence and 1 being unity gain.
     */
    float getVolume();

    long getCurrentPosition();

    long getDuration();

    boolean isPlaying();

    // =========@Base Extend
    float getPlaybackSpeed();

    /**
     * @return Whether support
     */
    boolean setPlaybackSpeed(float speed);

    /**
     * @return One of the {@code STATE} constants defined <ul> <li>{@link #STATE_IDLE} <li>{@link
     * #STATE_GOT_SOURCE} <li>{@link #STATE_BUFFERING} <li>{@link #STATE_READY} <li>{@link
     * #STATE_ENDED} </ul>
     */
    int getPlaybackState();

    /**
     * Playback state <ul> <li>!= {@link #STATE_IDLE} <li>!= {@link #STATE_GOT_SOURCE} <li>!= {@link
     * #STATE_BUFFERING} <ul/>
     *
     * @return Whether current state can playback[go player.start()]
     */
    boolean isPlayable();

    // ============================@Listener
    //基础的对播放功能的一些监听

    /**
     * 该监听者是针对播放器(eg.: MediaPlayer)的相关功能回调
     * <ul>
     * <li>{@link #onPrepared()}</li>
     * <li>{@link #onCompletion()}</li>
     * <li>{@link #onBufferingUpdate(int percent)}</li>
     * <li>{@link #onSeekComplete()}</li>
     * <li>{@link #onVideoSizeChanged(int, int, int, float)}</li>
     * <li>{@link #onError(int, int, Exception)}</li>
     * <li>{@link #onInfo(int, int)}</li>
     * <li>{@link #onStateChanged(int)}</li>
     * </ul>
     */
    interface Listener {

        void onPrepared();

        /**
         * @return CMD (need handle return command) {@link C#CMD_RETURN_FORCED}/{@link
         * C#CMD_RETURN_NORMAL}
         */
        int onCompletion();

        /**
         * 缓冲进度，百分制的0~100
         *
         * @param percent 0~100
         */
        void onBufferingUpdate(int percent);

        void onSeekComplete();

        void onVideoSizeChanged(int width, int height, int unappliedRotationDegrees, float pixelWidthHeightRatio);

        /**
         * PS: 该回调方法比较慢
         *
         * @param what
         * @param extra
         * @param e
         * @return true:表明当前监听者处理了该错误回调，则播放器不会回调onComplete(); false:由播放器处理
         */
        boolean onError(int what, int extra, Exception e);

        boolean onInfo(int what, int extra);

        // =========@Base Extend

        /**
         * Called when the value returned from {@link #getPlaybackState()} changes.
         */
        void onStateChanged(int playbackState);
    }

    // ============================@Constants
    // Do not change these values
    /**
     * The player does not have a source to play, so it is neither buffering nor ready to play.
     */
    int STATE_IDLE = 1;
    /**
     * The player not able to immediately play from the current position. This state typically occurs
     * when more data needs to be loaded to be ready to play, or more data needs to be buffered for
     * playback to resume.
     */
    int STATE_BUFFERING = 2;
    /**
     * The player is able to immediately play from the current position.
     */
    int STATE_READY = 3;
    /**
     * The player has finished playing the media.
     */
    int STATE_ENDED = 4;

    // =========@Extend
    // Constants Value 916+
    // [IPlayer First letter sequence]+
    /**
     * The player got a source to play. <p>setDataSource(...) after the state of the<p/>
     */
    int STATE_GOT_SOURCE = 9161;
}