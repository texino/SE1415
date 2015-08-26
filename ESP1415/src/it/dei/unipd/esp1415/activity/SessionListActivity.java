package it.dei.unipd.esp1415.activity;

import it.dei.unipd.esp1415.utils.PreferenceStorage;
import it.dei.unipd.esp1415.views.FloatingActionButton;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;

import com.example.esp1415.R;

/**
 * SessionListActivity class: First activity to be display that show and manage
 * a list of sessions
 */
public class SessionListActivity extends ActionBarActivity {

	private static FloatingActionButton fabButton;
	private Context context;
	private static boolean isSessionRunning;
	public static final String RUNNING = "IS_RUNNING";
	public static final int colour = Color.rgb(255, 165, 0);

	@Override
	protected void onCreate(Bundle savedInstanceState) {

		super.onCreate(savedInstanceState);
		// set the activity layout
		setContentView(R.layout.activity_session_list_layout);
		// activity context
		context = this;
		// check if a session is running
		String value = PreferenceStorage.getSimpleData(context,
				RUNNING);
		if (value.equals(""))
			isSessionRunning = false;
		else
			isSessionRunning = java.lang.Boolean.parseBoolean(value);
		// create and set FAB button
		fabButton = new FloatingActionButton.Builder(this)
				.withDrawable(
						getResources().getDrawable(
								R.drawable.plus_grey))
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

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.sessionlist_action_item, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// Handle action bar item clicks here. The action bar will
		// automatically handle clicks on the Home/Up button, so long
		// as you specify a parent activity in AndroidManifest.xml.
		int id = item.getItemId();
		if (id == R.id.action_settings) {
			Intent setting_intent = new Intent(this, SettingsActivity.class);
			startActivity(setting_intent);
			return true;
		}
		return super.onOptionsItemSelected(item);
	}

	@Override
	public void onResume() {
		super.onResume();
		String value = PreferenceStorage.getSimpleData(context,
				RUNNING);
		if (value.equals(""))
			isSessionRunning = false;
		else
			isSessionRunning = java.lang.Boolean.parseBoolean(value);
		if (isSessionRunning)
			fabButton.setVisibility(View.GONE);
		else
			fabButton.setVisibility(View.VISIBLE);
	}
}