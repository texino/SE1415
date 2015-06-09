package it.dei.unipd.esp1415.activity;

import java.io.IOException;
import java.util.ArrayList;

import com.example.esp1415.R;

import it.dei.unipd.esp1415.exceptions.LowSpaceException;
import it.dei.unipd.esp1415.exceptions.NoSuchSessionException;
import it.dei.unipd.esp1415.objects.FallInfo;
import it.dei.unipd.esp1415.objects.SessionData;
import it.dei.unipd.esp1415.objects.SessionInfo;
import it.dei.unipd.esp1415.tasks.ESPService;
import it.dei.unipd.esp1415.tasks.ESPService.ESPBinder;
import it.dei.unipd.esp1415.utils.LocalStorage;
import it.dei.unipd.esp1415.views.GraphicView;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
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
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
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
			setTimeText(service.getDuration());
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
			showDialog();
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
			setTimeText(duration);
			textName.setText(session.getName());
			textDate.setText(session.getDate());
			String todo;
			//TODO popolare la lista
			ArrayList<FallInfo> falls=session.getFalls();
			ArrayList<String> strings=new ArrayList<String>();
			int s=falls.size();
			for(int i=s-1;i>=0;i--)
				strings.add(falls.get(i).getId());
			adapter = new ArrayAdapter<String>(this,android.R.layout.simple_list_item_1, android.R.id.text1,strings);
			listFalls.setAdapter(adapter);
			listFalls.setOnItemClickListener(new OnItemClickListener(){
				@Override
				public void onItemClick(AdapterView<?> parent, View view,
						int position, long id) {
					Intent i=new Intent(actContext,FallDataActivity.class);
					i.putExtra(FallDataActivity.SESSION_ID_TAG,sessionId);
					i.putExtra(FallDataActivity.FALL_ID_TAG,adapter.getItem(position));
					startActivity(i);
				}});
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
		setContentView(R.layout.current_session_activity_layout);
		btnStart=(ImageButton)findViewById(R.id.button_start);
		btnStop=(ImageButton)findViewById(R.id.button_stop);
		textHours=(TextView)findViewById(R.id.text_hours);
		textMinutes=(TextView)findViewById(R.id.text_minutes);
		textSeconds=(TextView)findViewById(R.id.text_seconds);
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
		//fermo il service
		service.stop();
		//mi slego dal service
		unbindService(connection);
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
						SessionInfo s = LocalStorage.createNewSession(name);
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

	private void storeStatusPreference(boolean status)
	{
		Log.d(TAG,"Store Service Preference Status : "+status);
		Editor e=this.getPreferences(MODE_PRIVATE).edit();
		e.putBoolean(SERVICE_STATUS_TAG,status);
		e.commit();
	}

	private boolean getServiceSavedStatus()
	{
		boolean b=this.getPreferences(MODE_PRIVATE).getBoolean(SERVICE_STATUS_TAG, false);
		Log.d(TAG,"Get Service Preference Status : "+b);
		return b;
	}

	//INTENT METHODS

	private void decodeFallIntent(Intent intent)
	{
		String date=intent.getStringExtra(ESPService.EXTRA_FALL_DATE);
		adapter.insert(date,0);
	}

	private void decodeGraphicIntent(Intent intent)
	{
		//Log.d(TAG,"GRAPHIC INTENT "+System.currentTimeMillis());
		//aggiorna UI con i dati dell'intent
		float x=intent.getFloatExtra(ESPService.EXTRA_X, 0);
		float y=intent.getFloatExtra(ESPService.EXTRA_Y, 0);
		float z=intent.getFloatExtra(ESPService.EXTRA_Z, 0);
		graphic.add(x, y, z);
		duration = intent.getLongExtra(ESPService.EXTRA_TIME, 0);
		setTimeText(duration);
	}

	//ACTIVITY METHODS

	@Override
	public void onSaveInstanceState(Bundle instance)
	{
		Log.d(TAG,"SAVING");
		instance.putString("sec",textSeconds.getText().toString());
		instance.putString("min",textMinutes.getText().toString());
		instance.putString("hour",textHours.getText().toString());
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
		textSeconds.setText(state.getString("sec"));
		textMinutes.setText(state.getString("min"));
		textHours.setText(state.getString("hour"));
		graphic.restoreStatusFromBundle(state);
		sessionId=state.getString("sessionId");
		duration=state.getLong("duration");
		empty=state.getBoolean("empty");
		running=state.getBoolean("running");
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


	public class RenameDialog extends Dialog
	{
		private Context actContext;
		private String id;
		private EditText edit;

		protected RenameDialog(Context context,String sessionId) {
			super(context);
			actContext=context;
			id=sessionId;
			setTitle(R.string.dialog_name_title);
			setContentView(R.layout.session_name_dialog_layout);
			Button ok=(Button)findViewById(R.id.button_ok);
			edit=(EditText)findViewById(R.id.edit_name);
			ok.setOnClickListener(new View.OnClickListener() {
				@Override
				public void onClick(View v) {
					String name=edit.getText().toString();
					if(name.length()<1)//non è stato inserito un nome
						Toast.makeText(actContext, R.string.text_insert_name,Toast.LENGTH_SHORT).show();
					else
					{
						//creiamo la sessione
						try {
							//impostiamo i dati
							LocalStorage.renameSession(id,name);
						} catch (IllegalArgumentException e) {
							Toast.makeText(actContext,R.string.error_arguments,Toast.LENGTH_SHORT).show();
							e.printStackTrace();
						} catch (IOException e) {
							Toast.makeText(actContext,R.string.error_file_writing,Toast.LENGTH_SHORT).show();
							e.printStackTrace();
						} catch (NoSuchSessionException e) {
							Toast.makeText(actContext,R.string.error_inexistent_session,Toast.LENGTH_SHORT).show();
							e.printStackTrace();
						}
					}}});
			this.dismiss();
		}
	}
}