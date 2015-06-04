package it.dei.unipd.esp1415.views;

import it.dei.unipd.esp1415.Settings;

import java.util.Calendar;

import android.app.Dialog;
import android.app.TimePickerDialog;
import android.text.format.DateFormat;
import android.widget.TimePicker;

public class MyDialogPreference extends Dialog implements TimePickerDialog.OnTimeSetListener{
    
	protected Dialog onCreateDialog(int id) {
        switch (id) {
        case Settings.TIME_DIALOG_ID:
        	// Use the current time as the default time in the picker
            final Calendar c = Calendar.getInstance();
            int hour = c.get(Calendar.HOUR_OF_DAY);
            int min = c. get(Calendar.MINUTE);
            return new TimePickerDialog(getActivity(), this, hour, min, DateFormat.is24HourFormat(getActivity()));
            /*return new TimePickerDialog(this,
                    mTimeSetListener, pHour, pMinute, false);
        */}
        return null;
    }
	/*@Override
    protected Dialog onCreateDialog(int id) {
        // Use the current time as the default time in the picker
        final Calendar c = Calendar.getInstance();
        int hour = c.get(Calendar.HOUR_OF_DAY);
        int min = c. get(Calendar.MINUTE);
        int year = c.get(Calendar.YEAR);
        int month = c.get(Calendar.MONTH);
        int day = c.get(Calendar.DAY_OF_MONTH);

        // Create a new instance of DatePickerDialog and return it
        return new TimePickerDialog(getActivity(), this, hour, min, DateFormat.is24HourFormat(getActivity()));
    }*/

    public void onTimeSet (TimePicker view, int hourOfDay, int minute){
    	// Do something with the time chosen by the user
    }
}
