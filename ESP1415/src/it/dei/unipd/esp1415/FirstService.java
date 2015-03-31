package it.dei.unipd.esp1415;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.util.Log;

public class FirstService extends Service {
	
	private static String TAG = "TAG di prova";
	
	@Override
	public IBinder onBind(Intent arg0) {
		return null;
	}
 
	public void onCreate(){
		super.onCreate();
	}
	@Override
	public int onStartCommand(Intent intent, int flags, int startId) {
	    /*handleCommand(intent);
	    // We want this service to continue running until it is explicitly
	    // stopped, so return sticky.
	    return START_STICKY;*/
		super.onStartCommand(intent, flags, startId);
		Log.d(TAG, "FirstService started");
		this.stopSelf();
		return START_STICKY;
	}
 
	@Override
	public void onDestroy() {
		super.onDestroy();
		Log.d(TAG, "FirstService destroyed");
	}
}
