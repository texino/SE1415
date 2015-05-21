package it.dei.unipd.esp1415.utils;

import it.dei.unipd.esp1415.exceptions.IllegalDateFormatException;
import it.dei.unipd.esp1415.exceptions.IllegalIdException;
import it.dei.unipd.esp1415.exceptions.IllegalNameException;
import it.dei.unipd.esp1415.exceptions.IllegalNumberException;
import it.dei.unipd.esp1415.exceptions.LowSpaceException;
import it.dei.unipd.esp1415.exceptions.NoSuchSessionException;
import it.dei.unipd.esp1415.objects.AccelPoint;
import it.dei.unipd.esp1415.objects.FallData;
import it.dei.unipd.esp1415.objects.FallInfo;
import it.dei.unipd.esp1415.objects.SessionData;
import it.dei.unipd.esp1415.objects.SessionInfo;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Environment;
import android.os.StatFs;
import android.util.Log;

/**
 * A class to manage the local storage for this application
 */
public class LocalStorage {

	private final static File extDirectory=Environment.getExternalStorageDirectory();
	private final static String infoFolderPath=extDirectory+"/Working/Info/";
	private final static String infoFileName="sessions.txt";
	private final static String sessionsDataFolderPath=extDirectory+"/Working/SessionsData/";
	private final static String sessionImagesFolderPath=extDirectory+"/Working/SessionsImages/";
	private final static String TAG="LOCAL STORAGE";
	private final static String ID_TAG="ID";
	private final static String NAME_TAG="NAME";
	private final static String DURATION_TAG="DURATION";
	private final static String DATE_TAG="DATE";
	private final static String NUMBER_TAG="NUMBER",STATUS_TAG="STATUS";

	/**
	 * Prende la lista delle informazioni di tutte le sessioni memorizzate
	 * @return 
	 * -La lista delle SessionInfo in ordine cronologico di creazione dalla più recente<br>
	 * -Una lista vuota se non ci sono sessioni memorizzate<br>
	 * @throws IOException se c'è un'errore nella lettura/scrittura dei file
	 */
	public static List<SessionInfo> getSessionInfos() throws IOException
	{
		List<SessionInfo> list=new ArrayList<SessionInfo>();

		String pathInfo=infoFolderPath;
		File infoFile=new File(pathInfo);
		if(!infoFile.exists())
			infoFile.mkdirs();
		infoFile=new File(pathInfo+infoFileName);
		try{
			if(!infoFile.exists())
				infoFile.createNewFile();
			String finalS="";
			//Leggiamo le info dal file (che è una lista di id dal meno recente in poi)
			BufferedReader bufferedReader = new BufferedReader(new FileReader(infoFile));
			BufferedReader sessionReader;
			String id;
			while((id=bufferedReader.readLine())!=null)
			{
				Log.d(TAG,"INFO LINE:"+id);
				/*Se catturiamo un'eccezione è perchè i dati sulle info della sessione sono corrotti
				quindi non le consideriamo*/
				try{
					String sessionFolderPath=sessionsDataFolderPath+id+"/";//percorso del file della sessione
					File session=new File(sessionFolderPath+id+".txt");
					//passiamo in uscita le info della sessione solo se il file associato esiste
					if(session.exists()){
						sessionReader = new BufferedReader(new FileReader(session));
						String name=sessionReader.readLine();
						String date=sessionReader.readLine();
						String duration=sessionReader.readLine();
						String status=sessionReader.readLine();
						int number=0;
						while(sessionReader.readLine()!=null)
							number++;
						sessionReader.close();
						SessionInfo info=new SessionInfo(id,name,date,Integer.parseInt(duration),
								number,Boolean.parseBoolean(status));
						list.add(info);
						finalS+=id+"\n";
					}
				} catch (IllegalDateFormatException e) {
					e.printStackTrace();
				} catch (IllegalNameException e) {
					e.printStackTrace();
				} catch (IllegalNumberException e) {
					e.printStackTrace();
				} catch (IllegalIdException e) {
					e.printStackTrace();
				}
			}
			bufferedReader.close();
			/*Scriviamo sul file tutte le righe (raccolte in finalS) che hanno un file associato
				scartando così tutti quei dati che sono obsoleti*/
			BufferedWriter bufferedWriter = new BufferedWriter(new FileWriter(infoFile));
			bufferedWriter.write(finalS);
			bufferedWriter.flush();
			bufferedWriter.close();
			return list;
		} catch (java.io.IOException e) {
			e.printStackTrace();
			throw e;
		}
	}

