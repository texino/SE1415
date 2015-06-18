package it.dei.unipd.esp1415.views;

import java.util.ArrayList;

import it.dei.unipd.esp1415.objects.AccelPoint;
import it.dei.unipd.esp1415.utils.DataArray;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Paint.Align;
import android.os.Bundle;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;

public class GraphicView extends View
{
	/**Dati rappresentati in questo grafico*/
	DataArray data;
	Bitmap canvasBitmap;
	Paint canvasPaint=new Paint(Paint.DITHER_FLAG);
	Canvas backgroundCanvas;
	int pixelXIndex=30,pixelYIndex=20;
	float textYIndex=4f,startYIndex=4f;
	float textXIndex=1f;//ogni indice indica un secondo

	public GraphicView(Context context) {
		super(context);
		init();
	}
	public GraphicView(Context context,AttributeSet attr) {
		super(context,attr);
		init();
	}

	@Override
	public void onSizeChanged(int w,int h,int oW,int oH)
	{
		canvasBitmap = Bitmap.createBitmap(w, h, Bitmap.Config.ARGB_8888);
		backgroundCanvas = new Canvas(canvasBitmap);
		drawAxesOnCanvas(backgroundCanvas);
		String todo;//TODO prendiamo il rate dalle preference
		int rate=10;//dobbiamo avere questo numero di dati al secondo
		int sNumber=(int)((float)w/((pixelXIndex)));
		int dataNumber=sNumber*rate;
		data=new DataArray(dataNumber);
		super.onSizeChanged(w, h, oW, oH);
	}
	
	
	public void changeXscale(int xPixel)
	{
		pixelXIndex=xPixel;
		canvasBitmap = Bitmap.createBitmap(this.getWidth(),this.getHeight(), Bitmap.Config.ARGB_8888);
		backgroundCanvas = new Canvas(canvasBitmap);
		drawAxesOnCanvas(backgroundCanvas);
	}

	public void setRate(int rate)
	{
		data=new DataArray(rate);

		if(this.getWidth()>0)
			pixelXIndex=this.getWidth();
		else
			pixelXIndex=1;
	}

	private void init()
	{
		this.setBackgroundColor(0xFF000000);
	}

	@Override
	public void onDraw(Canvas canvas)
	{
		if(canvasBitmap!=null)
			canvas.drawBitmap(canvasBitmap, 0, 0, canvasPaint);
		drawDataOnCanvas(canvas,data);
	}

	private void drawAxesOnCanvas(Canvas canvas)
	{
		Paint paint=new Paint();
		paint.setAntiAlias(false);
		paint.setStyle(Paint.Style.STROKE);
		paint.setStrokeWidth(1);
		paint.setColor(0xFFFFFFFF);
		paint.setTextSize(10);
		paint.setTextAlign(Align.CENTER);

		int mH=this.getHeight()/2;
		int mW=this.getWidth()/2;
		//Disegno le assi
		canvas.drawLine(0,mH,this.getWidth(),mH,paint);
		canvas.drawLine(mW,0,mW,this.getHeight(),paint);

		paint.setAntiAlias(true);
		paint.setStyle(Paint.Style.FILL_AND_STROKE);
		canvas.drawText("0 s",mW,mH+5, paint);
		//Disegno gli indici sull'asse X
		int off=pixelXIndex;
		float text=textXIndex;
		while(off<mW)
		{
			canvas.drawLine(mW+off,mH-5,mW+off,mH+5, paint);
			canvas.drawLine(mW-off,mH-5,mW-off,mH+5, paint);
			canvas.drawText(""+text+" s",mW+off,mH-7,paint);
			canvas.drawText("-"+text+" s",mW-off,mH-7,paint);
			text=text+textXIndex;
			off=off+pixelXIndex;
		}

		//Disegno gli indici sull'asse Y
		paint.setTextAlign(Align.LEFT);
		text=textYIndex;
		off=pixelYIndex;
		while(off<mH)
		{
			canvas.drawLine(mW-5,mH-off-5,mW+5,mH-off-5, paint);
			canvas.drawLine(mW-5,mH+off-5,mW+5,mH+off-5, paint);
			canvas.drawText(""+text,mW+7,mH-off,paint);
			canvas.drawText("-"+text,mW+7,mH+off,paint);
			text=text+textYIndex;
			off=off+pixelYIndex;
		}
	}

