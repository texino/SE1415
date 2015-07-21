package it.dei.unipd.esp1415.utils;

import it.dei.unipd.esp1415.activity.SessionListActivity;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.net.Uri;
import android.preference.PreferenceManager;
import android.support.v4.app.NotificationCompat;
import android.text.TextUtils;

import com.example.esp1415.R;

public class AlarmReceiver extends BroadcastReceiver {
	private static Context context;
	private static int mId = 1;
	private NotificationManager mNotificationManager;

	@Override
	public void onReceive(Context arg0, Intent arg1) {
		context = arg0;
		Intent intent = new Intent(context, SessionListActivity.class);
		PendingIntent pIntent = PendingIntent.getActivity(context, 1, intent, Intent.FLAG_ACTIVITY_NEW_TASK);
		NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(
				context).setSmallIcon(R.drawable.button_play)
				.setContentTitle("ToDoSession Reminder")
				.setContentText("Remember to do a new daily session")
				.setLights(Color.GREEN, 1000, 1000)
				.setAutoCancel(true)
				.setContentIntent(pIntent);
		SharedPreferences sharedPref = PreferenceManager
				.getDefaultSharedPreferences(context);
		boolean vibration = sharedPref.getBoolean("vibrationkey", false);
		if (vibration)
			mBuilder.setVibrate(new long[] { 0, 500 });
		String stringRingtone = sharedPref.getString("ringtonekey", "");
		if (!TextUtils.isEmpty(stringRingtone))
			mBuilder.setSound(Uri.parse(stringRingtone));
		mNotificationManager = (NotificationManager) context
				.getSystemService(Context.NOTIFICATION_SERVICE);
		// mId allows you to update the notification later on.
		mNotificationManager.notify(mId, mBuilder.build());
	}

}