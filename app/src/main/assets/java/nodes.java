--main
split
addSource code
<<=>>
%1$s
%2$s
split
try
<<=>>
try {
%1$s
} catch(Throwable e){}
split
void name
<<=>>
public void %1$s {
%2$s
}
split
if__star__if__ boolean
<<=>>
if(%1$s){
%2$s
} else {
%3$s
}
%4$s
split
openUrl url
<<=>>
openUrl("%1$s");
%2$s
split
OpenScene scene
<<=>>
openScene("%1$s");
%2$s
split
finish
<<=>>
finish();
%1$s
split
_DoAfter delay
<<=>>
{
final int timer_id = timers.size();
new StarTimer(new Runnable() {
							@Override
							public void run() {
							try {
							%2$s
							} catch(Exception ex){
							debug("timer error : "+ex.toString());
							}
							}
					},(long)(%1$s),false);
	}
split
cancelTimer
<<=>>
timers.get(timer_id).cancel();
%1$s
split
_repeatEvery delay
<<=>>
{
final int timer_id = timers.size();
new StarTimer(new Runnable() {
							@Override
							public void run() {
							try {
							%2$s
							} catch(Exception ex){
							timers.get(timer_id).cancel();
							debug("timer error : "+ex.toString());
							}
							}
					},(long)(%1$s),true);
	}
