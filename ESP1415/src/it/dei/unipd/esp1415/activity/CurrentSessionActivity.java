package it.dei.unipd.esp1415.activity;

import com.example.esp1415.R;

import it.dei.unipd.esp1415.tasks.ESPService;
import it.dei.unipd.esp1415.tasks.ESPService.LocalBinder;
import it.dei.unipd.esp1415.views.GraphicView;
import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.IBinder;
import android.support.v4.content.LocalBroadcastManager;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

public class CurrentSessionActivity extends Activity{

	//Views
	private GraphicView graphic;
	private Button btn_start,btn_stop;
	private TextView hours,minutes,seconds;

	private ESPService service;
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
		//si lega al service
		Intent serviceIntent = new Intent(CurrentSessionActivity.this,ESPService.class);
		bindService(serviceIntent,connection,Context.BIND_AUTO_CREATE);
		//imposta layout ed eventi
		setLayout();
		//inizializza broadcast manager
		LocalBroadcastManager manager=LocalBroadcastManager.getInstance(this);
		manager.registerReceiver(
				new BroadcastReceiver() {
					@Override
					public void onReceive(Context context, Intent intent) {
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
						hours.setText(t);

						if(m<10)
							t="0"+m;
						else
							t=""+m;
						minutes.setText(t);

						if(s<10)
							t="0"+s;
						else
							t=""+s;
						seconds.setText(t);
					}
				}, new IntentFilter(ESPService.ACTION_LOCATION_BROADCAST)
				);
	}

	/**
	 * Imposta il layout per quest'activity e iniziaizza oggetti ed eventi
	 */
	private void setLayout()
	{
		setContentView(R.layout.session_activity_layout);
		btn_start=(Button)findViewById(R.id.button_start);
		btn_stop=(Button)findViewById(R.id.button_stop);
		hours=(TextView)findViewById(R.id.text_hours);
		minutes=(TextView)findViewById(R.id.text_minutes);
		seconds=(TextView)findViewById(R.id.text_seconds);
		graphic=(GraphicView)findViewById(R.id.graphic);
		btn_start.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				boolean run=service.isRunning();
				if(!run)
				{
					//il service è in pausa quindi lo si avvia
					service.play();
				}
				else
				{
					//il service è attivo quindi lo si mette in pausa
					service.pause();
				}
			}
		});
	}

	@Override
	public void onSaveInstanceState(Bundle instance)
	{
		instance.putString("sec",seconds.getText().toString());
		instance.putString("min",minutes.getText().toString());
		instance.putString("hour",hours.getText().toString());
		graphic.saveStatusOnBundle(instance);
		super.onSaveInstanceState(instance);
	}

	@Override
	public void onRestoreInstanceState(Bundle state) {
		super.onRestoreInstanceState(state);
		seconds.setText(state.getString("sec"));
		minutes.setText(state.getString("min"));
		hours.setText(state.getString("hour"));
		graphic.restoreStatusFromBundle(state);
	}
}