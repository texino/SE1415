package it.dei.unipd.esp1415.utils;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

/**
 * A class to manage the local storage for this application
 */
public class PreferenceStorage {	
	
	/** Tag for the ratio preferences in the accelerometer*/
	public static final String ACCEL_RATIO="aRatio";
	
	/** Tag for the number of session stored*/
	public static final String N_SESSIONS="nSession";
	
	private static final String preferencesName="com.rpt.workingforelders.preferences";
	
	/**
	 * Store in the context preferences a simple value (in string format) at the specified tag
	 * If the tag already exist, the value is override
	 * @param ctx The context of the preferences
	 * @param tag The tag of the value
	 * @param value The value to store
	 */
	public static void storeSimpleData(Context ctx,String tag,String value)
	{
		SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(ctx);
		//SharedPreferences prefs=ctx.getSharedPreferences(preferencesName,Context.MODE_PRIVATE);
		SharedPreferences.Editor editor = prefs.edit();
		editor.putString(tag, value);
		editor.commit();
	}

	/**
	 * Get the value of the specified tag by the preferences called from the context
	 * @param ctx The context using the method
	 * @param tag The tag of the value
	 * @return The value searched (return "" if there's no value for that tag)
	 */
	public static String getSimpleData(Context ctx,String tag)
	{
		SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(ctx);
		//SharedPreferences prefs=ctx.getSharedPreferences(preferencesName,Context.MODE_PRIVATE);
		//SharedPreferences sp=ctx.getSharedPreferences(preferencesName,Context.MODE_PRIVATE);
		String s=prefs.getString(tag,"");
		return s;
	}
	
	/**
	 * Delete from the device the value of the specified tag
	 * (It does nothing if the value doesn't exist)
	 * @param ctx The context using the method
	 * @param tag The tag of the value to remove
	 */
	public static void deleteSimpleData(Context ctx,String tag)
	{
		SharedPreferences sp=ctx.getSharedPreferences(preferencesName,
				Context.MODE_PRIVATE);
		SharedPreferences.Editor editor = sp.edit();
		editor.remove(tag);
		editor.commit();
	}
}