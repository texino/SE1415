package it.dei.unipd.esp1415;

import java.util.Calendar;
import java.util.GregorianCalendar;

public class Session {
	private String name;
	private String date;
	private int duration;
	private int fallNum;
	private int picture;

	// Constructor that build an object representing a session
	public Session(String name, String date, int duration, int fallNum,
			int picture) {
		super();
		this.name = name;
		this.date = date;
		this.duration = duration;
		this.fallNum = fallNum;
		this.picture = picture;
	}

	// next methods return single values from the session
	public String getName() {
		return name;
	}

	public String getDate() {
		return date;
	}

	public int getDuration() {
		return duration;
	}

	public int getFalls() {
		return fallNum;
	}

	public int getPicture() {
		return picture;
	}

	// public method to create a string of the actually date and time
	public static String getDateHour() {
		String date = "";
		GregorianCalendar gc = new GregorianCalendar();
		int year = gc.get(Calendar.YEAR);
		int month = gc.get(Calendar.MONTH) + 1;// addition, out of phase
												// otherwise
		int day = gc.get(Calendar.DATE);
		int hour = gc.get(Calendar.HOUR);
		int minute = gc.get(Calendar.MINUTE);
		// int second = gc.get(Calendar.SECOND); FIXME
		date = day + "/" + month + "/" + year + " " + hour + ":" + minute;
		return date;
	}
}