	/**
	 * Crea una nuova sessione in memoria
	 * @param context Il contesto in cui viene creata la sessione
	 * @param name Il nome della nuova sessione
	 * @return l'id della nuova sessione
	 * @throws IllegalArgumentException se il nome è null o non ha almeno un carattere
	 * @throws IOException se c'è un'errore nella lettura/scrittura dei file
	 */
	public static SessionData createNewSession(Context context,String name) throws IllegalArgumentException,IOException,LowSpaceException
	{
		if((name==null)||(name.length()<1))
			throw new IllegalArgumentException();

		DateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy-HH:mm");
		String date=dateFormat.format(new Date());
		String id=name;
		SessionData session;
		try {
			session = new SessionData(id,name,date,0,true,null);
		} catch (IllegalDateFormatException e1) {
			e1.printStackTrace();throw new IllegalArgumentException();
		} catch (IllegalNameException e1) {
			e1.printStackTrace();throw new IllegalArgumentException();
		} catch (IllegalNumberException e1) {
			e1.printStackTrace();throw new IllegalArgumentException();
		} catch (IllegalIdException e1) {
			e1.printStackTrace();throw new IllegalArgumentException();
		}
		String data=name+"\n"+
				date+"\n"+
				"0\n"+
				"true\n";//stringa da scrivere nel file della sessione

		String pathInfo=infoFolderPath;
		String pathSession=sessionsDataFolderPath+id+"/";
		File infoFile=new File(pathInfo+infoFileName);
		Log.d(TAG,"INFO FILE:\nPath:"+infoFile.getAbsolutePath()+
				"\nexist:"+infoFile.exists()+
				"\nisFile:"+infoFile.isFile());

		File sessionFile=new File(pathSession+id+".txt"); //Creiamo il file
		Log.d(TAG,"SESSION FILE\n"+"Path:"+sessionFile.getAbsolutePath()+
				"\nexist:"+sessionFile.exists()+
				"\nisFile:"+sessionFile.isFile());
		try{
			if(!infoFile.exists())
			{
				File folders=new File(pathInfo);
				folders.mkdirs();
				infoFile.createNewFile();
			}
			if(!sessionFile.exists())
			{
				File folders=new File(pathSession);
				folders.mkdirs();
				sessionFile.createNewFile();
			}
			StatFs stat = new StatFs(Environment.getExternalStorageDirectory().getPath());
			long bytesAvailable = (long)stat.getBlockCount()*(long)stat.getBlockSize();
			if(bytesAvailable<(id.length()+data.length()))
			{
				//non c'è abbastanza spazio
				throw new LowSpaceException(bytesAvailable,id.length()+data.length());
			}
			//SCRIVIAMO LE INFO
			BufferedWriter bufferedWriter = new BufferedWriter(new FileWriter(infoFile,true));
			bufferedWriter.write(id+"\n");
			bufferedWriter.flush();
			bufferedWriter.close();
			//SCRIVIAMO LA SESSIONE
			bufferedWriter = new BufferedWriter(new FileWriter(sessionFile,true));
			bufferedWriter.write(data);
			bufferedWriter.flush();
			bufferedWriter.close();
			return session;
		} catch (java.io.IOException e) {
			e.printStackTrace();
			throw e;}
	}

