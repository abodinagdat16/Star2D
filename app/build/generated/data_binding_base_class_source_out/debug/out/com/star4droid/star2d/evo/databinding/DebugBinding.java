// Generated by view binder compiler. Do not edit!
package com.star4droid.star2d.evo.databinding;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.viewbinding.ViewBinding;
import androidx.viewbinding.ViewBindings;
import com.star4droid.star2d.evo.R;
import java.lang.NullPointerException;
import java.lang.Override;
import java.lang.String;

public final class DebugBinding implements ViewBinding {
  @NonNull
  private final LinearLayout rootView;

  @NonNull
  public final Button button1;

  @NonNull
  public final Button button2;

  @NonNull
  public final ImageView imageview1;

  @NonNull
  public final LinearLayout linear1;

  @NonNull
  public final LinearLayout linear2;

  @NonNull
  public final TextView textview1;

  @NonNull
  public final TextView textview2;

  @NonNull
  public final ScrollView vscroll1;

  private DebugBinding(@NonNull LinearLayout rootView, @NonNull Button button1,
      @NonNull Button button2, @NonNull ImageView imageview1, @NonNull LinearLayout linear1,
      @NonNull LinearLayout linear2, @NonNull TextView textview1, @NonNull TextView textview2,
      @NonNull ScrollView vscroll1) {
    this.rootView = rootView;
    this.button1 = button1;
    this.button2 = button2;
    this.imageview1 = imageview1;
    this.linear1 = linear1;
    this.linear2 = linear2;
    this.textview1 = textview1;
    this.textview2 = textview2;
    this.vscroll1 = vscroll1;
  }

  @Override
  @NonNull
  public LinearLayout getRoot() {
    return rootView;
  }

  @NonNull
  public static DebugBinding inflate(@NonNull LayoutInflater inflater) {
    return inflate(inflater, null, false);
  }

  @NonNull
  public static DebugBinding inflate(@NonNull LayoutInflater inflater, @Nullable ViewGroup parent,
      boolean attachToParent) {
    View root = inflater.inflate(R.layout.debug, parent, false);
    if (attachToParent) {
      parent.addView(root);
    }
    return bind(root);
  }

  @NonNull
  public static DebugBinding bind(@NonNull View rootView) {
    // The body of this method is generated in a way you would not otherwise write.
    // This is done to optimize the compiled bytecode for size and performance.
    int id;
    missingId: {
      id = R.id.button1;
      Button button1 = ViewBindings.findChildViewById(rootView, id);
      if (button1 == null) {
        break missingId;
      }

      id = R.id.button2;
      Button button2 = ViewBindings.findChildViewById(rootView, id);
      if (button2 == null) {
        break missingId;
      }

      id = R.id.imageview1;
      ImageView imageview1 = ViewBindings.findChildViewById(rootView, id);
      if (imageview1 == null) {
        break missingId;
      }

      id = R.id.linear1;
      LinearLayout linear1 = ViewBindings.findChildViewById(rootView, id);
      if (linear1 == null) {
        break missingId;
      }

      id = R.id.linear2;
      LinearLayout linear2 = ViewBindings.findChildViewById(rootView, id);
      if (linear2 == null) {
        break missingId;
      }

      id = R.id.textview1;
      TextView textview1 = ViewBindings.findChildViewById(rootView, id);
      if (textview1 == null) {
        break missingId;
      }

      id = R.id.textview2;
      TextView textview2 = ViewBindings.findChildViewById(rootView, id);
      if (textview2 == null) {
        break missingId;
      }

      id = R.id.vscroll1;
      ScrollView vscroll1 = ViewBindings.findChildViewById(rootView, id);
      if (vscroll1 == null) {
        break missingId;
      }

      return new DebugBinding((LinearLayout) rootView, button1, button2, imageview1, linear1,
          linear2, textview1, textview2, vscroll1);
    }
    String missingId = rootView.getResources().getResourceName(id);
    throw new NullPointerException("Missing required view with ID: ".concat(missingId));
  }
}
