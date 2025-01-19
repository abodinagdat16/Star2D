package com.star4droid.template;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.utils.Align;
import com.badlogic.gdx.utils.viewport.FitViewport;
import com.star4droid.template.Utils.Utils;

public class LoadingStage extends Stage {
	ProgressItem progressItem;
	public LoadingStage(){
		super(new FitViewport(Gdx.graphics.getWidth(),Gdx.graphics.getHeight()));
		Image logo = new Image(Utils.getDrawable(Utils.internal("images/logo.png")));
		float size = getViewport().getWorldWidth()*0.45f,
		height=getViewport().getWorldHeight(),
		width=getViewport().getWorldWidth();
		logo.setSize(size,size);
		addActor(logo);
		logo.setX(width*0.5f,Align.center);
		logo.setY(height*0.5f,Align.center);
		BitmapFont font = new BitmapFont(Gdx.files.internal("files/default.fnt"));
		Label.LabelStyle labelStyle = new Label.LabelStyle(font, Color.GOLD);
		Label label = new Label("Loading...",labelStyle);
		label.setFontScale(3);
		addActor(label);
		label.setX(width*0.5f-label.getWidth(),Align.center);
		label.setY(logo.getY()-label.getHeight()-25);
		progressItem = new ProgressItem(this);
		addActor(progressItem);
		progressItem.setSize(width*0.75f,100);
		progressItem.setY(label.getY()-60,Align.top);
		progressItem.setX(width*0.5f,Align.center);
	}
	
	public boolean isLoaded(){
		return progressItem.getProgress()==progressItem.getMax();
	}
	
	public void setProgress(float progress){
		progressItem.setProgress(progress);
	}
	
	public ProgressItem getProgressItem(){
		return progressItem;
	}
}