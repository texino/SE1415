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
	private AlertDialog.Builder deleteDialog;
	private AlertDialog.Builder addDialog;
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
				.withButtonColor(R.color.fab_color)
				.withGravity(Gravity.BOTTOM | Gravity.RIGHT)
				.withMargins(0, 0, 16, 16).create();
		// set the listener to FAB button
		fabButton.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				addDialog = new AlertDialog.Builder(context);
				isAddDialogOpen = true;
				LayoutInflater inflater = getLayoutInflater();
				View dialoglayout = inflater.inflate(R.layout.dialog_rename,
						null);
				addedMail = (EditText) dialoglayout
						.findViewById(R.id.edittextrename);
				addDialog
						.setView(dialoglayout)
						.setTitle(R.string.mail_dialog_title1)
						.setPositiveButton(R.string.conferma,
								new DialogInterface.OnClickListener() {
									@Override
									public void onClick(DialogInterface dialog,
											int id) {
										String newMail = addedMail.getText()
												.toString();
										if (newMail.equals("")) {
											Toast.makeText(context,
													R.string.ritenta,
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
									public void onClick(DialogInterface dialog,
											int id) {
										dialog.dismiss();
										isAddDialogOpen = false;
									}
								}).create().setOnDismissListener(
				new DialogInterface.OnDismissListener() {

					@Override
					public void onDismiss(DialogInterface dialog) {
						isAddDialogOpen = false;
					}
				});
				addDialog.show();
			}
		});/*
		if (savedInstanceState != null)
			onRestoreInstanceState(savedInstanceState);*/
	}

	// saving the state of the activity
	public void onSaveInstanceState(Bundle state) {
		if (isAddDialogOpen) {
			state.putString("newMail", addedMail.getText().toString());
			state.putBoolean("addDialog", isAddDialogOpen);
		} else if (isDeleteDialogOpen) {
			state.putBoolean("deleteDialog", isDeleteDialogOpen);
			state.putString("deleteMail", mailString);
		}
		super.onSaveInstanceState(state);
	}

	// restore the state of the activity
	public void onRestoreInstanceState(Bundle state) {
		isAddDialogOpen = state.getBoolean("addDialog", false);
		isDeleteDialogOpen = state.getBoolean("deleteDialog", false);
		if (isAddDialogOpen) {
			fabButton.performClick();
			addedMail.setText(state.getCharSequence("newMail").toString());
		} else if (isDeleteDialogOpen) {
			mailString = state.getCharSequence("deleteMail").toString();
			createDeleteDialog();
			deleteDialog.show();
		}
	}

	// long click implementation
	protected boolean onLongListItemClick(View v, int pos, long id) {
		mailString = adapter.getItem(pos);
		createDeleteDialog();
		deleteDialog.show();
		isDeleteDialogOpen = true;
		return true;
	}

	private void createDeleteDialog() {
		// create the dialog
		deleteDialog = new AlertDialog.Builder(context)
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
		deleteDialog.create().setOnCancelListener(
				new DialogInterface.OnCancelListener() {
					
					@Override
					public void onCancel(DialogInterface dialog) {
						isDeleteDialogOpen = false;
					}
				});
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
