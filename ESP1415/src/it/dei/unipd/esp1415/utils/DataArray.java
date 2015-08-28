package it.dei.unipd.esp1415.utils;

public class DataArray {

	private float[] dataX;
	private float[] dataY;
	private float[] dataZ;
	private int index;
	private int rate;

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
		index=0;
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
	 * @param i Indice che deve essere compreso nell'intervallo [0,rate[<br>
	 * (Viene impostato a 0 se è negativo o a rate-1 se è troppo grande
	 */
	public void setIndex(int i)
	{
		if(i<0)
			index=0;
		else if(i>=rate)
			index=rate-1;
		else
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
	 * Prende il numero di dati raccolti in questo array
	 * @return
	 */
	public int getRate()
	{
		return rate;
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
	 * Copia il contenuto di questo array in un'altro indipendente
	 * @return
	 */
	public DataArray copy()
	{
		DataArray d=new DataArray(rate);
		for(int i=0;i<rate;i++)
		{
			d.dataX[i]=dataX[i];
			d.dataY[i]=dataY[i];
			d.dataZ[i]=dataZ[i];
			d.setIndex(index);
		}
		return d;
	}
}