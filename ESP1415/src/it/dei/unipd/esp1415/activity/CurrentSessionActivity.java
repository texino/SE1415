package it.dei.unipd.esp1415.activity;

import java.io.IOException;
import com.example.esp1415.R;

import it.dei.unipd.esp1415.exceptions.LowSpaceException;
import it.dei.unipd.esp1415.exceptions.NoSuchSessionException;
import it.dei.unipd.esp1415.objects.SessionData;
import it.dei.unipd.esp1415.tasks.ESPService;
import it.dei.unipd.esp1415.tasks.ESPService.LocalBinder;
import it.dei.unipd.esp1415.utils.LocalStorage;
import it.dei.unipd.esp1415.views.GraphicView;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnCancelListener;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.IBinder;
import android.support.v4.content.LocalBroadcastManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

public class CurrentSessionActivity extends Activity{

	//Views
	private GraphicView graphic;
	private Button btn_start,btn_stop;
	private TextView textHours,textMinutes,textSeconds,textName,textDate;
	private int duration;
	int i=0;
	String sessionId;
	boolean running;
	AlertDialog alertDialog;

	private ESPService service;
	public final static String TAG="ACTIVITY",ID_TAG="ID",NAME_TAG="NAME",DATE_TAG="DATE",EMPTY_TAG="EMPTY";

	private ServiceConnection connection = new ServiceConnection() {
		@Override
		public void onServiceConnected(ComponentName className,IBinder iBinder) 
		{
			//il service è collegato ed è rappresentato dalla variabile "iBinder"
			LocalBinder binder = (LocalBinder)iBinder;
			//prendiamo il service vero e proprio
			service = binder.getService();
		}

		@Override
		public void onServiceDisconnected(ComponentName arg0) {
			//il service è scollegato
		}
	};

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		//imposta layout ed eventi
		setLayout();
		//Popola coi dati
		Bundle extras=getIntent().getExtras();
		if(extras.getBoolean(EMPTY_TAG))//è una sessione ancora da creare
			showDialog();
		else
		{
			//è una sessione già creata quindi ci sono (la durata viene data dal service)
			sessionId=extras.getString(ID_TAG);
			textName.setText(extras.getString(NAME_TAG));
			textDate.setText(extras.getString(DATE_TAG));
			//ci leghiamo al service per questa sessione
			Intent serviceIntent = new Intent(CurrentSessionActivity.this,ESPService.class);
			serviceIntent.putExtra(ID_TAG,sessionId);
			bindService(serviceIntent,connection,Context.BIND_AUTO_CREATE);
		}

