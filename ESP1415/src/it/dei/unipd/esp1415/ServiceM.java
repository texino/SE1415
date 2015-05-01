package it.dei.unipd.esp1415;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.util.Log;

public class ServiceM extends Service{

	@Override
	public int onStartCommand(Intent i,int flags,int c)
	{
		super.onStartCommand(i,flags,c);
		return Log.d("SERVICE","started Start");
		
	}
	
	@Override
	public IBinder onBind(Intent intent) {
		// TODO Auto-generated method stub
		float x=Float.parseFloat(intent.getStringExtra("x"));
		float y=Float.parseFloat(intent.getStringExtra("y"));
		float z=Float.parseFloat(intent.getStringExtra("z"));
		Log.d("SERVICE","started Bind");
		return null;
	}

}