split
writeToFile path string
<<=>>
write(%1$s,%2$s);
%3$s
split
--Camera
split
setZoom zoom
<<=>>
setZoom((float)(%1$s));
%2$s
split
cameraFollow body
<<=>>
setCameraXY(%1$s,true);
%2$s
split
setCameraXAt body fixed?
<<=>>
setCameraX(%1$s,%2$s);
%3$s
split
setCameraYAt body fixed
<<=>>
setCameraY(%1$s,%2$s);
%3$s
split
setCameraXY x y
<<=>>
setCameraXY((float)(%1$s),(float)(%2$s));
%3$s
split
setCameraCenter x y
<<=>>
setCameraCenter((float)(%1$s),(float)(%2$s));
%3$s
split
setCameraX x
<<=>>
setCameraX((float)(%1$s));
%2$s
split
setCameraY y
<<=>>
setCameraY((float)(%1$s));
%2$s
split
setCameraOffset offsetX offsetY
<<=>>
setCameraOffset((float)(%1$s),(float)(%2$s));
%3$s
split
--Configurations
split
setSteps steps
<<=>>
setSteps(%1$s);
%2$s
split
ifCollision__star__if__ body1 body2
<<=>>
if(checkCollision(%1$s,%2$s)){
%3$s
} else {
%4$s
}
%5$s
split
Pause
<<=>>
Pause();
%1$s
split
Resume
<<=>>
Resume();
%1$s
saveValue key value
<<=>>
saveValue(%1$s,%2$s);
%3$s
split
setGravity x y
<<=>>
setGravity(%1$s,%2$s);
%3$s
split
--Debug
split
Toast message
<<=>>
toast(%1$s);
%2$s
split
debug message
<<=>>
debug(%1$s);
%2$s
split
--Ui Nodes
split
setVisibility item visibility
<<=>>
%1$s.getView().setVisibility(%2$s);
%3$s
split
setPath video path
<<=>>
//ToDo : Video Element...
//%1$s.asVideo().setVideoPath("%2$s");
%3$s
split
PauseVideo video
<<=>>
//%1$s.asVideo().pause();
%2$s
split
PlayVideo video
<<=>>
//%1$s.asVideo().start();
%2$s
split
setText ui text
<<=>>
%1$s.setItemText(%2$s);
%3$s
split
setAlpha body alpha
<<=>>
%1$s.getView().setAlpha(%2$s);
%3$s
split
setProgress progress value
<<=>>
%1$s.setProgress((int)(%2$s));
%3$s
split
setMax progress max
<<=>>
%1$s.setMax((int)(%2$s));
%3$s
split
--BodyModify
split
setScaleXY item scaleX scaleY
<<=>>
%1$s.getView().setScaleX((float)(%2$s));
%1$s.getView().setScaleY((float)(%3$s));
%4$s
split
setScaleX item scaleX
<<=>>
%1$s.getView().setScaleX((float)(%2$s));
%3$s
split
setScaleY item scaleY
<<=>>
%1$s.getView().setScaleY((float)(%2$s));
%3$s
split
setTransform body x y angle
<<=>>
%1$s.getBody().setTransform((float)(%2$s),(float)(%3$s),(float)(%4$s));
%5$s
split
setAnimation body animation
<<=>>
setAnimation(%1$s,"%2$s");
%3$s
split
setX body x
<<=>>
%1$s.getBody().setTransform((float)(%2$s),%1$s.getBody().getPosition().y,(float)(%1$s.getView().getRotation()));
%3$s
split
setY body y
<<=>>
%1$s.getBody().setTransform(%1$s.getBody().getPosition().x,(float)(%2$s),(%1$s).getView().getRotation());
%3$s
split
setAngle body angle
<<=>>
%1$s.getBody().setTransform(%1$s.getBody().getPosition(),(float)(%2$s));
%3$s
split
setImage body image
<<=>>
setImage(%1$s,"%2$s");
%3$s
split
copyBody body var
<<=>>
PlayerItem %2$s = %1$s.getClone();
%3$s
split
destroy body
<<=>>
%1$s.destroy();
%2$s
split
moveTowardBody body target step
<<=>>
//%1$s.moveTowardBody(%2$s,(float)(%3$s));
%4$s
split
moveTowardBody body Vector2 step
<<=>>
//%1$s.moveTowardPoint(%2$s,(float)(%3$s));
%4$s
split
--bodyControl
split
applyForceToCenter body x y wake
<<=>>
%1$s.getBody().applyForceToCenter((float)(%2$s),(float)(%3$s),%4$s);
%5$s
split
applyLinearImpulse body x y pointX pointY wake
<<=>>
%1$s.getBody().applyLinearImpulse((float)(%2$s),(float)(%3$s),(float)(%4$s),(float)(%5$s), %6$s);
%7$s
split
applyLinearImpulseToCenter body x y wake
<<=>>
%1$s.getBody().applyLinearImpulse((float)(%2$s),(float)(%3$s),%1$s.getBody().getWorldCenter().x,%1$s.getBody().getWorldCenter().y, %4$s);
%5$s
split
applyForce body x y pointX pointY wake
<<=>>
%1$s.getBody().applyForce((float)(%2$s),(float)(%3$s),(float)(%4$s),(float)(%5$s),%6$s);
%7$s
split
setAngularVelocity body velocity
<<=>>
%1$s.getBody().setAngularVelocity((float)(%2$s));
%3$s
split
applyAngularImpulse body impulse wake
<<=>>
%1$s.getBody().applyAngularImpulse((float)(%2$s),%3$s);
%4$s
split
setGravityScale body GravityScale
<<=>>
%1$s.getBody().setGravityScale(%2$s);
%3$s
split
setLinearVelocity body xVelocity yVelocity
<<=>>
%1$s.getBody().setLinearVelocity((float)(%2$s),(float)(%3$s));
%4$s
split
applyTorque body torque wake
<<=>>
%1$s.getBody().applyTorque((float)(%2$s),%3$s);
%4$s
split
setLinearDamping body value
<<=>>
%1$s.getBody().setLinearDamping((float)(%2$s));
%3$s
split
--bodyBooleans
split
setAwake body wake
<<=>>
%1$s.getBody().setAwake(%2$s);
%3$s
split
setBullet body bullet
<<=>>
%1$s.getBody().setBullet(%2$s);
%3$s
split
setFixedRotation body fixed
<<=>>
%1$s.getBody().setFixedRotation(%2$s);
%3$s
split
setActive body active
<<=>>
%1$s.getBody().setActive(%2$s);
%3$s
split
setSleepingAllowed body sleeping
<<=>>
%1$s.getBody().setSleepingAllowed(%2$s);
%3$s
split
--Ui_Animation
split
setProperty ui property value
<<=>>
%1$s.getView().animate().%2$s(%3$s);
%4$s
split
startAnimation UI
<<=>>
%1$s.getView().animate().start();
%2$s
split
--Sounds
split
createSound id sound repeat?
<<=>>
createSound("%1$s","%2$s");
loopSound("%1$s",%3$s);
%4$s
split
playSound SoundID
<<=>>
startSound("%1$s");
%2$s
split
pauseSound SoundID
<<=>>
pauseSound("%1$s");
%2$s
split
releaseSound SoundID
<<=>>
releaseSound("%1$s");
%2$s