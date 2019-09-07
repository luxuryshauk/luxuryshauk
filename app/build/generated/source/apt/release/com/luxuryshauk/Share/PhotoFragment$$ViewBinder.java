// Generated code from Butter Knife. Do not modify!
package com.luxuryshauk.Share;

import android.view.View;
import butterknife.ButterKnife.Finder;
import butterknife.ButterKnife.ViewBinder;

public class PhotoFragment$$ViewBinder<T extends com.luxuryshauk.Share.PhotoFragment> implements ViewBinder<T> {
  @Override public void bind(final Finder finder, final T target, Object source) {
    View view;
    view = finder.findRequiredView(source, 2131296737, "field 'settingsView' and method 'onSettingsClicked'");
    target.settingsView = finder.castView(view, 2131296737, "field 'settingsView'");
    view.setOnClickListener(
      new butterknife.internal.DebouncingOnClickListener() {
        @Override public void doClick(
          android.view.View p0
        ) {
          target.onSettingsClicked();
        }
      });
    view = finder.findRequiredView(source, 2131296466, "field 'flashSwitchView' and method 'onFlashSwitcClicked'");
    target.flashSwitchView = finder.castView(view, 2131296466, "field 'flashSwitchView'");
    view.setOnClickListener(
      new butterknife.internal.DebouncingOnClickListener() {
        @Override public void doClick(
          android.view.View p0
        ) {
          target.onFlashSwitcClicked();
        }
      });
    view = finder.findRequiredView(source, 2131296470, "field 'cameraSwitchView' and method 'onSwitchCameraClicked'");
    target.cameraSwitchView = finder.castView(view, 2131296470, "field 'cameraSwitchView'");
    view.setOnClickListener(
      new butterknife.internal.DebouncingOnClickListener() {
        @Override public void doClick(
          android.view.View p0
        ) {
          target.onSwitchCameraClicked();
        }
      });
    view = finder.findRequiredView(source, 2131296678, "field 'recordButton' and method 'onRecordButtonClicked'");
    target.recordButton = finder.castView(view, 2131296678, "field 'recordButton'");
    view.setOnClickListener(
      new butterknife.internal.DebouncingOnClickListener() {
        @Override public void doClick(
          android.view.View p0
        ) {
          target.onRecordButtonClicked();
        }
      });
    view = finder.findRequiredView(source, 2131296641, "field 'mediaActionSwitchView' and method 'onMediaActionSwitchClicked'");
    target.mediaActionSwitchView = finder.castView(view, 2131296641, "field 'mediaActionSwitchView'");
    view.setOnClickListener(
      new butterknife.internal.DebouncingOnClickListener() {
        @Override public void doClick(
          android.view.View p0
        ) {
          target.onMediaActionSwitchClicked();
        }
      });
    view = finder.findRequiredView(source, 2131296679, "field 'recordDurationText'");
    target.recordDurationText = finder.castView(view, 2131296679, "field 'recordDurationText'");
    view = finder.findRequiredView(source, 2131296681, "field 'recordSizeText'");
    target.recordSizeText = finder.castView(view, 2131296681, "field 'recordSizeText'");
    view = finder.findRequiredView(source, 2131296346, "field 'cameraLayout'");
    target.cameraLayout = view;
    view = finder.findRequiredView(source, 2131296309, "field 'addCameraButton' and method 'onAddCameraClicked'");
    target.addCameraButton = view;
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
    target.settingsView = null;
    target.flashSwitchView = null;
    target.cameraSwitchView = null;
    target.recordButton = null;
    target.mediaActionSwitchView = null;
    target.recordDurationText = null;
    target.recordSizeText = null;
    target.cameraLayout = null;
    target.addCameraButton = null;
  }
}
