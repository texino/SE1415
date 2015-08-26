package it.dei.unipd.esp1415.activity;

import it.dei.unipd.esp1415.exceptions.LowSpaceException;
import it.dei.unipd.esp1415.utils.LocalStorage;
import it.dei.unipd.esp1415.utils.PreferenceStorage;
import it.dei.unipd.esp1415.views.FloatingActionButton;

import java.io.IOException;
import java.util.List;

import com.example.esp1415.R;
import com.example.esp1415.R.drawable;
import com.example.esp1415.R.id;
import com.example.esp1415.R.layout;
import com.example.esp1415.R.string;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

public class MailingListActivity extends Activity {

	private static FloatingActionButton fabButton;
	protected List<String> items;
	private ArrayAdapter<String> adapter;
	private Context context;
	private AlertDialog.Builder deleteDialog;
	private AlertDialog.Builder renameDialog;
	private String mailString;
	private ListView lv;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		context = this;
		setContentView(R.layout.activity_mailing_list_layout);
		// get the list of session saved in the storage
		items = getData();
		// initialize and set the list adapter
		adapter = new ArrayAdapter<String>(this,
				android.R.layout.simple_list_item_1, items);
		lv = (ListView) findViewById(R.id.mailinglist);
		lv.setAdapter(adapter);
		// setting up long click
		lv.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
			@Override
			public boolean onItemLongClick(AdapterView<?> av, View v, int pos,
					long id) {
				return onLongListItemClick(v, pos, id);
			}
		});

		// create and set FAB button
		fabButton = new FloatingActionButton.Builder(this)
				.withDrawable(
						getResources().getDrawable(
								R.drawable.plus_grey))
				.withButtonColor(SessionListActivity.colour)
				.withGravity(Gravity.BOTTOM | Gravity.RIGHT)
				.withMargins(0, 0, 16, 16).create();
		// set the listener to FAB button
		fabButton.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				renameDialog = new AlertDialog.Builder(context);
				LayoutInflater inflater = getLayoutInflater();
				View dialoglayout = inflater.inflate(R.layout.dialog_rename, null);
				final EditText addedMail = (EditText) dialoglayout
						.findViewById(R.id.edittextrename);
				renameDialog
						.setView(dialoglayout)
						.setTitle("Set new mail address")
						.setPositiveButton(R.string.conferma,
								new DialogInterface.OnClickListener() {
									@Override
									public void onClick(DialogInterface dialog,
											int id) {
										String newMail = addedMail.getText()
												.toString();
										if (newMail.equals("")){
											Toast.makeText(context, "Retry",
													Toast.LENGTH_SHORT).show();
										}
										else {
											adapter.add(newMail);
											saveData();
										}
									}
								})
						.setNegativeButton(R.string.cancel,
								new DialogInterface.OnClickListener() {
									@Override
									public void onClick(DialogInterface dialog,
											int id) {
										dialog.dismiss();
									}
								}).create().show();
			}
		});
	}

	// long click implementation
	protected boolean onLongListItemClick(View v, int pos, long id) {
		mailString = adapter.getItem(pos);
		// create the dialog
		deleteDialog = new AlertDialog.Builder(context)
				.setTitle("Delete mail address?")
				.setMessage(mailString)
				.setPositiveButton(R.string.conferma,
						new DialogInterface.OnClickListener() {
							@Override
							public void onClick(DialogInterface dialog, int id) {
								adapter.remove(mailString);
								saveData();
							}
						})
				.setNegativeButton(R.string.cancel,
						new DialogInterface.OnClickListener() {
							@Override
							public void onClick(DialogInterface dialog, int id) {
								dialog.dismiss();
							}
						});
		;
		deleteDialog.create().show();
		return true;
	}

	private List<String> getData() {
		// get the list of session saved in the storage
		try {
			items = LocalStorage.getMailingList();
			return items;
		} catch (IOException e) {
			Log.i("ERROR", "Error getting mailing list - LocalStorage");
		}
		return null;
	}

	private void saveData() {
		try {
			LocalStorage.saveMailingListToFile(lv.getAdapter());
		} catch (IOException e) {
			Log.i("ERROR", "Error saving mailing list - LocalStorage");	
		} catch (LowSpaceException e) {
			Log.i("ERROR", "Low space error - LocalStorage");
		}
	}

	@Override
	public void onResume() {
		super.onResume();
		items = getData();
	}
}
