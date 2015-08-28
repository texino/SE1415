package it.dei.unipd.esp1415.activity;

import it.dei.unipd.esp1415.exceptions.LowSpaceException;
import it.dei.unipd.esp1415.utils.LocalStorage;
import it.dei.unipd.esp1415.views.FloatingActionButton;

import java.io.IOException;
import java.util.List;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

import com.example.esp1415.R;

/**
 * MailingListActivity class: sets and organizes mail addresses
 */
public class MailingListActivity extends Activity {

	private static FloatingActionButton fabButton;
	private List<String> items;
	private ArrayAdapter<String> adapter;
	private Context context;
	private AlertDialog.Builder deleteBuilder;
	private AlertDialog.Builder addBuilder;
	private AlertDialog addDialog;
	private AlertDialog deleteDialog;
	private String mailString;
	private ListView lv;
	private boolean isAddDialogOpen;
	private boolean isDeleteDialogOpen;
	private EditText addedMail;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		context = this;
		// set activity layout
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
				.withDrawable(getResources().getDrawable(R.drawable.ic_plus))
				.withButtonColor(getResources().getColor(R.color.fab_color))
				.withGravity(Gravity.BOTTOM | Gravity.RIGHT)
				.withMargins(0, 0, 16, 16).create();
		// set the listener to FAB button
		fabButton.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				createAddDialog();
				isAddDialogOpen = true;
				addDialog = addBuilder.create();
				addDialog.show();
			}
		});
	}

	// saving the state of the activity
	public void onSaveInstanceState(Bundle state) {
		if (isAddDialogOpen) {
			addDialog.dismiss();
			state.putString("newMail", addedMail.getText().toString());
			state.putBoolean("addDialog", isAddDialogOpen);
		} else if (isDeleteDialogOpen) {
			deleteDialog.dismiss();
			state.putString("deleteMail", mailString);
			state.putBoolean("deleteDialog", isDeleteDialogOpen);
		}
		super.onSaveInstanceState(state);
	}

	// restore the state of the activity
	public void onRestoreInstanceState(Bundle state) {
		isAddDialogOpen = state.getBoolean("addDialog", false);
		isDeleteDialogOpen = state.getBoolean("deleteDialog", false);
		if (isAddDialogOpen) {
			createAddDialog();
			addedMail.setText(state.getString("newMail"));
			addDialog = addBuilder.create();
			addDialog.show();
			isAddDialogOpen = true;
		} else if (isDeleteDialogOpen) {
			mailString = state.getString("deleteMail");
			createDeleteDialog();
			deleteDialog = deleteBuilder.create();
			deleteDialog.show();
			isDeleteDialogOpen = true;
		}
	}

	// long click implementation
	protected boolean onLongListItemClick(View v, int pos, long id) {
		mailString = adapter.getItem(pos);
		createDeleteDialog();
		deleteDialog = deleteBuilder.create();
		deleteDialog.show();
		isDeleteDialogOpen = true;
		return true;
	}

	private void createAddDialog() {
		addBuilder = new AlertDialog.Builder(context);
		LayoutInflater inflater = getLayoutInflater();
		View dialoglayout = inflater.inflate(R.layout.dialog_rename, null);
		addedMail = (EditText) dialoglayout.findViewById(R.id.edittextrename);
		addBuilder
				.setView(dialoglayout)
				.setTitle(R.string.mail_dialog_title1)
				.setPositiveButton(R.string.conferma,
						new DialogInterface.OnClickListener() {
							@Override
							public void onClick(DialogInterface dialog, int id) {
								String newMail = addedMail.getText().toString();
								if (newMail.equals("")) {
									Toast.makeText(context, R.string.ritenta,
											Toast.LENGTH_SHORT).show();
								} else {
									adapter.add(newMail);
									saveData();
								}
								isAddDialogOpen = false;
							}
						})
				.setNegativeButton(R.string.cancel,
						new DialogInterface.OnClickListener() {
							@Override
							public void onClick(DialogInterface dialog, int id) {
								dialog.dismiss();
								isAddDialogOpen = false;
							}
						}).create();
		addBuilder.setOnKeyListener(new DialogInterface.OnKeyListener() {

			@Override
			public boolean onKey(DialogInterface dialog, int keyCode,
					KeyEvent event) {

				if (keyCode == KeyEvent.KEYCODE_BACK) {
					isAddDialogOpen = false;
					dialog.dismiss();
					return true;
				}
				return false;

			}
		});
		addBuilder.setCancelable(false);
	}

	private void createDeleteDialog() {
		// create the dialog
		deleteBuilder = new AlertDialog.Builder(context)
				.setTitle(R.string.mail_dialog_title2)
				.setMessage(mailString)
				.setPositiveButton(R.string.conferma,
						new DialogInterface.OnClickListener() {
							@Override
							public void onClick(DialogInterface dialog, int id) {
								adapter.remove(mailString);
								saveData();
								isDeleteDialogOpen = false;
							}
						})
				.setNegativeButton(R.string.cancel,
						new DialogInterface.OnClickListener() {
							@Override
							public void onClick(DialogInterface dialog, int id) {
								dialog.dismiss();
								isDeleteDialogOpen = false;
							}
						});
		;
		deleteBuilder.setOnKeyListener(new DialogInterface.OnKeyListener() {

			@Override
			public boolean onKey(DialogInterface dialog, int keyCode,
					KeyEvent event) {

				if (keyCode == KeyEvent.KEYCODE_BACK) {
					isDeleteDialogOpen = false;
					dialog.dismiss();
					return true;
				}
				return false;

			}
		});
		deleteBuilder.setCancelable(false);
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
