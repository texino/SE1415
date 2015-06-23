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
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Binder;
import android.os.Bundle;
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
	private int rate;
	//Time
	private long totalDuration=0;
	//Service data
	private IBinder binder=new ESPBinder();
	private boolean running=false,startedElaborate=false;
	private double actualLat,actualLong;
	//Sensor data
	private SensorManager sensorManager;
	private LocationManager locationManager;
	private Sensor sensor;
	private ESPEventListener listener;
	private final LocationListener locationListener = new LocationListener() {
		@Override
		public void onLocationChanged(final Location location) {
			//your code here
			actualLat=location.getLatitude();
			actualLong=location.getLongitude();
		}
		@Override
		public void onStatusChanged(String provider, int status, Bundle extras) {}
		@Override
		public void onProviderEnabled(String provider) {}
		@Override
		public void onProviderDisabled(String provider) {}
	};
	//Classes
	public class ESPBinder extends Binder{
		/**Restituisce il riferimento al service associato*/
		public ESPService getService(){
			return ESPService.this;}}

	@Override
	public int onStartCommand(Intent intent,int flags,int startId)
	{
		sessionId=intent.getExtras().getString(CurrentSessionActivity.ID_TAG);
		if(running)//è già in corso
			return super.onStartCommand(intent, flags, startId);
		//è da avviare
		Log.d(TAG,"STARTED WITH = "+(totalDuration/1000)+" seconds");
		String r=PreferenceStorage.getSimpleData(ESPService.this.getBaseContext(),PreferenceStorage.ACCEL_RATIO);
		if(r.equals(""))
		{
			rate=GlobalConstants.MIN_RATIO;
			PreferenceStorage.storeSimpleData(ESPService.this,PreferenceStorage.ACCEL_RATIO,""+rate);
		}
		else
			rate=Integer.parseInt(r);
		listener=new ESPEventListener();
		//L'activity chiede di avviare il service
		totalDuration=intent.getExtras().getLong(CurrentSessionActivity.DURATION_TAG);
		prevDataTime=System.currentTimeMillis();
		sensorManager=(SensorManager)getSystemService(Context.SENSOR_SERVICE);
		locationManager=(LocationManager)getSystemService(Context.LOCATION_SERVICE);
		locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER,1000,
				1,locationListener);
		//actualLat=locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER).getLatitude();
		//actualLong=locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER).getLongitude();
		sensor=sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
		sensorManager.registerListener(listener,sensor,1);
		running=true;
		return super.onStartCommand(intent, flags, startId);
	}

	@Override
	public IBinder onBind(Intent intent) {
		//L'activity cerca di legarsi a questo service
		sessionId=intent.getExtras().getString(CurrentSessionActivity.ID_TAG);
		if(!running)
			totalDuration=intent.getExtras().getLong(CurrentSessionActivity.DURATION_TAG);
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
		if(sensorManager!=null)
		{
			sensorManager.unregisterListener(listener,sensor);
			locationManager.removeUpdates(locationListener);
		}
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
		if(sensorManager!=null)
		{
			sensorManager.unregisterListener(listener,sensor);
			locationManager.removeUpdates(locationListener);
		}
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
				System.out.println("ESP SERVICE DATA: "+data.getIndex());
				if(data.getIndex()==0)
					startedElaborate=true;
				if(startedElaborate)
					(new ElaborateTask(ESPService.this,data,sessionId,actualLat,actualLong)).execute(null,null,null);
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