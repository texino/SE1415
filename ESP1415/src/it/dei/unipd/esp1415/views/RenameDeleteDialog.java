package it.dei.unipd.esp1415.views;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.view.LayoutInflater;

import com.example.esp1415.R;

public class RenameDeleteDialog extends DialogFragment {

	@Override
	public Dialog onCreateDialog(Bundle savedInstanceState) {
		// Use the Builder class for convenient dialog construction
		AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
		// Get the layout inflater
		LayoutInflater inflater = getActivity().getLayoutInflater();

		builder.setTitle(R.string.dialog_title);
		// Inflate and set the layout for the dialog
		// Pass null as the parent view because its going in the dialog layout
		builder.setView(inflater.inflate(R.layout.rename_delete_dialog, null))
				// Add action buttons
				.setPositiveButton(R.string.rinomina,
						new DialogInterface.OnClickListener() {
							@Override
							public void onClick(DialogInterface dialog, int id) {
								// set the new name
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

}
