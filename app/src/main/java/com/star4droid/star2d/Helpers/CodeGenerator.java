package com.star4droid.star2d.Helpers;

import android.content.Context;
import android.net.Uri;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.view.View;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.star4droid.star2d.Items.*;
import com.star4droid.star2d.Utils;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class CodeGenerator {
	private static final String script_import ="//import .....script_here;";
	public interface GenerateListener {
		public void onGenerate(String s);
	}
	private static final String defualtItemCode=
	"\n		%1$s %2$s_def = new %1$s();\n"+//%1 => item def from ElementsDef class. 2 => name
	"%3$s \n		\n"+// => properites init...
	"			%2$s_def.elementEvents = new ElementEvent(){\n"+
	"%4$s \n};\n"+
	"	%2$s = (PlayerItem)(%2$s_def.build(this));\n";
	
	private static final String defaultLightCode = 
	"\n		%1$s %2$s_def = new %1$s();\n"+//%1 => item def from ElementsDef class. 2 => name
	"%3$s \n		\n"// => properites init...
	+"	%2$s = (%2$s_def.build(this));\n";
	
	private static final String defualtJointCode =
	"\n			%1$sDef %2$s_Def= new %1$sDef(); %3$s \n"; //e.g = WheelJointDef wj = new WheelJointDef();
	
	private static final String JointInit=
	"\n			%1$s_Def.initialize(%2$s);\n"+
	"%1$s = (%3$s)(this.world.createJoint(%1$s_Def));";
	
	public static void generateFor(Editor editor,final GenerateListener generateListener){
	    ArrayList<PropertySet<String,Object>> properties = new ArrayList<>();
	    for(int x=0;x<editor.getChildCount();x++){
	        View child = editor.getChildAt(x);
					if(Utils.isEditorItem(child)){
					    PropertySet<String,Object> propertySet=((EditorItem)child).getPropertySet();
					    properties.add(propertySet);
					}
	    }
	    generateFor(editor.getProject(), editor.getScene(),editor.getContext(),properties,generateListener);
	}
	
	public static void generateFor(Project project,String scene,Context context,ArrayList<PropertySet<String,Object>> properties,final GenerateListener generateListener){
		new Thread(){
			public void run(){
				String itemsCode="";
				String vars="",lightsVar="",jointVars="";
				final StringBuilder scriptBuilder= new StringBuilder("");
				boolean thereIsScript=false;
				ArrayList<String> scriptsList= new ArrayList<>();
				FileUtil.listDir(project.getScriptsPath(scene),scriptsList);
				
				for(String path:scriptsList){
					if(path.endsWith(".java")||path.endsWith(".code")){
						scriptBuilder.append(FileUtil.readFile(path));
					}
				}
				
				StringBuilder joint= new StringBuilder("");
				
				ArrayList<String> joints= new ArrayList<>();
				FileUtil.listDir(project.getJoints(scene),joints);
				
				for(String path:joints){
					String p= Uri.parse(path).getLastPathSegment();
					int shouldSkip = JointsHelper.get(p.split("-")[1],"params").split(",").length;
					
					if(p.contains("-")){
						jointVars+=p.split("-")[1]+" "+p.split("-")[0]+";\n";
						String initilaizer="";
						StringBuilder init = new StringBuilder("");
						ArrayList<HashMap<String, Object>> fields = new Gson().fromJson(FileUtil.readFile(path),new TypeToken<ArrayList<HashMap<String, Object>>>(){}.getType());
						for(HashMap<String,Object> hash:fields){
							if(shouldSkip>0){
								//skip initilaize vars...
								initilaizer+=(initilaizer.equals("")?"":",")+hash.get("value").toString().replace("&&",",");
								shouldSkip--;
								continue;
							}
							init.append(String.format(hash.get("code").toString().replace("ff","f"),p.split("-")[0]+"_Def"));
						}
						init.append(String.format(JointInit,p.split("-")[0],initilaizer,p.split("-")[1]));
						joint.append(String.format(defualtJointCode,p.split("-")[1],p.split("-")[0],init.toString()));
					}
					
				}
				
				String defEvents = Utils.readAssetFile("java/events.java",context);
				for(PropertySet<String,Object> propertySet:properties){
				        boolean isLight = propertySet.getString("TYPE").equals("LIGHT");
					    String[] eventsList ={"onClick","onTouchStart","onTouchEnd","OnBodyCreated","OnBodyUpdate","onBodyCollided","onBodyCollideEnd",""};
						try {
							String defType=getType(propertySet.getString("TYPE"));
							String initCode ="";
							
							String name = propertySet.getString("name");
							String script = propertySet.getString("Script");
							if(!isLight)
							for(int ev=0;ev<eventsList.length-1;ev++){
							    // String pth=eventsList[ev];
								eventsList[ev] = project.readEvent(scene,eventsList[ev], script);
							}
							eventsList[7]= "return \""+name+"\";";
							String events = String.format(defEvents,(Object[])eventsList);
							Class<?> cls= Class.forName("com.star4droid.star2d.ElementDefs."+defType);
							Object ins = cls.getConstructor().newInstance();
							for(Map.Entry<String,Object> entry:propertySet.entrySet()){
								//ignored properties...
								if("(TYPE)(Shape)(lock)(Script)(parent)".contains("("+entry.getKey()+")")) continue;
								if(entry.getKey().equals("lock")) continue;
								java.lang.reflect.Field field = cls.getField(entry.getKey().replace(" ","_"));
								String var_type = field.getType().getName().replace("java.lang.","");
								if(!"float_String_boolean_Vector2".contains(var_type)) continue;
								if(var_type.equals("float")&&field.getFloat(ins)==propertySet.getFloat(entry.getKey())) continue;
								if(var_type.equals("boolean")&&(field.getBoolean(ins)==propertySet.getString(entry.getKey()).equals("true"))) continue;
								if(var_type.equals("String")&&field.get(ins).toString().equals(propertySet.getString(entry.getKey()))) continue;
								String qu1=var_type.equals("String")?"\"":"";
								String qu2=(var_type.equals("String")?"\"":(var_type.equals("float")?"f":""));
								initCode = (initCode.equals("")?(""):(initCode+"\n"))+"			"+name+"_def."+entry.getKey().replace(" ","_")+"="+qu1+entry.getValue().toString()+qu2+";";
							}
							itemsCode=itemsCode+(itemsCode.equals("")?"":"\n")+String.format(isLight?defaultLightCode:defualtItemCode,defType,propertySet.getString("name"),initCode,events);
							
							if((!isLight)&&propertySet.getParent()!=null)
								itemsCode+=propertySet.getParent().getString("name")+".addChild("+name+");\n";
							if(!FileUtil.readFile(project.getBodyScriptPath(script,scene)).equals("")){
								itemsCode+=String.format("\n%1$s.setScript(new %1$sScript().setItem(%2$s).setStage(this));\n",script,name);
								thereIsScript=true;
							}
							if(!propertySet.getString("TYPE").contains("LIGHT"))
							vars+=(vars.equals("")?"PlayerItem ":",")+propertySet.getString("name");
							else lightsVar+=(lightsVar.equals("")?"Light ":",")+propertySet.getString("name");
						} catch(Exception ex){
							itemsCode=itemsCode+"\n //error in item at position : "+propertySet.getString("name")+", type : "+propertySet.getString("TYPE")+", \n /*error : \n"+Log.getStackTraceString(ex)+"\n */";
						}
				}
				
				for(PropertySet<String,Object> propertySet:properties){
				    if(propertySet.getString("TYPE").equals("LIGHT")&&!propertySet.getString("attach To").equals(""))
							    itemsCode+= "\n"+propertySet.getString("name")+".attachToBody("+
							    propertySet.getString("attach To")+".getBody());\n";
				}
				itemsCode+="\n"+joint.toString()+project.readEvent(scene,"OnCreate");
				if(!vars.equals("")) vars+=";";
				if(!lightsVar.equals("")) lightsVar+=";";
				final String code=itemsCode;
				final String variables=vars+"\n"+jointVars+"\n"+lightsVar;
				final boolean replaceImportOfTheScript = (!thereIsScript);
				if(generateListener!=null) {
					new Handler(Looper.getMainLooper()).post(()-> {
							String playerCode=Utils.readAssetFile("java/Player.java",context);
							playerCode=playerCode.replace(script_import,replaceImportOfTheScript?"":String.format("import com.star4droid.Game.Scripts.%1$s.*;",scene.toLowerCase()));
							generateListener.onGenerate(String.format(playerCode,scene.toLowerCase(),variables,code,project.readEvent(scene,"onPause"),project.readEvent(scene,"onResume"),project.readEvent(scene,"onStep"),project.readEvent(scene,"onCollisionStart"),project.readEvent(scene,"onCollisionEnd"),scriptBuilder.toString()));
					});
				}
			}
		}.start();
	}
	
	public static String getType(String type){
		if(type.equals("BOX")) return "BoxDef";
		if(type.equals("JOYSTICK")) return "JoyStickDef";
		if(type.equals("TEXT")) return "TextDef";
		if(type.equals("CIRCLE")) return "CircleDef";
		if(type.equals("PROGRESS")) return "ProgressDef";
		if(type.equals("LIGHT")) return "LightDef";
		return "Unknown";
	}
}