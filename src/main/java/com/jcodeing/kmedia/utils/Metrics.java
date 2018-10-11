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
package com.jcodeing.kmedia.utils;

import android.content.Context;

public class Metrics {

  /**
   * @return The absolute height of the available display size in pixels.
   */
  public static int heightPx(Context context) {
    return context.getResources().getDisplayMetrics().heightPixels;
  }

  /**
   * @return The absolute width of the available display size in pixels.
   */
  public static int widthPx(Context context) {
    return context.getResources().getDisplayMetrics().widthPixels;
  }

  public static int heightDp(Context context) {
    return px2dp(context, heightPx(context));
  }

  public static int widthDp(Context context) {
    return px2dp(context, widthPx(context));
  }

  public static int dp2px(Context context, float dpValue) {
    final float scale = context.getResources().getDisplayMetrics().density;
    return (int) (dpValue * scale + 0.5f);
  }

  public static int px2dp(Context context, float pxValue) {
    final float scale = context.getResources().getDisplayMetrics().density;
    return (int) (pxValue / scale + 0.5f);
  }

  public static int px2sp(Context context, float pxValue) {
    final float scale = context.getResources().getDisplayMetrics().density;
    return (int) (pxValue / scale + 0.5f);
  }

  public static int sp2px(Context context, float spValue) {
    final float scale = context.getResources().getDisplayMetrics().density;
    return (int) (spValue * scale + 0.5f);
  }
}