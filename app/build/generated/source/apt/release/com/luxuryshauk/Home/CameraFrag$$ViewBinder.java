// Generated code from Butter Knife. Do not modify!
package com.luxuryshauk.Home;

import android.view.View;
import butterknife.ButterKnife.Finder;
import butterknife.ButterKnife.ViewBinder;

public class CameraFrag$$ViewBinder<T extends com.luxuryshauk.Home.CameraFrag> implements ViewBinder<T> {
  @Override public void bind(final Finder finder, final T target, Object source) {
    View view;
    view = finder.findRequiredView(source, 2131296466, "method 'onFlashSwitcClicked'");
    view.setOnClickListener(
      new butterknife.internal.DebouncingOnClickListener() {
        @Override public void doClick(
          android.view.View p0
        ) {
          target.onFlashSwitcClicked();
        }
      });
    view = finder.findRequiredView(source, 2131296470, "method 'onSwitchCameraClicked'");
    view.setOnClickListener(
      new butterknife.internal.DebouncingOnClickListener() {
        @Override public void doClick(
          android.view.View p0
        ) {
          target.onSwitchCameraClicked();
        }
      });
    view = finder.findRequiredView(source, 2131296678, "method 'onRecordButtonClicked'");
    view.setOnClickListener(
      new butterknife.internal.DebouncingOnClickListener() {
        @Override public void doClick(
          android.view.View p0
        ) {
          target.onRecordButtonClicked();
        }
      });
    view = finder.findRequiredView(source, 2131296737, "method 'onSettingsClicked'");
    view.setOnClickListener(
      new butterknife.internal.DebouncingOnClickListener() {
        @Override public void doClick(
          android.view.View p0
        ) {
          target.onSettingsClicked();
        }
      });
    view = finder.findRequiredView(source, 2131296641, "method 'onMediaActionSwitchClicked'");
    view.setOnClickListener(
      new butterknife.internal.DebouncingOnClickListener() {
        @Override public void doClick(
          android.view.View p0
        ) {
          target.onMediaActionSwitchClicked();
        }
      });
    view = finder.findRequiredView(source, 2131296309, "method 'onAddCameraClicked'");
    view.setOnClickListener(
      new butterknife.internal.DebouncingOnClickListener() {
        @Override public void doClick(
          android.view.View p0
        ) {
          target.onAddCameraClicked();
        }
      });
  }

  @Override public void unbind(T target) {
  }
}
