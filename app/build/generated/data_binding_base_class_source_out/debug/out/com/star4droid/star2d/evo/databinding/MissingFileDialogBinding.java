// Generated by view binder compiler. Do not edit!
package com.star4droid.star2d.evo.databinding;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.viewbinding.ViewBinding;
import androidx.viewbinding.ViewBindings;
import com.google.android.material.button.MaterialButton;
import com.star4droid.star2d.evo.R;
import java.lang.NullPointerException;
import java.lang.Override;
import java.lang.String;

public final class MissingFileDialogBinding implements ViewBinding {
  @NonNull
  private final LinearLayout rootView;

  @NonNull
  public final MaterialButton download;

  @NonNull
  public final LinearLayout linear1;

  @NonNull
  public final LinearLayout linear2;

  @NonNull
  public final TextView text;

  @NonNull
  public final Button tutorial;

  @NonNull
  public final ScrollView vscroll1;

  private MissingFileDialogBinding(@NonNull LinearLayout rootView, @NonNull MaterialButton download,
      @NonNull LinearLayout linear1, @NonNull LinearLayout linear2, @NonNull TextView text,
      @NonNull Button tutorial, @NonNull ScrollView vscroll1) {
    this.rootView = rootView;
    this.download = download;
    this.linear1 = linear1;
    this.linear2 = linear2;
    this.text = text;
    this.tutorial = tutorial;
    this.vscroll1 = vscroll1;
  }

  @Override
  @NonNull
  public LinearLayout getRoot() {
    return rootView;
  }

  @NonNull
  public static MissingFileDialogBinding inflate(@NonNull LayoutInflater inflater) {
    return inflate(inflater, null, false);
  }

  @NonNull
  public static MissingFileDialogBinding inflate(@NonNull LayoutInflater inflater,
      @Nullable ViewGroup parent, boolean attachToParent) {
    View root = inflater.inflate(R.layout.missing_file_dialog, parent, false);
    if (attachToParent) {
      parent.addView(root);
    }
    return bind(root);
  }

  @NonNull
  public static MissingFileDialogBinding bind(@NonNull View rootView) {
    // The body of this method is generated in a way you would not otherwise write.
    // This is done to optimize the compiled bytecode for size and performance.
    int id;
    missingId: {
      id = R.id.download;
      MaterialButton download = ViewBindings.findChildViewById(rootView, id);
      if (download == null) {
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

      id = R.id.text;
      TextView text = ViewBindings.findChildViewById(rootView, id);
      if (text == null) {
        break missingId;
      }

      id = R.id.tutorial;
      Button tutorial = ViewBindings.findChildViewById(rootView, id);
      if (tutorial == null) {
        break missingId;
      }

      id = R.id.vscroll1;
      ScrollView vscroll1 = ViewBindings.findChildViewById(rootView, id);
      if (vscroll1 == null) {
        break missingId;
      }

      return new MissingFileDialogBinding((LinearLayout) rootView, download, linear1, linear2, text,
          tutorial, vscroll1);
    }
    String missingId = rootView.getResources().getResourceName(id);
    throw new NullPointerException("Missing required view with ID: ".concat(missingId));
  }
}
