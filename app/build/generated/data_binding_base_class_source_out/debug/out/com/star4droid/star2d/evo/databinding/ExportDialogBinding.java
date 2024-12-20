// Generated by view binder compiler. Do not edit!
package com.star4droid.star2d.evo.databinding;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.AppCompatCheckBox;
import androidx.viewbinding.ViewBinding;
import androidx.viewbinding.ViewBindings;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputEditText;
import com.star4droid.star2d.evo.R;
import java.lang.NullPointerException;
import java.lang.Override;
import java.lang.String;

public final class ExportDialogBinding implements ViewBinding {
  @NonNull
  private final LinearLayout rootView;

  @NonNull
  public final MaterialButton export;

  @NonNull
  public final ImageView icon;

  @NonNull
  public final TextInputEditText name;

  @NonNull
  public final TextInputEditText packageName;

  @NonNull
  public final AppCompatCheckBox portrait;

  @NonNull
  public final TextInputEditText version;

  @NonNull
  public final TextInputEditText versionName;

  private ExportDialogBinding(@NonNull LinearLayout rootView, @NonNull MaterialButton export,
      @NonNull ImageView icon, @NonNull TextInputEditText name,
      @NonNull TextInputEditText packageName, @NonNull AppCompatCheckBox portrait,
      @NonNull TextInputEditText version, @NonNull TextInputEditText versionName) {
    this.rootView = rootView;
    this.export = export;
    this.icon = icon;
    this.name = name;
    this.packageName = packageName;
    this.portrait = portrait;
    this.version = version;
    this.versionName = versionName;
  }

  @Override
  @NonNull
  public LinearLayout getRoot() {
    return rootView;
  }

  @NonNull
  public static ExportDialogBinding inflate(@NonNull LayoutInflater inflater) {
    return inflate(inflater, null, false);
  }

  @NonNull
  public static ExportDialogBinding inflate(@NonNull LayoutInflater inflater,
      @Nullable ViewGroup parent, boolean attachToParent) {
    View root = inflater.inflate(R.layout.export_dialog, parent, false);
    if (attachToParent) {
      parent.addView(root);
    }
    return bind(root);
  }

  @NonNull
  public static ExportDialogBinding bind(@NonNull View rootView) {
    // The body of this method is generated in a way you would not otherwise write.
    // This is done to optimize the compiled bytecode for size and performance.
    int id;
    missingId: {
      id = R.id.export;
      MaterialButton export = ViewBindings.findChildViewById(rootView, id);
      if (export == null) {
        break missingId;
      }

      id = R.id.icon;
      ImageView icon = ViewBindings.findChildViewById(rootView, id);
      if (icon == null) {
        break missingId;
      }

      id = R.id.name;
      TextInputEditText name = ViewBindings.findChildViewById(rootView, id);
      if (name == null) {
        break missingId;
      }

      id = R.id.package_name;
      TextInputEditText packageName = ViewBindings.findChildViewById(rootView, id);
      if (packageName == null) {
        break missingId;
      }

      id = R.id.portrait;
      AppCompatCheckBox portrait = ViewBindings.findChildViewById(rootView, id);
      if (portrait == null) {
        break missingId;
      }

      id = R.id.version;
      TextInputEditText version = ViewBindings.findChildViewById(rootView, id);
      if (version == null) {
        break missingId;
      }

      id = R.id.version_name;
      TextInputEditText versionName = ViewBindings.findChildViewById(rootView, id);
      if (versionName == null) {
        break missingId;
      }

      return new ExportDialogBinding((LinearLayout) rootView, export, icon, name, packageName,
          portrait, version, versionName);
    }
    String missingId = rootView.getResources().getResourceName(id);
    throw new NullPointerException("Missing required view with ID: ".concat(missingId));
  }
}
