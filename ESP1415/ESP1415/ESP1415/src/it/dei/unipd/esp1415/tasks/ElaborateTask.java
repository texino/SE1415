package it.dei.unipd.esp1415.tasks;

import java.io.IOException;

import it.dei.unipd.esp1415.exceptions.IllegalDateFormatException;
import it.dei.unipd.esp1415.exceptions.IllegalIdException;
import it.dei.unipd.esp1415.exceptions.IllegalNumberException;
import it.dei.unipd.esp1415.exceptions.LowSpaceException;
import it.dei.unipd.esp1415.exceptions.NoSuchSessionException;
import it.dei.unipd.esp1415.objects.FallData;
import it.dei.unipd.esp1415.utils.DataArray;
import it.dei.unipd.esp1415.utils.LocalStorage;
import it.dei.unipd.esp1415.utils.Utils;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;

public class ElaborateTask extends AsyncTask<Void,Void,Void>{

	private static final String TAG = "ELABORATE TASK";
	private float[] dX,dY,dZ;
	private int index;
	private double actualLat,actualLong;
	private String sessionId;
	private Context context;

	public ElaborateTask (Context context,DataArray data,String sessionId,double latitude,double longitude)
	{
		this.dX=data.getXData();
		this.dY=data.getYData();
		this.dZ=data.getZData();
		this.index=data.getIndex();
		this.sessionId=sessionId;
		this.context=context;
		this.actualLat=latitude;
		this.actualLong=longitude;
	}

	@Override
	protected Void doInBackground(Void... params) {
		int tI,maxZi=0,minZi=0,rI=0;
		float minZ=dZ[index],maxZ=dZ[index];
		for(tI=index;tI<dZ.length;tI++)
		{
			if(dZ[tI]>maxZ)
			{
				maxZ=dZ[tI];
				maxZi=rI;
			}
			else if(dZ[tI]<minZ)
			{
				minZ=dZ[tI];
				minZi=rI;
			}
			rI++;
		}
		for(tI=0;tI<index;tI++)
		{
			if(dZ[tI]>maxZ)
			{
				maxZ=dZ[tI];
				maxZi=rI;
			}
			else if(dZ[tI]<minZ)
			{
				minZ=dZ[tI];
				minZi=rI;
			}
			rI++;
		}
		if(maxZi<=minZi) //ci assicuriamo che il valore più alto sia DOPO quello più basso
			return null;
		if(((maxZ-minZ)>16)&&((ESPService)context).elaborateFall())
		{
			Log.d(TAG,"FALLEN");
			String date=Utils.getDateHour();
			DataArray datas=new DataArray(dZ.length);
			for(int i=index;i<dX.length;i++)
				datas.add(dX[i],dY[i],dZ[i]);
			for(int i=0;i<index;i++)
				datas.add(dX[i],dY[i],dZ[i]);
			//TODO get GPS
			//TODO make HTTP
			String todo;
			boolean notified=true;
			FallData data;
			try {
				data = new FallData(""+System.currentTimeMillis(),date,notified,"",actualLong,actualLat,datas);
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