package it.dei.unipd.esp1415.activity;

import it.dei.unipd.esp1415.adapters.FallAdapter;
import it.dei.unipd.esp1415.exceptions.IOException;
import it.dei.unipd.esp1415.exceptions.NoSuchSessionException;
import it.dei.unipd.esp1415.objects.FallInfo;
import it.dei.unipd.esp1415.objects.SessionData;
import it.dei.unipd.esp1415.utils.LocalStorage;
import it.dei.unipd.esp1415.utils.Utils;

import java.util.ArrayList;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.esp1415.R;

public class SessionDataActivity extends Activity {
	public static final String ID_TAG = "sessionId";
	public static final String NAME_TAG = "name";
	public static final String DURATION_TAG = "duration";
	public static final String DATE_TAG = "date";
	String id;
	String nameSession;
	final Context con = this;

	// String cambiaNomeSessione;
	// EditText cambiaNomeSessione=(EditText)findViewById(R.id.renamesession);

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_session_data_layout);
		TextView date = (TextView) findViewById(R.id.date);
		TextView nameS = (TextView) findViewById(R.id.Session_Name);
		TextView durata = (TextView) findViewById(R.id.durata);
		ListView lista = (ListView) findViewById(R.id.fall_list);
		Button cancellaSessione = (Button) findViewById(R.id.delete);
		Button rinominaSessione = (Button) findViewById(R.id.rename);
		// EditText
		// cambiaNomeSessione=(EditText)findViewById(R.id.renamesession);

		// Prendi dagli extra la sessionId
		id = getIntent().getExtras().getString(ID_TAG);

		SessionData session = null;
		try {
			session = LocalStorage.getSessionData(id);
		} catch (IllegalArgumentException e1) {
			e1.printStackTrace();
		} catch (NoSuchSessionException e1) {
			e1.printStackTrace();
		} catch (java.io.IOException e1) {
			e1.printStackTrace();
		}
		nameSession = session.getName();
		int duration = session.getDuration();
		String dataS = session.getDate();

		SessionData data = null;
		try {
			data = LocalStorage.getSessionData(id);
		} catch (IllegalArgumentException e) {
			finish();
			Toast.makeText(this, "id errato", Toast.LENGTH_SHORT).show();
			e.printStackTrace();
			return;
		} catch (NoSuchSessionException e) {
			Toast.makeText(this, "sessione non esistente!!", Toast.LENGTH_SHORT)
					.show();
			finish();
			e.printStackTrace();
			return;
		} catch (java.io.IOException e) {
			Toast.makeText(this, "Errore in lettura", Toast.LENGTH_SHORT)
					.show();
			finish();
			e.printStackTrace();
			return;
		}
		nameS.setText(nameSession);
		durata.setText(Utils.convertDuration(duration));
		date.setText(dataS);
		Log.d("ACTIVITY SECOND", "" + data.getFalls());

		ArrayList<FallInfo> falls = session.getFalls();
		ArrayList<FallInfo> orderedFalls = new ArrayList<FallInfo>();
		int s = falls.size();
		for (int i = s - 1; i >= 0; i--)
			orderedFalls.add(falls.get(i));
		FallAdapter adapter = new FallAdapter(this, orderedFalls, id);
		lista.setAdapter(adapter);

		cancellaSessione.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				// String idx=getIntent().getExtras().getString(ID_TAG);

				try {
					LocalStorage.deleteSession(id);

				} catch (NoSuchSessionException e) {
				}
				finish();
				Toast.makeText(getApplicationContext(),
						"Sessione" + " " + nameSession + " " + "cancellata",
						Toast.LENGTH_SHORT).show();
			}
		});

		rinominaSessione.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				final Dialog dialog = new Dialog(con);
				dialog.setContentView(R.layout.dialog_new_session_layout);
				dialog.setTitle("Rinomina Sessione");

				final EditText cambia = (EditText) dialog
						.findViewById(R.id.edit_name);
				final Button buttonConferma = (Button) dialog
						.findViewById(R.id.button_ok);

				buttonConferma.setOnClickListener(new OnClickListener() {

					@Override
					public void onClick(View v) {
						String cambiaNomeSessione = cambia.getText().toString();
						try {
							LocalStorage.renameSession(id, cambiaNomeSessione);
						} catch (IllegalArgumentException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						} catch (NoSuchSessionException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						} catch (java.io.IOException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
						Toast.makeText(
								getApplicationContext(),
								"ho cambiato il nome" + " "
										+ cambiaNomeSessione,
								Toast.LENGTH_SHORT).show();
						dialog.dismiss();
					}
				});

				dialog.show();
			}

		});

	}

}