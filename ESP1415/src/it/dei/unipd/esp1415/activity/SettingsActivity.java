package it.dei.unipd.esp1415.activity;

import it.dei.unipd.esp1415.utils.AlarmReceiver;
import it.dei.unipd.esp1415.utils.PreferenceStorage;

import java.util.Calendar;
import java.util.concurrent.TimeUnit;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.app.TimePickerDialog;
import android.app.TimePickerDialog.OnTimeSetListener;
import android.content.Context;
import android.content.Intent;
import android.media.Ringtone;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Bundle;
import android.preference.CheckBoxPreference;
import android.preference.EditTextPreference;
import android.preference.Preference;
import android.preference.Preference.OnPreferenceClickListener;
import android.preference.PreferenceActivity;
import android.preference.PreferenceManager;
import android.preference.RingtonePreference;
import android.text.TextUtils;
import android.util.Log;
import android.widget.TimePicker;
import android.widget.Toast;

import com.example.esp1415.R;

/**
 * SettingsActivity class: sets and organizes settings for the application
 */
public class SettingsActivity extends PreferenceActivity {
	//alarm variables
	private int hour;
	private int minutes;
	final static int RQS_1 = 1;
	//settings variables
	private String alarmSummary;
	private TimePickerDialog timePickerDialog;
	private static Context context;

	// listener for the choice of alarm time
	private OnTimeSetListener mOnTimeSetListener = new OnTimeSetListener() {

		@Override
		public void onTimeSet(TimePicker view, int hourOfDay, int minutesOfDay) {
			// update the current variables (hour and minutes)
			hour = hourOfDay;
			minutes = minutesOfDay;
			// saving values
			PreferenceStorage.storeSimpleData(context, "HOUR", "" + hour);
			PreferenceStorage.storeSimpleData(context, "MINUTES", "" + minutes);
			// update summary
			bindPreferenceSummaryToValue(findPreference("alarmtime_key"));
			// set the alarm
			setAlarm();
		}
	};

	// this method set the reminder through AlarmManager
	private void setAlarm() {

		// settings for alarm
		Calendar calNow = Calendar.getInstance();
		Calendar calSet = (Calendar) calNow.clone();

		// bind selected values hour, minutes
		calSet.set(Calendar.HOUR_OF_DAY, hour);
		calSet.set(Calendar.MINUTE, minutes);
		calSet.set(Calendar.SECOND, 0);
		calSet.set(Calendar.MILLISECOND, 0);

		// if set time is passed for today, count for tomorrow
		if (calSet.compareTo(calNow) <= 0) {
			calSet.add(Calendar.DATE, 1);
		}

		// AlarmManager settings
		Intent intent = new Intent(getBaseContext(), AlarmReceiver.class);
		PendingIntent pendingIntent = PendingIntent.getBroadcast(
				getBaseContext(), RQS_1, intent, 0);
		
		AlarmManager alarmManager = (AlarmManager) getSystemService(Context.ALARM_SERVICE);

		alarmManager.set(AlarmManager.RTC_WAKEUP, calSet.getTimeInMillis(),
				pendingIntent);

		alarmManager.setRepeating(AlarmManager.RTC_WAKEUP,
				calSet.getTimeInMillis(), TimeUnit.DAYS.toMillis(1),
				pendingIntent);

	}

	// this method cancel the reminder
	private void cancelAlarm() {

		Intent intent = new Intent(getBaseContext(), AlarmReceiver.class);
		PendingIntent pendingIntent = PendingIntent.getBroadcast(
				getBaseContext(), RQS_1, intent, 0);
		AlarmManager alarmManager = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
		alarmManager.cancel(pendingIntent);

	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		// this method is deprecated from API 11 to use a settings fragment (>
		// Android 3.0)
		// we choose this method to make it compatible with low level API
		addPreferencesFromResource(R.layout.activity_settings_layout);

		// ensures that settings values are properly initialized with default
		// values
		PreferenceManager.setDefaultValues(this,
				R.layout.activity_settings_layout, false);

		// activity context
		context = this;

		// Bind the summaries preferences to their stored values by keys
		bindPreferenceSummaryToValue(findPreference("ringtone key"));
		bindPreferenceSummaryToValue(findPreference("alarmtime_key"));
		// TODO bindPreferenceSummaryToValue(findPreference("samplerate_key"));
		bindPreferenceSummaryToValue(findPreference("maxduration_key"));

		// timePickerDialog setting and listener 
		//**hour and minutes are initialized by checking settings summaries**
		final Preference selectTimePref = (Preference) findPreference("alarmtime_key");
		timePickerDialog = new TimePickerDialog(this, mOnTimeSetListener, hour,
				minutes, true);
		//TOCLEAN ?
		timePickerDialog.setTitle("Set Alarm Time");
		selectTimePref
				.setOnPreferenceClickListener(new OnPreferenceClickListener() {
					public boolean onPreferenceClick(Preference preference) {
						timePickerDialog.show();
						return true;
					}
				});

		// handler to preference's object
		final Preference ringtone = (Preference) findPreference("ringtone key");
		final Preference vibration = (Preference) findPreference("vibration key");
		CheckBoxPreference checkboxPref = (CheckBoxPreference) findPreference("alarm key");
		// if checkbox is checked show notification preference screens
		if (checkboxPref.isChecked()) {
			ringtone.setEnabled(true);
			vibration.setEnabled(true);
			selectTimePref.setEnabled(true);
			// set the alarm TODO è corretto inviare più allarmi?
			setAlarm();
		}
		// checkbox listener to show/hide notification settings
		checkboxPref
				.setOnPreferenceChangeListener(new Preference.OnPreferenceChangeListener() {
					public boolean onPreferenceChange(Preference preference,
							Object newValue) {
						String stringValue = newValue.toString();
						boolean value = Boolean.parseBoolean(stringValue);
						// show or hide preferences
						ringtone.setEnabled(value);
						vibration.setEnabled(value);
						selectTimePref.setEnabled(value);
						// if unchecked dismiss alarm
						if (value)
							setAlarm();
						else
							cancelAlarm();
						return true;
					}
				});
	}

