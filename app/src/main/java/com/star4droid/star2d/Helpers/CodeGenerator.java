package com.star4droid.star2d.Helpers;

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
	"	%2$s = (PlayerItem)(%2$s_def.build(this));\n"
	+"	addView(%2$s.getView());";
	
	private static final String defualtJointCode =
	"\n			%1$sDef %2$s_Def= new %1$sDef(); %3$s \n"; //e.g = WheelJointDef wj = new WheelJointDef();
	
	private static final String JointInit=
	"\n			%1$s_Def.initialize(%2$s);\n"+
	"%1$s = (%3$s)(this.world.createJoint(%1$s_Def));";
	
	public static void generateFor(Editor editor,final GenerateListener generateListener){
		new Thread(){
			public void run(){
				String itemsCode="";
				String vars="";
				String jointVars="";
				final StringBuilder scriptBuilder= new StringBuilder("");
				boolean thereIsScript=false;
				ArrayList<String> scriptsList= new ArrayList<>();
				FileUtil.listDir(editor.getProject().getScriptsPath(editor.getScene()),scriptsList);
				
				for(String path:scriptsList){
					if(path.endsWith(".java")||path.endsWith(".code")){
						scriptBuilder.append(FileUtil.readFile(path));
					}
				}
				
				StringBuilder joint= new StringBuilder("");
				
				ArrayList<String> joints= new ArrayList<>();
				FileUtil.listDir(editor.getProject().getJoints(editor.getScene()),joints);
				
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
				
				String defEvents = Utils.readAssetFile("java/events.java",editor.getContext());
				for(int x=0;x<editor.getChildCount();x++){
					View child = editor.getChildAt(x);
					if(Utils.isEditorItem(child)){
					    String[] eventsList ={"onClick","onTouchStart","onTouchEnd","OnBodyCreated","OnBodyUpdate","onBodyCollided","onBodyCollideEnd",""};
						PropertySet<String,Object> propertySet=((EditorItem)child).getPropertySet();
						try {
							String defType=getType(child);
							String initCode ="";
							
							String name = propertySet.getString("name");
							String script = propertySet.getString("Script");
							for(int ev=0;ev<eventsList.length-1;ev++){
							    String pth=eventsList[ev];
								eventsList[ev] = editor.readEvent(eventsList[ev], script);
							}
							eventsList[7]= "return \""+name+"\";";
							String events = String.format(defEvents,(Object[])eventsList);
							Class<?> cls= Class.forName("com.star4droid.star2d.ElementDefs."+defType);
							for(Map.Entry<String,Object> entry:propertySet.entrySet()){
								//ignored properties...
								if("(TYPE)(lock)(Script)(parent)".contains("("+entry.getKey()+")")) continue;
								if(entry.getKey().equals("lock")) continue;
								
								String var_type = cls.getField(entry.getKey().replace(" ","_")).getType().getName().replace("java.lang.","");
								if(!"float_String_boolean_Vector2".contains(var_type)) continue;
								String qu1=var_type.equals("String")?"\"":"";
								String qu2=(var_type.equals("String")?"\"":(var_type.equals("float")?"f":""));
								initCode = (initCode.equals("")?(""):(initCode+"\n"))+"			"+name+"_def."+entry.getKey().replace(" ","_")+"="+qu1+entry.getValue().toString()+qu2+";";
							}
							itemsCode=itemsCode+(itemsCode.equals("")?"":"\n")+String.format(defualtItemCode,defType,propertySet.getString("name"),initCode,events);
							if(propertySet.getParent()!=null)
								itemsCode+=propertySet.getParent().getString("name")+".addChild("+name+");\n";
							if(!FileUtil.readFile(editor.getProject().getBodyScriptPath(script,editor.getScene())).equals("")){
								itemsCode+=String.format("\n%1$s.setScript(new %1$sScript(%2$s));\n",script,name);
								thereIsScript=true;
							}
							vars+=(vars.equals("")?"PlayerItem ":",")+propertySet.getString("name");
						} catch(Exception ex){
							itemsCode=itemsCode+"\n //error in item at position : "+x+", type : "+propertySet.getString("TYPE")+", \n /*error : \n"+Log.getStackTraceString(ex)+"\n */";
						}
					}
				}
				itemsCode+="\n"+joint.toString()+editor.readEvent("OnCreate");
				if(!vars.equals("")) vars+=";";
				final String code=itemsCode;
				final String variables=vars+"\n"+jointVars;
				final boolean replaceImportOfTheScript = (!thereIsScript);
				if(generateListener!=null) {
					new Handler(Looper.getMainLooper()).post(()-> {
							String playerCode=Utils.readAssetFile("java/Player.java",editor.getContext());
							playerCode=playerCode.replace(script_import,replaceImportOfTheScript?"":String.format("import com.star4droid.Game.Scripts.%1$s.*;",editor.getScene().toLowerCase()));
							generateListener.onGenerate(String.format(playerCode,editor.getScene().toLowerCase(),variables,code,editor.readEvent("onPause"),editor.readEvent("onResume"),editor.readEvent("onStep"),editor.readEvent("onCollisionStart"),editor.readEvent("onCollisionEnd"),scriptBuilder.toString()));
					});
				}
			}
		}.start();
	}
	
	public static String getType(View v){
		if(v instanceof BoxBody) return "BoxDef";
		if(v instanceof JoyStickItem) return "JoyStickDef";
		if(v instanceof TextItem) return "TextDef";
		if(v instanceof CircleBody) return "CircleDef";
		if(v instanceof ProgressItem) return "ProgressDef";
		return "Unknown";
	}
}