package it.dei.unipd.esp1415.views;

import it.dei.unipd.esp1415.utils.DataArray;
import it.dei.unipd.esp1415.utils.GlobalConstants;
import it.dei.unipd.esp1415.utils.PreferenceStorage;
import android.content.Context;
import android.graphics.Canvas;
import android.util.AttributeSet;

public class DataGraphicView extends GraphicView
{
	@Override
	public void onSizeChanged(int w,int h,int oW,int oH)
	{
		super.onSizeChanged(w, h, oW, oH);
		//if(w!=0)
			//this.changeXscale(w);
	}

	@Override
	public void onDraw(Canvas canvas)
	{
		if(canvasBitmap!=null)
			canvas.drawBitmap(canvasBitmap, 0, 0, canvasPaint);
		this.drawDataOnCanvas(canvas,data);
	}
	
	public void setData(DataArray data)
	{
		this.data=data;
		this.invalidate();
	}
	
	public DataGraphicView(Context context) {
		super(context);
	}
	public DataGraphicView(Context context,AttributeSet attr) {
		super(context,attr);
	}
}