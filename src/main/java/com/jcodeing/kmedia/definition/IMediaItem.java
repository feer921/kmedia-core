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

import android.net.Uri;
import android.os.Bundle;

import java.io.Serializable;

public interface IMediaItem extends Serializable{

  String getMediaId();

  Uri getMediaUri();

  CharSequence getTitle();

  CharSequence getDescription();

  Uri getIconUri();

  Bundle getExtras();

  /**
   * 获取当前媒体资源的来源类型:
   * @return 当前媒体资源的来源平台类型 1、本司；2、喜马拉雅；3：不告诉你
   */
  int getMediaSrcPlatformType();
  /**
   * 与其他的一个对象比较
   * @param otherOne 要比较的其他对象
   * @return 0:相等，<0:比otherOne数据旧；>0: 比otherOne数据新
   */
  int compareOther(IMediaItem otherOne);
}