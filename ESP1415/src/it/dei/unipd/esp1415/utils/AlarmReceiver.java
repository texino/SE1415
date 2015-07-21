package it.dei.unipd.esp1415.utils;

import android.app.NotificationManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.support.v4.app.NotificationCompat;

import com.example.esp1415.R;

public class AlarmReceiver extends BroadcastReceiver {
	private static Context context;
	private static int mId = 1;
	private NotificationManager mNotificationManager;
	
	@Override
	public void onReceive(Context arg0, Intent arg1) {
		context = arg0;
		NotificationCompat.Builder mBuilder =
		        new NotificationCompat.Builder(context)
		        .setSmallIcon(R.drawable.button_play)
		        .setContentTitle("ToDoSession Reminder")
		        .setContentText("Remember to do a new daily session");
		mNotificationManager =
			    (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
		// mId allows you to update the notification later on.
		mNotificationManager.notify(mId, mBuilder.build());
	}

}