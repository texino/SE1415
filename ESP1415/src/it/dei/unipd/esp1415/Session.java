package it.dei.unipd.esp1415;

import java.util.Calendar;
import java.util.GregorianCalendar;

import android.graphics.drawable.Drawable;

public class Session {
	private String name;
	private String date;
	private String duration;
	private int fallNum;
	private Drawable picture;

	// Constructor that build an object representing a session
	public Session(String name, String date, String duration, int fallNum,
			Drawable picture) {
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

	public String getDuration() {
		return duration;
	}

	public int getFalls() {
		return fallNum;
	}

	public Drawable getPicture() {
		return picture;
	}

	// public method to create a string of the actually date and time
	public static String getDateHour() {
		String date = "";
		String month= "";
		String day= "";
		String hour= "";
		String minute= "";
		
		//calendar is created
		GregorianCalendar gc = new GregorianCalendar();
		
		//next steps are getting calendar information
		//some voices have to be formatted in the form 0X
		int year = gc.get(Calendar.YEAR);
		int intMonth = gc.get(Calendar.MONTH) + 1;// addition, out of phase otherwise
		if (intMonth < 10) month = fixDate(intMonth);
		else month += intMonth;
		int intDay = gc.get(Calendar.DATE);
		if (intDay < 10) day = fixDate(intDay);
		else day += intDay;
		int intHour = gc.get(Calendar.HOUR_OF_DAY);
		if (intHour < 10) hour = fixDate(intHour);
		else hour += intHour;
		int intMinute = gc.get(Calendar.MINUTE);
		if (intMinute < 10) minute = fixDate(intMinute);
		else minute += intMinute;
		// int second = gc.get(Calendar.SECOND); FIXME need this?
		date = day + "/" + month + "/" + year + " " + hour + ":" + minute;
		return date;
	}
	//this method fix date format (X to 0X)
	public static String fixDate(int date){
		return "0" + date;
	}
}