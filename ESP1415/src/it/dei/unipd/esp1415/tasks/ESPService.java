package it.dei.unipd.esp1415.tasks;

import java.io.IOException;
import java.util.List;

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
import android.location.GpsStatus.Listener;
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
	public static final String EXTRA_TIME="extra_time";
	public static final String EXTRA_X="extra_x";
	public static final String EXTRA_Y="extra_y";
	public static final String EXTRA_Z="extra_z";	
	public static final String EXTRA_FALL_ID="fall_Id";	
	public static final String ACTION_GRAPHIC_BROADCAST="GRAPHIC";
	public static final String ACTION_TIME_BROADCAST="TIME";
	public static final String ACTION_FALL_BROADCAST="FALL";
	private final static String TAG="SERVICE";

	//Data
	private DataArray data;
	private String sessionId;
	//Time
	private long totalDuration=0;
	//Service data
	private IBinder binder=new ESPBinder();
	private boolean running=false;
	private double actualLat,actualLong;
	//Sensor data
	private SensorManager sensorManager;
	private LocationManager locationManager;
	private Sensor sensor;
	private ESPEventListener listener;

	private final Listener gpsListener=new Listener(){
		@Override
		public void onGpsStatusChanged(int event) {

		}};
		private final LocationListener locationListener = new LocationListener() {
			@Override
			public void onLocationChanged(final Location location) {
				actualLat=location.getLatitude();
				actualLong=location.getLongitude();}
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
			if(intent.getExtras().containsKey(CurrentSessionActivity.ID_TAG))
				sessionId=intent.getExtras().getString(CurrentSessionActivity.ID_TAG);
			if(sessionId==null)
				running=false;
			if(!running)
				start(intent.getExtras().getLong(CurrentSessionActivity.DURATION_TAG));//lo avviamo
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

		/**
		 * Comincia la sessione da una certa durata di tempo trascorsa
		 * @param duration La durata in millisecondi trascorsa finora
		 */
		private void start(long duration)
		{
			Log.d(TAG,"STARTED WITH = "+(duration/1000)+" seconds");
			running=true;
			totalDuration=duration;
			prevDataTime=System.currentTimeMillis();

			//consideriamo la frequenza a cui dobbiamo raccogliere i dati
			String r=PreferenceStorage.getSimpleData(ESPService.this.getBaseContext(),PreferenceStorage.ACCEL_RATIO);
			int rate=GlobalConstants.MIN_RATIO;
			if(r.equals(""))//non c'è una frequenza impostata
				PreferenceStorage.storeSimpleData(ESPService.this,PreferenceStorage.ACCEL_RATIO,""+rate);
			else
				rate=Integer.parseInt(r);
			Log.d(TAG,"STARTING LISTENING DATA WITH RATE = "+rate);

			//registriamo i sensori
			//ACCELEROMETRO
			listener=new ESPEventListener(rate);
			sensorManager=(SensorManager)getSystemService(Context.SENSOR_SERVICE);
			sensor=sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
			//GPS
			locationManager=(LocationManager)getSystemService(Context.LOCATION_SERVICE);
			locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER,1000,
					1,locationListener);
			locationManager.addGpsStatusListener(gpsListener);
			Location lastLocation=getLastKnownLocation();
			if(lastLocation!=null)
			{
				actualLat=lastLocation.getLatitude();
				actualLong=lastLocation.getLongitude();
			}
			sensorManager.registerListener(listener,sensor,1);
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
		 * Guarda la durata attuale della sessione
		 */
		public long getDuration()
		{
			return totalDuration;
		}

		@Override
		public void onDestroy()
		{
			Log.d(TAG,"ON DESTROY");
			pause();
			super.onDestroy();
		}

		private Location getLastKnownLocation()
		{
			locationManager = (LocationManager)getApplicationContext().getSystemService(LOCATION_SERVICE);
			List<String> providers = locationManager.getProviders(true);
			Location bestLocation = null;
			for (String provider : providers) {
				Location l = locationManager.getLastKnownLocation(provider);
				if (l == null) {
					continue;
				}
				if (bestLocation == null || l.getAccuracy() < bestLocation.getAccuracy()) {
					// Found best last known location: %s", l);
					bestLocation = l;
				}
			}
			return bestLocation;
		}

		private long prevDataTime;
		private long lastElaborateTime;

		private class ESPEventListener implements SensorEventListener{

			private float dataThreshold;

			/**
			 * Inizializza un listener per dati dell'accellerometro ad un rate specifico
			 * @param rate Il rate a cui vogliamo ascoltare i dati dell'accellerometro (dati/secondo)
			 */
			public ESPEventListener(int rate)
			{
				data=new DataArray(rate);
				dataThreshold=(1000f/rate);
				prevDataTime=System.currentTimeMillis();
				lastElaborateTime=prevDataTime;
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
					if((aTime-lastElaborateTime)>=1000)//impedisce che vengano rilevate più di una caduta al secondo
					{
						//N.B. passiamo una copia dei dati attuali, perchè quelli originali vengono 
						//continuamente cambiati dal sensore e quindi sono inconsistenti per l'elaborazione
						(new ElaborateTask(ESPService.this,data.copy(),sessionId,actualLat,actualLong)).execute(null,null,null);
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

		/**
		 * Notifica una caduta al service se il service può riceverne
		 * @return false se il service non può ricevere cadute
		 */
		public boolean elaborateFall()
		{
			if((System.currentTimeMillis()-lastElaborateTime)<1000)
				return false;
			lastElaborateTime=System.currentTimeMillis();
			return true;
		}
}