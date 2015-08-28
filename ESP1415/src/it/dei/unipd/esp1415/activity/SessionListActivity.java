package it.dei.unipd.esp1415.activity;

import it.dei.unipd.esp1415.objects.SessionInfo;
import it.dei.unipd.esp1415.utils.LocalStorage;
import it.dei.unipd.esp1415.utils.PreferenceStorage;
import it.dei.unipd.esp1415.views.FloatingActionButton;

import java.io.IOException;
import java.util.List;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.app.FragmentActivity;
import android.util.Log;
import android.view.Gravity;
import android.view.View;

import com.example.esp1415.R;

/**
 * SessionListActivity class: First activity to be display that show and manage
 * a list of sessions
 */
public class SessionListActivity extends FragmentActivity {

	private FloatingActionButton fabButton;
	private boolean isSessionRunning;
	private String language;
	public static final String RUNNING = "RUNNING";

	@Override
	protected void onCreate(Bundle savedInstanceState) {

		super.onCreate(savedInstanceState);
		// set the activity layout
		setContentView(R.layout.activity_session_list_layout);
		language = PreferenceStorage.getSimpleData(this,
				SettingsActivity.languageKey);
		LocalStorage.setSystemLanguage(language);
		// create and set FAB button
		fabButton = new FloatingActionButton.Builder(this)
				.withDrawable(getResources().getDrawable(R.drawable.ic_plus))
				.withButtonColor(getResources().getColor(R.color.fab_color))
				.withGravity(Gravity.BOTTOM | Gravity.RIGHT)
				.withMargins(0, 0, 16, 16).create();
		// set the listener to FAB button to go to third activity
		fabButton.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				Intent i = new Intent(SessionListActivity.this,
						CurrentSessionActivity.class);
				i.putExtra(CurrentSessionActivity.EMPTY_TAG, true);
				startActivity(i);
			}
		});
		// set settings button
		findViewById(R.id.settings_button).setOnClickListener(
				new View.OnClickListener() {
					@Override
					public void onClick(View v) {
						Intent settingIntent = new Intent(
								SessionListActivity.this,
								SettingsActivity.class);
						startActivity(settingIntent);
					}
				});
	}

	@Override
	public void onResume() {
		super.onResume();
		// on resume() check application state and set layout visibility
		List<SessionInfo> items;
		int size;
		try {
			items = LocalStorage.getSessionInfos();
			size = items.size();
			if (size != 0) { // the list is not empty
				findViewById(R.id.image_empty).setVisibility(View.GONE); // hide
																			// image
																			// fills
				if (items.get(size - 1).getStatus()) { // check if a session is
														// running
					isSessionRunning = true;
					PreferenceStorage.storeSimpleData(this, RUNNING, "true");
				} else {
					isSessionRunning = false;
					PreferenceStorage.storeSimpleData(this, RUNNING, "false");
				}
			} else { // the list is empty
				findViewById(R.id.image_empty).setVisibility(View.VISIBLE);
				isSessionRunning = false;
				PreferenceStorage.storeSimpleData(this, RUNNING, "false");
			}
		} catch (IOException e) {
			Log.i("ERROR", "Error getting session list - LocalStorage");
		}
		if (isSessionRunning) // check and set fab button visibility
			fabButton.setVisibility(View.GONE);
		else
			fabButton.setVisibility(View.VISIBLE);
	}
}