	// This method binds a preference's summary to its value. The summary is
	// also immediately updated upon calling this method.
	private void bindPreferenceSummaryToValue(Preference preference) {
		// if the preference is about alarm time
		if (preference.getKey().equalsIgnoreCase("alarmtime_key")) {
			// get stored values
			String stringHour = PreferenceStorage
					.getSimpleData(context, "HOUR");
			String stringMinutes = PreferenceStorage.getSimpleData(context,
					"MINUTES");
			// if settings are started for the first time fix TimePickerDialog
			// to 12:00
			if ((stringHour.equals(""))||(stringMinutes.equals(""))){
				hour = 12;
				minutes = 0;
			}
			else
				try {
					// string value to int value
					hour = Integer.parseInt(stringHour);
					// string value to int value
					minutes = Integer.parseInt(stringMinutes);
				} catch (NumberFormatException e) {
					Log.i("ERROR-SETTINGS", "Parse hour or minutes error");
				}
			//set alarmtime summary
			alarmSummary = fixTime(hour) + ":" + fixTime(minutes);
			preference.setSummary(alarmSummary);
		} else { // other settings
			// Set the listener to watch for value changes.
			preference
					.setOnPreferenceChangeListener(sBindPreferenceSummaryToValueListener);

			// Trigger the listener immediately with the preference's
			// current value.
			sBindPreferenceSummaryToValueListener.onPreferenceChange(
					preference,
					PreferenceManager.getDefaultSharedPreferences(
							preference.getContext()).getString(
							preference.getKey(), ""));

		}
	}

	// A preference value change listener that updates the preference's summary
	// to reflect its new value.
	private static Preference.OnPreferenceChangeListener sBindPreferenceSummaryToValueListener = new Preference.OnPreferenceChangeListener() {
		@Override
		public boolean onPreferenceChange(Preference preference, Object value) {
			// This string contains the changed preference's value.
			String stringValue = value.toString();
			Log.i("CHANGE", stringValue); //TOCLEAN  
			// if the value changed is the ringtone
			if (preference instanceof RingtonePreference) {
				// Null or 0 values correspond to 'silent' (no ringtone).
				if (TextUtils.isEmpty(stringValue))
					preference.setSummary("Silent");
				else {
					Ringtone ringtone = RingtoneManager.getRingtone(
							preference.getContext(), Uri.parse(stringValue));
					if (ringtone == null) {
						// Clear the summary if there was a lookup error.
						preference.setSummary(null);
					} else {
						// Set the summary to reflect the new ringtone display
						// name.
						String name = ringtone
								.getTitle(preference.getContext());
						preference.setSummary(name);
					}
				}

			} else if (preference instanceof EditTextPreference) {
				// get stored values
				String stringDuration = PreferenceStorage.getSimpleData(
						context, "DURATION");
				if (stringDuration.equalsIgnoreCase("")) {
					stringDuration = "1";
					((EditTextPreference) preference).setText(stringDuration); //FIXME?
					PreferenceStorage.storeSimpleData(context, "DURATION",
							stringDuration);
					preference.setSummary(stringDuration + " h");
				} else {
					// if the value is an empty string
					if (stringValue.equalsIgnoreCase("")) {
						Toast.makeText(context, "Retry", Toast.LENGTH_SHORT)
								.show();
						return false;
					}
					int duration;
					try {
						// string value to int value
						duration = Integer.parseInt(stringValue);
					} catch (NumberFormatException e) {
						Log.i("ERROR-SETTINGS", "Parse duration error");
						Toast.makeText(context, "An error occur, please retry", Toast.LENGTH_SHORT)
								.show();
						return false;
					}
					if (duration > 24) {
						Toast.makeText(context, R.string.message_24h,
								Toast.LENGTH_SHORT).show();
						return false;
					}
					PreferenceStorage.storeSimpleData(context, "DURATION", ""
							+ duration);
					// TODO settare l'edittext da 0024 a 24 es
					// ((EditTextPreference) preference).setText("" + duration);
					preference.setSummary(duration + " h");
				}

			} else { // TODO
				Log.i("PREFERENCE", preference.toString());
			}
			return true;
		}
	};

	// This method add a 0 to hour or minutes < 10
	public String fixTime(int time) {
		String string = "0";
		if (time < 10)
			return string + time;
		else
			return "" + time;
	}

}