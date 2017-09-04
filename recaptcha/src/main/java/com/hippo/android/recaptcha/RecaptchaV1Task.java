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
 * Created by Hippo on 2017/9/3.
 */

import android.annotation.SuppressLint;
import android.content.Context;
import android.os.Handler;
import android.support.annotation.Keep;
import android.text.TextUtils;
import android.webkit.JavascriptInterface;
import android.webkit.WebResourceResponse;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;

class RecaptchaV1Task {

  private static final String HTML_BODY_1 = "<html><body><script type=\"text/javascript\" src=\"https://www.google.com/recaptcha/api/challenge?k=";
  private static final String HTML_BODY_2 = "\"></script></body></html>";
  private static final String HTML_MIME_TYPE = "text/html";
  private static final String HTML_ENCODING = "UTF-8";

  private static final String URL_CHALLENGE = "javascript:Android.onGetChallenge('undefined'!=typeof RecaptchaState&&RecaptchaState.hasOwnProperty('challenge')?RecaptchaState.challenge:null)";
  private static final String URL_IMAGE = "https://www.google.com/recaptcha/api/image";

  private WebView webView;

  private Handler handler;
  private RecaptchaV1.RecaptchaCallback callback;

  private String image;
  private Runnable failureRunnable;
  private boolean destroyed = false;

  @SuppressLint({"SetJavaScriptEnabled", "AddJavascriptInterface"})
  public RecaptchaV1Task(Context context, String challenge, Handler handler, RecaptchaV1.RecaptchaCallback callback) {
    this.handler = handler;
    this.callback = callback;

    webView = new WebView(context.getApplicationContext());
    webView.setWebViewClient(new RecaptchaClient());
    webView.getSettings().setCacheMode(WebSettings.LOAD_CACHE_ELSE_NETWORK);
    webView.getSettings().setJavaScriptEnabled(true);
    webView.addJavascriptInterface(this, "Android");

    webView.loadData(HTML_BODY_1 + challenge + HTML_BODY_2, HTML_MIME_TYPE, HTML_ENCODING);
  }

  public void onPageFinished() {
    // onGetChallenge() may not be called, make sure callback.onFailure() called
    failureRunnable = new Runnable() {
      @Override
      public void run() {
        failureRunnable = null;
        if (!destroyed) {
          destroyed = true;
          webView.destroy();
          callback.onFailure();
        }
      }
    };
    handler.postDelayed(failureRunnable, 300);

    // Try to call onGetChallenge()
    webView.loadUrl(URL_CHALLENGE);
  }

  public void onGetImage(String image) {
    this.image = image;
  }

  @Keep
  @JavascriptInterface
  public void onGetChallenge(String challenge) {
    if (failureRunnable != null) {
      handler.removeCallbacks(failureRunnable);
      failureRunnable = null;
    } else {
      // FailureRunnable has run
      return;
    }

    final String finalChallenge = challenge;
    final String finalImage = image;
    final boolean success = !TextUtils.isEmpty(challenge) || !TextUtils.isEmpty(image);

    handler.post(new Runnable() {
      @Override
      public void run() {
        if (!destroyed) {
          destroyed = true;
          webView.destroy();
          if (success) {
            callback.onSuccess(finalChallenge, finalImage);
          } else {
            callback.onFailure();
          }
        }
      }
    });
  }

  private class RecaptchaClient extends WebViewClient {

    @SuppressWarnings("deprecation")
    @Override
    public WebResourceResponse shouldInterceptRequest(WebView view, String url) {
      if (url != null && url.startsWith(URL_IMAGE)) {
        onGetImage(url);
      }
      return super.shouldInterceptRequest(view, url);
    }

    @Override
    public void onPageFinished(WebView view, String url) {
      RecaptchaV1Task.this.onPageFinished();
    }
  }
}
