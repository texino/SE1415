package it.dei.unipd.esp1415.views;

import it.dei.unipd.esp1415.exceptions.NoSuchSessionException;
import it.dei.unipd.esp1415.utils.LocalStorage;

import java.io.IOException;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.example.esp1415.R;

/**
 * RenameDeleteDialog class: create a dialog to rename and delete a session
 */
public class RenameDeleteDialog extends DialogFragment {

	private String sessionId;
	private boolean isRunning;
	private String title = "Session name: ";
	private String sessionName;
	private Context context;

	public RenameDeleteDialog(String sessionId, String sessionName, String title, boolean isRunning) {
		this.sessionId = sessionId;
		this.sessionName = sessionName;
		this.title += title;
		this.isRunning = isRunning;
	}

	@Override
	public Dialog onCreateDialog(Bundle savedInstanceState) {
		// get activity context (for fragment)
		context = getActivity();
		// Use the Builder class for convenient dialog construction
		AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
		// Get the layout inflater
		LayoutInflater inflater = getActivity().getLayoutInflater();
		// Inflate and set the layout for the dialog
		// Pass null as the parent view because its going in the dialog layout
		final View layout = inflater.inflate((R.layout.dialog_rename_delete),
				null);
		builder.setTitle(title);
		// handler to garbage image object
		ImageView garbage = (ImageView) layout
				.findViewById(R.id.dialog_garbage);
		// secure check, if a session is running it can be eliminated
		if(isRunning)
			garbage.setVisibility(View.GONE);
		// set garbage button listener and onClick
		garbage.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				try {
					LocalStorage.deleteSession(sessionId);

				} catch (NoSuchSessionException e) {
					Log.i("ERROR", "La sessione non esiste - LocalStorage");
				} catch (IllegalArgumentException e) {
					Log.i("ERROR",
							"I parametri non sono coerenti - LocalStorage");
				}
				// fragment update
				refreshfragment();
				// toast to confirm deletion
				Toast.makeText(context, "Session " + sessionName + " delete",
						Toast.LENGTH_SHORT).show();

			}
		});
		// insert current session name in dialog's editText
		final EditText editText = (EditText) layout
				.findViewById(R.id.renamesession);
		editText.setText(sessionName);
		builder.setView(layout)
				// Add action buttons
				.setPositiveButton(R.string.rename,
						new DialogInterface.OnClickListener() {
							@Override
							public void onClick(DialogInterface dialog, int id) {

								String newName = editText.getText().toString();
								// if newname is an empty string, dismiss dialog
								// with toast
								if (newName.equals(""))
									Toast.makeText(context, "Retry",
											Toast.LENGTH_SHORT).show();
								else {
									// save new name
									try {
										LocalStorage.renameSession(sessionId,
												newName);
									} catch (IOException e) {
										Log.i("ERROR",
												"Error getting session list - LocalStorage");
									} catch (NoSuchSessionException e) {
										Log.i("ERROR",
												"La sessione non esiste - LocalStorage");
									} catch (IllegalArgumentException e) {
										Log.i("ERROR",
												"I parametri non sono coerenti - LocalStorage");
									}

									// update fragment
									refreshfragment();
									// toast to confirm rename operation
									Toast.makeText(context, "Session renamed",
											Toast.LENGTH_SHORT).show();
								}
							}
						})
				.setNegativeButton(R.string.cancel,
						new DialogInterface.OnClickListener() {
							public void onClick(DialogInterface dialog, int id) {
								RenameDeleteDialog.this.getDialog().cancel();
							}
						});
		return builder.create();

	}

	public void refreshfragment() {
		// update fragment during hide transaction
		Fragment fragment = getFragmentManager().findFragmentById(
				R.id.listfragment);
		getFragmentManager().beginTransaction().hide(fragment).commit();
		// show fragment updated and dismiss dialog
		SessionListFragment.dialog.dismiss();
		getFragmentManager().beginTransaction().show(fragment).commit();
	}
}