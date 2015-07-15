package it.dei.unipd.esp1415.activity;

import it.dei.unipd.esp1415.utils.PreferenceStorage;
import android.app.TimePickerDialog;
import android.app.TimePickerDialog.OnTimeSetListener;
import android.content.Context;
import android.content.SharedPreferences;
import android.media.Ringtone;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Bundle;
import android.preference.CheckBoxPreference;
import android.preference.Preference;
import android.preference.Preference.OnPreferenceClickListener;
import android.preference.PreferenceActivity;
import android.preference.PreferenceManager;
import android.preference.RingtonePreference;
import android.text.TextUtils;
import android.util.Log;
import android.widget.TimePicker;

import com.example.esp1415.R;

/**
 * SettingsActivity class: sets and organizes settings for the application
 */
public class SettingsActivity extends PreferenceActivity {
	private int hour;
	private int minutes;
	private String alarmSummary;
	private TimePickerDialog timePickerDialog;
	private static Context context;

	// listener for the choice of alarm time
	private OnTimeSetListener mOnTimeSetListener = new OnTimeSetListener() {

		@Override
		public void onTimeSet(TimePicker view, int hourOfDay, int minuteOfDay) {
			// update the current variables (hour and minutes)
			hour = hourOfDay;
			minutes = minuteOfDay;
			// saving values
			PreferenceStorage.storeSimpleData(context, "HOUR", "" + hour);
			PreferenceStorage.storeSimpleData(context, "MINUTES", "" + minutes);
			// update summary
			bindPreferenceSummaryToValue(findPreference("alarmtime_key"));
		}
	};

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

		// timePickerDialog setting and listener, hour and minutes are
		// initialized before by checking settings summaries
		final Preference selectTime = (Preference) findPreference("alarmtime_key");
		timePickerDialog = new TimePickerDialog(this, mOnTimeSetListener, hour,
				minutes, true);
		selectTime
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
		// if checkbox is checked show notification preferences screen
		if (checkboxPref.isChecked()) {
			ringtone.setEnabled(true);
			vibration.setEnabled(true);
			selectTime.setEnabled(true);
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
						selectTime.setEnabled(value);
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
			if (stringHour.equals(""))
				hour = 12;
			else
				try {
					// string value to int value
					hour = Integer.parseInt(stringHour);
				} catch (NumberFormatException e) {
					Log.i("ERROR-SETTINGS", "Parse hour error");
				}
			if (stringMinutes.equals(""))
				minutes = 0;
			else
				try {
					// string value to int value
					minutes = Integer.parseInt(stringMinutes);
				} catch (NumberFormatException e) {
					Log.i("ERROR-SETTINGS", "Parse minutes error");
				}
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