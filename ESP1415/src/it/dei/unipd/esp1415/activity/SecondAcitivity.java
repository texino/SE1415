package it.dei.unipd.esp1415.activity;

import it.dei.unipd.esp1415.exceptions.IOException;
import it.dei.unipd.esp1415.exceptions.NoSuchSessionException;
import it.dei.unipd.esp1415.objects.FallAdapter;
import it.dei.unipd.esp1415.objects.SessionData;
import it.dei.unipd.esp1415.utils.LocalStorage;

import java.util.Calendar;
import java.util.GregorianCalendar;

import android.app.Activity;
import android.content.res.Resources;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.util.Log;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.esp1415.R;
//import com.example.esp1415.R.layout;


public class SecondAcitivity extends Activity {
public static final String ID_TAG="sessionId";
public static final String NAME_TAG="name";
public static final String DURATION_TAG="duration";
public static final String DATE_TAG="date";

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_second_acitivity);
		Resources res=getResources();
		Drawable drawable=res.getDrawable(R.drawable.ic_launcher);
		TextView date=(TextView)findViewById(R.id.date);
		TextView nameS=(TextView)findViewById(R.id.Session_Name);
		TextView durata=(TextView)findViewById(R.id.durata);
		ListView lista=(ListView)findViewById(R.id.fall_list);
		

		//Prendi dagli extra la sessionId
		String id=getIntent().getExtras().getString(ID_TAG);
		String nameSession =getIntent().getExtras().getString(NAME_TAG);
		String duration=getIntent().getExtras().getString(DURATION_TAG);
		String dataS=getIntent().getExtras().getString(DATE_TAG);
		
		
	
		SessionData data=null;
		try {
			data=LocalStorage.getSessionData(this, id);
		} catch (IllegalArgumentException e) {
			finish();
			Toast.makeText(this, "id errato", Toast.LENGTH_SHORT).show();
			e.printStackTrace();
		} catch (NoSuchSessionException e) {
			Toast.makeText(this, "sessione non esistente!!", Toast.LENGTH_SHORT).show();
			finish();
			e.printStackTrace();
		} catch (java.io.IOException e) {
			// TODO Auto-generated catch block
			Toast.makeText(this, "Errore in lettura", Toast.LENGTH_SHORT).show();
			finish();
			e.printStackTrace();
		}
        nameS.setText(nameSession);
        durata.setText(duration);
        date.setText(dataS);
		Log.d("ACTIVITY SECOND",""+data.getFalls());
		FallAdapter ad=new FallAdapter(this,data.getFalls());//inizializzare oggetto
		lista.setAdapter(ad);
		
	}

}
