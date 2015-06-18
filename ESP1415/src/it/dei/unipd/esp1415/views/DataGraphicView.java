package it.dei.unipd.esp1415.views;

import it.dei.unipd.esp1415.utils.DataArray;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.util.AttributeSet;

public class DataGraphicView extends GraphicView
{
	int rate=2;
	DataArray tempData;

	public DataGraphicView(Context context) {
		super(context);
		init();
	}
	public DataGraphicView(Context context,AttributeSet attr) {
		super(context,attr);
		init();
	}

	private void init()
	{
		tempData=new DataArray(10);
	}

	@Override
	public void onSizeChanged(int w,int h,int oW,int oH)
	{
		super.onSizeChanged(w, h, oW, oH);
		if(w!=0)
			this.changeXscale(w);
	}

	@Override
	public void onDraw(Canvas canvas)
	{
		if(canvasBitmap!=null)
			canvas.drawBitmap(canvasBitmap, 0, 0, canvasPaint);
		this.drawDataOnCanvas(canvas,tempData);
	}
	
	public void add(float x,float y,float z)
	{
		tempData.add(x,y,z);
		this.invalidate();
	}

	public void setRate(int rate)
	{
		this.rate=rate;
		data=new DataArray(rate);
	}
}