	/**
	 * Prende i dati di una sessione specifica
	 * @param context The context that call this method
	 * @param sessionId L'id della sessione
	 * @return an object of the session data
	 * @throws NoSuchSessionException if this session isn't stored
	 * @throws IOException if there's been an error reading from files
	 * @throws IllegalArgumentException if one of the parameters is null
	 */
	public static SessionData getSessionData(Context context,String sessionId) throws NoSuchSessionException,IOException,IllegalArgumentException
	{
		if((context==null)||(sessionId==null))
			throw new IllegalArgumentException();
		String sessionPath=sessionsDataFolderPath+sessionId+"/";
		File sessionFile=new File(sessionPath+sessionId+".txt");
		if(!sessionFile.exists())
			throw new NoSuchSessionException();
		String finalS="";
		//Leggiamo dal file
		BufferedReader bufferedReader = new BufferedReader(new FileReader(sessionFile));
		//INFORMAZIONI
		String name=bufferedReader.readLine();
		finalS+=name+"\n";
		String date=bufferedReader.readLine();
		finalS+=date+"\n";
		int durata=Integer.parseInt(bufferedReader.readLine());
		finalS+=durata+"\n";
		boolean running=Boolean.parseBoolean(bufferedReader.readLine());
		finalS+=running+"\n";

		ArrayList<FallInfo> list=new ArrayList<FallInfo>();
		String line;
		JSONObject json;
		while((line=bufferedReader.readLine())!=null)
		{
			//Se catturiamo un'eccezione è perchè i dati sono corrotti e non li consideriamo
			try{
				json=new JSONObject(line);
				String fallId=json.getString(ID_TAG);//percorso del file della sessione
				File fall=new File(sessionPath+fallId+".txt");
				Log.d(TAG,"FALL FILE : "+fall.getAbsolutePath()+"\n"
						+"Exist : "+fall.exists()+"\n"+
						"Is file : "+fall.isFile()+"\n"+
						"Is directory : "+fall.isDirectory());
				//passiamo in uscita le info della sessione solo se il file associato esiste
				if(fall.exists()){
					FallInfo info=new FallInfo(fallId,json.getString(DATE_TAG),json.getBoolean(STATUS_TAG));
					list.add(info);
					finalS+=line+"\n";
				}
			} catch(JSONException e){
				e.printStackTrace();
			} catch (IllegalDateFormatException e) {
				e.printStackTrace();
			} catch (IllegalIdException e) {
				e.printStackTrace();
			}
		}
		bufferedReader.close();
		//Scriviamo sul file tutte le righe (raccolte in finalS) scartando così tutti quei dati corrotti
		BufferedWriter bufferedWriter = new BufferedWriter(new FileWriter(sessionFile));
		bufferedWriter.write(finalS);
		bufferedWriter.flush();
		bufferedWriter.close();
		try {
			return new SessionData(sessionId,name,date,durata,running,list);
		} catch (IllegalDateFormatException e) {
			e.printStackTrace();
		} catch (IllegalNameException e) {
			e.printStackTrace();
		} catch (IllegalNumberException e) {
			e.printStackTrace();
		} catch (IllegalIdException e) {
			e.printStackTrace();
		}
		//C'è un problema nei dati letti, quindi il file è corrotto
		throw new IOException();
	}

	/**
	 * Prende l'immagine della sessione specificata
	 * @param sessionId L'Id della sessione
	 * @return un oggetto di tipo Bitmap dell'immagine <br>
	 * (null se l'immagine non esiste o è danneggiata)
	 * @throws IllegalArgumentException se uno dei parametri è null
	 */
	public static Bitmap getSessionImage(Context context,String sessionId) throws IOException,IllegalArgumentException
	{
		if((context==null)||(sessionId==null))
			throw new IllegalArgumentException();

		File sdCardDirectory = Environment.getExternalStorageDirectory();
		String path=sdCardDirectory+sessionImagesFolderPath+sessionId+".png";
		File imageFile=new File(path);
		if(!imageFile.exists())
			return null;
		return BitmapFactory.decodeFile(imageFile.getAbsolutePath());
	}

