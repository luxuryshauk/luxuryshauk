package com.luxuryshauk.materialcamera;

import android.app.Fragment;
import android.support.annotation.NonNull;

import com.luxuryshauk.materialcamera.internal.BaseCaptureActivity;
import com.luxuryshauk.materialcamera.internal.Camera2Fragment;


public class CaptureActivity2 extends BaseCaptureActivity {

  @Override
  @NonNull
  public Fragment getFragment() {
    return Camera2Fragment.newInstance();
  }
}
