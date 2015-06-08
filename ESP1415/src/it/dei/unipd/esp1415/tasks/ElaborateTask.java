package it.dei.unipd.esp1415.tasks;

import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

import it.dei.unipd.esp1415.exceptions.IllegalDateFormatException;
import it.dei.unipd.esp1415.exceptions.IllegalIdException;
import it.dei.unipd.esp1415.exceptions.IllegalNumberException;
import it.dei.unipd.esp1415.exceptions.LowSpaceException;
import it.dei.unipd.esp1415.exceptions.NoSuchSessionException;
import it.dei.unipd.esp1415.objects.AccelPoint;
import it.dei.unipd.esp1415.objects.FallData;
import it.dei.unipd.esp1415.utils.DataArray;
import it.dei.unipd.esp1415.utils.LocalStorage;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;

public class ElaborateTask extends AsyncTask<Void,Void,Void>{

	private static final String TAG = "ELABORATE TASK";
	float[] dX,dY,dZ;
	int index;
	String sessionId;
	Context context;
	public ElaborateTask (float[] dX,float[] dY,float[] dZ)
	{
		this.dX=dX;
		this.dY=dY;
		this.dZ=dZ;
	}

	public ElaborateTask (Context context,DataArray data,String sessionId)
	{
		this.dX=data.getXData();
		this.dY=data.getYData();
		this.dZ=data.getYData();
		this.index=data.getIndex();
		this.sessionId=sessionId;
		this.context=context;
	}

	@Override
	protected Void doInBackground(Void... params) {
		int prevIndex=index-1;
		int middleIndex=index+dZ.length/2;
		if(middleIndex>=dZ.length)
			middleIndex=middleIndex-dZ.length;
		if(index==0)
			prevIndex=dZ.length-1;
		if((dZ[middleIndex]-dZ[index]>10)&&(dZ[middleIndex]-dZ[prevIndex]>10))
		{
			DateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy-HH:mm");
			String date=dateFormat.format(new Date());
			AccelPoint[] points=new AccelPoint[dX.length];
			int px=0;
			for(int i=index;i<dX.length;i++)
			{
				points[px]=new AccelPoint(dX[i],dY[i],dZ[i]);
				px++;}
			for(int i=0;i<index;i++)
			{
				points[px]=new AccelPoint(dX[i],dY[i],dZ[i]);
				px++;}
			FallData data;
			try {
				data = new FallData(""+System.currentTimeMillis(),date,true,200,200,points);
				LocalStorage.storeFallData(sessionId,data);
				sendBroadcastMessage(data.getId());
			} catch (IllegalArgumentException e) {
				e.printStackTrace();
			} catch (IllegalDateFormatException e) {
				e.printStackTrace();
			} catch (IllegalNumberException e) {
				e.printStackTrace();
			} catch (IllegalIdException e) {
				e.printStackTrace();
			} catch (NoSuchSessionException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (LowSpaceException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			Log.d("FALL EVENT","FALLEN");
		}
		return null;
	}
	
	private void sendBroadcastMessage(String date) {
		Log.d(TAG,"FALL DATE : "+date);
		Intent intent = new Intent(ESPService.ACTION_FALL_BROADCAST);
		intent.putExtra(ESPService.EXTRA_FALL_DATE,date);
		LocalBroadcastManager.getInstance(context).sendBroadcast(intent);
	}
}