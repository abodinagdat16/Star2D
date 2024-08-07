// Generated by view binder compiler. Do not edit!
package com.star4droid.star2d.evo.databinding;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.viewbinding.ViewBinding;
import androidx.viewbinding.ViewBindings;
import com.star4droid.star2d.evo.R;
import java.lang.NullPointerException;
import java.lang.Override;
import java.lang.String;

public final class PlayerBinding implements ViewBinding {
  @NonNull
  private final FrameLayout rootView;

  @NonNull
  public final TextView fpsText;

  @NonNull
  public final FrameLayout frame;

  @NonNull
  public final LinearLayout logo;

  @NonNull
  public final ImageView pause;

  private PlayerBinding(@NonNull FrameLayout rootView, @NonNull TextView fpsText,
      @NonNull FrameLayout frame, @NonNull LinearLayout logo, @NonNull ImageView pause) {
    this.rootView = rootView;
    this.fpsText = fpsText;
    this.frame = frame;
    this.logo = logo;
    this.pause = pause;
  }

  @Override
  @NonNull
  public FrameLayout getRoot() {
    return rootView;
  }

  @NonNull
  public static PlayerBinding inflate(@NonNull LayoutInflater inflater) {
    return inflate(inflater, null, false);
  }

  @NonNull
  public static PlayerBinding inflate(@NonNull LayoutInflater inflater, @Nullable ViewGroup parent,
      boolean attachToParent) {
    View root = inflater.inflate(R.layout.player, parent, false);
    if (attachToParent) {
      parent.addView(root);
    }
    return bind(root);
  }

  @NonNull
  public static PlayerBinding bind(@NonNull View rootView) {
    // The body of this method is generated in a way you would not otherwise write.
    // This is done to optimize the compiled bytecode for size and performance.
    int id;
    missingId: {
      id = R.id.fpsText;
      TextView fpsText = ViewBindings.findChildViewById(rootView, id);
      if (fpsText == null) {
        break missingId;
      }

      id = R.id.frame;
      FrameLayout frame = ViewBindings.findChildViewById(rootView, id);
      if (frame == null) {
        break missingId;
      }

      id = R.id.logo;
      LinearLayout logo = ViewBindings.findChildViewById(rootView, id);
      if (logo == null) {
        break missingId;
      }

      id = R.id.pause;
      ImageView pause = ViewBindings.findChildViewById(rootView, id);
      if (pause == null) {
        break missingId;
      }

      return new PlayerBinding((FrameLayout) rootView, fpsText, frame, logo, pause);
    }
    String missingId = rootView.getResources().getResourceName(id);
    throw new NullPointerException("Missing required view with ID: ".concat(missingId));
  }
}
