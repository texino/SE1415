package it.dei.unipd.esp1415.activity;

import it.dei.unipd.esp1415.utils.AlarmReceiver;
import it.dei.unipd.esp1415.utils.PreferenceStorage;

import java.util.Calendar;
import java.util.concurrent.TimeUnit;

import android.app.AlarmManager;
import android.app.AlertDialog;
import android.app.PendingIntent;
import android.app.TimePickerDialog;
import android.app.TimePickerDialog.OnTimeSetListener;
import android.content.Context;
import android.content.DialogInterface;
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
	// alarm variables
	private int hour;
	private int minutes;
	private int rate;
	final static int RQS_1 = 1;
	// settings variables
	private String alarmSummary;
	private TimePickerDialog timePickerDialog;
	private AlertDialog sampleRateDialog;
	private final CharSequence[] rateList = {"1","2","3","4","5"};
	private int selectedRate;
	protected static Context context;
	// preferences keys stored
	protected static String durationKey = "DURATION";
	protected static String hourKey = "HOUR";
	protected static String minutesKey = "MINUTES";
	protected static String rateKey = "RATE";

	// listener for the choice of alarm time
	private OnTimeSetListener mOnTimeSetListener = new OnTimeSetListener() {

		@SuppressWarnings("deprecation")
		@Override
		public void onTimeSet(TimePicker view, int hourOfDay, int minutesOfDay) {
			// update the current variables (hour and minutes)
			hour = hourOfDay;
			minutes = minutesOfDay;
			// saving values
			PreferenceStorage.storeSimpleData(context, hourKey, "" + hour);
			PreferenceStorage
					.storeSimpleData(context, minutesKey, "" + minutes);
			// update summary
			bindPreferenceSummaryToValue(findPreference("alarmtimekey"));
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

	@SuppressWarnings("deprecation")
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
		bindPreferenceSummaryToValue(findPreference("ringtonekey"));
		bindPreferenceSummaryToValue(findPreference("alarmtimekey"));
		bindPreferenceSummaryToValue(findPreference("sampleratekey"));
		bindPreferenceSummaryToValue(findPreference("maxdurationkey"));

		// timePickerDialog setting and listener
		// **hour and minutes are initialized by checking settings summaries**
		final Preference selectTimePref = (Preference) findPreference("alarmtimekey");
		timePickerDialog = new TimePickerDialog(this, mOnTimeSetListener, hour,
				minutes, true);
		// TOCLEAN ?
		timePickerDialog.setTitle("Set Alarm Time");
		selectTimePref
				.setOnPreferenceClickListener(new OnPreferenceClickListener() {
					public boolean onPreferenceClick(Preference preference) {
						timePickerDialog.show();
						return true;
					}
				});

		// handler to preference's object
		final Preference ringtonePref = (Preference) findPreference("ringtonekey");
		final Preference vibrationPref = (Preference) findPreference("vibrationkey");
		CheckBoxPreference checkboxPref = (CheckBoxPreference) findPreference("alarmkey");
		// if checkbox is checked show notification preference screens
		if (checkboxPref.isChecked()) {
			ringtonePref.setEnabled(true);
			vibrationPref.setEnabled(true);
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
						ringtonePref.setEnabled(value);
						vibrationPref.setEnabled(value);
						selectTimePref.setEnabled(value);
						// if unchecked dismiss alarm
						if (value)
							setAlarm();
						else
							cancelAlarm();
						return true;
					}
				});

		// set the sample rate dialog and listener
		final Preference ratePref = (Preference) findPreference("sampleratekey");
		AlertDialog.Builder builder = new AlertDialog.Builder(this);
		builder.setTitle(R.string.text_select_rate);
		builder.setSingleChoiceItems(rateList,selectedRate,
				new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog, int rate) {
						switch(rate)
						{
						case(0):
							selectedRate=1;break;
						case(1):
							selectedRate=2;break;
						case(2):
							selectedRate=3;break;
						case(3):
							selectedRate=4;break;
						case(4):
							selectedRate=5;break;
						}
						selectedRate=rate+1;
						PreferenceStorage.storeSimpleData(context,PreferenceStorage.ACCEL_RATIO,""+selectedRate);
						// update summary
						ratePref.setSummary(rate+" "+R.string.text_rate_measurement);
						sampleRateDialog.dismiss();
					}
				});
		sampleRateDialog = builder.create();
		ratePref.setOnPreferenceClickListener(new OnPreferenceClickListener() {
			public boolean onPreferenceClick(Preference preference) {
				sampleRateDialog.show();
				return true;
			}
		});

	}

	// This method binds a preference's summary to its value. The summary is
	// also immediately updated upon calling this method.
	private void bindPreferenceSummaryToValue(Preference preference) {
		// if the preference is about alarm time
		if (preference.getKey().equalsIgnoreCase("alarmtimekey")) {
			// get stored values
			String stringHour = PreferenceStorage.getSimpleData(context,
					hourKey);
			String stringMinutes = PreferenceStorage.getSimpleData(context,
					minutesKey);
			// if settings are started for the first time fix TimePickerDialog
			// to 12:00
			if ((stringHour.equals("")) || (stringMinutes.equals(""))) {
				hour = 12;
				minutes = 0;
			} else
				try {
					// string value to int value
					hour = Integer.parseInt(stringHour);
					minutes = Integer.parseInt(stringMinutes);
				} catch (NumberFormatException e) {
					Log.i("ERROR-SETTINGS", "Parse hour or minutes error");
				}
			// set alarmtime summary
			alarmSummary = fixTime(hour) + ":" + fixTime(minutes);
			preference.setSummary(alarmSummary);
		} else if (preference.getKey().equalsIgnoreCase("sampleratekey")) {
			// get stored values
			String stringRate = PreferenceStorage.getSimpleData(context,
					rateKey);
			if (stringRate.equals("")) {
				selectedRate = 0;
				rate = 0; // TODO Scrivere o far riferimento al primo rate
				PreferenceStorage.storeSimpleData(context, rateKey, "" + rate);
				//set sample rate summary
				preference.setSummary(rate + " campioni/s");
			} else {
				try {
					// string value to int value
					rate = Integer.parseInt(stringRate);
				} catch (NumberFormatException e) {
					Log.i("ERROR-SETTINGS", "Parse sample rate error");
				}
				switch (rate){
				case 0: selectedRate=0; break;
				case 1: selectedRate=1; break;
				case 2: selectedRate=2; break;
				}
				PreferenceStorage.storeSimpleData(context, rateKey, "" + rate);
				//set sample rate summary
				preference.setSummary(rate + " campioni/s");
			}
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
			Log.i("CHANGE", stringValue); // TOCLEAN
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
						context, durationKey);
				if (stringDuration.equalsIgnoreCase("")) {
					stringDuration = "1";
					((EditTextPreference) preference).setText(stringDuration); // FIXME?
					PreferenceStorage.storeSimpleData(context, durationKey,
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
						Toast.makeText(context, "An error occur, please retry",
								Toast.LENGTH_SHORT).show();
						return false;
					}
					if (duration > 24) {
						Toast.makeText(context, R.string.message_24h,
								Toast.LENGTH_SHORT).show();
						return false;
					}
					PreferenceStorage.storeSimpleData(context, durationKey, ""
							+ duration);
					// TODO settare l'edittext da 0024 a 24 es
					((EditTextPreference) preference).setText("" + duration);
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