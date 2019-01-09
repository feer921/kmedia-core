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

import android.text.TextUtils;
import com.jcodeing.kmedia.IPlayer;
import com.jcodeing.kmedia.assist.C;
import com.jcodeing.kmedia.utils.Assert;
import com.jcodeing.kmedia.utils.L;
import java.util.List;
import java.util.Random;
import java.util.concurrent.CopyOnWriteArraySet;

public class MediaQueue implements IMediaQueue {

  protected IPlayer player;

  protected String curMediaQueueName = "";
  /**
   * 在主动调用跳转到下一曲/上一曲时，是否要关心当前的播放模式
   * 比如：当前为单曲循环时，则下一曲和上一曲仍然是当前播放的这一首;
   * 如果当前是随机播放模式，则下一曲和上一曲应该是随机的
   */
  protected boolean needCarePlayModeOnSkipToNextOrPre = true;
  @Override
  public String getMediaQueueName() {
    return curMediaQueueName;
  }

  /**
   * 设置当前 媒体队列的名称是什么；eg.:《最近收听》、《播放列表》等
   *
   * @param curQueueName 当前的播放队列名称
   */
  @Override
  public void setMediaQueueName(String curQueueName) {
    this.curMediaQueueName = curQueueName;
  }

  @Override
  public void init(IPlayer player) {
    this.player = player;
  }

  @Override
  public void destroy() {
    listeners.clear();
    listeners = null;
    player = null;
  }

  // ============================@Queue@============================
  /**
   * 媒体集合
   */
  private List<? extends IMediaItem> queue;

  @Override
  public void update(List<? extends IMediaItem> newQueue) {
//    if (queue == newQueue) {
//      return;
//    }
    queue = newQueue;
    currentIndex = 0;
    onQueueUpdated(newQueue);
  }

  @Override
  public boolean isEmpty() {
    return queue == null || queue.isEmpty();
  }

  @Override
  public int size() {
    if (queue != null) {
      return queue.size();
    }
    return 0;
  }

  @Override
  public int indexOf(IMediaItem item) {
    if (queue != null) {
      return queue.indexOf(item);
    }
    return -1;
  }

  @Override
  public IMediaItem remove(int index) {
    if (queue != null) {
      IMediaItem removed = queue.remove(index);
      if (removed != null) {
        onItemRemoved(index);
        return removed;
      }
    }
    return null;
  }

  @Override
  public void clear() {
    if (queue != null) {
      try {
        queue.clear();
        //added by fee 2018-04-11: 清空了媒体播放队列应该通知监听者吧??..
        currentIndex = 0;
        onQueueUpdated(queue);
      } catch (Exception e) {
        //UnsupportedOperationException
        L.printStackTrace(e);
      }
    }
  }

  // ============================@MediaItem
  @Override
  public IMediaItem getMediaItem(int index) {
    if (queue != null && Assert.checkIndex(index, queue.size())) {
      return queue.get(index);
    }
    return null;
  }

  @Override
  public IMediaItem getMediaItem(String mediaId) {
    return getMediaItem(seekIndexByMediaId(mediaId));
  }

  @Override
  public IMediaItem getCurrentMediaItem() {
    return getMediaItem(currentIndex);
  }

  /**
   * 获取当前播放队列的列表数据
   *
   * @return 当前播放的列表数据
   */
  @Override
  public List<? extends IMediaItem> getCurQueueListDatas() {
    return queue;
  }

  // ============================@Index
  protected int currentIndex = C.INDEX_UNSET;

  @Override
  public int getCurrentIndex() {
    return currentIndex;
  }

  @Override
  public boolean setCurrentIndex(int index) {
    if (queue != null && Assert.checkIndex(index, queue.size())) {
      currentIndex = index;
      onCurrentQueueIndexUpdated(index);
      return true;
    }
    return false;
  }

  @Override
  public boolean setCurrentIndex(String mediaId) {
    return setCurrentIndex(seekIndexByMediaId(mediaId));
  }

  @Override
  public int seekIndexByMediaId(String mediaId) {
    if (size() > 0 && !TextUtils.isEmpty(mediaId)) {
      for (int i = 0; i < size(); i++) {
        if (mediaId.equals(getMediaItem(i).getMediaId())) {
          return i;
        }
      }
    }
    return -1;
  }

  private Random mRandom;

  @Override
  public int getRandomIndex() {
    if (mRandom == null) {
      mRandom = new Random();
      mRandom.setSeed(System.currentTimeMillis());
    }
    if (size() > 0) {
      return Math.abs(mRandom.nextInt() % queue.size());
    }
    return -1;
  }


  // ============================@Skip@============================
  @Override
  public boolean skipToIndex(int index) {
    if (setCurrentIndex(index)) {
      if (!onSkipQueueIndex(index)) {
        //If there is no one handled, with so me handle
        if (player != null) {
          player.play(getMediaItem(index));
        }
      }
      return true;
    }
    return false;
  }

  @Override
  public boolean skipToIndexByIncrement(int increment) {
    if (size() >= 1) {//modify by fee 2018-03-24 22:40 周六: change to >= 1
      int index = currentIndex + increment;
      //cycle queue index
      if (index < 0) {
        index = size() - 1;
      } else {
        index %= size();//0<= index <size()
      }
      return skipToIndex(index);
    }
    return false;
  }

