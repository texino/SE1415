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
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.example.esp1415.R;

public class RenameDeleteDialog extends DialogFragment {

	private String sessionId;
	private String title = "Session name: ";
	private String sessionName;
	private Context context;

	public RenameDeleteDialog(String sessionId, String sessionName, String title) {
		this.sessionId = sessionId;
		this.sessionName = sessionName;
		this.title += title;
	}

	@Override
	public Dialog onCreateDialog(Bundle savedInstanceState) {
		// get activity context (for fragment)
		context = getActivity();
		// Use the Builder class for convenient dialog construction
		AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
		// Get the layout inflater
		LayoutInflater inflater = getActivity().getLayoutInflater();
		// layout constant created to use EditText
		final View layout = inflater.inflate((R.layout.dialog_rename_delete),
				null);
		builder.setTitle(title);
		ImageView garbage = (ImageView) layout
				.findViewById(R.id.dialog_garbage);
		// set garbage button listener
		garbage.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				try {
					LocalStorage.deleteSession(sessionId);

				} catch (NoSuchSessionException e) {
					Log.i("ERROR", "la sessione non esiste - LocalStorage");
				} catch (IllegalArgumentException e) {
					Log.i("ERROR",
							"i parametri non sono coerenti - LocalStorage");
				}
				Toast.makeText(context,
						"Sessione " + sessionName + " cancellata",
						Toast.LENGTH_SHORT).show();
				SessionListFragment.dialog.dismiss();
				//TODO aggiornare la lista del fragment
			}
		});
		// insert current session name in dialog's editText
		final EditText newName = (EditText) layout
				.findViewById(R.id.renamesession);
		newName.setText(sessionName);
		// Inflate and set the layout for the dialog
		// Pass null as the parent view because its going in the dialog layout
		builder.setView(layout)
				// Add action buttons
				.setPositiveButton(R.string.rinomina,
						new DialogInterface.OnClickListener() {
							@Override
							public void onClick(DialogInterface dialog, int id) {

								// if newname is an empty string, dismiss dialog
								if (newName.getText().toString().equals(""))
									Toast.makeText(context, "Retry",
											Toast.LENGTH_SHORT).show();
								else {
									// scrive il nuovo nome
									try {
										LocalStorage.renameSession(sessionId,
												newName.getText().toString());
										// SessionListFragment.listrefresh();
									} catch (IOException e) {
										Log.i("ERROR",
												"Error getting session list - LocalStorage");
									} catch (NoSuchSessionException e) {
										Log.i("ERROR",
												"La sessione non esiste - LocalStorage");
									} catch (IllegalArgumentException e) {
										Log.i("ERROR",
												"i parametri non sono coerenti - LocalStorage");
									}
								}
								//TODO aggiornare la lista del fragment
								//non funziona
								// SessionListFragment.adapter.notifyDataSetChanged();
							}
						})
				.setNegativeButton(R.string.annulla,
						new DialogInterface.OnClickListener() {
							public void onClick(DialogInterface dialog, int id) {
								RenameDeleteDialog.this.getDialog().cancel();
							}
						});
		return builder.create();

	}

	public void setTitle(String title) {
		this.title = title;
	}
}