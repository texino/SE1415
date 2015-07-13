package it.dei.unipd.esp1415.activity;

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

public class SettingsActivity extends PreferenceActivity {
	public static final int TIME_DIALOG_ID = 1;
	private int mSelectedHour;
	private int mSelectedMinutes;
	private TimePickerDialog timePickerDialog;
	// private Preference alarm = (Preference) findPreference("alarm key");
	// private final Preference selectTime = (Preference)
	// findPreference("alarmtime_key");
	// private final Preference ringtone = (Preference)
	// findPreference("ringtone key");
	// private final Preference vibration = (Preference)
	// findPreference("vibration key");
	private static Context context;

	private OnTimeSetListener mOnTimeSetListener = new OnTimeSetListener() {

		@Override
		public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
			// update the current variables (hour and minutes)
			mSelectedHour = hourOfDay;
			mSelectedMinutes = minute;
			Log.i("TIMEPICKERDIALOG", "" + mSelectedHour + mSelectedMinutes);
			// update txtTime with the selected time
			// updateTimeUI();
		}
	};

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		// this method is deprecated from API 11 to use a modern fragment
		// we choose this method to make it compatible with low level API
		addPreferencesFromResource(R.layout.activity_settings_layout);

		context = getApplicationContext();

		// Bind the summaries of EditText/List/Dialog/Ringtone preferences
		// to their stored values.
		bindPreferenceSummaryToValue(findPreference("ringtone key"));

		// timepickerdialog setting
		final Preference selectTime = (Preference) findPreference("alarmtime_key");
		int hour = 12;
		int minutes = 0;
		timePickerDialog = new TimePickerDialog(this, mOnTimeSetListener, hour,
				minutes, true);
		selectTime
				.setOnPreferenceClickListener(new OnPreferenceClickListener() {
					public boolean onPreferenceClick(Preference preference) {
						timePickerDialog.show();
						/*
						 * DialogFragment newFragment = new
						 * MyDialogPreference();
						 * newFragment.show(getSupportFragmentManager(),
						 * "timePicker"); Log.i("OnClick",
						 * "Preference dialog clicked");
						 * //showDialog(TIME_DIALOG_ID);
						 */return true;
					}
				});

		// checkbox listener
		final Preference ringtone = (Preference) findPreference("ringtone key");
		final Preference vibration = (Preference) findPreference("vibration key");
		CheckBoxPreference checkboxPref = (CheckBoxPreference) findPreference("alarm key");
		if(checkboxPref.isChecked()){
			ringtone.setEnabled(true);
			vibration.setEnabled(true);
			selectTime.setEnabled(true);
		}
		checkboxPref
				.setOnPreferenceChangeListener(new Preference.OnPreferenceChangeListener() {
					public boolean onPreferenceChange(Preference preference,
							Object newValue) {
						String stringValue = newValue.toString();
						// boolean value = Boolean.getBoolean(stringValue);
						boolean value = Boolean.parseBoolean(stringValue);
						// Log.i("BOOLEAN",
						// stringValue/*+""+Boolean.toString(value)*/);
						Log.i("BOOLEAN", "" + value);
						// if(stringValue.equalsIgnoreCase("true"))
						ringtone.setEnabled(value);
						vibration.setEnabled(value);
						selectTime.setEnabled(value);
						/*
						 * enableSetting(true); else enableSetting(false);
						 */
						return true;
					}
				});
	}

	/**
	 * A preference value change listener that updates the preference's summary
	 * to reflect its new value.
	 */
	private static Preference.OnPreferenceChangeListener sBindPreferenceSummaryToValueListener = new Preference.OnPreferenceChangeListener() {
		@Override
		public boolean onPreferenceChange(Preference preference, Object value) {
			// This string contains the changed preference's value.
			String stringValue = value.toString();
			// Log.i("CHECKBOX", "CheckBox onClickListener");
			/*
			 * //If the preference is the editable 'Display Name'. if
			 * (preference instanceof EditTextPreference) { if
			 * (stringValue.equalsIgnoreCase("User") || stringValue.equals(""))
			 * {
			 * preference.setSummary("Define a personal username for the app");
			 * EditTextPreference editTextPreference = (EditTextPreference)
			 * preference; editTextPreference.setText("User"); } else {
			 * preference.setSummary(stringValue); } } else
			 */if (preference instanceof RingtonePreference) {
				Log.i("RINGTONE", "Ringtone onClickListener");
				// If the preference is the ringtone: for ringtone preferences,
				// look up the correct display value using RingtoneManager.
				if (TextUtils.isEmpty(stringValue)) {
					// Empty values correspond to 'silent' (no ringtone).
					preference.setSummary("Nothing");

				} else {
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

			} /*
			 * else if(preference instanceof CheckBoxPreference){
			 * 
			 * 
			 * if(stringValue.equalsIgnoreCase("true")) enableSetting(true);
			 * else enableSetting(false);
			 * 
			 * }
			 */else {
				// For all other preferences, set the summary to the value's
				// simple string representation.
				preference.setSummary(stringValue);
			}
			return true;
		}
	};

	/*
	 * private void enableSetting(boolean bool){ Log.i("CHECKBOX",
	 * "CheckBox onClickListener"); //Preference ringtone = (Preference)
	 * findPreference("ringtone key").setEnabled(bool);; //((PreferenceActivity)
	 * PreferenceManager
	 * .getDefaultSharedPreferences(context)).findPreference("alarmtime_key"
	 * ).setEnabled(bool);; //((PreferenceActivity)
	 * getPreferenceManager().findPreference("alarmtime_key").setEnabled(bool);;
	 * selectTime.setEnabled(true); ringtone.setEnabled(true);
	 * vibration.setEnabled(true); }
	 */

	/**
	 * Binds a preference's summary to its value. More specifically, when the
	 * preference's value is changed, its summary (line of text below the
	 * preference title) is updated to reflect the value. The summary is also
	 * immediately updated upon calling this method. The exact display format is
	 * dependent on the type of preference.
	 * 
	 * @see #sBindPreferenceSummaryToValueListener
	 */
	private static void bindPreferenceSummaryToValue(Preference preference) {
		// Set the listener to watch for value changes.
		preference
				.setOnPreferenceChangeListener(sBindPreferenceSummaryToValueListener);

		// Trigger the listener immediately with the preference's
		// current value.
		sBindPreferenceSummaryToValueListener.onPreferenceChange(
				preference,
				PreferenceManager.getDefaultSharedPreferences(
						preference.getContext()).getString(preference.getKey(),
						""));
	}

	// This method update the summary information on settings change
	public void onSharedPreferenceChanged(SharedPreferences sharedPreferences,
			String key) {
		// only ringtone needs summary change
		if (key.equals("ringtone key"))
			bindPreferenceSummaryToValue(findPreference(key));

	}

}