  @Override
  public boolean skipToNext() {
    if (needCarePlayModeOnSkipToNextOrPre) {
      if (autoSkipMode == AUTO_SKIP_MODE_RANDOM) {
        return skipToRandom();
      }
      if (autoSkipMode == AUTO_SKIP_MODE_SINGLE_LOOP) {
        return skipToIndex(currentIndex);
      }
    }
    return skipToIndexByIncrement(1);
  }

  @Override
  public boolean skipToPrevious() {
    if (needCarePlayModeOnSkipToNextOrPre) {
      if (autoSkipMode == AUTO_SKIP_MODE_RANDOM) {
        return skipToRandom();
      }
      if (autoSkipMode == AUTO_SKIP_MODE_SINGLE_LOOP) {
        return skipToIndex(currentIndex);
      }
    }
    return skipToIndexByIncrement(-1);
  }

  @Override
  public boolean skipToRandom() {
    return skipToIndexByIncrement(getRandomIndex() - currentIndex);
  }

  // ============================@Auto
  private int autoSkipMode = AUTO_SKIP_MODE_LIST_LOOP;

  @Override
  public int getAutoSkipMode() {
    return autoSkipMode;
  }

  @Override
  public void setAutoSkipMode(int autoSkipMode) {
    this.autoSkipMode = autoSkipMode;
  }

  @Override
  public boolean skipToAutoAssigned() {
    if (isEmpty()) {
      return false;
    }
    switch (autoSkipMode) {
      case AUTO_SKIP_MODE_LIST_LOOP:
      case AUTO_SKIP_MODE_RANDOM:
        //Correct itemLoopMode
        //Don't allow item infinity loop
        if (itemLoopMode < 0) {//-8
          itemLoopMode = 0;
          itemLoopedCount = 0;
        }
        if (itemLoopProcessing() == 1) {
          return true;//processing
        }
        else {
          if (autoSkipMode == AUTO_SKIP_MODE_LIST_LOOP) {//列表循环
            return skipToNext();//下一曲
          }
          else {//随机播放模式
            return skipToRandom();//
          }
        }
      case AUTO_SKIP_MODE_SINGLE:
        return itemLoopProcessing() == 1;
      case AUTO_SKIP_MODE_SINGLE_LOOP:
        itemLoopMode = -8;
        itemLoopProcessing();
        return true;//infinity loop
      case AUTO_SKIP_MODE_SINGLE_ONCE:
        return false;//not skip and processing
    }
    return false;
  }

  // ============================@Item@============================
  private int itemLoopMode = 0;
  private int itemLoopedCount = 0;

  @Override
  public void setItemLoop(int loopMode) {
    //Support use C.PARAM_RESET constant reset values.
    if (loopMode == C.PARAM_RESET) {
      loopMode = 0;
    }
    //Support use C.PARAM_ORIGINAL constant keeping the original values
    //...loop after the end of values not reset, so don't have to set up again
    if (loopMode != C.PARAM_ORIGINAL) {
      itemLoopMode = loopMode;
    }
    //reset tag
    itemLoopedCount = 0;
  }

  protected int itemLoopProcessing() {
    // =========@Processing@=========
    if (itemLoopMode == -8) {
      // =========@infinity loop[-8]@=========
      if (player != null) {
        player.play(getCurrentMediaItem());
      }
      return 1;//enable(processing)
    } else if (itemLoopMode > 0) {
      // =========@specified loop[>0]@=========
      if (itemLoopedCount < itemLoopMode) {
        if (player != null) {
          player.play(getCurrentMediaItem());
        }
        itemLoopedCount++;
        return 1;//enable(processing)
      } else {
        // =========@specified loop finish@=========
        // reset tag(itemLoopedCount = 0)
        itemLoopedCount = 0;
        return 2;//enable(loop finish)
      }
    } else {
      // =========@not loop[<=0]@=========
      return 0;
    }
  }

  // ============================@Listener@============================
  private CopyOnWriteArraySet<Listener> listeners;

  @Override
  public void addListener(Listener listener) {
    if (listeners == null) {
      listeners = new CopyOnWriteArraySet<>();
    }
    if (listener != null) {
      listeners.add(listener);
    }
  }

  @Override
  public void removeListener(Listener listener) {
    if (listeners != null && listener != null) {
      listeners.remove(listener);
    }
  }


  protected void onQueueUpdated(List<? extends IMediaItem> newQueue) {
    if (listeners != null) {
      for (Listener listener : listeners) {
        listener.onQueueUpdated(newQueue);
      }
    }
  }

  protected void onItemRemoved(int index) {
    if (listeners != null) {
      for (Listener listener : listeners) {
        listener.onItemRemoved(index);
      }
    }
  }

  /**
   * 在主动设置当前队列的想播放的序号时，回调<br/>
   * 参见：{@link #setCurrentIndex(int)}
   * @param index 当前要设置为播放的队列序号
   */
  protected void onCurrentQueueIndexUpdated(int index) {
    if (listeners != null) {
      for (Listener listener : listeners) {
        listener.onCurrentQueueIndexUpdated(index);
      }
    }
  }

  protected boolean onSkipQueueIndex(int index) {
    if (listeners != null) {
      boolean was_handled = false;
      for (Listener listener : listeners) {
        if (listener.onSkipQueueIndex(index)) {//这样是为了让所有的listener都有机会处理该事件
          was_handled = true;
        }
      }
      return was_handled;
    }
    return false;
  }
}