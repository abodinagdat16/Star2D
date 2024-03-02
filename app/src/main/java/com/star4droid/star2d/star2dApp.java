package com.star4droid.star2d;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.app.Application;
import android.os.Process;
import android.util.Log;
import com.star4droid.star2d.Helpers.EngineSettings;
import com.star4droid.star2d.Helpers.FileUtil;

public class star2dApp extends Application {
	private Thread.UncaughtExceptionHandler uncaughtExceptionHandler;
	private static Context mApplicationContext;
	public static Context getContext() {
		return mApplicationContext;
	}
	public void onCreate() {
		EngineSettings.init(this);
		Utils.setLanguage(this);
		mApplicationContext = this.getApplicationContext();
		this.uncaughtExceptionHandler = Thread.getDefaultUncaughtExceptionHandler();
		Thread.setDefaultUncaughtExceptionHandler(new Thread.UncaughtExceptionHandler(){
			
			public void uncaughtException(Thread thread, Throwable throwable) {
				final Intent intent = new Intent(mApplicationContext, DebugActivity.class);
				intent.putExtra("error", Log.getStackTraceString(throwable));
				intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
				PendingIntent pendingIntent = PendingIntent.getActivity(getApplicationContext(), 11111, intent, PendingIntent.FLAG_ONE_SHOT);
				int x=0;
				String str=FileUtil.getPackageDataDir(star2dApp.this)+"/logs/log"+"%1$s"+".txt";
				while(FileUtil.isExistFile(FileUtil.getPackageDataDir(star2dApp.this)+"/logs/log"+x+".txt")){
					x++;
				}
				String log=Log.getStackTraceString(throwable);
				if(x>0&&FileUtil.readFile(String.format(str,x+"")).equals(log))
				FileUtil.writeFile(String.format(str,x+""),log);
				AlarmManager am = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
				am.set(AlarmManager.ELAPSED_REALTIME_WAKEUP, 1000, pendingIntent);
				Process.killProcess(Process.myPid());
				System.exit(1);
				mApplicationContext.startActivity(intent);
				//Logger.initialize(this);
				star2dApp.this.uncaughtExceptionHandler.uncaughtException(thread, throwable);
			}
		});
		super.onCreate();
	}
}