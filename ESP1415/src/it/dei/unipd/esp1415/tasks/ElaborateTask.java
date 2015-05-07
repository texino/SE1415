package it.dei.unipd.esp1415.tasks;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

import it.dei.unipd.esp1415.exceptions.IllegalDateFormatException;
import it.dei.unipd.esp1415.exceptions.IllegalIdException;
import it.dei.unipd.esp1415.exceptions.IllegalNumberException;
import it.dei.unipd.esp1415.objects.AccelPoint;
import it.dei.unipd.esp1415.objects.FallData;
import it.dei.unipd.esp1415.utils.DataArray;
import it.dei.unipd.esp1415.utils.LocalStorage;
import android.os.AsyncTask;
import android.util.Log;

public class ElaborateTask extends AsyncTask<Void,Void,Void>{

	float[] dX,dY,dZ;
	int index;
	String sessionId;
	public ElaborateTask (float[] dX,float[] dY,float[] dZ)
	{
		this.dX=dX;
		this.dY=dY;
		this.dZ=dZ;
	}
	
	public ElaborateTask (DataArray data,String sessionId)
	{
		this.dX=data.getXData();
		this.dY=data.getYData();
		this.dZ=data.getYData();
		this.index=data.getIndex();
		this.sessionId=sessionId;
	}
	
	@Override
	protected Void doInBackground(Void... params) {
		int prevIndex=index-1;
		if(index==0)
			prevIndex=dZ.length-1;
		if(dZ[prevIndex]-dZ[index]>10)
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
			} catch (IllegalArgumentException e) {
				e.printStackTrace();
			} catch (IllegalDateFormatException e) {
				e.printStackTrace();
			} catch (IllegalNumberException e) {
				e.printStackTrace();
			} catch (IllegalIdException e) {
				e.printStackTrace();
			}
			Log.d("FALL EVENT","FALLEN");
		}
		return null;
	}
}