/*
 * //This fragment shows app preferences public class PrefsFragment extends
 * PreferenceFragment implements OnSharedPreferenceChangeListener {
 * 
 * @Override public void onCreate(Bundle savedInstanceState) {
 * 
 * super.onCreate(savedInstanceState);
 * addPreferencesFromResource(R.layout.activity_settings);
 * 
 * // Bind the summaries of EditText/List/Dialog/Ringtone preferences // to
 * their stored values.
 * bindPreferenceSummaryToValue(findPreference("pref_key_ringtone"));
 * bindPreferenceSummaryToValue(findPreference("pref_username"));
 * 
 * }
 * 
 * //For proper lifecycle management in the activity we register //and
 * unregister SharedPreferences.OnSharedPreferenceChangeListener //in methods
 * onResume() and onPause()
 * 
 * @Override public void onResume() { super.onResume(); // Set up a listener
 * whenever a key changes getPreferenceScreen().getSharedPreferences()
 * .registerOnSharedPreferenceChangeListener(this); }
 * 
 * @Override public void onPause() { super.onPause(); // Unregister the listener
 * whenever a key changes getPreferenceScreen().getSharedPreferences()
 * .unregisterOnSharedPreferenceChangeListener(this); }
 * 
 * //This method update the summary information on settings change public void
 * onSharedPreferenceChanged( SharedPreferences sharedPreferences, String key) {
 * //vibration doesn't need summary
 * if(key.equals("pref_key_ringtone_vibration")){} else{
 * bindPreferenceSummaryToValue(findPreference(key)); } } }
 */