package it.dei.unipd.esp1415.activity;

import it.dei.unipd.esp1415.adapters.FallAdapter;
import it.dei.unipd.esp1415.exceptions.LowSpaceException;
import it.dei.unipd.esp1415.exceptions.NoSuchFallException;
import it.dei.unipd.esp1415.exceptions.NoSuchSessionException;
import it.dei.unipd.esp1415.objects.FallInfo;
import it.dei.unipd.esp1415.objects.SessionData;
import it.dei.unipd.esp1415.objects.SessionInfo;
import it.dei.unipd.esp1415.tasks.ESPService;
import it.dei.unipd.esp1415.tasks.ESPService.ESPBinder;
import it.dei.unipd.esp1415.utils.LocalStorage;
import it.dei.unipd.esp1415.utils.Utils;
import it.dei.unipd.esp1415.views.GraphicView;

import java.io.IOException;
import java.util.ArrayList;

import com.example.esp1415.R;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.Dialog;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.content.SharedPreferences.Editor;
import android.os.Bundle;
import android.os.IBinder;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

@SuppressLint("InflateParams") 
public class CurrentSessionActivity extends Activity{

	//Views
	private GraphicView graphic;
	private ImageButton btnStart,btnStop;
	private TextView textDuration,textName,textDate;
	private Context actContext;
	private ListView listFalls;
	private String sessionId;
	private boolean running;
	private FallAdapter adapter;
	private long duration;
	private boolean empty;
	private ESPService service;
	public final static String TAG="ACTIVITY",ID_TAG="ID",EMPTY_TAG="EMPTY";
	public final static String DURATION_TAG="DURATION";

	private BroadcastReceiver graphicReceiver=new BroadcastReceiver() {
		@Override
		public void onReceive(Context context, Intent intent) {
			decodeGraphicIntent(intent);}
	};
	private BroadcastReceiver fallReceiver=new BroadcastReceiver() {
		@Override
		public void onReceive(Context context, Intent intent) {
			decodeFallIntent(intent);}
	};

	private ServiceConnection connection = new ServiceConnection() {
		@Override
		public void onServiceConnected(ComponentName className,IBinder iBinder) 
		{
			//il service è collegato ed è rappresentato dalla variabile "iBinder"
			ESPBinder binder = (ESPBinder)iBinder;
			//prendiamo il service vero e proprio
			service = binder.getService();
			textDuration.setText(Utils.convertDuration((int)service.getDuration()));
			setRunning(service.isRunning());
		}

		@Override
		public void onServiceDisconnected(ComponentName arg0) {
			//il service è scollegato
		}
	};

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		Log.d(TAG,"ON CREATE");
		setLayout();//Imposta layout ed eventi
		actContext=this;
		if(savedInstanceState!=null)//abbiamo già dei dati
			onRestoreInstanceState(savedInstanceState);
		else//l'activity è creata da zero
		{
			Bundle extras=getIntent().getExtras();
			empty=extras.getBoolean(EMPTY_TAG);
			sessionId=extras.getString(ID_TAG);
		}
		//facciamo qui il recupero dei dati per evitare casi di incongruenze coi dati visualizzati
		//e i dati presenti in memoria
		if(empty)//è una sessione ancora da creare
			(new CreateSessionDialog(this)).show();
		else//dobbiamo recuperare i dati
		{
			boolean ok=false;
			SessionData session=null;
			try {
				session=LocalStorage.getSessionData(sessionId);
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
			if(!ok){//c'è stato un errore
				this.finish();
				return;}
			duration=session.getDuration();
			textDuration.setText(Utils.convertDuration((int) duration));
			textName.setText(session.getName());
			textDate.setText(session.getDate());
			ArrayList<FallInfo> falls=session.getFalls();
			ArrayList<FallInfo> orderedFalls=new ArrayList<FallInfo>();
			int s=falls.size();
			for(int i=s-1;i>=0;i--)
				orderedFalls.add(falls.get(i));
			adapter = new FallAdapter(this,orderedFalls);
			listFalls.setAdapter(adapter);
			//a questo punto abbiamo il layout e i dati impostati
		}
	}

	//BUILDING METHODS

