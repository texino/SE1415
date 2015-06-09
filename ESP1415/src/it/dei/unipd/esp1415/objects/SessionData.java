package it.dei.unipd.esp1415.objects;

import java.util.ArrayList;

import it.dei.unipd.esp1415.exceptions.IllegalDateFormatException;
import it.dei.unipd.esp1415.exceptions.IllegalIdException;
import it.dei.unipd.esp1415.exceptions.IllegalNameException;
import it.dei.unipd.esp1415.exceptions.IllegalNumberException;

public class SessionData extends SessionInfo{

	private ArrayList<FallInfo> fallList;

	/**
	 * Create an object for the session with all the datas
	 * @param Id The session's id <br>
	 * (It must be != null and with a length >0)
	 * @param name The name of the session<br>
	 * (It must be != null and with a length >0)
	 * @param date The date in when the session is started<br>
	 * (It must be in the format "gg/mm/aaaa-hh:mm")
	 * @param duration The duration of this session in seconds<br>
	 * (It can't be negative)
	 * @param status The status of the session (true=running, false=terminated)
	 * @param fallList An array for the list of falls happened in this session<br>
	 * (null if there is no fall in this session)
	 * @throws IllegalDateFormatException if the date format is wrong
	 * @throws IllegalNameException if the name doesn't exist
	 * @throws IllegalIdException if the id isn't acceptable
	 * @throws IllegalNumberException if "n" or "duration" are negative
	 */
	public SessionData(String id,String name,String date,int duration,boolean status,ArrayList<FallInfo> falls) 
			throws IllegalDateFormatException, IllegalNameException, IllegalNumberException ,IllegalIdException
			{
		super(id,name,date,duration,0,status);
		this.fallList=falls;
		if(fallList!=null)
			this.numberFalls=falls.size();
			}
	
	/**
	 * Get the list of falls happened in this session
	 * @return null if there is no fall
	 */
	public ArrayList<FallInfo> getFalls()
	{
		return fallList;
	}
}