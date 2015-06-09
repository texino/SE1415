package it.dei.unipd.esp1415.tasks;

import java.io.IOException;

import it.dei.unipd.esp1415.activity.CurrentSessionActivity;
import it.dei.unipd.esp1415.exceptions.LowSpaceException;
import it.dei.unipd.esp1415.exceptions.NoSuchSessionException;
import it.dei.unipd.esp1415.utils.DataArray;
import it.dei.unipd.esp1415.utils.GlobalConstants;
import it.dei.unipd.esp1415.utils.LocalStorage;
import it.dei.unipd.esp1415.utils.PreferenceStorage;
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
	public static final String EXTRA_FALL_ID="fall_Id";	
	public static final String ACTION_GRAPHIC_BROADCAST="GRAPHIC";
	public static final String ACTION_TIME_BROADCAST="TIME";
	public static final String ACTION_FALL_BROADCAST="FALL";

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
		//L'activity chiede di avviare il service
		if(!running)
		{
			//Il service non è attivo
			Log.d(TAG,"STARTED WITH = "+(totalDuration/1000)+" seconds");
			totalDuration=intent.getExtras().getLong(CurrentSessionActivity.DURATION_TAG);
			prevDataTime=System.currentTimeMillis();
			sensorManager=(SensorManager)getSystemService(Context.SENSOR_SERVICE);
			sensor=sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
			sensorManager.registerListener(listener,sensor,1);
		}
		running=true;
		sessionId=intent.getExtras().getString(CurrentSessionActivity.ID_TAG);
		return super.onStartCommand(intent, flags, startId);
	}

	@Override
	public IBinder onBind(Intent intent) {
		//L'activity cerca di legarsi a questo service
		sessionId=intent.getExtras().getString(CurrentSessionActivity.ID_TAG);
		return binder;
	}

	/**
	 * Dice se il service è in corso oppure no
	 * @return true se il service è in corso
	 */
	public boolean isRunning()
	{
		return running;
	}

	@Override
	public void onDestroy()
	{
		Log.d(TAG,"ON DESTROY");
		//Cerco di salvare la durata attuale nel file 
		try {
			LocalStorage.pauseSession(sessionId,(int)totalDuration);
		} catch (NoSuchSessionException e) {
			//se il tempo non è aggiornato semplicemente non abbiamo dei dati aggiornati
			e.printStackTrace();
		} catch (IllegalArgumentException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	/**
	 * Ferma completamente il lavoro del service
	 */
	public void stop()
	{
		Log.d(TAG,"STOPPED:    STORING= "+(totalDuration/1000)+" seconds");
		running=false;
		sensorManager.unregisterListener(listener,sensor);
		try {LocalStorage.stopSession(sessionId,(int)totalDuration);} 
		catch (NoSuchSessionException e) {e.printStackTrace();} catch (IllegalArgumentException e) {
			e.printStackTrace();
		} catch (LowSpaceException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		this.stopSelf();
	}

	/**
	 * Mette in pausa il lavoro del service
	 */
	public void pause()
	{
		Log.d(TAG,"PAUSED:    STORING= "+(totalDuration/1000)+" seconds");
		running=false;
		sensorManager.unregisterListener(listener,sensor);
		try {LocalStorage.pauseSession(sessionId,(int) totalDuration);} 
		catch (NoSuchSessionException e) {
			e.printStackTrace();
		} catch (IllegalArgumentException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		this.stopSelf();
	}

	/**
	 * Guarda la durata attuale della sessione
	 */
	public long getDuration()
	{
		return totalDuration;
	}

	@Override
	public void finalize()
	{
		Log.d(TAG,"FINALIZED");
		try {LocalStorage.pauseSession(sessionId,(int) totalDuration);
		super.finalize();} 
		catch (NoSuchSessionException e) {e.printStackTrace();}
		catch (Throwable e) {e.printStackTrace();}
	}

	private long prevDataTime;
	private float dataThreshold;

	private class ESPEventListener implements SensorEventListener{

		public ESPEventListener()
		{
			//String r=PreferenceStorage.getSimpleData(ESPService.this,PreferenceStorage.ACCEL_RATIO);
			String r="10";
			int rate;
			if(r.equals(""))
			{
				rate=GlobalConstants.MIN_RATIO;
				PreferenceStorage.storeSimpleData(ESPService.this,PreferenceStorage.ACCEL_RATIO,""+rate);
			}
			else
				rate=Integer.parseInt(r);
			data=new DataArray(rate);
			dataThreshold=(1000f/rate);
			prevDataTime=System.currentTimeMillis();
		}

		@Override
		public void onSensorChanged(SensorEvent event)
		{
			//tempo in cui si stanno prendendo i dati
			long aTime=System.currentTimeMillis();

			//tempo passato dall'ultimo dato elaborato
			long deltaTime=aTime-prevDataTime;
			if(deltaTime>=dataThreshold)
			{
				//prev data deve rappresentare un "multiplo" di dataThreshold
				prevDataTime=System.currentTimeMillis();
				//tempo passato dall'inizio della sessione (in millisecondi)
				totalDuration+=deltaTime;
				float x=event.values[0],y=event.values[1],z=event.values[2];
				data.add(x,y,z);
				sendBroadcastMessage(totalDuration,x,y,z);

				(new ElaborateTask(ESPService.this,data,sessionId)).execute(null,null,null);
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