package it.dei.unipd.esp1415.objects;

import it.dei.unipd.esp1415.exceptions.IllegalDateFormatException;
import it.dei.unipd.esp1415.exceptions.IllegalIdException;
import it.dei.unipd.esp1415.exceptions.IllegalNumberException;

/**
 * A class for a fall event with:
 * -date
 * -notified status
 * -latitude,longitude
 * -1s worth of accelerometer data
 */
public class FallData extends FallInfo{

	private String sessionName;
	private int latitude,longitude;
	private AccelPoint[] data;

	/**
	 * Create a new object for a fall data
	 * @param id The id of the fall in this particular session<br>
	 * (It must be != null and with a length >0)
	 * @param date The date when the fall happened<br>
	 * (It must be in the format "gg/mm/aaaa-hh:mm")
	 * @param notified The notified state of the fall <br>
	 * (true if it has been notified)
	 * @param latitude The latitude in where happened the fall
	 * @param longitude The longitude in where happened the fall
	 * @throws IllegalDateFormatException if the date isn't in the correct format
	 * @throws IllegalNumberException if latitude or longitude are negative
	 * @throws IllegalIdException if the id isn't acceptable
	 * @throws IllegalArgumentException if points is null
	 */
	public FallData(String id,String date, boolean notified,int latitude,int longitude,AccelPoint[] points)
			throws IllegalDateFormatException,IllegalNumberException,IllegalIdException,IllegalArgumentException {
		super(id,date,notified);
		if(latitude<0||longitude<0)
			throw new IllegalNumberException();
		if(points==null)
			throw new IllegalArgumentException("Accelerometer data haven't been passed successfully");
		this.latitude=latitude;
		this.longitude=longitude;
		data=points;
	}

	/**
	 * Get the name of the session of this fall event
	 * @return the name of the session
	 */
	public String getSessionName()
	{
		return sessionName;
	}
	
	/**
	 * Get the collected accelerometer data for this fall event
	 * @return an array of AccelPoint
	 */
	public AccelPoint[] getAccelDatas()
	{
		return data;
	}
	
	/**
	 * Get the latitude for this fall
	 * @return the latitude for this fall
	 */
	public int getLatitude()
	{
		return latitude;
	}
	
	/**
	 * Get the longitude for this fall
	 * @return the longitude for this fall
	 */
	public int getLongitude()
	{
		return longitude;
	}
}
