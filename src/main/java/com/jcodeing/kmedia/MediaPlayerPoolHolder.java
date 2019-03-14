package com.jcodeing.kmedia;

import android.content.Context;
import android.media.AudioManager;
import android.util.Log;

import com.jcodeing.kmedia.utils.Assert;

import java.util.concurrent.CopyOnWriteArrayList;

/**
 * ******************(^_^)***********************<br>
 * User: fee(QQ/WeiXin:1176610771)<br>
 * Date: 2019/3/11<br>
 * Time: 16:53<br>
 * <P>DESC:
 * 使用MediaPlayer来自定义一个播放池的holder类
 * </p>
 * ******************(^_^)***********************
 */
public class MediaPlayerPoolHolder {
    private static final String TAG = "MediaPlayerPoolHolder";

    private int theStreamType = AudioManager.STREAM_MUSIC;

    private Context context;

    private CopyOnWriteArrayList<WorkStatePlayer> playerPool;

    private PlayerListener outSideListener;
    private boolean isDebug = false;
    /**
     * 播放器池是否为多播放器模式
     * def: true;
     */
    private boolean isPoolMultiPlayerMode = true;

    /**
     * 播放器中的播放器是否在播放完成后自动播放空媒体
     * def:true
     */
    private boolean isThePlayerAutoPlayEmptyMediaWhenOver = true;

    public MediaPlayerPoolHolder withMediaStreamType(int mediaStreamType) {
        this.theStreamType = mediaStreamType;
        return this;
    }

    public MediaPlayerPoolHolder withPlayerListener(PlayerListener playerListener) {
        this.outSideListener = playerListener;
        return this;
    }

    public MediaPlayerPoolHolder withContext(Context context) {
        if (context != null) {
            this.context = context.getApplicationContext();
        }
        return this;
    }

    public MediaPlayerPoolHolder withDebugEable(boolean enableDebug) {
        this.isDebug = enableDebug;
        return this;
    }

    public MediaPlayerPoolHolder withPoolMultiPlayerMode(boolean isPoolMultiPlayerMode) {
        this.isPoolMultiPlayerMode = isPoolMultiPlayerMode;
        return this;
    }

    public MediaPlayerPoolHolder withPlayerAutoPlayEmptyMedia(boolean isAutoPlayEmptyMedia) {
        this.isThePlayerAutoPlayEmptyMediaWhenOver = isAutoPlayEmptyMedia;
        return this;
    }
    /**
     * 使用播放器池里的一个播放器来播放媒体资源
     * @param mediaFilePath 要播放的媒体文件
     * @param loopTime 播放完成后要循环的次数 0不循环; -1:无限循环
     * @param providedMediaPlayer 外部提供实现的播放器
     * @return true:准备播放; false:未执行
     */
    public boolean play(String mediaFilePath, int loopTime, IMediaPlayer providedMediaPlayer) {
        boolean isEmptyMedia = WorkStatePlayer.EMPTY_MEDIA_PATH.equals(mediaFilePath);
        if (isEmptyMedia) {
            if (playerPool == null) {
                return false;
            }
        }
        if (!Assert.isEmpty(mediaFilePath)) {
            if (isEmptyMedia) {
                return stopAllWithEmptyMediaFile(mediaFilePath);
            }
            //去播放
            WorkStatePlayer willPlayPlayer = null;
            if (playerPool == null) {
                playerPool = new CopyOnWriteArrayList<>();
                willPlayPlayer = gainAPlayer(providedMediaPlayer);
                willPlayPlayer.setPlayerFlag("Player[1]");
                playerPool.add(willPlayPlayer);
            }
            else{//播放池不为空
                if (!isPoolMultiPlayerMode) {//如果当前播放器池不是多播放器模式
                    if (!playerPool.isEmpty()) {
                        willPlayPlayer = playerPool.get(0);
                    }
                }
                if (willPlayPlayer == null) {//这里还是留一手，就算当前播放池不是多播放器模式下，如果上面找到的播放器为空也再去获取一个
                    willPlayPlayer = findFreePlayerOrSamePlayer(mediaFilePath);
                }
                if (willPlayPlayer == null) {//没有找到播放器也没有空闲的播放器
                    willPlayPlayer = gainAPlayer(providedMediaPlayer);
                    playerPool.add(willPlayPlayer);
                    willPlayPlayer.setPlayerFlag("Player[" + playerPool.size() + "]");
                }
            }
            boolean playSuc = willPlayPlayer.play(mediaFilePath, loopTime);
            if (isDebug) {
                Log.i(TAG, "--> play() " + willPlayPlayer + " playSuc=" + playSuc);
            }
            return playSuc;
        }
        return false;
    }

    public WorkStatePlayer gainAPlayer(IMediaPlayer mediaPlayer) {
        if (mediaPlayer == null) {
            mediaPlayer = defProvideMediaPlayer();
        }
        mediaPlayer.setAudioStreamType(theStreamType);
        WorkStatePlayer aPlayer = new WorkStatePlayer(context);
        if (outSideListener != null) {
            aPlayer.addListener(outSideListener);
        }
        aPlayer.init(mediaPlayer);
        aPlayer.setDebug(isDebug);
        aPlayer.setAutoPlayEmptyMediaWhenOver(isThePlayerAutoPlayEmptyMediaWhenOver);
        return aPlayer;
    }
    /**
     * 使用播放器池里的一个播放器来播放媒体资源
     * @param mediaFilePath 要播放的媒体文件
     * @param loopTime 播放完成后要循环的次数 0不循环; -1:无限循环
     * @return true:准备播放; false:未执行
     */
    public boolean play(String mediaFilePath, int loopTime) {
        return play(mediaFilePath, loopTime, null);
    }

