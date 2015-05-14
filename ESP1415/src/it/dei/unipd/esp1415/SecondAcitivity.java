package it.dei.unipd.esp1415;

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
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.esp1415.R;
//import com.example.esp1415.R.layout;


public class SecondAcitivity extends Activity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_second_acitivity);
		Resources res=getResources();
		 Drawable drawable=res.getDrawable(R.drawable.ic_launcher);
		 TextView date=(TextView)findViewById(R.id.date);
		 ListView lista=(ListView)findViewById(R.id.fall_list);
		
		//Prendi dagli extra la sessionId
		 String id="FallId";
		 SessionData data=null;
		 try {
			data=LocalStorage.getSessionData(this, id);
		} catch (IllegalArgumentException e) {
			finish();
			Toast.makeText(this, "id errato", Toast.LENGTH_SHORT).show();
			e.printStackTrace();
		} catch (NoSuchSessionException e) {
			// TODO Auto-generated catch block
			Toast.makeText(this, "sessione non esistente!!", Toast.LENGTH_SHORT).show();
			finish();
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			Toast.makeText(this, "Errore in lettura", Toast.LENGTH_SHORT).show();
			   finish();
			e.printStackTrace();
		}
		 
		 FallAdapter ad=new FallAdapter(this,data.getFalls());//inizializzare oggetto
		 lista.setAdapter(ad);
		 TextView hour=(TextView)findViewById(R.id.durata);
         GregorianCalendar cal =new GregorianCalendar();
         //LocalStorage.getSessionData(null,null);
	     date.setText(cal.get(Calendar.DAY_OF_MONTH)+"/"+cal.get(Calendar.MONTH)+"/"+cal.get(Calendar.YEAR));
	     //hour.setText(cal.get(Calendar.AM_PM)+":"+cal.get(Calendar.MINUTE));
		 
	}
	    
}
