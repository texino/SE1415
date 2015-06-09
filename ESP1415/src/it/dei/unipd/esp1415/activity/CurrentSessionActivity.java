package it.dei.unipd.esp1415.activity;

import it.dei.unipd.esp1415.exceptions.LowSpaceException;
import it.dei.unipd.esp1415.exceptions.NoSuchSessionException;
import it.dei.unipd.esp1415.objects.SessionData;
import it.dei.unipd.esp1415.tasks.ESPService;
import it.dei.unipd.esp1415.tasks.ESPService.ESPBinder;
import it.dei.unipd.esp1415.utils.LocalStorage;
import it.dei.unipd.esp1415.utils.PreferenceStorage;
import it.dei.unipd.esp1415.views.GraphicView;

import java.io.IOException;

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
import android.content.SharedPreferences.Editor;
import android.os.Bundle;
import android.os.IBinder;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.esp1415.R;

public class CurrentSessionActivity extends Activity{

	//Views
	private GraphicView graphic;
	private Button btnStart,btnStop;
	private TextView textHours,textMinutes,textSeconds,textName,textDate;
	private ImageView imgSession;
	private Context actContext;
	private boolean emptySession;

	String sessionId;
	boolean running;
	AlertDialog alertDialog;
	private long duration;

	private ESPService service;
	public final static String TAG="ACTIVITY",ID_TAG="ID",NAME_TAG="NAME",DATE_TAG="DATE",EMPTY_TAG="EMPTY";
	public final static String DURATION_TAG="DURATION";

	private ServiceConnection connection = new ServiceConnection() {
		@Override
		public void onServiceConnected(ComponentName className,IBinder iBinder) 
		{
			//il service è collegato ed è rappresentato dalla variabile "iBinder"
			ESPBinder binder = (ESPBinder)iBinder;
			//prendiamo il service vero e proprio
			service = binder.getService();
			setTimeText(service.getDuration());
			if(service.isRunning())
				btnStart.setBackgroundResource(R.drawable.button_pause);
			else
				btnStart.setBackgroundResource(R.drawable.button_play);
		}

		@Override
		public void onServiceDisconnected(ComponentName arg0) {
			//il service è scollegato
		}
	};

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		actContext=this;
		//prende i dati per popolare il layout
		getData();
		//Imposta layout ed eventi
		setLayout();
		//Popola layout coi dati
		populateLayout();

