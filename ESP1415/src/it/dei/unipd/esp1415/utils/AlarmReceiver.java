package it.dei.unipd.esp1415.utils;

import it.dei.unipd.esp1415.activity.CurrentSessionActivity;
import it.dei.unipd.esp1415.activity.SessionListActivity;
import it.dei.unipd.esp1415.activity.SettingsActivity;
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
import android.util.Log;

import com.example.esp1415.R;

/**
 * AlarmReceiver class: receive the alarm intent and set a notification
 */
public class AlarmReceiver extends BroadcastReceiver {
	private static int mId = 1;
	private NotificationManager mNotificationManager;
	private boolean isSessionRunning;

	@Override
	public void onReceive(Context context, Intent intent) {
		// get application's preferences
		SharedPreferences sharedPref = PreferenceManager
				.getDefaultSharedPreferences(context);
		// if the system is rebooted check the notification alarm
		if ("android.intent.action.BOOT_COMPLETED".equals(intent.getAction())) {
			// ripristina l'allarme con le notifiche
			boolean activated = sharedPref.getBoolean("alarmkey", false);
			if (activated) {
				// set the alarm
				int hour;
				int minutes;
				// get stored values
				String stringHour = PreferenceStorage.getSimpleData(context,
						SettingsActivity.hourKey);
				String stringMinutes = PreferenceStorage.getSimpleData(context,
						SettingsActivity.minutesKey);
				if (!((stringHour.equals("")) || (stringMinutes.equals("")))) {
					try {
						// string value to int value
						hour = Integer.parseInt(stringHour);
						minutes = Integer.parseInt(stringMinutes);
						SettingsActivity.setAlarm(hour, minutes, context);
					} catch (NumberFormatException e) {
						Log.i("ERROR", "Parse hour or minutes error");
					}
				}
			}
		} else {
			// check if a session is already running
			String value = PreferenceStorage.getSimpleData(context,
					SessionListActivity.RUNNING);
			if (value.equals(""))
				isSessionRunning = false;
			else
				isSessionRunning = java.lang.Boolean.parseBoolean(value);
			// if a session is not running start the notification
			if (!isSessionRunning) {
				// set the intent to perform on click of the notification
				Intent intentClick = new Intent(context,
						CurrentSessionActivity.class);
				intentClick.putExtra(CurrentSessionActivity.EMPTY_TAG, true);
				PendingIntent pIntent = PendingIntent.getActivity(context, 1,
						intentClick, Intent.FLAG_ACTIVITY_NEW_TASK);
				// build the notification
				NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(
						context)
						.setSmallIcon(R.drawable.plus_circle)
						.setContentTitle(
								context.getString(R.string.notification_title))
						.setContentText(
								context.getString(R.string.notification_message))
						.setLights(Color.GREEN, 1000, 1000).setAutoCancel(true)
						.setContentIntent(pIntent);
				// check and set notification settings
				boolean vibration = sharedPref
						.getBoolean("vibrationkey", false);
				if (vibration)
					mBuilder.setVibrate(new long[] { 0, 500 });
				String stringRingtone = sharedPref.getString("ringtonekey", "");
				if (!TextUtils.isEmpty(stringRingtone))
					mBuilder.setSound(Uri.parse(stringRingtone));
				// set the notification
				mNotificationManager = (NotificationManager) context
						.getSystemService(Context.NOTIFICATION_SERVICE);
				mNotificationManager.notify(mId, mBuilder.build());
			}
		}
	}

}