	public void drawDataOnCanvas(Canvas canvas,DataArray data)
	{
		Paint paintX=new Paint();
		paintX.setAntiAlias(true);
		paintX.setStyle(Paint.Style.STROKE);
		paintX.setStrokeWidth(1);
		Paint paintY=new Paint(paintX);
		Paint paintZ=new Paint(paintY);
		paintX.setColor(0xFFFF0000);
		paintY.setColor(0xFF00FF00);
		paintZ.setColor(0xFF0000FF);
		int index=data.getIndex();
		float dataX[]=data.getXData(),dataY[]=data.getYData(),dataZ[]=data.getZData();
		int w=this.getWidth();
		int x=0;
		int rate=data.getRate();//numero di dati da rappresentare
		int xOff=w/(rate-1);
		int i;
		for(i=index+1;i<rate;i++)
		{		
			canvas.drawLine(x,adjust(dataX[i-1]),x+xOff,adjust(dataX[i]),paintX);
			canvas.drawLine(x,adjust(dataY[i-1]),x+xOff,adjust(dataY[i]),paintY);
			canvas.drawLine(x,adjust(dataZ[i-1]),x+xOff,adjust(dataZ[i]),paintZ);
			x=x+xOff;
		}
		if(index!=0)//caso ponte
		{
			canvas.drawLine(x,adjust(dataX[rate-1]),x+xOff,adjust(dataX[0]),paintX);
			canvas.drawLine(x,adjust(dataY[rate-1]),x+xOff,adjust(dataY[0]),paintY);
			canvas.drawLine(x,adjust(dataZ[rate-1]),x+xOff,adjust(dataZ[0]),paintZ);
			x=x+xOff;
			for(i=1;i<index;i++)
			{
				canvas.drawLine(x,adjust(dataX[i-1]),x+xOff,adjust(dataX[i]),paintX);
				canvas.drawLine(x,adjust(dataY[i-1]),x+xOff,adjust(dataY[i]),paintY);
				canvas.drawLine(x,adjust(dataZ[i-1]),x+xOff,adjust(dataZ[i]),paintZ);
				x=x+xOff;
			}
		}
		i--;
		canvas.drawLine(x,adjust(dataX[i]),w-1,adjust(dataX[i]),paintX);
		canvas.drawLine(x,adjust(dataY[i]),w-1,adjust(dataY[i]),paintY);
		canvas.drawLine(x,adjust(dataZ[i]),w-1,adjust(dataZ[i]),paintZ);
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

	/**
	 * When this method is called the graphic scale is changed to that of a second
	 * @param dataList
	 */
	public void setData(AccelPoint[] dataList)
	{
		pixelXIndex=this.getWidth();
		int length=dataList.length;
		System.out.println("LENGTH = "+length);
		data=new DataArray(length);
		for(int i=0;i<length;i++)
		{
			System.out.println("REMAINING = "+(length-i));
			AccelPoint p=dataList[i];
			data.add(p.getX(),p.getY(),p.getZ());
		}
	}

	/**
	 * Adatta il valore ad una scala di un certo tipo ad altezza data
	 * @param f il valore da adattare
	 * @param h L'altezza (in pixel) della scala
	 * @param type Il tipo di scala da utilizzare tra:<br>
	 * X_SCALE, Y_SCALE, Z_SCALE
	 * @return il valore intero (in pixel) adattato alla scala
	 */
	private int adjust(float f)
	{
		//il valore dell'acceleratore è circa nell'intervallo
		int h=this.getHeight();
		float offset=f-startYIndex;//valore rispetto l'origine
		float indexNumber=offset/textYIndex;
		float pixelOffset=indexNumber*pixelYIndex;

		int rH=(int)((h/2)-(pixelOffset));

		if(rH<0)
			return 0;
		if(rH>=h)
			return h-1;
		return rH;
	}
}