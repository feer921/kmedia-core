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

import android.content.Intent;
import android.text.TextUtils;
import android.util.Log;

import com.jcodeing.kmedia.assist.C;

/**
 * Simple player listener
 *
 * @see IPlayer#addListener(IPlayer.Listener)
 */
public class PlayerListener implements IPlayer.Listener {
  protected String TAG = getClass().getSimpleName();
  public boolean LOG_DEBUG = false ;

  public PlayerListener() {
    boolean tagIsEmpty = TextUtils.isEmpty(TAG);
    if (tagIsEmpty) {
      TAG = "PlayerListener";
    }
  }
  @Override
  public void onPrepared() {
    //Do nothing
    if (LOG_DEBUG) {
      Log.i(TAG, "-->onPrepared()");
    }
  }

  @Override
  public void onBufferingUpdate(int percent) {
    //Do nothing
    if (LOG_DEBUG) {
      Log.i(TAG, "-->onBufferingUpdate() percent = " + percent);
    }
  }

  @Override
  public void onSeekComplete() {
    //Do nothing
    if (LOG_DEBUG) {
      Log.i(TAG, "-->onSeekComplete()");
    }
  }

  @Override
  public int onCompletion() {
    if (LOG_DEBUG) {
      Log.i(TAG, "-->onCompletion()");
    }
    return C.CMD_RETURN_NORMAL;
  }

  @Override
  public boolean onInfo(int what, int extra) {
    if (LOG_DEBUG) {
      Log.i(TAG, "-->onInfo() what = " + what + " extra = " + extra);
    }
    return false;
  }

  @Override
  public boolean onError(int what, int extra, Exception e) {
    if (LOG_DEBUG) {
      Log.e(TAG, "-->onError() what = " + what + " extra = " + extra + " e:" + e);
    }
    return false;
  }

  @Override
  public void onVideoSizeChanged(int width, int height,
      int unappliedRotationDegrees, float pixelWidthHeightRatio) {
    //Do nothing
    if (LOG_DEBUG) {
      Log.i(TAG, "-->onVideoSizeChanged() width = " + width + " height = " + height + "  unappliedRotationDegrees = " + unappliedRotationDegrees
              + " pixelWidthHeightRatio = " + pixelWidthHeightRatio
      );
    }
  }

  // ============================@Extend
  @Override
  public void onStateChanged(int playbackState) {
    //Do nothing
    if (LOG_DEBUG) {
      Log.i(TAG, "-->onStateChanged() playbackState =" + playbackState);
    }
  }

  public boolean onPlayProgress(long position, long duration) {
    if (LOG_DEBUG) {
      Log.i(TAG, "-->onPlayProgress() position = "+position +" duration = "+duration);
    }
    return false;
  }

  @Override
  public void onPositionUnitProgress(long position, int posUnitIndex, int posUnitState) {
    //Do nothing
    if (LOG_DEBUG) {
      Log.i(TAG, "-->onPositionUnitProgress() position =" + position + " posUnitIndex = " + posUnitIndex + " posUnitState = " + posUnitState);
    }
  }

  @Override
  public void onABProgress(long position, long duration, int abState) {
    //Do nothing
    if (LOG_DEBUG) {
      Log.i(TAG, "-->onABProgress() position = " + position + " duration = " + duration + " abState = " + abState);
    }
  }

  @Override
  public void onNotificationRequired(int order) {
    //Do nothing
    if (LOG_DEBUG) {
      Log.i(TAG, "-->onNotificationRequired() order = " + order);
    }
  }

  @Override
  public boolean onAudioFocusChange(int focusChange) {
    if (LOG_DEBUG) {
      Log.i(TAG, "-->onAudioFocusChange() focusChange = " + focusChange);
    }
    return false;
  }

  @Override
  public boolean onIntent(Intent intent) {
    if (LOG_DEBUG) {
      Log.i(TAG, "-->onIntent() intent= " + intent);
    }
    return intent != null;
  }


  // ============================@Life
  @Override
  public void onAdded() {
    //Do nothing
    if (LOG_DEBUG) {
      Log.i(TAG, "-->onAdded()");
    }
  }

  @Override
  public void onRemoved() {
    //Do nothing
    if (LOG_DEBUG) {
      Log.i(TAG, "-->onRemoved()");
    }
  }
}