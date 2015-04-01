package it.dei.unipd.esp1415;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.util.Log;

public class FirstService extends Service {

	private static String TAG = "TAG di prova";

	// call when created
	/*
	 * public void onCreate(){ super.onCreate(); }
	 */
	@Override
	public int onStartCommand(Intent intent, int flags, int startId) {
		//operations to do
		//this method write to the log of the phone
		Log.d(TAG, "FirstService started");
		//stop the service
		this.stopSelf();
		return Service.START_NOT_STICKY;
	}

	@Override
	public IBinder onBind(Intent intent) {
		// TODO for communication return IBinder implementation
		return null;
	}

	@Override
	public void onDestroy() {
		super.onDestroy();
		//this method write to the log of the phone
		Log.d(TAG, "FirstService destroyed");
	}
}
