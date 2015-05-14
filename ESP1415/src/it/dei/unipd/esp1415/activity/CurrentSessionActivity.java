package it.dei.unipd.esp1415.activity;

import java.io.IOException;
import java.util.ArrayList;

import com.example.esp1415.R;

import it.dei.unipd.esp1415.exceptions.LowSpaceException;
import it.dei.unipd.esp1415.exceptions.NoSuchSessionException;
import it.dei.unipd.esp1415.objects.FallInfo;
import it.dei.unipd.esp1415.objects.SessionData;
import it.dei.unipd.esp1415.tasks.ESPService;
import it.dei.unipd.esp1415.tasks.ESPService.ESPBinder;
import it.dei.unipd.esp1415.utils.LocalStorage;
import it.dei.unipd.esp1415.utils.PreferenceStorage;
import it.dei.unipd.esp1415.views.GraphicView;
import android.annotation.SuppressLint;
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
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

@SuppressLint("InflateParams") public class CurrentSessionActivity extends Activity{

	//Views
	private GraphicView graphic;
	private Button btnStart,btnStop;
	private TextView textHours,textMinutes,textSeconds,textName,textDate;
	private Context actContext;
	private ListView listFalls;
	String sessionId;
	boolean running;
	ArrayAdapter<String> adapter;
	AlertDialog alertDialog;
	private long duration;
	private boolean empty;
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
			running=service.isRunning();
			if(running)
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
		if(savedInstanceState==null)//l'activity è creata da zero
		{
			registerReceivers();
			setLayout();//Imposta layout ed eventi
			Bundle extras=getIntent().getExtras();
			empty=extras.getBoolean(EMPTY_TAG);
			sessionId=extras.getString(ID_TAG);
			Log.d(TAG,"Session: "+sessionId);
		}
		//facciamo qui il recupero dei dati per evitare casi di incongruenze coi dati visualizzati
		//e i dati presenti in memoria
		if(empty)//è una sessione ancora da creare
			showDialog();
		else//dobbiamo recuperare i dati
		{
			boolean ok=false;
			SessionData session=null;
			try {
				session=LocalStorage.getSessionData(actContext, sessionId);
				ok=true;
			} catch (IllegalArgumentException e) {
				Toast.makeText(CurrentSessionActivity.this,R.string.error_arguments,Toast.LENGTH_SHORT).show();
				e.printStackTrace();
			} catch (IOException e) {
				Toast.makeText(CurrentSessionActivity.this,R.string.error_file_writing,Toast.LENGTH_SHORT).show();
				e.printStackTrace();
			} catch (NoSuchSessionException e) {
				Toast.makeText(CurrentSessionActivity.this,R.string.error_inexistent_session,Toast.LENGTH_SHORT).show();
				e.printStackTrace();
			}
			if(!ok){
				this.finish();
				return;}
			duration=session.getDuration();
			setTimeText(duration);
			textName.setText(session.getName());
			textDate.setText(session.getDate());
			//TODO popolare la lista
			ArrayList<FallInfo> falls=session.getFalls();
			ArrayList<String> strings=new ArrayList<String>();
			int s=falls.size();
			for(int i=0;i<s;i++)
				strings.add(falls.get(i).getDate());
			adapter = new ArrayAdapter<String>(this,android.R.layout.simple_list_item_1, android.R.id.text1,strings);
			listFalls.setAdapter(adapter);
			//a questo punto abbiamo il layout e i dati impostati
			//visualizziamo lo stato del service
			running=getServiceSavedStatus();
			if(running)//il service dovrebbe essere attivo
			{
				//ci leghiamo al service che dovrebbe essere già esistente 
				Intent serviceIntent = new Intent(CurrentSessionActivity.this,ESPService.class);
				serviceIntent.putExtra(ID_TAG,sessionId);//gli diamo l'id della sessione
				serviceIntent.putExtra(DURATION_TAG,duration);//gli diamo l'ultima conosciuta durata della sessione
				startService(serviceIntent);
				bindService(serviceIntent,connection,Context.BIND_AUTO_CREATE);
				btnStart.setBackgroundResource(R.drawable.button_pause);
			}
		}
	}

	//BUILDING METHODS

	private void registerReceivers()
	{
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

	private boolean getServiceSavedStatus()
	{
		return this.getPreferences(MODE_PRIVATE).getBoolean(SERVICE_STATUS_TAG, false);
	}

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
		listFalls=(ListView)findViewById(R.id.list);

		//Eventi
		btnStart.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				if(!running)
					startClicked();
				else
					pauseClicked();}});
		btnStop.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				stopClicked();}});


	}

	private void startClicked()
	{
		running=true;
		btnStart.setBackgroundResource(R.drawable.button_pause);

		//si avvia il service dandogli durata e id Sessione
		Intent serviceIntent = new Intent(actContext,ESPService.class);
		serviceIntent.putExtra(ID_TAG,sessionId);
		serviceIntent.putExtra(DURATION_TAG,duration);
		startService(serviceIntent);
		//lego l'activity al service
		bindService(serviceIntent,connection,Context.BIND_AUTO_CREATE);
		storeStatusPreference(true);
	}

	private void pauseClicked()
	{
		running=false;
		btnStart.setBackgroundResource(R.drawable.button_play);

		this.unbindService(connection);
		//la durata è salvata alla chiusura del service
		duration=service.getDuration();
		service.stop();
		storeDurationPreference(duration);
		storeStatusPreference(false);
	}

	private void stopClicked()
	{
		running=false;
		btnStart.setBackgroundResource(R.drawable.button_play);

		if(service!=null)//siamo legati ad una sessione attiva
			service.stop();
		storeStatusPreference(false);
		try {
			LocalStorage.stopSession(sessionId,(int)duration);}
		catch (NoSuchSessionException e) {
			e.printStackTrace();}

		CurrentSessionActivity.this.finish();
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
						//impostiamo i dati
						SessionData s = LocalStorage.createNewSession(CurrentSessionActivity.this,name);
						textName.setText(s.getName());
						textDate.setText(s.getDate());
						sessionId=s.getId();
						duration=0;
						empty=false;
						running=false;
						adapter = new ArrayAdapter<String>(actContext,android.R.layout.simple_list_item_1, android.R.id.text1,(new ArrayList<String>()));
						listFalls.setAdapter(adapter);
						//impostiamo lo stato del service e la durata della sessione
						storeStatusPreference(false);
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
		Log.d(TAG,"INTENT TYPE : "+intent.getType());
		String date=intent.getStringExtra(ESPService.EXTRA_FALL_DATE);
		Log.d(TAG,"FALL DATE : "+date);
		adapter.add(date);
	}

	private void decodeGraphicIntent(Intent intent)
	{
		Log.d(TAG,"RECEIVED GRAPHIC INTENT");
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