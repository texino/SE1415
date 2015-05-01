package it.dei.unipd.esp1415.tasks;

import it.dei.unipd.esp1415.utils.DataArray;
import android.os.AsyncTask;
import android.util.Log;

public class ElaborateTask extends AsyncTask<Void,Void,Void>{

	float[] dX,dY,dZ;
	int index;
	public ElaborateTask (float[] dX,float[] dY,float[] dZ)
	{
		this.dX=dX;
		this.dY=dY;
		this.dZ=dZ;
	}
	
	public ElaborateTask (DataArray data)
	{
		this.dX=data.getXData();
		this.dY=data.getYData();
		this.dZ=data.getYData();
		this.index=data.getIndex();
	}
	
	@Override
	protected Void doInBackground(Void... params) {
		int prevIndex=index-1;
		if(index==0)
			prevIndex=dZ.length-1;
		if(dZ[prevIndex]-dZ[index]>10)
			Log.d("FALL EVENT","FALLEN");
		return null;
	}
}