	/**
	 * Get the data of the specified session's fall
	 * @param context The context that call this method
	 * @param sessionId The id of the session to get
	 * @param fallId The id of the fall in the session
	 * @return an object of the fall data
	 * @throws NoSuchSessionException if this session isn't stored
	 * @throws IOException if there's been an error reading from files
	 * @throws IllegalArgumentException if one of the parameters is null
	 */
	public static FallData getFallData(Context context,String sessionId,String fallId) throws NoSuchSessionException,IOException,IllegalArgumentException
	{
		if((context==null)||(sessionId==null))
			throw new IllegalArgumentException();

		File sdCardDirectory = Environment.getExternalStorageDirectory();
		String path=sdCardDirectory+sessionsDataFolderPath+sessionId+"/"+fallId+".txt";
		File fallFile=new File(path);

		//Leggiamo i dati
		BufferedReader bufferedReader = new BufferedReader(new FileReader(fallFile));
		//INFORMAZIONI
		int rate=Integer.parseInt(bufferedReader.readLine());
		String data=bufferedReader.readLine();
		boolean notificato=Boolean.parseBoolean(bufferedReader.readLine());
		int longit=Integer.parseInt(bufferedReader.readLine());
		int lat=Integer.parseInt(bufferedReader.readLine());

		AccelPoint[] points=new AccelPoint[rate];
		String line;
		int nP=0;
		while((line=bufferedReader.readLine())!=null)
		{
			String[] coords=line.split(";");
			points[nP]=new AccelPoint(Float.parseFloat(coords[0]),
					Float.parseFloat(coords[1]),
					Float.parseFloat(coords[2]));
			nP++;
		}
		bufferedReader.close();
		try {
			return new FallData(fallId,data,notificato,longit,lat,points);
		} catch (IllegalDateFormatException e) {
			e.printStackTrace();
		} catch (IllegalNumberException e) {
			e.printStackTrace();
		} catch (IllegalIdException e) {
			e.printStackTrace();
		}
		return null;
	}

	public static void storeFallData(String sessionId,FallData data)
	{
		String dataId=data.getId();
		String path=sessionsDataFolderPath+sessionId+"/";
		File fallFile=new File(path+dataId+".txt");
		File sessionFile=new File(path+sessionId+".txt");
		try{
			JSONObject json=new JSONObject();
			try {
				json.put(ID_TAG, dataId);
				json.put(DATE_TAG, data.getDate());
				json.put(STATUS_TAG, data.isNotified());
			} catch (JSONException e) {
				e.printStackTrace();
				return;
			}
			BufferedWriter bufferedWriter=new BufferedWriter(new FileWriter(sessionFile,true));
			bufferedWriter.write(json.toString()+"\n");
			bufferedWriter.flush();
			bufferedWriter.close();

			fallFile.createNewFile();
			AccelPoint[] dataPoints=data.getAccelDatas();
			bufferedWriter = new BufferedWriter(new FileWriter(fallFile));
			bufferedWriter.write(""+dataPoints.length+"\n");
			bufferedWriter.write(""+data.getDate()+"\n");
			bufferedWriter.write(""+data.isNotified()+"\n");
			bufferedWriter.write(""+data.getLongitude()+"\n");
			bufferedWriter.write(""+data.getLatitude()+"\n");
			for(int i=0;i<dataPoints.length;i++)
				bufferedWriter.write(""+dataPoints[i].getX()+";"+dataPoints[i].getY()+";"
						+dataPoints[i].getZ()+"\n");
			bufferedWriter.flush();
			bufferedWriter.close();
		}
		catch(IOException e)
		{
			e.printStackTrace();
		}
	}

