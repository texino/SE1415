package it.dei.unipd.esp1415.utils;

import it.dei.unipd.esp1415.exceptions.IllegalDateFormatException;
import it.dei.unipd.esp1415.exceptions.IllegalIdException;
import it.dei.unipd.esp1415.exceptions.IllegalNameException;
import it.dei.unipd.esp1415.exceptions.IllegalNumberException;
import it.dei.unipd.esp1415.exceptions.LowSpaceException;
import it.dei.unipd.esp1415.exceptions.NoSuchFallException;
import it.dei.unipd.esp1415.exceptions.NoSuchSessionException;
import it.dei.unipd.esp1415.objects.AccelPoint;
import it.dei.unipd.esp1415.objects.FallData;
import it.dei.unipd.esp1415.objects.FallInfo;
import it.dei.unipd.esp1415.objects.SessionData;
import it.dei.unipd.esp1415.objects.SessionInfo;

import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import org.json.JSONException;
import org.json.JSONObject;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Environment;
import android.os.StatFs;
import android.util.Log;

/**
 * Classe che gestisce il salvataggio ed il recupero delle sessioni
 */
public class LocalStorage {

	private final static File extDirectory=Environment.getExternalStorageDirectory();
	private final static String infoFolderPath=extDirectory+"/Working/Info/";
	private final static String infoFileName="sessions.txt";
	private final static String sessionsDataFolderPath=extDirectory+"/Working/SessionsData/";
	private final static String sessionImagesFolderPath=extDirectory+"/Working/SessionsImages/";
	private final static String TAG="LOCAL STORAGE";
	private final static String ID_TAG="ID";
	private final static String DATE_TAG="DATE";
	private final static String STATUS_TAG="STATUS";

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
		try{
			File infoFile=new File(pathInfo);
			if(!infoFile.exists())
				infoFile.mkdirs();
			infoFile=new File(pathInfo+infoFileName);
			if(!infoFile.exists())
				infoFile.createNewFile();
			String finalS="";
			//Leggiamo le info dal file (che è una lista di id dal meno recente in poi)
			BufferedReader bufferedReader = new BufferedReader(new FileReader(infoFile));
			String id;
			while((id=bufferedReader.readLine())!=null)
			{
				SessionInfo info=getSessionInfo(id);
				if(info!=null)//consideriamo l'id solo se appunto i dati esistono e non sono corrotti
					list.add(info);
				finalS+=id+"\n";
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
	 * @param name Il nome della nuova sessione
	 * @return la SessionData salvata se il salvataggio ha avuto successo (null altrimenti)
	 * @throws IllegalArgumentException se il nome è null o non ha almeno un carattere
	 * @throws IOException se c'è un'errore nella lettura/scrittura dei file
	 * @throws LowSpaceException se non c'è abbastanza spazio in memoria per salvare
	 */
	public static SessionInfo createNewSession(String name) throws IllegalArgumentException,IOException,LowSpaceException
	{
		if((name==null)||(name.length()<1))
			throw new IllegalArgumentException();
		DateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy-HH:mm",Locale.ITALIAN);
		String date=dateFormat.format(new Date());
		String id=""+System.currentTimeMillis();
		SessionInfo session;
		try {
			session = new SessionInfo(id,name,date,0,0,true);
			saveSessionInfoInFile(session);
			saveSessionIdInInfoFile(id);
		} catch (IllegalDateFormatException e1) {
			e1.printStackTrace();throw new IllegalArgumentException();
		} catch (IllegalNameException e1) {
			e1.printStackTrace();throw new IllegalArgumentException();
		} catch (IllegalNumberException e1) {
			e1.printStackTrace();throw new IllegalArgumentException();
		} catch (IllegalIdException e1) {
			e1.printStackTrace();throw new IllegalArgumentException();
		}catch (LowSpaceException e1) {
			throw e1;
		}catch (IOException e1) {
			throw e1;
		}
		return session;
	}

	/**
	 * Prende i dati di una sessione specifica
	 * @param sessionId L'id della sessione
	 * @return un oggetto di tipo SessionData se sono stati raccolti i dati (null altrimenti)
	 * @throws NoSuchSessionException Se la sessione non esiste
	 * @throws IOException se c'è stato un'errore di salvataggio
	 * @throws IllegalArgumentException Se l'id della sessione non è valido
	 */
	public static SessionData getSessionData(String sessionId) throws NoSuchSessionException,IOException,IllegalArgumentException
	{
		if(sessionId==null||sessionId.equals(""))
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
				//passiamo in uscita le info della sessione solo se il file associato esiste
				if(fall.exists()){
					FallInfo info=new FallInfo(fallId,json.getString(DATE_TAG),json.getBoolean(STATUS_TAG),name);
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
	 * Prende i dati di una specifica caduta di una sessione
	 * @param sessionId L'Id della sessione
	 * @param fallId L'Id della caduta
	 * @return un oggetto di tipo FallData se ci sono i dati (null altrimenti)
	 * @throws NoSuchFallException Se la caduta non esiste
	 * @throws IOException Se c'è stato un errore nella lettura dei file
	 * @throws IllegalArgumentException Se uno dei parametri è incosistente
	 */
	public static FallData getFallData(String sessionId,String fallId) throws NoSuchFallException,IOException,IllegalArgumentException
	{
		if(sessionId==null||fallId==null||sessionId.equals("")||fallId.equals(""))
			throw new IllegalArgumentException();

		String path=sessionsDataFolderPath+sessionId+"/"+sessionId+".txt";
		File fallFile=new File(path);
		if(!fallFile.exists())
			throw new NoSuchFallException();
		BufferedReader bufferedReader = new BufferedReader(new FileReader(fallFile));
		String sessionName=bufferedReader.readLine();
		bufferedReader.close();
		path=sessionsDataFolderPath+sessionId+"/"+fallId+".txt";
		fallFile=new File(path);
		if(!fallFile.exists())
			throw new NoSuchFallException();
		//Leggiamo i dati
		bufferedReader = new BufferedReader(new FileReader(fallFile));
		//INFORMAZIONI
		int rate=Integer.parseInt(bufferedReader.readLine());
		String data=bufferedReader.readLine();
		boolean notificato=Boolean.parseBoolean(bufferedReader.readLine());
		int longit=Integer.parseInt(bufferedReader.readLine());
		int lat=Integer.parseInt(bufferedReader.readLine());

		DataArray points=new DataArray(rate);
		String line;
		while((line=bufferedReader.readLine())!=null)
		{
			String[] coords=line.split(";");
			points.add(Float.parseFloat(coords[0]),
					Float.parseFloat(coords[1]),
					Float.parseFloat(coords[2]));
		}
		bufferedReader.close();
		try {
			return new FallData(fallId,data,notificato,sessionName,longit,lat,points);
		} catch (IllegalDateFormatException e) {
			e.printStackTrace();
		} catch (IllegalNumberException e) {
			e.printStackTrace();
		} catch (IllegalIdException e) {
			e.printStackTrace();
		}
		return null;
	}

	/**
	 * Salva i dati della caduta in un file specifico
	 * @param sessionId L'Id della sessione in cui è avvenuta la caduta
	 * @param data Un oggetto di tipo FallData per i dati della caduta
	 * @throws IllegalArgumentException se i parametri sono incosistenti (null o "")
	 * @throws IOException se c'è un'errore nella lettura/scrittura dei file
	 * @throws NoSuchSessionException se non esiste in memoria la sessione associata a questa caduta
	 * @throws LowSpaceException se non c'è abbastanza spazio in memoria per salvare
	 */
	public static boolean storeFallData(String sessionId,FallData data) throws NoSuchSessionException,IllegalArgumentException,IOException,LowSpaceException
	{
		if((sessionId==null)||(sessionId.length()<1)||(data==null))
			throw new IllegalArgumentException();

		//CONTENUTO AGGIUNTIVO NEL FILE INFO DELLA SESSIONE
		JSONObject json=new JSONObject();
		try{
			json.put(ID_TAG,data.getId());
			json.put(DATE_TAG,data.getDate());
			json.put(STATUS_TAG,data.isNotified());
		}catch(JSONException e)
		{
			e.printStackTrace();
			throw new IOException();
		}
		String info=json.toString();

		//CONTENUTO DEL FILE DELLA SESSIONE
		DataArray dataPoints=data.getAccelDatas();
		int pointsN=dataPoints.getRate();
		String file="";
		file+=pointsN+"\n";
		file+=data.getDate()+"\n";
		file+=data.isNotified()+"\n";
		file+=data.getLongitude()+"\n";
		file+=data.getLatitude()+"\n";
		float[] x=dataPoints.getXData();
		float[] y=dataPoints.getYData();
		float[] z=dataPoints.getZData();
		for(int i=0;i<pointsN;i++)
			file+=x[i]+";"+y[i]+";"+z[i]+"\n";

		//CONTROLLIAMO CHE CI SIA ABBASTANZA SPAZIO
		long bytesAvailable=getAvailableSpace();
		if(bytesAvailable<(info.length()+file.length()))
		{
			//non c'è abbastanza spazio
			throw new LowSpaceException(bytesAvailable,file.length()+info.length());
		}
		String pathSession=sessionsDataFolderPath+sessionId+"/";
		File mfile=new File(pathSession);
		if(!mfile.exists())
			throw new NoSuchSessionException();
		mfile=new File(pathSession+sessionId+".txt");
		if(!mfile.exists())
			throw new NoSuchSessionException();
		BufferedWriter bufferedWriter = new BufferedWriter(new FileWriter(mfile,true));
		bufferedWriter.write(info+"\n");
		bufferedWriter.flush();
		bufferedWriter.close();

		mfile=new File(pathSession+data.getId()+".txt"); //Creiamo il file
		if(!mfile.exists())
			mfile.createNewFile();
		bufferedWriter = new BufferedWriter(new FileWriter(mfile,true));
		bufferedWriter.write(file);
		bufferedWriter.flush();
		bufferedWriter.close();
		return true;
	}

	/**
	 * Prende l'immagine della sessione specificata
	 * @param sessionId L'Id della sessione
	 * @return un oggetto di tipo Bitmap dell'immagine <br>
	 * (null se l'immagine non esiste o è danneggiata)
	 * @throws IllegalArgumentException se uno dei parametri è null
	 * @throws IOException se c'è stato un'errore nella lettura
	 */
	public static Bitmap getSessionImage(Context context,String sessionId) throws IOException,IllegalArgumentException
	{
		if((context==null)||(sessionId==null))
			throw new IllegalArgumentException();

		String path=sessionImagesFolderPath+sessionId+".png";
		File imageFile=new File(path);
		if(!imageFile.exists())
			return null;
		return BitmapFactory.decodeFile(imageFile.getAbsolutePath());
	}

	/**
	 * Ferma una sessione cambiandone lo stato nei file e salvandone un'immagine
	 * @param sessionId L'id della sessione (deve essere diversa da null)
	 * @param totalDuration La durata totale dell'intera sessione (deve essere >0)
	 * @throws IllegalArgumentException Se i parametri non sono coerenti
	 * @throws NoSuchSessionException Se la sessione non esiste
	 * @throws IOException Se c'è stato un'errore nella scrittura
	 * @throws LowSpaceException Se non c'è abbastanza spazio per salvare l'immagine
	 */
	public static void stopSession(String sessionId,int totalDuration) throws LowSpaceException,NoSuchSessionException,IllegalArgumentException,IOException
	{
		if(sessionId==null||totalDuration<0)
			throw new IllegalArgumentException();
		String sessionPath=sessionsDataFolderPath+sessionId+"/";
		File sessionFile=new File(sessionPath+sessionId+".txt");
		if(!sessionFile.exists())
			throw new NoSuchSessionException();
		String finalS="";
		//Leggiamo dal file
		BufferedReader bufferedReader = new BufferedReader(new FileReader(sessionFile));
		//SESSIONE
		finalS+=bufferedReader.readLine()+"\n";//nome
		finalS+=bufferedReader.readLine()+"\n";//data
		bufferedReader.readLine();
		finalS+=totalDuration+"\n";//durata
		bufferedReader.readLine();
		finalS+="false\n";//stato di esecuzione
		String line;
		while((line=bufferedReader.readLine())!=null)
			finalS+=line+"\n";
		bufferedReader.close();
		Log.d(TAG,"STORING: "+finalS+"\nIn "+sessionId);
		BufferedWriter bufferedWriter = new BufferedWriter(new FileWriter(sessionFile));
		bufferedWriter.write(finalS);
		bufferedWriter.flush();
		bufferedWriter.close();
		storeImageForSession(sessionId);
	}

	/**
	 * Salva in memoria un'immagine identificativa per la sessione specificata
	 * @param sessionId
	 * @throws IllegalArgumentException Se i parametri sono incosistenti
	 * @throws NoSuchSessionException Se la sessione non esiste in memoria
	 * @throws LowSpaceException Se non c'è abbastanzaa spazio in memoria per l'immagine
	 * @throws IOException Se c'è stato un problema nella scrittura dei file
	 */
	private static void storeImageForSession(String sessionId) throws IllegalArgumentException,NoSuchSessionException,IOException, LowSpaceException
	{
		String todo;
		//TODO pensare ad un logaritmo
		if((sessionId==null))
			throw new IllegalArgumentException();

		File imageFile=new File(sessionImagesFolderPath);
		if(!imageFile.exists())
			imageFile.mkdirs();
		String path=sessionImagesFolderPath+sessionId+".png";
		imageFile=new File(path);
		if(!imageFile.exists())
			imageFile.createNewFile();
		int[] pixels=new int[50*50];
		for(int c=0;c<50;c++)
		{
			for(int r=0;r<50;r++)
				pixels[(r*50)+c]=0xffffff00;
		}
		Bitmap b=Bitmap.createBitmap(pixels, 50, 50, Bitmap.Config.ARGB_8888);
		storeBitmapInFile(b,imageFile.getAbsolutePath());
	}

	/**
	 * Salva una bitmap nel file al percorso specificato (creandolo se necessario)
	 * @param btm La bitmap da salvare
	 * @param filePath Il percorso in cui salvare la bitmap
	 * @throws IOException se c'è stato un'errore nella scrittura
	 * @throws LowSpaceException se non c'è abbastanza spazio in memoria
	 */
	public static void storeBitmapInFile(Bitmap btm,String filePath) throws IOException,LowSpaceException
	{
		long availableSpace=getAvailableSpace();
		int neededSpace=4*(btm.getWidth()*btm.getHeight());//spazio necessario in byte
		if(availableSpace<neededSpace)
			throw new LowSpaceException(availableSpace,neededSpace);
		File file=new File(filePath);
		if(!file.exists())
			file.createNewFile();
		file.setWritable(true,false);
		BufferedOutputStream fOut = new BufferedOutputStream(new FileOutputStream(file));
		btm.compress(Bitmap.CompressFormat.JPEG,100,fOut);
		fOut.flush();
		fOut.close();
		System.gc();
	}

	/**
	 * Aggiorna la durata di una determinata sessione
	 * @param sessionId L'id della sessione (deve essere diversa da null)
	 * @param totalDuration L'attuale durata della sessione (deve essere >0)
	 * @throws IllegalArgumentException Se i parametri non sono coerenti
	 * @throws NoSuchSessionException Se la sessione non esiste
	 * @throws IOException Se c'è stato un'errore nella scrittura
	 */
	public static void pauseSession(String sessionId,int totalDuration) throws IllegalArgumentException,IOException,NoSuchSessionException
	{
		if(sessionId==null||totalDuration<0)
			throw new IllegalArgumentException();
		String sessionPath=sessionsDataFolderPath+sessionId+"/";
		File sessionFile=new File(sessionPath+sessionId+".txt");
		if(!sessionFile.exists())
			throw new NoSuchSessionException();
		String finalS="";
		//Leggiamo dal file
		BufferedReader bufferedReader = new BufferedReader(new FileReader(sessionFile));
		//SESSIONE
		finalS+=bufferedReader.readLine()+"\n";//nome
		finalS+=bufferedReader.readLine()+"\n";//data
		bufferedReader.readLine();
		finalS+=totalDuration+"\n";//durata
		finalS+=bufferedReader.readLine()+"\n";//stato di esecuzione
		String line;
		while((line=bufferedReader.readLine())!=null)
			finalS+=line+"\n";
		bufferedReader.close();
		Log.d(TAG,"STORING: "+finalS+"\nIn "+sessionId);
		BufferedWriter bufferedWriter = new BufferedWriter(new FileWriter(sessionFile));
		bufferedWriter.write(finalS);
		bufferedWriter.flush();
		bufferedWriter.close();
	}

	/**
	 * Rinomina una sessione
	 * @param sessionId L'id della sessione (deve essere diversa da null)
	 * @param newName il nuovo nome della sessione (deve essere diverso da null)
	 * @throws IllegalArgumentException Se i parametri non sono coerenti
	 * @throws NoSuchSessionException Se la sessione non esiste
	 * @throws IOException Se c'è stato un'errore nella scrittura
	 */
	public static void renameSession(String sessionId,String newName) throws NoSuchSessionException,IOException,IllegalArgumentException
	{
		if(sessionId==null||newName==null)
			throw new IllegalArgumentException();
		String sessionPath=sessionsDataFolderPath+sessionId+"/";
		File sessionFile=new File(sessionPath+sessionId+".txt");
		if(!sessionFile.exists())
			throw new NoSuchSessionException();
		String finalS="";
		//Leggiamo dal file
		BufferedReader bufferedReader = new BufferedReader(new FileReader(sessionFile));
		//SESSIONE
		bufferedReader.readLine();
		finalS+=newName+"\n";//nome
		String line;
		while((line=bufferedReader.readLine())!=null)
			finalS+=line+"\n";
		bufferedReader.close();
		Log.d(TAG,"STORING: "+finalS+"\nIn "+sessionId);
		BufferedWriter bufferedWriter = new BufferedWriter(new FileWriter(sessionFile));
		bufferedWriter.write(finalS);
		bufferedWriter.flush();
		bufferedWriter.close();
	}

	/**
	 * Cancella una sessione
	 * @param sessionId L'id della sessione (deve essere diversa da null)
	 * @throws IllegalArgumentException Se i parametri non sono coerenti
	 * @throws NoSuchSessionException Se la sessione non esiste
	 */
	public static void deleteSession(String sessionId) throws NoSuchSessionException,IllegalArgumentException
	{
		if(sessionId==null)
			throw new IllegalArgumentException();
		String sessionPath=sessionsDataFolderPath+sessionId+"/";
		File sessionFolder=new File(sessionPath);
		if(!sessionFolder.exists())
			throw new NoSuchSessionException();
		String[] fileNames=sessionFolder.list();
		int nFiles=fileNames.length;
		for(int i=0;i<nFiles;i++)
		{
			File internalFile=new File(sessionFolder,fileNames[i]);
			if(internalFile.isFile())
			{
				internalFile.delete();
				continue;
			}
			//TODO Delete content of subfolders
		}
		sessionFolder.delete();
		String path=sessionImagesFolderPath+sessionId+".png";
		File imageFile=new File(path);
		if(!imageFile.exists())
			return;
		imageFile.delete();
	}

	private static long getAvailableSpace()
	{
		StatFs stat = new StatFs(Environment.getExternalStorageDirectory().getPath());
		return (long)stat.getBlockCount()*(long)stat.getBlockSize();
	}

	/**
	 * Prende i dati di informazione di una sessione specifica
	 * @param sessionId L'id della sessione di cui prendere i dati
	 * @return null Se i dati non ci sono o sono corrotti (o l'id non è valido)
	 */
	private static SessionInfo getSessionInfo(String sessionId)
	{
		if(sessionId==null)
			return null;
		String sessionPath=sessionsDataFolderPath+sessionId+"/";
		File sessionFile=new File(sessionPath+sessionId+".txt");
		if(!sessionFile.exists())
			return null;
		try{
			//Leggiamo dal file
			BufferedReader bufferedReader = new BufferedReader(new FileReader(sessionFile));
			//SESSIONE
			String name=bufferedReader.readLine();//nome
			String date=bufferedReader.readLine();//data
			String duration=bufferedReader.readLine();//durata
			String running=bufferedReader.readLine();//stato di esecuzione
			int nFalls=0;
			while((bufferedReader.readLine())!=null)
				nFalls++;
			bufferedReader.close();
			return new SessionInfo(sessionId,name,date,Integer.parseInt(duration),nFalls,Boolean.parseBoolean(running));
		}
		catch(IOException e){
			e.printStackTrace();
		} catch (IllegalDateFormatException e) {
			e.printStackTrace();
		} catch (IllegalNameException e) {
			e.printStackTrace();
		} catch (IllegalNumberException e) {
			e.printStackTrace();
		} catch (IllegalIdException e) {
			e.printStackTrace();
		}
		return null;
	}

	/**
	 * Salva i dati di informazione di una sessione nell'apposito file
	 * @param session I dati della sessione
	 * @return false Se i dati non sono stati salvati (poco spazio o errore di scrittura)
	 */
	private static void saveSessionInfoInFile(SessionInfo session) throws LowSpaceException,IOException
	{
		String finalS="";
		finalS+=session.getName()+"\n";//nome
		finalS+=session.getDate()+"\n";//data
		finalS+=session.getDuration()+"\n";//durata
		finalS+=session.getStatus()+"\n";//stato di esecuzione
		long aSpace=getAvailableSpace();
		int nSpace=finalS.length();
		if(aSpace<nSpace)
			throw new LowSpaceException(aSpace,nSpace);
		String sessionPath=sessionsDataFolderPath+session.getId()+"/";
		File sessionFile=new File(sessionPath);
		if(!sessionFile.exists())
			sessionFile.mkdirs();
		sessionFile=new File(sessionPath+session.getId()+".txt");
		if(!sessionFile.exists())
			sessionFile.createNewFile();
		//Scriviamo nel file
		BufferedWriter bufferedWriter = new BufferedWriter(new FileWriter(sessionFile));
		bufferedWriter.write(finalS);
		bufferedWriter.flush();
		bufferedWriter.close();
	}

	/**
	 * Aggiunge l'id di una sessione alla lista di sessioni accessibili dal file info
	 * @param session I dati della sessione
	 * @return false Se i dati non sono stati salvati (poco spazio o errore di scrittura)
	 */
	private static void saveSessionIdInInfoFile(String sessionId) throws LowSpaceException,IOException
	{
		long aSpace=getAvailableSpace();
		int nSpace=sessionId.length();
		if(aSpace<nSpace)
			throw new LowSpaceException(aSpace,nSpace);

		File infoFile=new File(infoFolderPath);		
		if(!infoFile.exists())
			infoFile.mkdirs();
		infoFile=new File(infoFolderPath+infoFileName);
		if(!infoFile.exists())
			infoFile.createNewFile();
		//Scriviamo nel file
		BufferedWriter bufferedWriter = new BufferedWriter(new FileWriter(infoFile,true));
		bufferedWriter.write(sessionId);
		bufferedWriter.flush();
		bufferedWriter.close();
	}
}