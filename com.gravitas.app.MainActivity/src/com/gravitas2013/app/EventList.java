package com.gravitas2013.app;

import java.net.URI;

import android.graphics.Bitmap;

public class EventList implements Comparable<EventList> {

	public String eventName;
	public String time;
	public String location;
	public String category;
	public String color;
	public int id;
	public int day;
	public int alarmStatus;
	public Bitmap icon;
	public String thumb;

	public EventList()
	{
		super();
	}

	public EventList(String eventName, String time, String location, String category, int id, int day, int alarmStatus, String thumbUrl, String color)
	{
		super();
		this.eventName = eventName;
		this.location = location;
		this.time = time;
		this.category = category;
		this.id = id;
		this.day = day;
		this.alarmStatus = alarmStatus;
		this.thumb = thumbUrl;
		this.color = color;
	}

	@Override
	public int compareTo(EventList another) {
		// TODO Auto-generated method stub
		return eventName.compareTo(another.eventName);
	}
}