	private void registerReceivers()
	{
		//inizializza broadcast manager
		LocalBroadcastManager manager=LocalBroadcastManager.getInstance(this);
		//receiver per i dati del grafico 
		manager.registerReceiver(graphicReceiver,new IntentFilter(ESPService.ACTION_GRAPHIC_BROADCAST));
		//receiver per i dati delle cadute
		manager.registerReceiver(fallReceiver,new IntentFilter(ESPService.ACTION_FALL_BROADCAST));
	}

	private void unregisterReceivers()
	{
		LocalBroadcastManager manager=LocalBroadcastManager.getInstance(this);
		//receiver per i dati del grafico 
		manager.unregisterReceiver(graphicReceiver);
		//receiver per i dati delle cadute
		manager.unregisterReceiver(fallReceiver);
	}

	private void setLayout()
	{
		setContentView(R.layout.activity_current_session_layout);
		btnStart=(ImageButton)findViewById(R.id.button_start);
		btnStop=(ImageButton)findViewById(R.id.button_stop);
		textDuration=(TextView)findViewById(R.id.text_duration);
		textName=(TextView)findViewById(R.id.text_name);
		textDate=(TextView)findViewById(R.id.text_date);
		listFalls=(ListView)findViewById(R.id.list);
		graphic=(GraphicView)findViewById(R.id.graphic);

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

	/**
	 * Imposta lo stato corrente di esecuzione (salvandolo nelle preferenze) 
	 * cambiando il layout di conseguenza
	 * @param run Lo stato della sessione (true se in esecuzione)
	 */
	private void setRunning(boolean run)
	{
		running=run;
		storeStatusPreference(running);
		if(run)
			btnStart.setImageResource(R.drawable.button_pause);
		else
			btnStart.setImageResource(R.drawable.button_play);
	}

	/**Chiamato quando viene premuto il tasto Play*/
	private void startClicked()
	{
		setRunning(true);
		Intent serviceIntent = new Intent(actContext,ESPService.class);
		serviceIntent.putExtra(ID_TAG,sessionId);
		serviceIntent.putExtra(DURATION_TAG,duration);
		//Avviamo il service passandogli durata e id sessione
		startService(serviceIntent);
		//mi lego al service
		bindService(serviceIntent,connection,Context.BIND_AUTO_CREATE);
	}

	/**Chiamato quando viene premuto il tasto Pause*/
	private void pauseClicked()
	{
		setRunning(false);
		//metto in pausa il service
		service.pause();
		//mi slego dal service
		unbindService(connection);
	}

	/**Chiamato quando viene premuto il tasto Stop*/
	private void stopClicked()
	{
		setRunning(false);
		if(service.isRunning())
		{
			//mi slego dal service
			unbindService(connection);
		}
		//fermo il service
		service.stop();
		Intent i=new Intent(CurrentSessionActivity.this,SessionDataActivity.class);
		i.putExtra(SessionDataActivity.ID_TAG,sessionId);
		startActivity(i);
		CurrentSessionActivity.this.finish();
	}

	//PREFERENCE METHODS

	private final static String SERVICE_STATUS_TAG="ServiceStatus";

	/**
	 * Salva il corrente stato della sessione
	 * @param status Lo stato della sessione (true se in esecuzione)
	 */
	private void storeStatusPreference(boolean status)
	{
		Log.d(TAG,"Store Service Preference Status : "+status);
		Editor e=this.getPreferences(MODE_PRIVATE).edit();
		e.putBoolean(SERVICE_STATUS_TAG,status);
		e.commit();
	}

	/**
	 * Recupera lo stato della sessione salvato
	 * @return true se la sessione dovrebbe essere in esecuzione
	 */
	private boolean getServiceSavedStatus()
	{
		boolean b=this.getPreferences(MODE_PRIVATE).getBoolean(SERVICE_STATUS_TAG, false);
		Log.d(TAG,"Get Service Preference Status : "+b);
		return b;
	}

	//INTENT METHODS

	/**
	 * Interpreta l'intent per l'aggiornamento della lista di cadute
	 */
	private void decodeFallIntent(Intent intent)
	{
		String fallId=intent.getStringExtra(ESPService.EXTRA_FALL_ID);
		FallInfo info=null;
		try {
			info = LocalStorage.getFallData(sessionId,fallId);
		} catch (IllegalArgumentException e) {
			e.printStackTrace();
		} catch (NoSuchFallException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		if(info!=null)
			adapter.insert(info,0);
	}

	/**
	 * Interpreta l'intent per l'aggiornamento del grafico
	 */
	private void decodeGraphicIntent(Intent intent)
	{
		float x=intent.getFloatExtra(ESPService.EXTRA_X, 0);
		float y=intent.getFloatExtra(ESPService.EXTRA_Y, 0);
		float z=intent.getFloatExtra(ESPService.EXTRA_Z, 0);
		graphic.add(x, y, z);
		duration = intent.getLongExtra(ESPService.EXTRA_TIME, 0);
		textDuration.setText(Utils.convertDuration((int)duration));
	}

	//ACTIVITY METHODS

	@Override
	public void onSaveInstanceState(Bundle instance)
	{
		Log.d(TAG,"SAVING");
		graphic.saveStatusOnBundle(instance);
		instance.putString("sessionId",sessionId);
		instance.putBoolean("running",running);
		instance.putBoolean("empty",empty);
		instance.putLong("duration",duration);
		super.onSaveInstanceState(instance);
	}

	@Override
	public void onRestoreInstanceState(Bundle state) {
		super.onRestoreInstanceState(state);
		Log.d(TAG,"RESTORING");
		graphic.restoreStatusFromBundle(state);
		sessionId=state.getString("sessionId");
		duration=state.getLong("duration");
		textDuration.setText(Utils.convertDuration((int)duration));
		empty=state.getBoolean("empty");
		running=state.getBoolean("running");
	}

	//ACTIVITY LIFE
	public void onRestart()
	{
		super.onRestart();
		Log.d(TAG,"ON RESTART");
	}

	public void onPause()
	{
		Log.d(TAG,"ON PAUSE");
		//cerchiamo di salvare l'ultimo durata nota
		if(running)//siamo legati ad un service attivo quindi ci sleghiamo
			this.unbindService(connection);
		try {LocalStorage.pauseSession(sessionId,(int)duration);} 
		catch (NoSuchSessionException e) {
			e.printStackTrace();
		} catch (IllegalArgumentException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		unregisterReceivers();
		super.onPause();
	}

	public void onStop()
	{
		super.onStop();
		Log.d(TAG,"ON STOP");
	}

	public void onResume()
	{
		super.onResume();
		Log.d(TAG,"ON RESUME");
		registerReceivers();
		//visualizziamo lo stato del service
		running=getServiceSavedStatus();
		if(running)//il service dovrebbe essere attivo
			startClicked();
	}

	public void onStart()
	{
		super.onStart();
		Log.d(TAG,"ON START");
	}

	public void onDestroy()
	{
		Log.d(TAG,"ON DESTROY");
		//cerchiamo di salvare l'ultimo durata nota
		if(service!=null){
			try {LocalStorage.pauseSession(sessionId,(int)service.getDuration());} 
			catch (NoSuchSessionException e) {
				e.printStackTrace();
			} catch (IllegalArgumentException e) {
				e.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
			}}
		super.onDestroy();
	}

	public class CreateSessionDialog extends Dialog
	{
		private Context actContext;
		private EditText edit;

		protected CreateSessionDialog(Context context) {
			super(context);
			actContext=context;
			setTitle(R.string.dialog_name_title);
			setContentView(R.layout.dialog_new_session_layout);
			Button ok=(Button)findViewById(R.id.button_ok);
			edit=(EditText)findViewById(R.id.edit_name);
			setTitle(R.string.dialog_name_title);
			setOnCancelListener(new OnCancelListener(){
				@Override
				public void onCancel(DialogInterface dialog) {
					//L'utente si rifiuta di inserire il nome quindi torniamo indietro
					CurrentSessionActivity.this.finish();}});
			ok.setOnClickListener(new View.OnClickListener() {
				@Override
				public void onClick(View v) {
					String name=edit.getText().toString();
					if(name.length()<1)//non è stato inserito un nome
						Toast.makeText(actContext, R.string.text_insert_name,Toast.LENGTH_SHORT).show();
					//creiamo la sessione
					try {
						//impostiamo i dati
						SessionInfo s = LocalStorage.createNewSession(name);
						textName.setText(s.getName());
						textDate.setText(s.getDate());
						sessionId=s.getId();
						duration=0;
						empty=false;
						running=false;
						adapter = new FallAdapter(actContext,(new ArrayList<FallInfo>()));
						listFalls.setAdapter(adapter);
						setRunning(false);
						CreateSessionDialog.this.dismiss();
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
				}});
		}
	}
}