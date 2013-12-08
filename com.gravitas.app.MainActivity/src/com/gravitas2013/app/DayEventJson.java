package com.gravitas2013.app;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.util.Log;

public class DayEventJson {
	JSONObject data;
	JSONArray cash_prize;
	JSONArray coordinators;
	public int id;
	public String color;
	public String title;
	public String eventLocation;
	public String desc;
	public String rule;
	public String time;
	public String[] coordName;
	public String[] coordPhone; 
	public String[] coordEmail;
	public String[] prize;
	public int day;
	public String category;
	public int price;
	public String thumb;
	public String cover;

	public DayEventJson()
	{
		this.prize = new String[5];
	}
	public DayEventJson(JSONObject data, int day)
	{
		this.data = data;
		this.day = day;
		//this.prize = new String[3];
		fillData();
	}

	private void fillData() {
		try {
			id = data.getInt("id");
			try {
				title = URLDecoder.decode(data.getString("title"), "UTF-8");
				title = title.replaceAll("&rsquo;", "'");
				title = title.replaceAll("<br>", "");
				title = title.replaceAll("<br/>", "");
				title = title.replaceAll("<br />", "");

			} catch (UnsupportedEncodingException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
			try {
				desc = URLDecoder.decode(data.getString("desp"), "UTF-8");
				desc = desc.replaceAll("<br>", "\n");
				desc = desc.replaceAll("<br/>", "\n");
				desc = desc.replaceAll("<br />", "\n");
				desc = desc.replaceAll("%92", "");
				desc = desc.replaceAll("<i>", "");
				desc = desc.replaceAll("</i>", "");
			} catch (UnsupportedEncodingException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			cash_prize = data.getJSONArray("prize");
			if(cash_prize.length() > 3)
				prize = new String[cash_prize.length()];
			else
				prize = new String[3];
			for(int i = 0; i < cash_prize.length(); i++)
			{
				Log.d("I", "Event: " + title +"Length: " + cash_prize.length() + " I: " + i );
				prize[i] = cash_prize.getString(i);
			}

			rule = data.getString("rules");
			time = data.getString("time");
			eventLocation = data.getString("venue");
			coordinators = data.getJSONArray("coordinators");
			coordName = new String[3];
			coordEmail = new String[3];
			coordPhone = new String[3];
			for(int i = 0; i < coordinators.length(); i++)
			{
				coordName[i] = coordinators.getJSONObject(i).getString("name");
				coordEmail[i] = coordinators.getJSONObject(i).getString("email");
				coordPhone[i] = coordinators.getJSONObject(i).getString("phone");
			}
			category = data.getString("category");
			price = data.getInt("price");
			thumb = data.getString("thumb");
			cover = data.getString("cover");
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}	
	}
}


