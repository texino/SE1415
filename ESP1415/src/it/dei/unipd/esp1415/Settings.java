package it.dei.unipd.esp1415;

import it.dei.unipd.esp1415.views.MyDialogPreference;
import android.media.Ringtone;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Bundle;
import android.preference.Preference;
import android.preference.Preference.OnPreferenceClickListener;
import android.preference.PreferenceActivity;
import android.preference.PreferenceManager;
import android.preference.RingtonePreference;
import android.support.v4.app.DialogFragment;
import android.text.TextUtils;
import android.util.Log;

import com.example.esp1415.R;

public class Settings extends PreferenceActivity {
	public static final int TIME_DIALOG_ID = 1;
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		addPreferencesFromResource(R.layout.settings_activity);
		
		// Bind the summaries of EditText/List/Dialog/Ringtone preferences
		// to their stored values.
		bindPreferenceSummaryToValue(findPreference("ringtone key"));
		
		Preference timedialog = (Preference) findPreference("alarmtime_key");
		timedialog.setOnPreferenceClickListener(new OnPreferenceClickListener() {
		             public boolean onPreferenceClick(Preference preference) {
		            	 /*DialogFragment newFragment = new MyDialogPreference();
		            	    newFragment.show(getSupportFragmentManager(), "timePicker");*/
		            	 Log.i("OnClick", "Preference dialog clicked");
		            	 //showDialog(TIME_DIALOG_ID);
		            	 return true;
		             }
		         });
		/*getFragmentManager().beginTransaction()
				.replace(android.R.id.content, new PrefsFragment()).commit();
		PreferenceManager.setDefaultValues(Settings.this,
				R.layout.settings_activity, false);*/

	}

	/**
	 * A preference value change listener that updates the preference's summary
	 * to reflect its new value.
	 */
	private static Preference.OnPreferenceChangeListener sBindPreferenceSummaryToValueListener = new Preference.OnPreferenceChangeListener() {
		@Override
		public boolean onPreferenceChange(Preference preference, Object value) {
			//This string contains the changed preference's value.
			String stringValue = value.toString();
/*
			//If the preference is the editable 'Display Name'.
			if (preference instanceof EditTextPreference) {
				if (stringValue.equalsIgnoreCase("User") || stringValue.equals("")) {
					preference.setSummary("Define a personal username for the app");
					EditTextPreference editTextPreference = (EditTextPreference) preference;
					editTextPreference.setText("User");
				} else {
					preference.setSummary(stringValue);
				}
			} else */if (preference instanceof RingtonePreference) {
				//If the preference is the ringtone: for ringtone preferences,
				//look up the correct display value using RingtoneManager.
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
						String name = ringtone.getTitle(preference.getContext());
						preference.setSummary(name);
					}
				}

			} else {
				// For all other preferences, set the summary to the value's
				// simple string representation.
				preference.setSummary(stringValue);
			}
			return true;
		}
	};

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

	/*//This fragment shows  app preferences
	public class PrefsFragment extends PreferenceFragment implements
			OnSharedPreferenceChangeListener {

		@Override
		public void onCreate(Bundle savedInstanceState) {

			super.onCreate(savedInstanceState);
			addPreferencesFromResource(R.layout.activity_settings);

			// Bind the summaries of EditText/List/Dialog/Ringtone preferences
			// to their stored values.
			bindPreferenceSummaryToValue(findPreference("pref_key_ringtone"));
			bindPreferenceSummaryToValue(findPreference("pref_username"));

		}

		//For proper lifecycle management in the activity we register
		//and unregister SharedPreferences.OnSharedPreferenceChangeListener 
		//in methods onResume() and onPause()
		@Override
		public void onResume() {
			super.onResume();
			// Set up a listener whenever a key changes
			getPreferenceScreen().getSharedPreferences()
					.registerOnSharedPreferenceChangeListener(this);
		}

		@Override
		public void onPause() {
			super.onPause();
			// Unregister the listener whenever a key changes
			getPreferenceScreen().getSharedPreferences()
					.unregisterOnSharedPreferenceChangeListener(this);
		}

		//This method update the summary information on settings change
		public void onSharedPreferenceChanged(
				SharedPreferences sharedPreferences, String key) {
			//vibration doesn't need summary
			if(key.equals("pref_key_ringtone_vibration")){}
			else{
				bindPreferenceSummaryToValue(findPreference(key));
			}
		}
	}*/
}