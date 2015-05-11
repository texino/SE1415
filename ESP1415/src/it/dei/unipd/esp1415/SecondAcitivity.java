package it.dei.unipd.esp1415;

import it.dei.unipd.esp1415.utils.LocalStorage;

import java.util.Calendar;
import java.util.GregorianCalendar;

import android.app.Activity;
import android.content.res.Resources;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.widget.TextView;

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
		 //TextView hour=(TextView)findViewById(R.id.durata);
         GregorianCalendar cal =new GregorianCalendar();
         //LocalStorage.getSessionData(null,null);
	     date.setText(cal.get(Calendar.DAY_OF_MONTH)+"/"+cal.get(Calendar.MONTH)+"/"+cal.get(Calendar.YEAR));
	     //hour.setText(cal.get(Calendar.AM_PM)+":"+cal.get(Calendar.MINUTE));
		 
	}
	    
}
