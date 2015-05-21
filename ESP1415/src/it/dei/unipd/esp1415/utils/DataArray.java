package it.dei.unipd.esp1415.utils;

import android.graphics.Canvas;
import android.graphics.Paint;
import android.util.Log;
import android.util.Log;

public class DataArray {
	
	private static int X_SCALE=0,Y_SCALE=1,Z_SCALE=2;
	private float[] dataX;
	private float[] dataY;
	private float[] dataZ;
	private int index;
	private int rate;
	private Paint paintX,paintY,paintZ;
	
	/**
	 * Crea un nuovo array disegnabile per i dati dell'accellerometro
	 * @param rate Il numero di dati memorizzabili in questo array (almeno 1)<br>
	 * (sarà il numero di dati visualizzati nel grafico)
	 * @throws IllegalArgumentException se rate è minore di 1
	 */
	public DataArray(int rate) throws IllegalArgumentException
	{
		if(rate<1)
			throw new IllegalArgumentException("Wrong rate");
		this.rate=rate;
		this.dataX=new float[rate];
		this.dataY=new float[rate];
		this.dataZ=new float[rate];
		for(int i=0;i<rate;i++)
		{
			dataX[i]=-1;
			dataY[i]=-1;
			dataZ[i]=-1;
		}
		index=0;
		
		paintX=new Paint();
		paintX.setAntiAlias(true);
		paintX.setStyle(Paint.Style.STROKE);
		paintX.setStrokeJoin(Paint.Join.ROUND);
		paintX.setStrokeCap(Paint.Cap.ROUND);
		paintX.setXfermode(null);
		paintX.setStrokeWidth(2);
		paintY=new Paint(paintX);
		paintZ=new Paint(paintY);
		paintX.setColor(0xFFFF0000);
		paintY.setColor(0xFF00FF00);
		paintZ.setColor(0xFF0000FF);
	}

	/**
	 * Aggiunge dei nuovi dati a questo array
	 * @param x I dati da aggiungere
	 */
	public void add(float x,float y,float z)
	{
		dataX[index]=x;
		dataY[index]=y;
		dataZ[index]=z;
		index++;
		if(index>=rate)
			index=0;
	}

	/**
	 * Imposta l'indice di lettura di questo array
	 * @param i Indice che deve essere compreso nell'intervallo [0,rate[
	 */
	public void setIndex(int i) throws IllegalArgumentException
	{
		if(i<0||i>=rate)
			throw new IllegalArgumentException("Wrong index");
		index=i;
	}
	
	/**
	 * Prende l'indice di lettura di questo array
	 * @return Un valore intero nell'intervallo [0,rate[
	 */
	public int getIndex()
	{
		return index;
	}

	/**
	 * Prende l'array di valori sull'asse X di questo array
	 * @return Un array di valori float
	 */
	public float[] getXData()
	{
		return dataX;
	}

	/**
	 * Prende l'array di valori sull'asse Y di questo array
	 * @return Un array di valori float
	 */
	public float[] getYData()
	{
		return dataY;
	}

	/**
	 * Prende l'array di valori sull'asse Z di questo array
	 * @return Un array di valori float
	 */
	public float[] getZData()
	{
		return dataZ;
	}
	
	/**
	 * Disegna i dati di questo array sulla canvas di un grafico
	 * @param c La canvas del grafico
	 * @param w La larghezza in pixel del grafico
	 * @param h L'altezza in pixel del grafico
	 */
	public void drawOnCanvas(Canvas ca,int w,int h)
	{
		int x=0;
		int xOff=w/(rate-1);
		for(int i=index+1;i<rate;i++)
		{		
			ca.drawLine(x,adjust(dataX[i-1],h,X_SCALE),x+xOff,adjust(dataX[i],h,X_SCALE),paintX);
			ca.drawLine(x,adjust(dataY[i-1],h,Y_SCALE),x+xOff,adjust(dataY[i],h,Y_SCALE),paintY);
			ca.drawLine(x,adjust(dataZ[i-1],h,Z_SCALE),x+xOff,adjust(dataZ[i],h,Z_SCALE),paintZ);
			x=x+xOff;
		}
		ca.drawLine(x,adjust(dataX[rate-1],h,X_SCALE),x+xOff,adjust(dataX[0],h,X_SCALE),paintX);
		ca.drawLine(x,adjust(dataY[rate-1],h,Y_SCALE),x+xOff,adjust(dataY[0],h,Y_SCALE),paintY);
		ca.drawLine(x,adjust(dataZ[rate-1],h,Z_SCALE),x+xOff,adjust(dataZ[0],h,Z_SCALE),paintZ);
		x=x+xOff;
		for(int i=1;i<index;i++)
		{
			ca.drawLine(x,adjust(dataX[i-1],h,X_SCALE),x+xOff,adjust(dataX[i],h,X_SCALE),paintX);
			ca.drawLine(x,adjust(dataY[i-1],h,Y_SCALE),x+xOff,adjust(dataY[i],h,Y_SCALE),paintY);
			ca.drawLine(x,adjust(dataZ[i-1],h,Z_SCALE),x+xOff,adjust(dataZ[i],h,Z_SCALE),paintZ);
			x=x+xOff;
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
	private int adjust(float f,int h,int type)
	{
		int delta=0;
		if(type==X_SCALE)
			delta=30;
		else if(type==Y_SCALE)
			delta=30;
		else if(type==Z_SCALE)
			delta=30;

		delta=delta/2;
		if(f<-delta)
			return h;
		if(f>delta)
			return 0;
		return (int)((h/2)*(1-(f/delta)));
	}
}