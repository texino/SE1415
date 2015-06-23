package it.dei.unipd.esp1415.views;

import it.dei.unipd.esp1415.exceptions.NoSuchSessionException;
import it.dei.unipd.esp1415.utils.LocalStorage;

import java.io.IOException;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;

import com.example.esp1415.R;

public class RenameDeleteDialog extends DialogFragment {

	private String sessionId;
	private String title = "";
	
	public RenameDeleteDialog(String sessionId)
	{
		this.sessionId=sessionId;
	}
	
	@Override
	public Dialog onCreateDialog(Bundle savedInstanceState) {
		// Use the Builder class for convenient dialog construction
		AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
		// Get the layout inflater
		LayoutInflater inflater = getActivity().getLayoutInflater();
		//layout constant created to use EditText
		final View layout = inflater.inflate((R.layout.dialog_rename_delete), null);
		builder.setTitle(title);
		// Inflate and set the layout for the dialog
		// Pass null as the parent view because its going in the dialog layout
		builder.setView(layout)
				// Add action buttons
				.setPositiveButton(R.string.rinomina,
						new DialogInterface.OnClickListener() {
							@Override
							public void onClick(DialogInterface dialog, int id) {
								
								EditText newName = (EditText) layout.findViewById(R.id.renamesession);
								//if newname is an empty string, dismiss dialog
								//TOCLEAN Log.i("View", newname.getText().toString());
								if (newName.getText().equals("")) RenameDeleteDialog.this.getDialog().cancel();
								else {
									//scrive il nuovo nome
									try {
										LocalStorage.renameSession(sessionId,
												newName.getText().toString());
									} catch (IOException e) {
										Log.i("ERROR",
												"Error getting session list - LocalStorage");
									} catch (NoSuchSessionException e) {
										Log.i("ERROR",
												"Se la sessione non esiste - LocalStorage");
									} catch (IllegalArgumentException e) {
										Log.i("ERROR",
												"i parametri non sono coerenti - LocalStorage");
									}
								}
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
