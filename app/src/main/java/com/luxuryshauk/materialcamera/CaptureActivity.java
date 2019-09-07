package com.luxuryshauk.materialcamera;

import android.app.Fragment;
import android.support.annotation.NonNull;

import com.luxuryshauk.materialcamera.internal.BaseCaptureActivity;
import com.luxuryshauk.materialcamera.internal.CameraFragment;

public class CaptureActivity extends BaseCaptureActivity {

  @Override
  @NonNull
  public Fragment getFragment() {
    return CameraFragment.newInstance();
  }
}
