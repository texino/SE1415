package it.dei.unipd.esp1415.views;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.view.LayoutInflater;
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

		builder.setTitle(title);
		// Inflate and set the layout for the dialog
		// Pass null as the parent view because its going in the dialog layout
		builder.setView(inflater.inflate(R.layout.dialog_rename_delete, null))
				// Add action buttons
				.setPositiveButton(R.string.rinomina,
						new DialogInterface.OnClickListener() {
							@Override
							public void onClick(DialogInterface dialog, int id) {
								//TODO set the new name
								//EditText newname = (EditText) getView().findViewById(R.id.renamesession);
								//if newname is an empty string, dismiss dialog
								/*if (newname.getText().equals("")) RenameDeleteDialog.this.getDialog().cancel();
								else {
									//scrive il nuovo nome TODO
									//newname.getText()
								}*/
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
