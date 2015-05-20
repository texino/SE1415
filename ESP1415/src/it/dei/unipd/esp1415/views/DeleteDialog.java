package it.dei.unipd.esp1415.views;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;

import com.example.esp1415.R;

public class DeleteDialog extends DialogFragment{

	//String id;
	
	@Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        // Use the Builder class for convenient dialog construction
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setMessage(R.string.delete_dialog)
               .setPositiveButton(R.string.conferma, new DialogInterface.OnClickListener() {
                   public void onClick(DialogInterface dialog, int id) {
                       // TODO rinomina la sessione
                   }
               })
               .setNegativeButton(R.string.annulla, new DialogInterface.OnClickListener() {
                   public void onClick(DialogInterface dialog, int id) {
                       // User cancelled the dialog
                   }
               })
               .setNeutralButton(R.string.elimina, new DialogInterface.OnClickListener() {
				
				@Override
				public void onClick(DialogInterface dialog, int id) {
					// TODO Auto-generated method stub
					
				}
			});
        // Create the AlertDialog object and return it
        return builder.create();
    }
/*public DeleteDialog(Context context,String sessionId) {
		super(context);
		this.id=sessionId;
		//TODO crea un dialog per eliminare una sessione
	}*/

}