		//inizializza broadcast manager
		LocalBroadcastManager manager=LocalBroadcastManager.getInstance(this);
		manager.registerReceiver(new BroadcastReceiver() {
			@Override
			public void onReceive(Context context, Intent intent) {
				decodeReceivedIntent(intent);}}, new IntentFilter(ESPService.ACTION_LOCATION_BROADCAST));
	}

	/**
	 * Imposta il layout per quest'activity e iniziaizza oggetti ed eventi
	 */
	private void setLayout()
	{
		setContentView(R.layout.current_session_activity_layout);
		btn_start=(Button)findViewById(R.id.button_start);
		btn_stop=(Button)findViewById(R.id.button_stop);
		textHours=(TextView)findViewById(R.id.text_hours);
		textMinutes=(TextView)findViewById(R.id.text_minutes);
		textSeconds=(TextView)findViewById(R.id.text_seconds);
		textName=(TextView)findViewById(R.id.text_name);
		textDate=(TextView)findViewById(R.id.text_date);	
		graphic=(GraphicView)findViewById(R.id.graphic);

		//Eventi
		btn_start.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				if(!running){
					//il service è fermo quindi lo avvio 
					service.play();
					running=true;}
				else{
					//il service è attivo quindi lo si mette in pausa
					service.pause();
					running=false;}}});
		btn_stop.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				//Fermo la sessione e il service e vado alla schermata associata
				service.stop();
				try {
					LocalStorage.stopSession(sessionId,duration);}
				catch (NoSuchSessionException e) {
					e.printStackTrace();}
				CurrentSessionActivity.this.finish();}});
	}

	/**
	 * Mostra un dialog per la creazione della sessione
	 */
	private void showDialog()
	{
		LayoutInflater inflater = (LayoutInflater)getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		ViewGroup vg = (ViewGroup) inflater.inflate(R.layout.session_name_dialog_layout, null);
		AlertDialog.Builder builder = new AlertDialog.Builder(this);
		builder.setTitle(R.string.dialog_name_title);
		builder.setView(vg);
		builder.setOnCancelListener(new OnCancelListener(){
			@Override
			public void onCancel(DialogInterface dialog) {
				//L'utente si rifiuta di inserire il nome quindi torniamo indietro
				CurrentSessionActivity.this.finish();}});

		Button ok=(Button)vg.findViewById(R.id.button_ok);
		final EditText edit=(EditText)vg.findViewById(R.id.edit_name);

		ok.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				String name=edit.getText().toString();
				if(name.length()<1)//non è stato inserito un nome
					Toast.makeText(CurrentSessionActivity.this, R.string.text_insert_name,Toast.LENGTH_SHORT).show();
				else
				{
					//creiamo la sessione
					try {
						SessionData s = LocalStorage.createNewSession(CurrentSessionActivity.this,name);
						textName.setText(s.getName());
						textDate.setText(s.getDate());
						sessionId=s.getId();
						//Ci leghiamo al service
						Intent serviceIntent = new Intent(CurrentSessionActivity.this,ESPService.class);
						serviceIntent.putExtra(ID_TAG,sessionId);
						bindService(serviceIntent,connection,Context.BIND_AUTO_CREATE);
						alertDialog.dismiss();
					} catch (IllegalArgumentException e) {
						Toast.makeText(CurrentSessionActivity.this,R.string.error_arguments,Toast.LENGTH_SHORT).show();
						e.printStackTrace();
					} catch (IOException e) {
						Toast.makeText(CurrentSessionActivity.this,R.string.error_file_writing,Toast.LENGTH_SHORT).show();
						e.printStackTrace();
					} catch (LowSpaceException e) {
						Toast.makeText(CurrentSessionActivity.this,R.string.text_low_memory,Toast.LENGTH_SHORT).show();
						e.printStackTrace();
					}
				}}});
		alertDialog = builder.create();
		alertDialog.setCancelable(true);
		alertDialog.show();
	}

	private void decodeReceivedIntent(Intent intent)
	{
		//aggiorna UI con i dati dell'intent
		float x=intent.getFloatExtra(ESPService.EXTRA_X, 0);
		float y=intent.getFloatExtra(ESPService.EXTRA_Y, 0);
		float z=intent.getFloatExtra(ESPService.EXTRA_Z, 0);
		graphic.add(x, y, z);
		long time = intent.getLongExtra(ESPService.EXTRA_TIME, 0);
		int totalSec=(int)(time/1000);
		int h=totalSec/3600;
		int m=(totalSec%3600)/60;
		int s=(totalSec%3600)%60;

		String t="00";
		if(h<10)
			t="0"+h;
		else
			t=""+h;
		textHours.setText(t);

		if(m<10)
			t="0"+m;
		else
			t=""+m;
		textMinutes.setText(t);

		if(s<10)
			t="0"+s;
		else
			t=""+s;
		textSeconds.setText(t);
	}

	@Override
	public void onSaveInstanceState(Bundle instance)
	{
		instance.putString("sec",textSeconds.getText().toString());
		instance.putString("min",textMinutes.getText().toString());
		instance.putString("hour",textHours.getText().toString());
		graphic.saveStatusOnBundle(instance);
		super.onSaveInstanceState(instance);
	}

	@Override
	public void onRestoreInstanceState(Bundle state) {
		super.onRestoreInstanceState(state);
		textSeconds.setText(state.getString("sec"));
		textMinutes.setText(state.getString("min"));
		textHours.setText(state.getString("hour"));
		graphic.restoreStatusFromBundle(state);
	}
}