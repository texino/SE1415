package it.dei.unipd.esp1415.activity;

import it.dei.unipd.esp1415.objects.SessionInfo;
import it.dei.unipd.esp1415.utils.LocalStorage;
import it.dei.unipd.esp1415.utils.PreferenceStorage;
import it.dei.unipd.esp1415.views.FloatingActionButton;

import java.io.IOException;
import java.util.List;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
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
	public static final int colour = Color.rgb(255, 165, 0);
	public static final String RUNNING = "RUNNING";

	@Override
	protected void onCreate(Bundle savedInstanceState) {

		super.onCreate(savedInstanceState);
		// set the activity layout
		setContentView(R.layout.activity_session_list_layout);
		// create and set FAB button
		fabButton = new FloatingActionButton.Builder(this)
				.withDrawable(getResources().getDrawable(R.drawable.plus_grey))
				.withButtonColor(colour)
				.withGravity(Gravity.BOTTOM | Gravity.RIGHT)
				.withMargins(0, 0, 16, 16).create();
		// hide the fab button if a session is running
		if (isSessionRunning)
			fabButton.setVisibility(View.GONE);
		else
			fabButton.setVisibility(View.VISIBLE);

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
	}

	/*
	 * Intent setting_intent = new Intent(this, SettingsActivity.class);
	 * startActivity(setting_intent); return true;
	 */
	@Override
	public void onResume() {
		super.onResume();
		List<SessionInfo> items;
		int size;
		try {
			items = LocalStorage.getSessionInfos();
			size = items.size();
			if (size != 0) {
				findViewById(R.id.image_empty).setVisibility(View.GONE);
				if (items.get(size - 1).getStatus()) {
					isSessionRunning = true;
					PreferenceStorage.storeSimpleData(this, RUNNING, "true");
				} else {
					isSessionRunning = false;
					PreferenceStorage.storeSimpleData(this, RUNNING, "false");
				}
			} else {
				findViewById(R.id.image_empty).setVisibility(View.VISIBLE);
				isSessionRunning = false;
				PreferenceStorage.storeSimpleData(this, RUNNING, "false");
			}
		} catch (IOException e) {
			Log.i("ERROR", "Error getting session list - LocalStorage");
		}
		if (isSessionRunning)
			fabButton.setVisibility(View.GONE);
		else
			fabButton.setVisibility(View.VISIBLE);
	}
}