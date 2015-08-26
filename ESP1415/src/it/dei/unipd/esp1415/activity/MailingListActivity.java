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
		// setting up single click
		lv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> av, View v, int pos, long id) {
				onListItemClick(v, pos, id);
			}
		});

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
				// nuovo dialog con l'inserimento della mail
				renameDialog = new AlertDialog.Builder(context);
				LayoutInflater inflater = getLayoutInflater();
				View layout = inflater.inflate(R.layout.dialog_rename, null);
				final EditText addedMail = (EditText) layout
						.findViewById(R.id.edittextrename);
				renameDialog
						.setView(layout)
						.setTitle("Set a new mail address")
						.setPositiveButton(R.string.conferma,
								new DialogInterface.OnClickListener() {
									@Override
									public void onClick(DialogInterface dialog,
											int id) {
										// aggiungi l'indirizzo all'adapter e
										// salva
										String newMail = addedMail.getText()
												.toString();
										if (newMail.equals(""))
											dialog.dismiss();
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
										// annulla
										dialog.dismiss();
									}
								}).create().show();
			}
		});
	}

	// single click implementation
	protected void onListItemClick(View v, int pos, long id) {

	}

	// long click implementation
	protected boolean onLongListItemClick(View v, int pos, long id) {

		mailString = adapter.getItem(pos);
		// create the dialog
		deleteDialog = new AlertDialog.Builder(context)
				.setMessage("Delete " + mailString + " ?")
				.setPositiveButton(R.string.conferma,
						new DialogInterface.OnClickListener() {
							@Override
							public void onClick(DialogInterface dialog, int id) {
								// rimuovi dall'adapter e aggiorna la listview
								// eliminated = true;

								adapter.remove(mailString);
								saveData();
							}
						})
				.setNegativeButton(R.string.cancel,
						new DialogInterface.OnClickListener() {
							@Override
							public void onClick(DialogInterface dialog, int id) {
								// annulla
								dialog.dismiss();
							}
						});
		;
		deleteDialog.create().show();
		// returning true means that Android stops event propagation
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

		} catch (LowSpaceException e) {

		}
	}

	@Override
	public void onResume() {
		super.onResume();
		items = getData();

	}

	@Override
	public void onPause() {
		saveData();
		super.onPause();
	}
}