    /**
     * 去播放器池中查找正在播放相同音频资源的播放器，如果找不到继续查找空闲的播放器
     * @param toPlayMediaFilePath 准备要被播放的资源
     * @return null if no;
     */
    public WorkStatePlayer findFreePlayerOrSamePlayer(String toPlayMediaFilePath) {
        if (playerPool != null) {
            WorkStatePlayer theWillPlayPlayer = null;
            if (!Assert.isEmpty(toPlayMediaFilePath)) {
                //先找播放相同媒体文件的播放器
                for (WorkStatePlayer workStatePlayer : playerPool) {
                    String thePlayFilePath = workStatePlayer.getCurPlayMediaPath();
                    log(TAG, "-->findFreePlayerOrSamePlayer() thePlayFilePath = " + thePlayFilePath + " toPlayMediaFilePath = " + toPlayMediaFilePath);
                    if (toPlayMediaFilePath.equals(thePlayFilePath)) {
                        theWillPlayPlayer = workStatePlayer;
                        break;
                    }
                }
                log(TAG, "-->findFreePlayerOrSamePlayer() the same player: " + theWillPlayPlayer);
                if (theWillPlayPlayer == null) {//没有找到正在播放相同媒体资源的播放器
                    for (WorkStatePlayer workStatePlayer : playerPool) {//则去找是否有空闲的播放器
                        if (!workStatePlayer.isWorking()) {
                            theWillPlayPlayer = workStatePlayer;
                            break;
                        }
                    }
                }
                return theWillPlayPlayer;
            }
        }
        return null;
    }

    public WorkStatePlayer findCurWorkingPlayer() {
        if (!isPoolMultiPlayerMode) {
            if (playerPool != null && !playerPool.isEmpty()) {
                return playerPool.get(0);
            }
        }
        return null;
    }
    public void release() {
        //1、先全部释放播放器池中的播放器
        letPlayerPoolDo(OPT_RELEASE);
        //2、播放器引用全部移除
        if (playerPool != null) {
            playerPool.clear();
        }
        playerPool = null;
        outSideListener = null;
        context = null;
    }

    public boolean stopAll() {
        return letPlayerPoolDo(OPT_STOP);
    }

    public boolean stopAllWithEmptyMediaFile(String theEmptyMediaFile) {
        if (playerPool != null) {
            if (Assert.isEmpty(theEmptyMediaFile)) {
                theEmptyMediaFile = WorkStatePlayer.EMPTY_MEDIA_PATH;
            }
            boolean isDoneAllOk = true;
            for (WorkStatePlayer workStatePlayer : playerPool) {
                boolean isDone = workStatePlayer.play(theEmptyMediaFile, 0);
                if (!isDone) {
                    isDoneAllOk = false;
                }
            }
            return isDoneAllOk;
        }
        return false;
    }

    /**
     * 暂停播放
     * @return
     */
    public boolean pause() {
        return letPlayerPoolDo(OPT_PAUSE);
    }

    public boolean pause(boolean isActive) {
        return letPlayerPoolDo(isActive ? OPT_PAUSE_ACTIVE : OPT_PAUSE);
    }
    /**
     * 恢复播放
     * @return
     */
    public boolean resumePlay() {
        return letPlayerPoolDo(OPT_RESUME);
    }
    public boolean resumePlay(boolean isActive) {
        return letPlayerPoolDo(isActive ? OPT_RESUME_ACTIVE : OPT_RESUME);
    }
    public static final String OPT_PAUSE = "pause";
    public static final String OPT_PAUSE_ACTIVE = "pause_active";
    public static final String OPT_RESUME = "resume";
    public static final String OPT_RESUME_ACTIVE = "resume_active";
    public static final String OPT_STOP = "stop";
    public static final String OPT_RELEASE = "release";
//    public static final String OPT_PAUSE = "pause";

    public boolean letPlayerPoolDo(String optType) {
        if (playerPool != null) {
            boolean isDoneAllOk = true;
            for (WorkStatePlayer workStatePlayer : playerPool) {
                boolean isOptSuc = true;
                switch (optType) {
                    case OPT_PAUSE:
                    case OPT_PAUSE_ACTIVE:
                        if (workStatePlayer.isPlaying()) {
                            isOptSuc = workStatePlayer.pause(OPT_PAUSE_ACTIVE.equals(optType));
                        }
                        break;
                    case OPT_RESUME:
                    case OPT_RESUME_ACTIVE:
                        isOptSuc = workStatePlayer.play(OPT_RESUME_ACTIVE.equals(optType));
                        break;
                    case OPT_STOP:
                        workStatePlayer.stop();
                        break;
                    case OPT_RELEASE:
//                        workStatePlayer.release();
                        workStatePlayer.shutdown();//该操作已经包含player.stop()操作
                        break;
                    default:
                        isOptSuc = false;
                        break;
                }
                if (!isOptSuc) {
                    isDoneAllOk = false;
                }
            }
            return isDoneAllOk;
        }
        return false;
    }

    protected IMediaPlayer defProvideMediaPlayer() {
        return new AndroidMediaPlayer();
    }

    private void log(String tag, String logInfo) {
        if (isDebug) {
            Log.i(tag, logInfo);
        }
    }
}
