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
package com.jcodeing.kmedia.definition;

import androidx.annotation.IntDef;

import com.jcodeing.kmedia.IPlayer;
import com.jcodeing.kmedia.Player;
import com.jcodeing.kmedia.assist.C;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.util.List;

public interface IMediaQueue {

  String getMediaQueueName();

  /**
   * 设置当前 媒体队列的名称是什么；eg.:《最近收听》、《播放列表》等
   * @param curQueueName 当前的播放队列名称
   */
  void setMediaQueueName(String curQueueName);

  /**
   * Don't have to manually call, automatically initialized in the {@link
   * Player#setMediaQueue(IMediaQueue)}
   */
  void init(IPlayer player);

  /**
   * destroy this queue
   */
  void destroy();

  // ============================@Queue

  /**
   * update this queue, with external use the same object, not independent maintenance
   */
  void update(List<? extends IMediaItem> newQueue);

  /**
   * Returns <tt>true</tt> if this queue contains no {@link IMediaItem}s.
   *
   * @return <tt>true</tt> if this queue contains no {@link IMediaItem}s
   */
  boolean isEmpty();

  /**
   * Returns the number of {@link IMediaItem}s in this queue.  If this queue contains more than
   * <tt>Integer.MAX_VALUE</tt> {@link IMediaItem}s, returns <tt>Integer.MAX_VALUE</tt>.
   *
   * @return the number of {@link IMediaItem}s in this queue
   */
  int size();

  /**
   * Returns the index of the first occurrence of the specified mediaItem in this queue, or -1 if
   * this queue does not contain the mediaItem.
   */
  int indexOf(IMediaItem item);

  /**
   * Removes the mediaItem at the specified position in this queue.
   *
   * @param index the index of the mediaItem to be removed
   * @return the mediaItem previously at the specified position
   */
  IMediaItem remove(int index);

  /**
   * Removes all of the {@link IMediaItem}s from this queue (optional operation). The queue will be
   * empty after this call returns.
   */
  void clear();

  // ============================@MediaItem
  IMediaItem getMediaItem(int index);

  IMediaItem getMediaItem(String mediaId);

  IMediaItem getCurrentMediaItem();

  /**
   * 获取当前播放队列的列表数据
   * @return 当前播放的列表数据
   */
  List<? extends IMediaItem> getCurQueueListDatas();
  // ============================@Index
  int getCurrentIndex();

  /**
   * set the current queue index
   *
   * @param index queue index
   * @return is set success
   */
  boolean setCurrentIndex(int index);

  /**
   * set the current queue index from the media Id
   *
   * @param mediaId {@link IMediaItem#getMediaId()}
   * @return is set success
   */
  boolean setCurrentIndex(String mediaId);

  /**
   * seek queue index by media id
   *
   * @param mediaId {@link IMediaItem#getMediaId()}
   * @return mediaId->queueIndex or -1(didn't find)
   */
  int seekIndexByMediaId(String mediaId);

  /**
   * @return a random queue index
   */
  int getRandomIndex();

  // ============================@Skip@============================

  /**
   * Skip to the specify queue index media item.
   *
   * @param index queue index
   * @return is skip success
   */
  boolean skipToIndex(int index);

  /**
   * skipToIndex(currentIndex + increment)
   *
   * @see #skipToIndex(int)
   */
  boolean skipToIndexByIncrement(int increment);

  /**
   * Skip to the next media item. <p>skipToIndexByIncrement(1)<p/>
   */
  boolean skipToNext();

  /**
   * Skip to the previous media item. <p>skipToIndexByIncrement(-1)<p/>
   */
  boolean skipToPrevious();

  boolean skipToRandom();

  void setAutoSkipMode(@AutoSkipMode int autoSkipMode);

  /**
   * <p>*param loopMode can use {@link C#PARAM_ORIGINAL} keeping the original values <p>*param
   * loopMode can use {@link C#PARAM_UNSET}/{@link C#PARAM_RESET} unset/reset values<p/><p/>
   */
  void setItemLoop(int loopMode);

  int getAutoSkipMode();

  /**
   * 在一首歌播放完成后
   * --流程： 播放器-->回调onCompletion()-->skipToAutoAssigned():true:播放队列自动分配下一首；false:队列不分配，则回调给各监听者，播放完成
   * 媒体播放队列是否自动分配下一个要播放的
   * @return is skip success or have processing
   */
  boolean skipToAutoAssigned();

  // ============================@Listener

  /**
   * 用来监听播放队列的一些状态
   */
  interface Listener {
    /**
     * 媒体播放队列中媒体数据有更新
     * 一般触发事件为调用了{@link IMediaQueue#update(List)}
     * @param newQueue 新的媒体数据
     */
    void onQueueUpdated(List<? extends IMediaItem> newQueue);

    /**
     * 媒体播放队列中指定序号的媒体item从队列中被移除的回调
     * 一般触发事件为调用了{@link IMediaQueue#remove(int)}
     * @param index 被移除的媒体item在队列中的序号
     */
    void onItemRemoved(int index);

    /**
     * 队列中当前播放的序号更新
     * 注：该方法回调只表示，播放队列中的将要播放的序号更新成功,但不一定会播放!!!
     * 一般触发事件为调用了{@link IMediaQueue#skipToIndex(int)}
     * <br/>
     * 调用后，接下来会回调{@link #onCurrentQueueIndexUpdated(int)}
     * 然后{@link #onSkipQueueIndex(int)}
     * 再然后，如果{@link #onSkipQueueIndex(int)}外部没有拦截处理，则开始播放.
     * 因此可能存在一个问题，就是如果被监听者外部拦截处理了，因为导致了不播放index的媒体了，所以本方法回调时写的逻辑可能会不正确了
     * @param index 当前设置为要播放的序号
     */
    void onCurrentQueueIndexUpdated(int index);

    /**
     * 调用顺序：skipToIndex(int index)-->setCurrentIndex(int index){-->onCurrentQueueIndexUpdated(index)}:true-->onSkipQueueIndex(index)
     * 当播放队列中当前播放序号有跳转(可能为主动设置到该序号)时，是否要拦截处理该序号
     * @param willPlayIndex 将要播放的当前列表中的序号
     * @return true:拦截处理，则不会去播放，false:不拦截处理，则会去播放
     */
    boolean onSkipQueueIndex(int willPlayIndex);
  }

  /**
   * 添加对媒体播放队列的状态监听者
   * @param listener
   */
  void addListener(Listener listener);

  void removeListener(Listener listener);

  // ============================@Constants
  //Do not change these values
  /**
   * 播放模式：列表循环
   */
  int AUTO_SKIP_MODE_LIST_LOOP = 1;
  /**
   * 播放模式：随机播放
   */
  int AUTO_SKIP_MODE_RANDOM = 2;
  int AUTO_SKIP_MODE_SINGLE = 3;
  /**
   * 单曲循环
   */
  int AUTO_SKIP_MODE_SINGLE_LOOP = 31;
  /**
   * 播放单曲模式
   */
  int AUTO_SKIP_MODE_SINGLE_ONCE = 32;

  /**
   * auto skip modes for {@link #setAutoSkipMode(int)}
   */
  @Retention(RetentionPolicy.SOURCE)
  @IntDef({AUTO_SKIP_MODE_LIST_LOOP, AUTO_SKIP_MODE_RANDOM,
      AUTO_SKIP_MODE_SINGLE, AUTO_SKIP_MODE_SINGLE_LOOP, AUTO_SKIP_MODE_SINGLE_ONCE})
  @interface AutoSkipMode {

  }
}