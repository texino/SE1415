package it.dei.unipd.esp1415.objects;

import it.dei.unipd.esp1415.exceptions.IllegalDateFormatException;
import it.dei.unipd.esp1415.exceptions.IllegalIdException;
import it.dei.unipd.esp1415.utils.Utils;

/**
 * A class for the basic information on a fall event: <br>
 *-the date when it happened
 *-the status of the notification (true if the fall has been notified)
 */
public class FallInfo {
	
	private String fallId;
	private String date;
	private boolean notified;
	
	/**
	 * Create a new object for a fall info
	 * @param id The id of the fall in this particular session<br>
	 * (It must be != null and with a length >0)
	 * @param date The date when the fall happened<br>
	 * (It must be in the format "gg/mm/aaaa-hh:mm")
	 * @param notified The notified state of the fall <br>
	 * (true if it has been notified)
	 * @throws IllegalDateFormatException if the date isn't in the correct format
	 * @throws IllegalIdException if the id isn't acceptable
	 */
	public FallInfo(String id,String date,boolean notified)throws IllegalDateFormatException,IllegalIdException
	{
		if(date==null||!Utils.checkDateFormat(date))
			throw new IllegalDateFormatException();
		if((id==null)||(id.length()<1))
			throw new IllegalIdException();
		this.fallId=id;
		this.date=date;
		this.notified=notified;
	}

	/**
	 * Get this fall id
	 * @return The fall's id
	 */
	public String getId()
	{
		return fallId;
	}
	
	/**
	 * Get the status of this fall
	 * @return
	 * -true if the fall has been notified<br>
	 * -false if the fall hasn't been notified
	 */
	public boolean isNotified()
	{
		return notified;
	}
	
	/**
	 * Get the date in when this fall have happened
	 * @return the date in when this fall have happened (in the format "gg/mm/aaaa")
	 */
	public String getDate()
	{
		return date.substring(0,10);
	}
	
	/**
	 * Get the date in when this fall have happened
	 * @return the date in when this fall have happened (in the format "hh:mm")
	 */
	public String getTime()
	{
		return date.substring(10);
	}
}