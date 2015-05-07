package it.dei.unipd.esp1415.tasks;

import it.dei.unipd.esp1415.activity.CurrentSessionActivity;
import it.dei.unipd.esp1415.utils.DataArray;
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

public class ESPService extends Service{

	//Tags
	public static final String EXTRA_TIME="extra_time";
	public static final String EXTRA_X="extra_x";
	public static final String EXTRA_Y="extra_y";
	public static final String EXTRA_Z="extra_z";	
	public static final String ACTION_LOCATION_BROADCAST=ESPService.class.getName()+"LocationBroadcast";

	//data
	private DataArray data;

	//time 
	private long totalTime,oldTime,dataTime;
	private long startTime,pausedTime;

	//Service data
	private IBinder binder=new LocalBinder();
	private boolean running=false;

	//Sensor data
	private SensorManager sensorManager;
	private Sensor sensor;
	private String sessionId;
	private ESPEventListener listener=new ESPEventListener();

	@Override
	public IBinder onBind(Intent intent) {
		//un activity si sta collegando a questo service
		sensorManager=(SensorManager)getSystemService(Context.SENSOR_SERVICE);
		sensor=sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
		sessionId=intent.getExtras().getString(CurrentSessionActivity.ID_TAG);
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
		startTime=System.currentTimeMillis();
		sensorManager.registerListener(listener,sensor,1);
	}
	
	public void stop()
	{
		running=false;
		pausedTime=totalTime;
		sensorManager.unregisterListener(listener,sensor);
	}

	/**
	 * Mette in pausa il lavoro del service (non effettua azioni se il service è già in pausa)
	 */
	public void pause()
	{
		running=false;
		pausedTime=totalTime;
		sensorManager.unregisterListener(listener,sensor);
	}

	public class LocalBinder extends Binder 
	{
		public ESPService getService() 
		{
			// Return this instance of LocalService so clients can call public methods
			return ESPService.this;
		}
	}

	private class ESPEventListener implements SensorEventListener{

		public ESPEventListener()
		{
			int rate=10;
			data=new DataArray(rate);
			
			dataTime=(1000/rate);
			totalTime=0;
			pausedTime=0;
			oldTime=startTime;
		}

		@Override
		public void onSensorChanged(SensorEvent event)
		{
			if(running)
			{
				//tempo in cui si stanno prendendo i dati
				long aTime=System.currentTimeMillis();
				
				//tempo passato dall'ultimo dato elaborato
				if((aTime-oldTime)>=dataTime)
				{
					oldTime=aTime;
					//tempo passato dall'inizio della sessione (in millisecondi)
					totalTime = pausedTime+(aTime-startTime);
					
					float x=event.values[0],y=event.values[1],z=event.values[2];
					data.add(x,y,z);
					sendBroadcastMessage(totalTime,x,y,z);
					(new ElaborateTask(data,sessionId)).execute(null,null,null);
					//TODO elaborate data
					String a;
				}
			}
		}

		private void sendBroadcastMessage(long time,float x,float y,float z) {
			Intent intent = new Intent(ACTION_LOCATION_BROADCAST);
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