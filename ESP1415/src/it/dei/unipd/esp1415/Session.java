package it.dei.unipd.esp1415;

import android.graphics.drawable.Drawable;

public class Session {
	private String name, date, duration;
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

}