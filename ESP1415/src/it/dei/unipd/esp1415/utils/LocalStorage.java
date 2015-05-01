package it.dei.unipd.esp1415.utils;

import it.dei.unipd.esp1415.exceptions.IOException;
import it.dei.unipd.esp1415.exceptions.IllegalDateFormatException;
import it.dei.unipd.esp1415.exceptions.IllegalIdException;
import it.dei.unipd.esp1415.exceptions.IllegalNameException;
import it.dei.unipd.esp1415.exceptions.IllegalNumberException;
import it.dei.unipd.esp1415.exceptions.NoSuchSessionException;
import it.dei.unipd.esp1415.objects.AccelPoint;
import it.dei.unipd.esp1415.objects.FallData;
import it.dei.unipd.esp1415.objects.SessionData;
import it.dei.unipd.esp1415.objects.SessionInfo;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.graphics.Bitmap;
import android.os.Environment;

/**
 * A class to manage the local storage for this application
 */
public class LocalStorage {

	private final static String sessionsFolder="Working/SessionFiles/";

	/**
	 * Get a list of info for the stored sessions
	 * @param context The context that wants the info
	 * @return 
	 * -a list of the stored SessionInfo<br>
	 * (In the list of session info, the first is the most recent)<br>
	 * -a void list if there is no session stored<br>
	 * @throws IOException if there's been an error reading from files
	 * @throws IllegalArgumentException if one of the parameters is null
	 */
	public static List<SessionInfo> getSessionInfos(Context context) throws IOException,IllegalArgumentException
	{
		if((context==null))
			throw new IllegalArgumentException();
		List<SessionInfo> list=new ArrayList<SessionInfo>();
		String n=PreferenceStorage.getSimpleData(context, PreferenceStorage.N_SESSIONS);
		int number=Integer.parseInt(n);

		File sdCardDirectory = Environment.getExternalStorageDirectory();
		String pathS=sdCardDirectory+sessionsFolder;
		File fileS=new File(pathS);

		//mi basta avere il numero di files
		File[] sessionsFile=fileS.listFiles();
		int filesNumber=sessionsFile.length;

		if(number!=filesNumber)
			throw new NumberFormatException();

		if(number==0)
			return list;

		int i=0;
		int fId=1;
		File aSess;
		while(i<number)
		{
			aSess=new File(pathS+fId+".txt");
			if(aSess.exists())
			{
				//TODO Leggere il file, creare oggetto e aggiungerlo in modo push
				try{
					list.add(new SessionInfo(""+fId,"Sessione"+fId,"gg/mm/aaaa-hh:mm", 3600,fId,true));
				}
				catch(Exception e)
				{
					e.printStackTrace();
				}
				i++;
			}
			fId++;
		}
		//TODO go to the folder and read all the files
		//if there's an error in reading return null
		//else add the file data to the list
		return list;
	}

	/**
	 * Get the data of the specified session
	 * @param context The context that call this method
	 * @param sessionId The id of the session to get
	 * @return an object of the session data
	 * @throws NoSuchSessionException if this session isn't stored
	 * @throws IOException if there's been an error reading from files
	 * @throws IllegalArgumentException if one of the parameters is null
	 */
	public static SessionData getSessionData(Context context,String sessionId) throws NoSuchSessionException,IOException,IllegalArgumentException
	{
		if((context==null)||(sessionId==null))
			throw new IllegalArgumentException();
		//TODO make this method
		try {
			return (new SessionData(sessionId,"Sessione"+sessionId,"gg/mm/aaaa-hh:mm", 
					3600,0,true,null));
		} catch (IllegalDateFormatException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IllegalNameException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IllegalNumberException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IllegalIdException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}return null;
	}

	/**
	 * Get the image of the specified session
	 * @param context The context that call this method
	 * @param sessionId The id of the session
	 * @return a bitmap object of the session image <br>
	 * (return null if the bitmap doesn't exist)
	 * @throws IOException if there's been an error reading from files
	 * @throws IllegalArgumentException if one of the parameters is null
	 */
	public static Bitmap getSessionImage(Context context,String sessionId) throws IOException,IllegalArgumentException
	{
		if((context==null)||(sessionId==null))
			throw new IllegalArgumentException();
		//TODO make this method
		return null;
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
		//TODO make this method
		AccelPoint[] points=new AccelPoint[20];
		for(int i=0;i<20;i++)
			points[i]=new AccelPoint(10,10,10);
		try {
			return new FallData(fallId,"gg/mm/aaaa-hh:mm",true,0, 0, points);
		} catch (IllegalDateFormatException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IllegalNumberException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IllegalIdException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return null;
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
		if((context==null)||(sessionId==null)||(name==null)||(name.length()==0))
			throw new IllegalArgumentException();
		//TODO make this method
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
