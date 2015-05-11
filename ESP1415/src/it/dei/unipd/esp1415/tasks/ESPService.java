package it.dei.unipd.esp1415.tasks;

import it.dei.unipd.esp1415.activity.CurrentSessionActivity;
import it.dei.unipd.esp1415.utils.DataArray;
import it.dei.unipd.esp1415.utils.LocalStorage;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Binder;
import android.os.IBinder;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;

public class ESPService extends Service{

	//Tags
	private final static String TAG="SERVICE";
	public static final String EXTRA_TIME="extra_time";
	public static final String EXTRA_X="extra_x";
	public static final String EXTRA_Y="extra_y";
	public static final String EXTRA_Z="extra_z";	
	public static final String ACTION_GRAPHIC_BROADCAST=ESPService.class.getName()+"LocationBroadcast";
	public static final String ACTION_TIME_BROADCAST=ESPService.class.getName()+"LocationBroadcast";
	public static final String ACTION_FALL_BROADCAST=ESPService.class.getName()+"LocationBroadcast";

	//Data
	private DataArray data;
	private String sessionId;
	//Time
	private long totalDuration=0;
	//Service data
	private IBinder binder=new ESPBinder();
	private boolean running=false;
	//Sensor data
	private SensorManager sensorManager;
	private Sensor sensor;
	private ESPEventListener listener=new ESPEventListener();
	//Classes
	public class ESPBinder extends Binder{
		public ESPService getService(){
			// Return this instance of LocalService so clients can call public methods
			return ESPService.this;}}
	
	@Override
	public int onStartCommand(Intent intent,int flags,int startId)
	{
		sessionId=intent.getExtras().getString(CurrentSessionActivity.ID_TAG);
		totalDuration=Long.parseLong(intent.getExtras().getString(CurrentSessionActivity.DURATION_TAG));
		sensorManager=(SensorManager)getSystemService(Context.SENSOR_SERVICE);
		sensor=sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
		play();
		return super.onStartCommand(intent, flags, startId);
	}
	
	@Override
 	public IBinder onBind(Intent intent) {
		//l' activity di una sessione si sta collegando a questo service
		
		//prendiamo l'id della sessione
		sessionId=intent.getExtras().getString(CurrentSessionActivity.ID_TAG);
		Log.d(TAG,"Running: "+running);
		return binder;
	}

	/**
	 * Dice se il service sta lavorando o no
	 * @return true se il service sta lavorando <br>
	 * false se il service è in pausa
	 */
	public boolean isRunning()
	{
		return running;
	}

	/**
	 * Avvia il lavoro del service (non effettua azioni se il service è già attivo)
	 */
	public void play()
	{
		running=true;
		//ascoltiamo il sensore
		sensorManager.registerListener(listener,sensor,1);
		Log.d(TAG,"Running: "+running);
	}

	/**
	 * Mette in pausa il lavoro del service (non effettua azioni se il service è già in pausa)
	 */
	public void pause()
	{
		running=false;
		prevDataTime=System.currentTimeMillis();
		//smettiamo di ascoltare il sensore
		sensorManager.unregisterListener(listener,sensor);
		this.stopSelf();
		Log.d(TAG,"Running: "+running);
	}
	
	/**
	 * Ferma completamente il lavoro del service
	 */
	public void stop()
	{
		pause();
	}
	
	/**
	 * Guarda la durata attuale della sessione
	 */
	public long getDuration()
	{
		return totalDuration;
	}
	





	private long prevDataTime,dataTime;
	
	private class ESPEventListener implements SensorEventListener{

		public ESPEventListener()
		{
			int rate=10;
			data=new DataArray(rate);	
			dataTime=(1000/rate);
			prevDataTime=System.currentTimeMillis();
		}

		@Override
		public void onSensorChanged(SensorEvent event)
		{
			if(running){//considera i dati solo se il service è attivo
				//tempo in cui si stanno prendendo i dati
				long aTime=System.currentTimeMillis();
				
				//tempo passato dall'ultimo dato elaborato
				int deltaTime=(int) (aTime-prevDataTime);
				if(deltaTime>=dataTime)
				{
					prevDataTime=aTime-(deltaTime-dataTime);
					
					//tempo passato dall'inizio della sessione (in millisecondi)
					totalDuration+=dataTime;
					
					float x=event.values[0],y=event.values[1],z=event.values[2];
					data.add(x,y,z);
					sendBroadcastMessage(totalDuration,x,y,z);
					(new ElaborateTask(data,sessionId)).execute(null,null,null);
				}
			}
		}

		private void sendBroadcastMessage(long time,float x,float y,float z) {
			Intent intent = new Intent(ACTION_GRAPHIC_BROADCAST);
			intent.putExtra(EXTRA_TIME, time);
			intent.putExtra(EXTRA_X, x);
			intent.putExtra(EXTRA_Y, y);
			intent.putExtra(EXTRA_Z, z);
			LocalBroadcastManager.getInstance(ESPService.this).sendBroadcast(intent);
		}

		@Override
		public void onAccuracyChanged(Sensor sensor, int accuracy) {}
	}
}