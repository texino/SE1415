package it.dei.unipd.esp1415.utils;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.widget.Toast;

public class AlarmReceiver extends BroadcastReceiver {

	@Override
	public void onReceive(Context arg0, Intent arg1) {
		//TODO notifica da implementare
		Toast.makeText(arg0, "Alarm received!", Toast.LENGTH_LONG).show();
		Log.i("ALARM", "Passato da onReceive()");
	}

}