	public static void stopSession(String sessionId,int totalDuration) throws NoSuchSessionException
	{
		try{
			String sessionPath=sessionsDataFolderPath+sessionId+"/";
			File sessionFile=new File(sessionPath+sessionId+".txt");
			if(!sessionFile.exists())
				throw new NoSuchSessionException();
			String finalS="";
			//Leggiamo dal file
			BufferedReader bufferedReader = new BufferedReader(new FileReader(sessionFile));
			//SESSIONE
			finalS+=bufferedReader.readLine()+"\n";
			finalS+=bufferedReader.readLine()+"\n";//saltiamo nome e data
			bufferedReader.readLine();
			finalS+=totalDuration+"\n";
			bufferedReader.readLine();
			finalS+="false\n";
			String line;
			while((line=bufferedReader.readLine())!=null)
				finalS+=line+"\n";
			bufferedReader.close();
			Log.d(TAG,"STORING: "+finalS+"\nIn "+sessionId);
			BufferedWriter bufferedWriter = new BufferedWriter(new FileWriter(sessionFile));
			bufferedWriter.write(finalS);
			bufferedWriter.flush();
			bufferedWriter.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	public static void pauseSession(String sessionId,int totalDuration) throws NoSuchSessionException
	{
		try{
			String sessionPath=sessionsDataFolderPath+sessionId+"/";
			File sessionFile=new File(sessionPath+sessionId+".txt");
			if(!sessionFile.exists())
				throw new NoSuchSessionException();
			String finalS="";
			//Leggiamo dal file
			BufferedReader bufferedReader = new BufferedReader(new FileReader(sessionFile));
			//SESSIONE
			finalS+=bufferedReader.readLine()+"\n";
			finalS+=bufferedReader.readLine()+"\n";//saltiamo nome e data
			bufferedReader.readLine();
			finalS+=totalDuration+"\n";
			finalS+=bufferedReader.readLine()+"\n";
			String line;
			while((line=bufferedReader.readLine())!=null)
				finalS+=line+"\n";
			bufferedReader.close();

			Log.d(TAG,"STORING: "+finalS+"\nIn "+sessionId);
			BufferedWriter bufferedWriter = new BufferedWriter(new FileWriter(sessionFile));
			bufferedWriter.write(finalS);
			bufferedWriter.flush();
			bufferedWriter.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	/**
	 * Rename an existing session
	 * @param context The context that call this method
	 * @param sessionId The id of the session to rename
	 * @param name The new name of the session
	 * @throws NoSuchSessionException if this session isn't stored
	 * @throws IOException if there's been an error reading from files
	 * @throws IllegalArgumentException if one of the parameters is null (or name is "")
	 */
	public static void renameSession(Context context,String sessionId,String name) throws NoSuchSessionException,IOException,IllegalArgumentException
	{
		try{
			String sessionPath=sessionsDataFolderPath+sessionId+"/";
			File sessionFile=new File(sessionPath+sessionId+".txt");
			if(!sessionFile.exists())
				throw new NoSuchSessionException();
			String finalS="";
			//Leggiamo dal file
			BufferedReader bufferedReader = new BufferedReader(new FileReader(sessionFile));
			//SESSIONE
			bufferedReader.readLine();
			finalS=name+"\n";
			finalS+=bufferedReader.readLine()+"\n";
			finalS+=bufferedReader.readLine()+"\n";
			finalS+=bufferedReader.readLine()+"\n";
			String line;
			while((line=bufferedReader.readLine())!=null)
				finalS+=line+"\n";
			bufferedReader.close();

			BufferedWriter bufferedWriter = new BufferedWriter(new FileWriter(sessionFile));
			bufferedWriter.write(finalS);
			bufferedWriter.flush();
			bufferedWriter.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	/**
	 * Delete an existing session
	 * @param context The context that call this method
	 * @param sessionId The id of the session to delete
	 * @throws NoSuchSessionException if this session isn't stored
	 * @throws IOException if there's been an error reading from files
	 * @throws IllegalArgumentException if one of the parameters is null
	 */
	public static void deleteSession(Context context,String sessionId) throws NoSuchSessionException,IOException,IllegalArgumentException
	{
		if((context==null)||(sessionId==null))
			throw new IllegalArgumentException();
		//TODO make this method
	}
}
