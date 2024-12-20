// Generated by view binder compiler. Do not edit!
package com.star4droid.star2d.evo.databinding;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.HorizontalScrollView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.AppCompatSpinner;
import androidx.viewbinding.ViewBinding;
import androidx.viewbinding.ViewBindings;
import androidx.viewpager2.widget.ViewPager2;
import com.star4droid.star2d.Items.Editor;
import com.star4droid.star2d.evo.R;
import java.lang.NullPointerException;
import java.lang.Override;
import java.lang.String;

public final class EditorBinding implements ViewBinding {
  @NonNull
  private final LinearLayout rootView;

  @NonNull
  public final ImageView addBody;

  @NonNull
  public final ImageView back;

  @NonNull
  public final AppCompatSpinner bodiesSpinner;

  @NonNull
  public final LinearLayout bottomLin;

  @NonNull
  public final HorizontalScrollView bottomScroll;

  @NonNull
  public final ImageView centerCamera;

  @NonNull
  public final ImageView copyScene;

  @NonNull
  public final ImageView deleteBody;

  @NonNull
  public final ImageView deleteScene;

  @NonNull
  public final Editor editor;

  @NonNull
  public final ImageView filesManager;

  @NonNull
  public final FrameLayout frame;

  @NonNull
  public final ImageView grid;

  @NonNull
  public final LinearLayout linear4;

  @NonNull
  public final LinearLayout linear5;

  @NonNull
  public final ImageView lock;

  @NonNull
  public final ImageView move;

  @NonNull
  public final ImageView play;

  @NonNull
  public final ProgressBar progress;

  @NonNull
  public final LinearLayout propsLinear;

  @NonNull
  public final ImageView redo;

  @NonNull
  public final ImageView renameScene;

  @NonNull
  public final LinearLayout rightLinear;

  @NonNull
  public final ImageView rightSwipe;

  @NonNull
  public final ViewPager2 rightVp;

  @NonNull
  public final ImageView rotate;

  @NonNull
  public final ImageView rotateScreen;

  @NonNull
  public final ImageView save;

  @NonNull
  public final ImageView scale;

  @NonNull
  public final ImageView sceneColor;

  @NonNull
  public final LinearLayout sceneLinear;

  @NonNull
  public final AppCompatSpinner scenesSpinner;

  @NonNull
  public final TextView textview1;

  @NonNull
  public final TextView textview2;

  @NonNull
  public final LinearLayout topLinear;

  @NonNull
  public final HorizontalScrollView topScroll;

  @NonNull
  public final ImageView undo;

  private EditorBinding(@NonNull LinearLayout rootView, @NonNull ImageView addBody,
      @NonNull ImageView back, @NonNull AppCompatSpinner bodiesSpinner,
      @NonNull LinearLayout bottomLin, @NonNull HorizontalScrollView bottomScroll,
      @NonNull ImageView centerCamera, @NonNull ImageView copyScene, @NonNull ImageView deleteBody,
      @NonNull ImageView deleteScene, @NonNull Editor editor, @NonNull ImageView filesManager,
      @NonNull FrameLayout frame, @NonNull ImageView grid, @NonNull LinearLayout linear4,
      @NonNull LinearLayout linear5, @NonNull ImageView lock, @NonNull ImageView move,
      @NonNull ImageView play, @NonNull ProgressBar progress, @NonNull LinearLayout propsLinear,
      @NonNull ImageView redo, @NonNull ImageView renameScene, @NonNull LinearLayout rightLinear,
      @NonNull ImageView rightSwipe, @NonNull ViewPager2 rightVp, @NonNull ImageView rotate,
      @NonNull ImageView rotateScreen, @NonNull ImageView save, @NonNull ImageView scale,
      @NonNull ImageView sceneColor, @NonNull LinearLayout sceneLinear,
      @NonNull AppCompatSpinner scenesSpinner, @NonNull TextView textview1,
      @NonNull TextView textview2, @NonNull LinearLayout topLinear,
      @NonNull HorizontalScrollView topScroll, @NonNull ImageView undo) {
    this.rootView = rootView;
    this.addBody = addBody;
    this.back = back;
    this.bodiesSpinner = bodiesSpinner;
    this.bottomLin = bottomLin;
    this.bottomScroll = bottomScroll;
    this.centerCamera = centerCamera;
    this.copyScene = copyScene;
    this.deleteBody = deleteBody;
    this.deleteScene = deleteScene;
    this.editor = editor;
    this.filesManager = filesManager;
    this.frame = frame;
    this.grid = grid;
    this.linear4 = linear4;
    this.linear5 = linear5;
    this.lock = lock;
    this.move = move;
    this.play = play;
    this.progress = progress;
    this.propsLinear = propsLinear;
    this.redo = redo;
    this.renameScene = renameScene;
    this.rightLinear = rightLinear;
    this.rightSwipe = rightSwipe;
    this.rightVp = rightVp;
    this.rotate = rotate;
    this.rotateScreen = rotateScreen;
    this.save = save;
    this.scale = scale;
    this.sceneColor = sceneColor;
    this.sceneLinear = sceneLinear;
    this.scenesSpinner = scenesSpinner;
    this.textview1 = textview1;
    this.textview2 = textview2;
    this.topLinear = topLinear;
    this.topScroll = topScroll;
    this.undo = undo;
  }

