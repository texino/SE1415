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
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.DialogInterface.OnDismissListener;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.esp1415.R;


public class SessionDataActivity extends Activity {
	public static final String ID_TAG="sessionId";
	String id;
	String nameSession;
	final Context con =this;
	TextView date,nameS,durata;
	ListView lista;
	private AlertDialog alertDialog;
	//String cambiaNomeSessione;
	//EditText cambiaNomeSessione=(EditText)findViewById(R.id.renamesession);

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_session_data_layout_new);
		date=(TextView)findViewById(R.id.text_date);
		nameS=(TextView)findViewById(R.id.text_name);
		durata=(TextView)findViewById(R.id.durata);
		lista=(ListView)findViewById(R.id.fall_list);
		Button cancellaSessione=(Button)findViewById(R.id.delete);
		Button rinominaSessione=(Button)findViewById(R.id.rename);
		//EditText cambiaNomeSessione=(EditText)findViewById(R.id.renamesession);

		//Prendi dagli extra la sessionId
		id=getIntent().getExtras().getString(ID_TAG);

		cancellaSessione.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				cancelClicked();		}
		});

		rinominaSessione.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				final	Dialog dialog=new Dialog(con);
				dialog.setContentView(R.layout.dialog_new_session_layout);
				dialog.setTitle("Rinomina Sessione");

				final EditText cambia=(EditText)dialog.findViewById(R.id.edit_name);
				final	Button buttonConferma = (Button) dialog.findViewById(R.id.button_ok);

				buttonConferma.setOnClickListener(new OnClickListener() {

					@Override
					public void onClick(View v) {
						String cambiaNomeSessione=cambia.getText().toString();
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
						Toast.makeText(getApplicationContext(),"ho cambiato il nome"+" "+cambiaNomeSessione, Toast.LENGTH_SHORT).show();
						dialog.dismiss();
					}
				});

				dialog.show();	
			}


		});




	}
	private void cancelClicked()
	{
		AlertDialog.Builder builder = new AlertDialog.Builder(this);
		LayoutInflater inflater = (LayoutInflater)getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		ViewGroup vg = (ViewGroup)inflater.inflate(R.layout.dialog_delete_session_layout,null);
		builder.setView(vg);
		((Button)vg.findViewById(R.id.button_ok)).setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				try{LocalStorage.deleteSession(id);
				}catch(NoSuchSessionException e){}
				finish();
				Toast.makeText(getApplicationContext(),"Sessione"+" "+nameSession+" "+"cancellata", Toast.LENGTH_SHORT).show();
				alertDialog.dismiss();
			}});
		((Button)vg.findViewById(R.id.button_ko)).setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				alertDialog.dismiss();
			}});
		alertDialog=builder.create();
		alertDialog.setCancelable(true);
		alertDialog.show();
	}

	public void onResume(){
		super.onResume();
		SessionData data=null;
		try {
			data=LocalStorage.getSessionData(id);
		} catch (IllegalArgumentException e) {
			finish();
			Toast.makeText(this, "id errato", Toast.LENGTH_SHORT).show();
			e.printStackTrace();return;
		} catch (NoSuchSessionException e) {
			Toast.makeText(this, "sessione non esistente!!", Toast.LENGTH_SHORT).show();
			finish();
			e.printStackTrace();return;
		} catch (java.io.IOException e) {
			Toast.makeText(this, "Errore in lettura", Toast.LENGTH_SHORT).show();
			finish();
			e.printStackTrace();return;
		}
		nameSession =data.getName();
		int duration=data.getDuration();
		String dataS=data.getDate();
		nameS.setText(nameSession);
		durata.setText(Utils.convertDuration(duration));
		date.setText(dataS);
		Log.d("ACTIVITY SECOND",""+data.getFalls());
		ArrayList<FallInfo> falls=data.getFalls();
		ArrayList<FallInfo> orderedFalls=new ArrayList<FallInfo>();
		int s=falls.size();
		if(s!=0)
		{
		for(int i=s-1;i>=0;i--)
			orderedFalls.add(falls.get(i));
		FallAdapter adapter = new FallAdapter(this,orderedFalls,id);
		lista.setAdapter(adapter);
		}
		else
		{
			((ImageView)this.findViewById(R.id.image_bkg)).setImageDrawable(getResources().getDrawable(R.drawable.image_empty_list));
		}

	}
}