package it.dei.unipd.esp1415.objects;

import it.dei.unipd.esp1415.exceptions.IllegalDateFormatException;
import it.dei.unipd.esp1415.exceptions.IllegalIdException;
import it.dei.unipd.esp1415.exceptions.IllegalNameException;
import it.dei.unipd.esp1415.exceptions.IllegalNumberException;
import it.dei.unipd.esp1415.utils.Utils;

/**
 * A class for the primary values of a session: <br>
 * -Name
 * -Date&Time
 * -Duration
 * -n° of falls
 * -status (running or terminated)
 */
public class SessionInfo {
	
	private String sessionId;
	private String sessionName,sessionDate;
	private int numberFalls,sessionDuration;
	private boolean running;
	
	/**
	 * Create an object for the session with only the initial data
	 * @param Id The session's id <br>
	 * (It must be != null and with a length >0)
	 * @param name The name of the session<br>
	 * (It must be != null and with a length >0)
	 * @param date The date in when the session is started<br>
	 * (It must be in the format "gg/mm/aaaa-hh:mm")
	 * @param duration The duration of this session in seconds<br>
	 * (It can't be negative)
	 * @param n The number of falls in this session<br>
	 * (It can't be negative)
	 * @param status The status of the session (true=running, false=terminated)
	 * @throws IllegalDateFormatException if the date format is wrong
	 * @throws IllegalNameException if the name doesn't exist
	 * @throws IllegalIdException if the id isn't acceptable
	 * @throws IllegalNumberException if "n" or "duration" are negative
	 */
	public SessionInfo(String id,String name,String date,int duration,int n,boolean status) 
			throws IllegalDateFormatException, IllegalNameException, IllegalNumberException ,IllegalIdException
	{
		if((id==null)||(id.length()<1))
			throw new IllegalIdException();
		if((date==null)||(!Utils.checkDateFormat(date)))
			throw new IllegalDateFormatException();
		if((name==null)||(name.length()<1))
			throw new IllegalNumberException();
		if(n<0||duration<0)
			throw new IllegalNumberException();
		this.sessionId=id;
		this.sessionName=name;
		this.sessionDate=date;
		this.numberFalls=n;
		this.running=status;
		this.sessionDuration=duration;
	}

	/**
	 * Get this session's Id
	 * @return this session's id
	 */
	public String getId()
	{
		return sessionId;
	}
	
	/**
	 * Get the name of this session
	 * @return the name of this session
	 */
	public String getName()
	{
		return sessionName;
	}
	
	/**
	 * Get the date in when this session is created
	 * @return the date in when this session is created ("gg/mm/aaaa-hh:mm")
	 */
	public String getDate()
	{
		return sessionDate;
	}
	
	/**
	 * Get the number of falls
	 * @return the number of falls in this session
	 */
	public int getNumberOfFalls()
	{
		return numberFalls;
	}
	
	/**
	 * Get the status of this session
	 * @return 
	 * -true if this session must yet be terminated<br>
	 * -false if this session is terminated
	 */
	public boolean getStatus()
	{
		return running;
	}
	
	/**
	 * Get the duration of this session
	 * @return the duration in seconds
	 */
	public int getDuration()
	{
		return sessionDuration;
	}
}