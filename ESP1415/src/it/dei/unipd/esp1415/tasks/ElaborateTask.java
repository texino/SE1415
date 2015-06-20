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
		if(index==0)
			prevIndex=dZ.length-1;
		int middleIndex=index+dZ.length/2;
		if(middleIndex>=dZ.length)
			middleIndex=middleIndex-dZ.length;
		//if((dZ[middleIndex]-dZ[index]>10)&&(dZ[middleIndex]-dZ[prevIndex]>10))
		if((dY[index]-dY[prevIndex])>5)
		{
			DateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy-HH:mm");
			String date=dateFormat.format(new Date());
			DataArray datas=new DataArray(dX.length);
			for(int i=index;i<dX.length;i++)
				datas.add(dX[i],dY[i],dZ[i]);
			for(int i=0;i<index;i++)
				datas.add(dX[i],dY[i],dZ[i]);
			FallData data;
			try {
				data = new FallData(""+System.currentTimeMillis(),date,true,"",200,200,datas);
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
				e.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
			} catch (LowSpaceException e) {
				e.printStackTrace();
			}
			Log.d("FALL EVENT","FALLEN");
		}
		return null;
	}

	private void sendBroadcastMessage(String id) {
		Log.d(TAG,"FALL ID : "+id);
		Intent intent = new Intent(ESPService.ACTION_FALL_BROADCAST);
		intent.putExtra(ESPService.EXTRA_FALL_ID,id);
		LocalBroadcastManager.getInstance(context).sendBroadcast(intent);
	}
}