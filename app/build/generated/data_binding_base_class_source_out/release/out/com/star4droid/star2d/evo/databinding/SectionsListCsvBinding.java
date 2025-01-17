// Generated by view binder compiler. Do not edit!
package com.star4droid.star2d.evo.databinding;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
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

public final class SectionsListCsvBinding implements ViewBinding {
  @NonNull
  private final LinearLayout rootView;

  @NonNull
  public final ImageView icon;

  @NonNull
  public final LinearLayout linear1;

  @NonNull
  public final TextView name;

  private SectionsListCsvBinding(@NonNull LinearLayout rootView, @NonNull ImageView icon,
      @NonNull LinearLayout linear1, @NonNull TextView name) {
    this.rootView = rootView;
    this.icon = icon;
    this.linear1 = linear1;
    this.name = name;
  }

  @Override
  @NonNull
  public LinearLayout getRoot() {
    return rootView;
  }

  @NonNull
  public static SectionsListCsvBinding inflate(@NonNull LayoutInflater inflater) {
    return inflate(inflater, null, false);
  }

  @NonNull
  public static SectionsListCsvBinding inflate(@NonNull LayoutInflater inflater,
      @Nullable ViewGroup parent, boolean attachToParent) {
    View root = inflater.inflate(R.layout.sections_list_csv, parent, false);
    if (attachToParent) {
      parent.addView(root);
    }
    return bind(root);
  }

  @NonNull
  public static SectionsListCsvBinding bind(@NonNull View rootView) {
    // The body of this method is generated in a way you would not otherwise write.
    // This is done to optimize the compiled bytecode for size and performance.
    int id;
    missingId: {
      id = R.id.icon;
      ImageView icon = ViewBindings.findChildViewById(rootView, id);
      if (icon == null) {
        break missingId;
      }

      id = R.id.linear1;
      LinearLayout linear1 = ViewBindings.findChildViewById(rootView, id);
      if (linear1 == null) {
        break missingId;
      }

      id = R.id.name;
      TextView name = ViewBindings.findChildViewById(rootView, id);
      if (name == null) {
        break missingId;
      }

      return new SectionsListCsvBinding((LinearLayout) rootView, icon, linear1, name);
    }
    String missingId = rootView.getResources().getResourceName(id);
    throw new NullPointerException("Missing required view with ID: ".concat(missingId));
  }
}
