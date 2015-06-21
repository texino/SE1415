package it.dei.unipd.esp1415.activity;

import it.dei.unipd.esp1415.adapters.FallAdapter;
import it.dei.unipd.esp1415.exceptions.NoSuchSessionException;
import it.dei.unipd.esp1415.objects.FallInfo;
import it.dei.unipd.esp1415.objects.SessionData;
import it.dei.unipd.esp1415.utils.LocalStorage;
import it.dei.unipd.esp1415.utils.Utils;


import java.util.ArrayList;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.esp1415.R;


public class SessionDataActivity extends Activity {
	public static final String ID_TAG="sessionId";

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
	    String id;
		setContentView(R.layout.activity_session_data_layout);
		TextView date=(TextView)findViewById(R.id.date);
		TextView nameS=(TextView)findViewById(R.id.Session_Name);
		TextView durata=(TextView)findViewById(R.id.durata);
		Button delete=(Button)findViewById(R.id.delete);
		ListView lista=(ListView)findViewById(R.id.fall_list);
		
       
		//Prendi dagli extra la sessionId
	    id=getIntent().getExtras().getString(ID_TAG);
		SessionData session = null;
		try {
			session=LocalStorage.getSessionData(id);
		} catch (IllegalArgumentException e1) {
			e1.printStackTrace();
		} catch (NoSuchSessionException e1) {
			e1.printStackTrace();
		} catch (java.io.IOException e1) {
			e1.printStackTrace();
		}
		String nameSession =session.getName();
		int duration=session.getDuration();
		String dataS=session.getDate();
		
		



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
		
		delete.setOnClickListener(new OnClickListener() {
		
			@Override
			public void onClick(View v) {
		
				LocalStorage.deleteSession(id);				
			}
		});
		
		nameS.setText(nameSession);
		durata.setText(Utils.convertDuration(duration));
		date.setText(dataS);
		Log.d("ACTIVITY SECOND",""+data.getFalls());
		//FallAdapter ad=new FallAdapter(this,data.getFalls());//inizializzare oggetto
		//lista.setAdapter(ad);
		
		
		
		ArrayList<FallInfo> falls=session.getFalls();
		ArrayList<FallInfo> orderedFalls=new ArrayList<FallInfo>();
		int s=falls.size();
		for(int i=s-1;i>=0;i--)
			orderedFalls.add(falls.get(i));
		FallAdapter adapter = new FallAdapter(this,orderedFalls);
		lista.setAdapter(adapter);

	}
}