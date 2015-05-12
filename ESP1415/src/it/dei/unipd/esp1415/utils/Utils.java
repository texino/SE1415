package it.dei.unipd.esp1415.utils;

import java.util.Calendar;
import java.util.GregorianCalendar;

public class Utils {
	
	/**
	 * Check if the date syntax is correct
	 * @param date The string date to check
	 * @return
	 * -true If the date is in the correct format ("gg/mm/aaaa-hh:mm")
	 * -false If the date isn't in the correct format
	 */
	public static boolean checkDateFormat(String date)
	{
		int length=date.length();
		if(length!=16)
			return false;
		//TODO further accurate this check
		String a;
		return true;
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
			date = day + "/" + month + "/" + year + "\n" + hour + ":" + minute;
			return date;
		}
		//this method fix date format (X to 0X)
		public static String fixDate(int date){
			return "0" + date;
		}

}
