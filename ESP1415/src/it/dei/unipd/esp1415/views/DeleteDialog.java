package it.dei.unipd.esp1415.views;

import android.app.Dialog;
import android.content.Context;

public class DeleteDialog extends Dialog{

	String id;
	
	public DeleteDialog(Context context,String sessionId) {
		super(context);
		this.id=sessionId;
		//TODO crea un dialog per eliminare una sessione
	}

}
