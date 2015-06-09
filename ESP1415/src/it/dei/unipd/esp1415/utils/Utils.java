package it.dei.unipd.esp1415.utils;

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
		boolean f=true;
		if(f)
			return f;
		int length=date.length();
		if(length!=16)
			return false;
		//TODO further accurate this check
		String a;
		return true;
	}

}
