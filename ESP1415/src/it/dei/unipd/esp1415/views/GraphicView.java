package it.dei.unipd.esp1415.views;

import it.dei.unipd.esp1415.utils.DataArray;
import android.content.Context;
import android.graphics.Canvas;
import android.os.Bundle;
import android.util.AttributeSet;
import android.view.View;

public class GraphicView extends View
{
	DataArray data;

	public GraphicView(Context context) {
		super(context);
		init();
	}
	public GraphicView(Context context,AttributeSet attr) {
		super(context,attr);
		init();
	}

	private void init()
	{
		this.setBackgroundColor(0xFF555555);
		int dataN=10;
		data=new DataArray(dataN);
	}

	@Override
	public void onDraw(Canvas canvas)
	{
		data.drawOnCanvas(canvas,this.getWidth(),this.getHeight());
	}

	public void add(float x,float y,float z)
	{
		data.add(x,y,z);
		this.invalidate();
	}

	public void saveStatusOnBundle(Bundle bundle)
	{
		bundle.putFloatArray("x_array",data.getXData());
		bundle.putFloatArray("y_array",data.getYData());
		bundle.putFloatArray("z_array",data.getZData());
		bundle.putInt("index",data.getIndex());
	}

	public void restoreStatusFromBundle(Bundle bundle)
	{
		float[] x=bundle.getFloatArray("x_array");
		float[] y=bundle.getFloatArray("y_array");
		float[] z=bundle.getFloatArray("z_array");
		int l=x.length;
		for(int i=0;i<l;i++)
			data.add(x[i],y[i],z[i]);
		data.setIndex(bundle.getInt("index"));
		this.invalidate();
	}
}