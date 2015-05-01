package it.dei.unipd.esp1415;

import com.example.esp1415.R;

import android.support.v7.app.ActionBarActivity;
import android.app.Service;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;

//CREATE->START->RESUME      PAUSE->STOP
public class MainActivity extends ActionBarActivity  implements SensorEventListener{

	SensorManager sensorManager;
	ServiceM service;
	Sensor sensor;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		Log.d("ACTIVITY","CREATE");
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		sensorManager = (SensorManager)getSystemService(Context.SENSOR_SERVICE);
		sensor = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
		
		if(savedInstanceState==null)
		{
			Intent intent=new Intent(this,ServiceM.class);
			ComponentName name=startService(intent);
			bindService(intent, null, 0);
			sensorManager.registerListener(this,sensor,1);
			System.out.println(name.toString());
		}
		else
		{
			Intent intent=new Intent(this,ServiceM.class);
			ComponentName name=startService(intent);
			sensorManager.registerListener(this,sensor,1);
			System.out.println(name.toString());
		}

	}

	@Override
	public void onPause()
	{
		Log.d("ACTIVITY","PAUSE");
		super.onPause();
	}

	@Override
	public void onStart()
	{
		Log.d("ACTIVITY","START");
		super.onStart();
	}

	@Override
	public void onResume()
	{
		Log.d("ACTIVITY","RESUME");
		super.onResume();
	}

	@Override
	public void onStop()
	{
		Log.d("ACTIVITY","STOP");
		super.onStop();
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// Handle action bar item clicks here. The action bar will
		// automatically handle clicks on the Home/Up button, so long
		// as you specify a parent activity in AndroidManifest.xml.
		int id = item.getItemId();
		if (id == R.id.action_settings) {
			return true;
		}
		return super.onOptionsItemSelected(item);
	}


	@Override
	public void onSensorChanged(SensorEvent event) {
		// TODO Auto-generated method stub
		//System.out.println("newData");
	}


	@Override
	public void onAccuracyChanged(Sensor sensor, int accuracy) {
		// TODO Auto-generated method stub

	}
}
