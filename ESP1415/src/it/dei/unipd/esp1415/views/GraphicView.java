package it.dei.unipd.esp1415.views;

import com.example.esp1415.R;

import it.dei.unipd.esp1415.utils.DataArray;
import it.dei.unipd.esp1415.utils.GlobalConstants;
import it.dei.unipd.esp1415.utils.PreferenceStorage;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Paint.Align;
import android.os.Bundle;
import android.util.AttributeSet;
import android.view.View;

public class GraphicView extends View
{
	/**Dati rappresentati in questo grafico*/
	Context actContext;
	DataArray data;
	Bitmap canvasBitmap;
	Paint canvasPaint=new Paint(Paint.DITHER_FLAG),paintX,paintY,paintZ;
	int pixelY=20;//pixel Y per uno slot
	float textYIndex=4f;//numero di g per uno slot
	float textXIndex=1f;//numero di secondi per uno slot
	boolean oneSecond=false;
	private static final int MIN_ACC=-16,MAX_ACC=16;
	private int SECONDS=10;

	@Override
	public void onSizeChanged(int w,int h,int oW,int oH)
	{
		if(w==0||h==0)//non abbiamo misure per adattare
			return;
		//creiamo la bitmap di sfondo e l'allegato canvas
		canvasBitmap = Bitmap.createBitmap(w,h,Bitmap.Config.ARGB_8888);
		Canvas backgroundCanvas = new Canvas(canvasBitmap);
		
		int secondPixel=w/SECONDS;
		pixelY=(int)(h/((MAX_ACC/textYIndex)-(MIN_ACC/textYIndex)));
		//disegniamo gli assi
		drawAxesOnCanvas(backgroundCanvas,secondPixel,pixelY);
		
		//misuriamo il numero di dati da rappresentare
		String r;
		//r=PreferenceStorage.getSimpleData(actContext,PreferenceStorage.ACCEL_RATIO);
		String cancellaQuesteDueRighe;
		r="2";
		int rate=2;
		if(r.equals(""))
		{
			rate=GlobalConstants.MIN_RATIO;
			PreferenceStorage.storeSimpleData(actContext,PreferenceStorage.ACCEL_RATIO,""+rate);
		}
		float sNumber=((float)w)/secondPixel;
		int dataNumber=(int)(sNumber*rate);
		if(data==null)//non ci sono dati da visualizzare
		{
			data=new DataArray(dataNumber);
			super.onSizeChanged(w,h,oW,oH);
			return;			
		}
		//ci sono già dei dati che vorremmo visualizzare
		DataArray tData=new DataArray(dataNumber);//dati effettivi da visualizzare
		int oldDataNumber=data.getRate();//numero di dati da cerare di riprodurre
		
		int rIndex=data.getIndex(); //indice da cui riprodurre i dati 
		int off=dataNumber-oldDataNumber;//se <0 allora devo troncare dati
		if(off<0)
		{
			rIndex=(data.getIndex()-off);
			if(rIndex>=oldDataNumber)
				rIndex-=oldDataNumber;
		}
		float[] Xs=data.getXData();
		float[] Ys=data.getYData();
		float[] Zs=data.getZData();
		for (int i=rIndex;i<data.getRate();i++)
			tData.add(Xs[i],Ys[i],Zs[i]);
		for (int i=0;i<data.getIndex();i++)
			tData.add(Xs[i],Ys[i],Zs[i]);
		super.onSizeChanged(w,h,oW,oH);
	}
	
	public void setScaleToOneSecond()
	{
		oneSecond=true;
		SECONDS=1;
	}

	@Override
	public void onDraw(Canvas canvas)
	{
		if(canvasBitmap!=null)
			canvas.drawBitmap(canvasBitmap,0,0,canvasPaint);
		drawDataOnCanvas(canvas,data);
	}

	/**
	 * Disegna le assi sul canvas
	 * @param canvas Il canvas su cui disegnare
	 * @param pixelX Il numero di pixel tra un secondo e l'altro nell'asse X
	 * @param pixelY Il numero di pixel tra un valore e l'altro nell'asse Y
	 */
	private void drawAxesOnCanvas(Canvas canvas,int pixelX,int pixelY)
	{
		Paint paint=new Paint();
		paint.setAntiAlias(false);
		paint.setStyle(Paint.Style.STROKE);
		paint.setStrokeWidth(1);
		paint.setColor(actContext.getResources().getColor(R.color.graphic_axes_color));
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
		int off=pixelX;
		float text=textXIndex;
		while(off<mW)
		{
			canvas.drawLine(mW+off,mH-5,mW+off,mH+5, paint);
			canvas.drawLine(mW-off,mH-5,mW-off,mH+5, paint);
			canvas.drawText(""+text+" s",mW+off,mH-7,paint);
			canvas.drawText("-"+text+" s",mW-off,mH-7,paint);
			text+=textXIndex;
			off=off+pixelX;
		}

		//Disegno gli indici sull'asse Y
		paint.setTextAlign(Align.LEFT);
		text=textYIndex;
		off=pixelY;
		while(off<mH)
		{
			canvas.drawLine(mW-5,mH-off-5,mW+5,mH-off-5, paint);
			canvas.drawLine(mW-5,mH+off-5,mW+5,mH+off-5, paint);
			canvas.drawText(""+text,mW+7,mH-off,paint);
			canvas.drawText("-"+text,mW+7,mH+off,paint);
			text+=textYIndex;
			off+=pixelY;
		}
	}

	public void setData(DataArray datas)
	{
		data=datas;
	}
	
	public void drawDataOnCanvas(Canvas canvas,DataArray data)
	{
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

	/**
	 * Salva l'attuale stato del grafico sul bundle
	 * @param bundle
	 */
	public void saveStatusOnBundle(Bundle bundle)
	{
		bundle.putFloatArray("x_array",data.getXData());
		bundle.putFloatArray("y_array",data.getYData());
		bundle.putFloatArray("z_array",data.getZData());
		bundle.putInt("index",data.getIndex());
	}

	/**
	 * Ripristina lo stato del grafico dal bundle
	 * @param bundle
	 */
	public void restoreStatusFromBundle(Bundle bundle)
	{
		float[] x=bundle.getFloatArray("x_array");
		float[] y=bundle.getFloatArray("y_array");
		float[] z=bundle.getFloatArray("z_array");
		int dataNumber=x.length;
		data=new DataArray(dataNumber);
		int l=x.length;
		for(int i=0;i<l;i++)
			data.add(x[i],y[i],z[i]);
		data.setIndex(bundle.getInt("index"));
		this.invalidate();
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
		
		float pixelOffset=pixelY*(((float)f)/textYIndex);

		int rH=(int)((h/2)-(pixelOffset));

		if(rH<0)
			return 0;
		if(rH>=h)
			return h-1;
		return rH;
	}

	public GraphicView(Context context) {
		super(context);
		init(context);
	}
	public GraphicView(Context context,AttributeSet attr) {
		super(context,attr);
		init(context);
	}

	private void init(Context context)
	{
		actContext=context;
		this.setBackgroundColor(context.getResources().getColor(R.color.graphic_background_color));
		paintX=new Paint();
		paintX.setAntiAlias(true);
		paintX.setStyle(Paint.Style.STROKE);
		paintX.setStrokeWidth(1);
		paintY=new Paint(paintX);
		paintZ=new Paint(paintY);
		paintX.setColor(context.getResources().getColor(R.color.graphic_x_color));
		paintY.setColor(context.getResources().getColor(R.color.graphic_y_color));
		paintZ.setColor(context.getResources().getColor(R.color.graphic_z_color));
	}
}