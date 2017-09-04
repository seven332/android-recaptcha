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

package com.hippo.android.recaptcha.demo;

import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import com.hippo.android.recaptcha.RecaptchaV1;
import com.squareup.picasso.Picasso;

public class MainActivity extends AppCompatActivity {

  private Button button;
  private TextView challenge;
  private TextView url;
  private ImageView image;

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_main);

    button = findViewById(R.id.button);
    challenge = findViewById(R.id.challenge);
    url = findViewById(R.id.url);
    image = findViewById(R.id.image);

    button.setOnClickListener(new View.OnClickListener() {
      @Override
      public void onClick(View v) {
        challenge.setText(null);
        url.setText(null);
        image.setImageDrawable(null);

        RecaptchaV1.recaptcha(MainActivity.this, "6LdtfgYAAAAAALjIPPiCgPJJah8MhAUpnHcKF8u_",
            new Handler(Looper.getMainLooper()), new RecaptchaV1.RecaptchaCallback() {
              @Override
              public void onSuccess(@NonNull String c, @NonNull String i) {
                challenge.setText(c);
                url.setText(i);
                Picasso.with(MainActivity.this).load(i).into(image);
              }

              @Override
              public void onFailure() {
                challenge.setText("Failed");
                url.setText("Failed");
              }
            });
      }
    });
  }
}
