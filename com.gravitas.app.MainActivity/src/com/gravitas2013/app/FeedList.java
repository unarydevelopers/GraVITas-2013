package com.gravitas2013.app;

import java.net.URI;

import android.graphics.Bitmap;

public class FeedList{

	public String feedTitle;
	public String feedTime;
	public String feedText;
	public String category;
	public String color;
	public int id;
	public int day;
	public int alarmStatus;
	public String thumb;
	public FeedList(String feedTitle, String feedTime, String feedText,
			String category, String color, int id, int day, int alarmStatus, String thumb) {
		super();
		this.feedTitle = feedTitle;
		this.feedTime = feedTime;
		this.feedText = feedText;
		this.category = category;
		this.color = color;
		this.id = id;
		this.day = day;
		this.alarmStatus = alarmStatus;
		this.thumb = thumb;
	}
	
}
