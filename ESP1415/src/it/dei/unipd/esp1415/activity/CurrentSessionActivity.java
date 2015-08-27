package it.dei.unipd.esp1415.activity;

import java.io.IOException;
import java.util.ArrayList;

import com.example.esp1415.R;

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
import it.dei.unipd.esp1415.views.PlayAnimatedButton;
import it.dei.unipd.esp1415.views.PlayButtonDrawable;
import it.dei.unipd.esp1415.views.StopAnimatedButton;
import it.dei.unipd.esp1415.views.StopButtonDrawable;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnDismissListener;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.graphics.drawable.StateListDrawable;
import android.os.Bundle;
import android.os.IBinder;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

public class CurrentSessionActivity extends Activity{
	//TAGS
	public final static String TAG="ACTIVITY",ID_TAG="ID",EMPTY_TAG="EMPTY";
	public final static String DURATION_TAG="DURATION";
	//Views
	private GraphicView graphic;
	private StopAnimatedButton btnStop;
	private PlayAnimatedButton btnStart;
	private TextView textDuration,textName,textDate;
	private Context actContext;
	private ListView listFalls;
	//Variables
	private String sessionId,sessionDialogName;
	private boolean running,empty;
	private FallAdapter adapter;
	private long duration;
	private ESPService service;
	private CreateSessionDialog createDialog;
	private StateListDrawable stopStates;
	private AlertDialog alertDialog;

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
			running=service.isRunning();
			if(running)//la sessione è in corso
				startClicked();//faccio come se avessi premuto start
		}
		@Override
		public void onServiceDisconnected(ComponentName arg0) {//il service è scollegato
		}};

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
			//manager.unregisterReceiver(fallReceiver);
		}

		/**
		 * Imposta lo stato corrente di esecuzione
		 * cambiando il layout di conseguenza
		 * @param run Lo stato della sessione (true se in esecuzione)
		 */
		private void setRunning(boolean run)
		{
			running=run;
		}

		/**Chiamato quando viene premuto il tasto Play*/
		private void startClicked()
		{
			setRunning(true);
			btnStart.toggle(false);
			Intent serviceIntent = new Intent(actContext,ESPService.class);
			serviceIntent.putExtra(ID_TAG,sessionId);
			serviceIntent.putExtra(DURATION_TAG,duration);
			//"Avviamo" il service passandogli durata e id sessione
			startService(serviceIntent);
			//ascolto i cambiamenti
			registerReceivers();
		}

		/**Chiamato quando viene premuto il tasto Pause*/
		private void pauseClicked()
		{
			setRunning(false);
			btnStart.toggle(true);
			//metto in pausa il service
			service.pause();
			unregisterReceivers();
		}

		/**Chiamato quando viene premuto il tasto Stop*/
		private void stopClicked()
		{
			btnStop.toggle(true);
			AlertDialog.Builder builder = new AlertDialog.Builder(this);
			LayoutInflater inflater = (LayoutInflater)getSystemService(Context.LAYOUT_INFLATER_SERVICE);
			ViewGroup vg = (ViewGroup)inflater.inflate(R.layout.dialog_stop_session_layout,null);
			builder.setView(vg);
			((Button)vg.findViewById(R.id.button_ok)).setOnClickListener(new View.OnClickListener() {
				@Override
				public void onClick(View v) {
					pauseClicked();
					//fermo il service
					service.stop();
					Intent i=new Intent(CurrentSessionActivity.this,SessionDataActivity.class);
					i.putExtra(SessionDataActivity.ID_TAG,sessionId);
					startActivity(i);
					CurrentSessionActivity.this.finish();
					alertDialog.dismiss();
				}});
			((Button)vg.findViewById(R.id.button_ko)).setOnClickListener(new View.OnClickListener() {
				@Override
				public void onClick(View v) {
					alertDialog.dismiss();
				}});
			alertDialog=builder.create();
			alertDialog.setCancelable(true);
			alertDialog.setOnDismissListener(new OnDismissListener(){
				@Override
				public void onDismiss(DialogInterface dialog) {
					btnStop.toggle(false);}});
			alertDialog.show();
		}

		//INTENT METHODS

		/**Interpreta l'intent per l'aggiornamento della lista di cadute*/
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

		/**Interpreta l'intent per l'aggiornamento del grafico*/
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
			instance.putBoolean("empty",empty);
			if(empty)
				instance.putString("dialogName",createDialog.getSessionName());
			else
			{
				instance.putString("sessionId",sessionId);
				instance.putBoolean("running",running);
				instance.putLong("duration",duration);			
				instance.putString("sessionName",textName.getText().toString());
				instance.putString("sessionDate",textDate.getText().toString());
				if(alertDialog==null)
					instance.putBoolean("dialogPresence",false);
				else
					instance.putBoolean("dialogPresence",alertDialog.isShowing());
				graphic.saveStatusOnBundle(instance);
			}
			super.onSaveInstanceState(instance);
		}

		@Override
		public void onRestoreInstanceState(Bundle state) {
			super.onRestoreInstanceState(state);
			empty=state.getBoolean("empty");
			if(empty)//è una sessione ancora da creare
				sessionDialogName=state.getString("dialogName");
			else
			{   //abbiamo dati da recuperare
				sessionId=state.getString("sessionId");
				running=state.getBoolean("running");	
				duration=state.getLong("duration");	
				graphic.restoreStatusFromBundle(state);
				textName.setText(state.getString("sessionName"));
				textDate.setText(state.getString("sessionDate"));
				textDuration.setText(Utils.convertDuration((int)duration));
				if(state.getBoolean("dialogPresence"))
				{
					if(alertDialog==null)
						stopClicked();
					else
						alertDialog.show();
				}
			}
		}

		//ACTIVITY LIFE
		@Override
		protected void onCreate(Bundle savedInstanceState) {
			super.onCreate(savedInstanceState);
			Log.d(TAG,"ON CREATE");
			actContext=this;
			setLayout();//Imposta layout ed eventi
			if(savedInstanceState!=null)//l'activity viene ripristinata
				onRestoreInstanceState(savedInstanceState);
			else
			{
				//L'activity è creata da zero
				Bundle extras=getIntent().getExtras();
				empty=extras.getBoolean(EMPTY_TAG);//sessione nuova o già esistente
				if(extras.containsKey(ID_TAG))
					sessionId=extras.getString(ID_TAG);//id della sessione
				sessionDialogName="";
			}
			if(empty)
			{
				//Vogliamo creare una nuova sessione e non ne abbiamo il nome
				createDialog=new CreateSessionDialog(this,sessionDialogName);
				createDialog.show();
				return;
			}

			//C'è una sessione per questa activity
			SessionData session=null;
			boolean ok=false;
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
			if(!ok)
			{
				this.finish();
				return;
			}
			duration=session.getDuration();
			textDuration.setText(Utils.convertDuration((int)duration));
			textName.setText(session.getName());
			textDate.setText(session.getDate());

			ArrayList<FallInfo> falls=session.getFalls();
			ArrayList<FallInfo> orderedFalls=new ArrayList<FallInfo>();
			if(falls!=null)
			{
				int s=falls.size();
				for(int i=s-1;i>=0;i--)
					orderedFalls.add(falls.get(i));
			}
			//popoliamo la lista
			adapter = new FallAdapter(this,orderedFalls,sessionId);
			listFalls.setAdapter(adapter);
		}

		public void onPause()
		{
			Log.d(TAG,"ON PAUSE");
			if(empty)//non dobbiamo fare niente
			{
				super.onPause();
				return;
			}
			//ci sleghiamo dal service
			this.unbindService(connection);
			//cerchiamo di aggiornare l'ultima durata conosciuta
			try {LocalStorage.pauseSession(sessionId,(int)duration);} 
			catch (NoSuchSessionException e) {
				e.printStackTrace();
			} catch (IllegalArgumentException e) {
				e.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
			}
			//ci sleghiamo dai receivers
			unregisterReceivers();
			super.onPause();
		}

		public void onStop()
		{
			Log.d(TAG,"ON STOP");
			super.onStop();
		}

		public void onDestroy()
		{
			Log.d(TAG,"ON DESTROY");//può avvenire molto dopo l'on pause
			//cerchiamo di salvare l'ultima durata nota se c'è un riferimento al service
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

		public void onRestart()
		{
			super.onRestart();
			Log.d(TAG,"ON RESTART");
		}

		public void onStart()
		{
			super.onStart();
			Log.d(TAG,"ON START");
		}

		public void onResume()
		{
			super.onResume();
			Log.d(TAG,"ON RESUME");
			//i dati sono popolati, dobbiamo solo vedere come deve comportarsi l'activity
			if(empty)//non ho altro da fare
				return;
			//sono legato ad una sessione
			bindToService();
		}

		private void setLayout()
		{
			stopStates=new StateListDrawable();
			stopStates.addState(new int[]{},new StopButtonDrawable(actContext));

			setContentView(R.layout.activity_current_session_layout);

			btnStart=(PlayAnimatedButton)findViewById(R.id.button_start);
			//btnStart.setImageDrawable(playStates);
			btnStart.setImageDrawable(new PlayButtonDrawable(actContext));

			btnStop=(StopAnimatedButton)findViewById(R.id.button_stop);
			//btnStop.setImageDrawable(stopStates);
			btnStop.setImageDrawable(new StopButtonDrawable(actContext));

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

		private void bindToService()
		{
			Intent serviceIntent = new Intent(actContext,ESPService.class);
			serviceIntent.putExtra(ID_TAG,sessionId);
			serviceIntent.putExtra(DURATION_TAG,duration);
			//mi lego al possibile service
			bindService(serviceIntent,connection,Context.BIND_AUTO_CREATE);
		}

		public class CreateSessionDialog extends Dialog
		{
			private Context actContext;
			private EditText edit;

			protected CreateSessionDialog(Context context,String name) {
				super(context);
				actContext=context;
				this.requestWindowFeature(Window.FEATURE_NO_TITLE);
				setContentView(R.layout.dialog_new_session_layout);
				Button ok=(Button)findViewById(R.id.button_ok);
				edit=(EditText)findViewById(R.id.edit_name);
				edit.setText(name);
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
							adapter = new FallAdapter(actContext,(new ArrayList<FallInfo>()),sessionId);
							listFalls.setAdapter(adapter);
							setRunning(false);
							bindToService();
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

			public String getSessionName()
			{
				return edit.getText().toString();
			}
		}
}