  @Override
  @NonNull
  public LinearLayout getRoot() {
    return rootView;
  }

  @NonNull
  public static EditorBinding inflate(@NonNull LayoutInflater inflater) {
    return inflate(inflater, null, false);
  }

  @NonNull
  public static EditorBinding inflate(@NonNull LayoutInflater inflater, @Nullable ViewGroup parent,
      boolean attachToParent) {
    View root = inflater.inflate(R.layout.editor, parent, false);
    if (attachToParent) {
      parent.addView(root);
    }
    return bind(root);
  }

  @NonNull
  public static EditorBinding bind(@NonNull View rootView) {
    // The body of this method is generated in a way you would not otherwise write.
    // This is done to optimize the compiled bytecode for size and performance.
    int id;
    missingId: {
      id = R.id.addBody;
      ImageView addBody = ViewBindings.findChildViewById(rootView, id);
      if (addBody == null) {
        break missingId;
      }

      id = R.id.back;
      ImageView back = ViewBindings.findChildViewById(rootView, id);
      if (back == null) {
        break missingId;
      }

      id = R.id.bodiesSpinner;
      AppCompatSpinner bodiesSpinner = ViewBindings.findChildViewById(rootView, id);
      if (bodiesSpinner == null) {
        break missingId;
      }

      id = R.id.bottomLin;
      LinearLayout bottomLin = ViewBindings.findChildViewById(rootView, id);
      if (bottomLin == null) {
        break missingId;
      }

      id = R.id.bottom_scroll;
      HorizontalScrollView bottomScroll = ViewBindings.findChildViewById(rootView, id);
      if (bottomScroll == null) {
        break missingId;
      }

      id = R.id.center_camera;
      ImageView centerCamera = ViewBindings.findChildViewById(rootView, id);
      if (centerCamera == null) {
        break missingId;
      }

      id = R.id.copyScene;
      ImageView copyScene = ViewBindings.findChildViewById(rootView, id);
      if (copyScene == null) {
        break missingId;
      }

      id = R.id.deleteBody;
      ImageView deleteBody = ViewBindings.findChildViewById(rootView, id);
      if (deleteBody == null) {
        break missingId;
      }

      id = R.id.deleteScene;
      ImageView deleteScene = ViewBindings.findChildViewById(rootView, id);
      if (deleteScene == null) {
        break missingId;
      }

      id = R.id.editor;
      Editor editor = ViewBindings.findChildViewById(rootView, id);
      if (editor == null) {
        break missingId;
      }

      id = R.id.files_manager;
      ImageView filesManager = ViewBindings.findChildViewById(rootView, id);
      if (filesManager == null) {
        break missingId;
      }

      id = R.id.frame;
      FrameLayout frame = ViewBindings.findChildViewById(rootView, id);
      if (frame == null) {
        break missingId;
      }

      id = R.id.grid;
      ImageView grid = ViewBindings.findChildViewById(rootView, id);
      if (grid == null) {
        break missingId;
      }

      id = R.id.linear4;
      LinearLayout linear4 = ViewBindings.findChildViewById(rootView, id);
      if (linear4 == null) {
        break missingId;
      }

      id = R.id.linear5;
      LinearLayout linear5 = ViewBindings.findChildViewById(rootView, id);
      if (linear5 == null) {
        break missingId;
      }

      id = R.id.lock;
      ImageView lock = ViewBindings.findChildViewById(rootView, id);
      if (lock == null) {
        break missingId;
      }

      id = R.id.move;
      ImageView move = ViewBindings.findChildViewById(rootView, id);
      if (move == null) {
        break missingId;
      }

      id = R.id.play;
      ImageView play = ViewBindings.findChildViewById(rootView, id);
      if (play == null) {
        break missingId;
      }

      id = R.id.progress;
      ProgressBar progress = ViewBindings.findChildViewById(rootView, id);
      if (progress == null) {
        break missingId;
      }

      id = R.id.propsLinear;
      LinearLayout propsLinear = ViewBindings.findChildViewById(rootView, id);
      if (propsLinear == null) {
        break missingId;
      }

      id = R.id.redo;
      ImageView redo = ViewBindings.findChildViewById(rootView, id);
      if (redo == null) {
        break missingId;
      }

      id = R.id.renameScene;
      ImageView renameScene = ViewBindings.findChildViewById(rootView, id);
      if (renameScene == null) {
        break missingId;
      }

      id = R.id.right_linear;
      LinearLayout rightLinear = ViewBindings.findChildViewById(rootView, id);
      if (rightLinear == null) {
        break missingId;
      }

      id = R.id.right_swipe;
      ImageView rightSwipe = ViewBindings.findChildViewById(rootView, id);
      if (rightSwipe == null) {
        break missingId;
      }

      id = R.id.right_vp;
      ViewPager2 rightVp = ViewBindings.findChildViewById(rootView, id);
      if (rightVp == null) {
        break missingId;
      }

      id = R.id.rotate;
      ImageView rotate = ViewBindings.findChildViewById(rootView, id);
      if (rotate == null) {
        break missingId;
      }

      id = R.id.rotateScreen;
      ImageView rotateScreen = ViewBindings.findChildViewById(rootView, id);
      if (rotateScreen == null) {
        break missingId;
      }

      id = R.id.save;
      ImageView save = ViewBindings.findChildViewById(rootView, id);
      if (save == null) {
        break missingId;
      }

      id = R.id.scale;
      ImageView scale = ViewBindings.findChildViewById(rootView, id);
      if (scale == null) {
        break missingId;
      }

      id = R.id.sceneColor;
      ImageView sceneColor = ViewBindings.findChildViewById(rootView, id);
      if (sceneColor == null) {
        break missingId;
      }

      id = R.id.sceneLinear;
      LinearLayout sceneLinear = ViewBindings.findChildViewById(rootView, id);
      if (sceneLinear == null) {
        break missingId;
      }

      id = R.id.scenesSpinner;
      AppCompatSpinner scenesSpinner = ViewBindings.findChildViewById(rootView, id);
      if (scenesSpinner == null) {
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

      id = R.id.topLinear;
      LinearLayout topLinear = ViewBindings.findChildViewById(rootView, id);
      if (topLinear == null) {
        break missingId;
      }

      id = R.id.top_scroll;
      HorizontalScrollView topScroll = ViewBindings.findChildViewById(rootView, id);
      if (topScroll == null) {
        break missingId;
      }

      id = R.id.undo;
      ImageView undo = ViewBindings.findChildViewById(rootView, id);
      if (undo == null) {
        break missingId;
      }

      return new EditorBinding((LinearLayout) rootView, addBody, back, bodiesSpinner, bottomLin,
          bottomScroll, centerCamera, copyScene, deleteBody, deleteScene, editor, filesManager,
          frame, grid, linear4, linear5, lock, move, play, progress, propsLinear, redo, renameScene,
          rightLinear, rightSwipe, rightVp, rotate, rotateScreen, save, scale, sceneColor,
          sceneLinear, scenesSpinner, textview1, textview2, topLinear, topScroll, undo);
    }
    String missingId = rootView.getResources().getResourceName(id);
    throw new NullPointerException("Missing required view with ID: ".concat(missingId));
  }
}
