package com.star4droid.Game;

import android.view.MotionEvent;
import com.star4droid.star2d.player.*;
import android.content.Context;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.World;
import com.star4droid.star2d.Helpers.Project;
import com.star4droid.star2d.ElementDefs.*;
import android.view.View;
import android.graphics.Canvas;
import com.star4droid.star2d.Helpers.PropertySet;
import com.badlogic.gdx.physics.box2d.joints.*;
//import .....script_here;

public class %1$s extends PlayerView {
    %2$s
    public %1$s(Context context, String path, String scene){
        super(context,path,scene);
%3$s
    }
    @Override
    public void onPause(){
    super.onPause();
%4$s
    }
    @Override
    public void onResume(){
    super.onResume();
%5$s
    }
    
    @Override
    protected void onDraw(Canvas cv){
    super.onDraw(cv);
%6$s
    }
    @Override
    public void onCollisionBegin(PlayerItem body1,PlayerItem body2){
%7$s
	}
	@Override
	public void onCollisionEnd(PlayerItem body1,PlayerItem body2){
%8$s
	}
	//scripts goes here ...
	%9$s
}