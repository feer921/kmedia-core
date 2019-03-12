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
            WorkStatePlayer willPlayPlayer;
            if (playerPool == null) {
                playerPool = new CopyOnWriteArrayList<>();
                willPlayPlayer = gainAPlayer(providedMediaPlayer);
                willPlayPlayer.setPlayerFlag("Player[1]");
                playerPool.add(willPlayPlayer);
            }
            else{//播放池不为空
                willPlayPlayer = findFreePlayerOrSamePlayer(mediaFilePath);
                if (willPlayPlayer == null) {//没有找到播放器也没有空闲的播放器
                    willPlayPlayer = gainAPlayer(providedMediaPlayer);
                    playerPool.add(willPlayPlayer);
                    willPlayPlayer.setPlayerFlag("Player[" + playerPool.size() + "]");
                }
            }
            if (willPlayPlayer != null) {
                boolean playSuc = willPlayPlayer.play(mediaFilePath, loopTime);
                Log.i(TAG, "--> play() " + willPlayPlayer + " playSuc=" + playSuc);
                return playSuc;
            }
        }
        return false;
    }

    private WorkStatePlayer gainAPlayer(IMediaPlayer mediaPlayer) {
        if (mediaPlayer == null) {
            mediaPlayer = defProvideMediaPlayer();
        }
        mediaPlayer.setAudioStreamType(theStreamType);
        WorkStatePlayer aPlayer = new WorkStatePlayer(context);
        if (outSideListener != null) {
            aPlayer.addListener(outSideListener);
        }
        aPlayer.init(mediaPlayer);
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
    public WorkStatePlayer findFreePlayerOrSamePlayer(String toPlayMediaFilePath) {
        if (playerPool != null) {
            WorkStatePlayer theWillPlayPlayer = null;
            if (!Assert.isEmpty(toPlayMediaFilePath)) {
                //先找播放相同媒体文件的播放器
                for (WorkStatePlayer workStatePlayer : playerPool) {
                    String thePlayFilePath = workStatePlayer.getCurPlayMediaPath();
                    if (toPlayMediaFilePath.equals(thePlayFilePath)) {
                        theWillPlayPlayer = workStatePlayer;
                        break;
                    }
                }
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

    /**
     * 恢复播放
     * @return
     */
    public boolean resumePlay() {
        return letPlayerPoolDo(OPT_RESUME);
    }

    public static final String OPT_PAUSE = "pause";
    public static final String OPT_RESUME = "resume";
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
                        if (workStatePlayer.isPlaying()) {
                            isOptSuc = workStatePlayer.pause();
                        }
                        break;
                    case OPT_RESUME:
                        isOptSuc = workStatePlayer.play();
                        break;
                    case OPT_STOP:
                        workStatePlayer.stop();
                        break;
                    case OPT_RELEASE:
//                        workStatePlayer.release();
                        workStatePlayer.shutdown();
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
}
