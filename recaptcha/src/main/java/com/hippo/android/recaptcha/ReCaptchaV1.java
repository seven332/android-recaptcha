/*
 * Copyright 2017 Hippo Seven
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.hippo.android.recaptcha;

/*
 * Created by Hippo on 2017/8/21.
 */

import android.content.Context;
import android.os.Handler;
import android.support.annotation.NonNull;

public class ReCaptchaV1 {

  /**
   * Request reCaptcha.
   *
   * @param context A context to be used for request reCaptcha.
   * @param challenge A challenge to request reCaptcha.
   * @param handler A handler to be processed the callback.
   * @param callback A callback that will be triggered when results are obtained.
   */
  public static void reCaptcha(@NonNull Context context, @NonNull String challenge, @NonNull Handler handler, @NonNull ReCaptchaCallback callback) {
    new ReCaptchaV1Task(context, challenge, handler, callback);
  }

  public interface ReCaptchaCallback {

    /**
     * Called then a reCaptcha request completed.
     *
     * @param challenge challenge for this image
     * @param image url for this image
     */
    void onSuccess(@NonNull String challenge, @NonNull String image);

    /**
     * Called when a reCaptcha request failed.
     */
    void onFailure();
  }
}