		//inizializza broadcast manager
		LocalBroadcastManager manager=LocalBroadcastManager.getInstance(this);
		//receiver per i dati del grafico 
		manager.registerReceiver(new BroadcastReceiver() {
			@Override
			public void onReceive(Context context, Intent intent) {
				decodeGraphicIntent(intent);}},
				new IntentFilter(ESPService.ACTION_GRAPHIC_BROADCAST));
		//receiver per i dati del tempo
		manager.registerReceiver(new BroadcastReceiver() {
			@Override
			public void onReceive(Context context, Intent intent) {
				decodeTimeIntent(intent);}},
				new IntentFilter(ESPService.ACTION_TIME_BROADCAST));
		//receiver per i dati delle cadute
		manager.registerReceiver(new BroadcastReceiver() {
			@Override
			public void onReceive(Context context, Intent intent) {
				decodeFallIntent(intent);}},
				new IntentFilter(ESPService.ACTION_FALL_BROADCAST));
	}

	//BUILDING METHODS

	private void getData()
	{
		Bundle extras=getIntent().getExtras();
		emptySession=extras.getBoolean(EMPTY_TAG);
		duration=getDurationPreference();
		running=getStatusPreference();

	}

	/**
	 * Imposta il layout per quest'activity e iniziaizza oggetti ed eventi
	 */
	private void setLayout()
	{
		setContentView(R.layout.current_session_activity_layout);
		btnStart=(Button)findViewById(R.id.button_start);
		btnStop=(Button)findViewById(R.id.button_stop);
		textHours=(TextView)findViewById(R.id.text_hours);
		textMinutes=(TextView)findViewById(R.id.text_minutes);
		textSeconds=(TextView)findViewById(R.id.text_seconds);
		textName=(TextView)findViewById(R.id.text_name);
		textDate=(TextView)findViewById(R.id.text_date);	
		graphic=(GraphicView)findViewById(R.id.graphic);

		//Eventi
		btnStart.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				if(!running){//il service non c'è quindi lo avvio dandogli sessione e durata
					Log.d(TAG,"STARTED");
					Intent serviceIntent = new Intent(actContext,ESPService.class);
					serviceIntent.putExtra(ID_TAG,sessionId);
					serviceIntent.putExtra(DURATION_TAG,duration);
					actContext.startService(serviceIntent);
					//lego l'activity al service
					bindService(serviceIntent,connection,Context.BIND_AUTO_CREATE);
					storeStatusPreference(true);
					running=true;
					btnStart.setBackgroundResource(R.drawable.button_pause);}
				else{//il service è attivo quindi lo fermo
					Log.d(TAG,"STOPPED");
					btnStart.setBackgroundResource(R.drawable.button_play);
					storeDurationPreference(service.getDuration());
					storeStatusPreference(false);
					running=false;
					service.pause();
					try {
						LocalStorage.pauseSession(sessionId,(int)duration);}
					catch (NoSuchSessionException e) {
						e.printStackTrace();}}}});
		btnStop.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				btnStart.setBackgroundResource(R.drawable.button_play);
				//Fermo la sessione e il service e vado alla schermata associata
				service.stop();
				PreferenceStorage.storeSimpleData(CurrentSessionActivity.this,"ServiceStatus","false");
				try {
					LocalStorage.stopSession(sessionId,duration);}
				catch (NoSuchSessionException e) {

					e.printStackTrace();}
				CurrentSessionActivity.this.finish();}});
	}

	private void populateLayout()
	{	
		//impostiamo la durata
		duration=getDurationPreference();
		setTimeText(duration);

		Bundle extras=getIntent().getExtras();
		if(extras.getBoolean(EMPTY_TAG))//è una sessione ancora da creare
		{	
			showDialog();
			return;
		}
		//è una sessione già creata
		//recuperiamo e impostiamo i dati della sessione
		sessionId=extras.getString(ID_TAG);
		textName.setText(extras.getString(NAME_TAG));
		textDate.setText(extras.getString(DATE_TAG));
		running=getStatusPreference();
		if(running)
		{
			//ci leghiamo al service già esistente dandogli l'id della sessione
			Intent serviceIntent = new Intent(CurrentSessionActivity.this,ESPService.class);
			serviceIntent.putExtra(ID_TAG,sessionId);
			bindService(serviceIntent,connection,Context.BIND_AUTO_CREATE);
			btnStart.setBackgroundResource(R.drawable.button_pause);
		}
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
						//impostiamo lo stato del service e la durata della sessione
						storeStatusPreference(false);
						storeDurationPreference(0);
						running=false;
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

	//PREFERENCE METHODS

	private final static String SERVICE_STATUS_TAG="ServiceStatus";

	private void storeDurationPreference(long duration)
	{
		Editor e=this.getPreferences(MODE_PRIVATE).edit();
		e.putLong(DURATION_TAG,duration);
		e.commit();
	}

	private void storeStatusPreference(boolean status)
	{
		Editor e=this.getPreferences(MODE_PRIVATE).edit();
		e.putBoolean(SERVICE_STATUS_TAG,status);
		e.commit();
	}

	private long getDurationPreference()
	{
		return this.getPreferences(MODE_PRIVATE).getLong(DURATION_TAG,0);
	}

	private boolean getStatusPreference()
	{
		return this.getPreferences(MODE_PRIVATE).getBoolean(SERVICE_STATUS_TAG, false);
	}

	//INTENT METHODS

	private void decodeTimeIntent(Intent intent)
	{

	}

	private void decodeFallIntent(Intent intent)
	{

	}

	private void decodeGraphicIntent(Intent intent)
	{
		//aggiorna UI con i dati dell'intent
		float x=intent.getFloatExtra(ESPService.EXTRA_X, 0);
		float y=intent.getFloatExtra(ESPService.EXTRA_Y, 0);
		float z=intent.getFloatExtra(ESPService.EXTRA_Z, 0);
		graphic.add(x, y, z);
		long time = intent.getLongExtra(ESPService.EXTRA_TIME, 0);
		setTimeText(time);
	}

	//ACTIVITY METHODS

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

	private void setTimeText(long time)
	{
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
}