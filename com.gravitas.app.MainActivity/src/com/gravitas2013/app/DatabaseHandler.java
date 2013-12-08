package com.gravitas2013.app;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

public class DatabaseHandler extends SQLiteOpenHelper {
	Context context;
	private static String makeTable = "CREATE TABLE IF NOT EXISTS main"+"" +
			" (id NUMBER, day NUMBER, title TEXT, desp TEXT, prize1 TEXT, prize2 TEXT,"+
			" prize3 TEXT, rule TEXT, time TEXT, venue TEXT, coord1_name TEXT,"+
			" coord2_name TEXT, coord3_name TEXT, coord1_email TEXT, "+
			"coord2_email TEXT, coord3_email TEXT, coord1_phone NUMBER, "+
			"coord2_phone NUMBER, coord3_phone NUMBER, category TEXT, price NUMBER, color TEXT, cid NUMBER," + 
			"thumb TEXT, cover TEXT, bell NUMBER)";
	private static final int DATABASE_VERSION = 1;
	private static final String DATABASE_NAME = "new1234";
	private static final String feedTable = "CREATE TABLE IF NOT EXISTS feed" + 
	"(eventid NUMBER, msg TEXT, time TEXT )";
	
	public DatabaseHandler(Context context) {
		
		super(context, DATABASE_NAME, null, DATABASE_VERSION);
		this.context = context;
		// TODO Auto-generated constructor stub
	}
	public void addFeed(int eventid, String msg, String time)
	{
		Calendar cal = Calendar.getInstance();
		SQLiteDatabase db = this.getWritableDatabase();
		ContentValues values= new ContentValues();
		values.put("eventid", eventid);
		values.put("msg", msg);
		values.put("time", cal.getTime().toString());
		db.insert("feed", null, values);
		db.close();
	}
	
	public Cursor getFeeds()
	{
		SQLiteDatabase db = this.getReadableDatabase();
		String query = "SELECT * FROM feed";
		Cursor cursor = db.rawQuery(query,null);
		return cursor;
	}
	
	public void setAlarm(int id, int status)
	{
		SQLiteDatabase db = this.getWritableDatabase();
		String query = "UPDATE main SET bell=" + status + " WHERE id=" + id;
		db.execSQL(query);
	}
	public int getAlarm(int id)
	{
		String query = "SELECT bell FROM main WHERE id=" + id;
		Log.d("Query", query);
		SQLiteDatabase db = this.getReadableDatabase();
		Cursor cursor = db.rawQuery(query, null);
		if(cursor.moveToFirst())
		{
			return Integer.parseInt(cursor.getString(cursor.getColumnIndex("bell")));
		}
		else
		{
			return 0;
		}
		
	}
	@Override
	public void onCreate(SQLiteDatabase arg0) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onUpgrade(SQLiteDatabase arg0, int arg1, int arg2) {
		// TODO Auto-generated method stub
		
	}
	
	public void createTable()
	{
		SQLiteDatabase db = this.getWritableDatabase();
		Log.d("Query", makeTable);
		db.execSQL(makeTable);
		db.execSQL(feedTable);
	}
	
	public void addEvent(DayEventJson dayEvent)
	{
		/*if(dayEvent.id == 79 || dayEvent.id == 90)
			return;*/
		SQLiteDatabase db = this.getWritableDatabase();
		ContentValues values_main= new ContentValues();
		Log.d("Here", dayEvent.id + dayEvent.title);
		values_main.put("id", dayEvent.id);
		values_main.put("day", dayEvent.day);
		values_main.put("title", dayEvent.title);
		values_main.put("desp", dayEvent.desc);
		values_main.put("prize1", dayEvent.prize[0]);
		values_main.put("prize2", dayEvent.prize[1]);
		values_main.put("prize3", dayEvent.prize[2]);
		values_main.put("rule", dayEvent.rule);
		values_main.put("time", dayEvent.time);
		values_main.put("venue", dayEvent.eventLocation);
		values_main.put("coord1_name", dayEvent.coordName[0]);
		values_main.put("coord2_name", dayEvent.coordName[1]);
		values_main.put("coord3_name", dayEvent.coordName[2]);
		values_main.put("coord1_email", dayEvent.coordEmail[0]);
		values_main.put("coord2_email", dayEvent.coordEmail[1]);
		values_main.put("coord3_email", dayEvent.coordEmail[2]);
		values_main.put("coord1_phone", dayEvent.coordPhone[0]);
		values_main.put("coord2_phone", dayEvent.coordPhone[1]);
		values_main.put("coord3_phone", dayEvent.coordPhone[2]);
		values_main.put("category", dayEvent.category);
		values_main.put("price", dayEvent.price);
		values_main.put("bell", 0);
		
		try{
		Log.d("Url Fetch", dayEvent.thumb);
		values_main.put("thumb", dayEvent.thumb);
		}
		catch(NullPointerException e){
			values_main.put("thumb", "stock");
			//Toast.makeText(context, dayEvent.thumb, Toast.LENGTH_LONG).show();
		}
		try{
			values_main.put("cover", dayEvent.cover);
		}
		catch(NullPointerException e){
			values_main.put("cover", "stockcover");
		}
		
		if(dayEvent.category.equals("Applied Engineering"))
		{
			values_main.put("color", "#fa6900");
			values_main.put("cid", 0);
		}
		else if(dayEvent.category.equals("Bioxyn"))
		{
			values_main.put("color", "#60a917");
			values_main.put("cid", 1);
		}
		else if(dayEvent.category.equals("Builtrix"))
		{
			values_main.put("color", "#6a01fe");
			values_main.put("cid", 2);
		}
		else if(dayEvent.category.equals("Informals"))
		{
			values_main.put("color", "#a20025");
			values_main.put("cid", 3);
		}
		else if(dayEvent.category.equals("Online"))
		{
			values_main.put("color", "#008a00");
			values_main.put("cid", 4);
		}
		else if(dayEvent.category.equals("Electrical"))
		{
			values_main.put("color", "#a6a6a6");
			values_main.put("cid", 5);
		}
		else if(dayEvent.category.equals("Robotix"))
		{
			values_main.put("color", "#647687");
			values_main.put("cid", 6);
		}
		else if(dayEvent.category.equals("Management"))
		{
			values_main.put("color", "#f1a30b");
			values_main.put("cid", 7);
		}
		else if(dayEvent.category.equals("Computer"))
		{
			values_main.put("color", "#258dcc");
			values_main.put("cid", 8);
		}
		else if(dayEvent.category.equals("Premium"))
		{
			values_main.put("cid", 9);
			values_main.put("color", "#d8c100");
		}
		else if(dayEvent.category.equals("Quizzes"))
		{
			values_main.put("color", "#0050ef");
			values_main.put("cid", 10);
		}
		else if(dayEvent.category.equals("Paper Presentation"))
		{
			values_main.put("color", "#00aba9");
			values_main.put("cid", 11);
		}
		else
		{
			Log.d("Category", dayEvent.category);
			values_main.put("color", "#000000");
			values_main.put("cid", 12);
		}
		db.insert("main", null, values_main);
	}
	
	public List<DayEventJson> getEvents(int day)
	{
		String query = "SELECT * FROM main WHERE day="+day;
		SQLiteDatabase db = this.getWritableDatabase();
		List<DayEventJson> data =new ArrayList<DayEventJson>();
		Cursor cursor = db.rawQuery(query, null);
		if(cursor.moveToFirst())
		{
			do
			{
				DayEventJson event = new DayEventJson();
				event.id = Integer.parseInt(cursor.getString(cursor.getColumnIndex("id")));
				event.day = day;
				event.category = cursor.getString(cursor.getColumnIndex("category"));
				event.title = cursor.getString(cursor.getColumnIndex("title"));
				event.time = cursor.getString(cursor.getColumnIndex("time"));
				event.desc = cursor.getString(cursor.getColumnIndex("desp"));
				event.eventLocation = cursor.getString(cursor.getColumnIndex("venue"));
				event.prize[0] = cursor.getString(cursor.getColumnIndex("prize1"));
				event.prize[1] = cursor.getString(cursor.getColumnIndex("prize2"));
				event.prize[2] = cursor.getString(cursor.getColumnIndex("prize3"));
				event.color = cursor.getString(cursor.getColumnIndex("color"));
				event.thumb = cursor.getString(cursor.getColumnIndex("thumb"));
				event.cover = cursor.getString(cursor.getColumnIndex("cover"));
				data.add(event);
				
				
				
			}
			while(cursor.moveToNext());
		}

		return data;
	}
	
	public List<DayEventJson> getAllEvents()
	{
		String query = "SELECT * FROM main";
		SQLiteDatabase db = this.getWritableDatabase();
		List<DayEventJson> data =new ArrayList<DayEventJson>();
		Cursor cursor = db.rawQuery(query, null);
		if(cursor.moveToFirst())
		{
			do
			{
				DayEventJson event = new DayEventJson();
				event.id = Integer.parseInt(cursor.getString(cursor.getColumnIndex("id")));
				event.category = cursor.getString(cursor.getColumnIndex("category"));
				event.title = cursor.getString(cursor.getColumnIndex("title"));
				event.time = cursor.getString(cursor.getColumnIndex("time"));
				event.desc = cursor.getString(cursor.getColumnIndex("desp"));
				event.eventLocation = cursor.getString(cursor.getColumnIndex("venue"));
				event.prize[0] = cursor.getString(cursor.getColumnIndex("prize1"));
				event.prize[1] = cursor.getString(cursor.getColumnIndex("prize2"));
				event.prize[2] = cursor.getString(cursor.getColumnIndex("prize3"));
				event.color = cursor.getString(cursor.getColumnIndex("color"));
				event.thumb = cursor.getString(cursor.getColumnIndex("thumb"));
				event.cover = cursor.getString(cursor.getColumnIndex("cover"));
				data.add(event);				
			}
			while(cursor.moveToNext());
		}
		return data;
	}
	
	public List<DayEventJson> getEventsCate(int cid)
	{
		String query = "SELECT * FROM main WHERE cid="+cid;
		SQLiteDatabase db = this.getWritableDatabase();
		List<DayEventJson> data =new ArrayList<DayEventJson>();
		Cursor cursor = db.rawQuery(query, null);
		if(cursor.moveToFirst())
		{
			do
			{
				DayEventJson event = new DayEventJson();
				event.id = Integer.parseInt(cursor.getString(cursor.getColumnIndex("id")));
				event.category = cursor.getString(cursor.getColumnIndex("category"));
				event.title = cursor.getString(cursor.getColumnIndex("title"));
				event.time = cursor.getString(cursor.getColumnIndex("time"));
				event.desc = cursor.getString(cursor.getColumnIndex("desp"));
				event.eventLocation = cursor.getString(cursor.getColumnIndex("venue"));
				event.prize[0] = cursor.getString(cursor.getColumnIndex("prize1"));
				event.prize[1] = cursor.getString(cursor.getColumnIndex("prize2"));
				event.prize[2] = cursor.getString(cursor.getColumnIndex("prize3"));
				event.color = cursor.getString(cursor.getColumnIndex("color"));
				event.thumb = cursor.getString(cursor.getColumnIndex("thumb"));
				event.cover = cursor.getString(cursor.getColumnIndex("cover"));
				
				data.add(event);
			}
			while(cursor.moveToNext());
		}

		return data;
	}
	
	public List<DayEventJson> getEvent(int day, int id)
	{
		String query = "SELECT * FROM main WHERE day="+day+" AND id="+id;
		SQLiteDatabase db = this.getWritableDatabase();
		List<DayEventJson> data =new ArrayList<DayEventJson>();
		Cursor cursor = db.rawQuery(query, null);
		if(cursor.moveToFirst())
		{
			do
			{
				DayEventJson event = new DayEventJson();
				event.id = Integer.parseInt(cursor.getString(cursor.getColumnIndex("id")));
				event.day = day;
				event.title = cursor.getString(cursor.getColumnIndex("title"));
				event.time = cursor.getString(cursor.getColumnIndex("time"));
				event.desc = cursor.getString(cursor.getColumnIndex("desp"));
				event.eventLocation = cursor.getString(cursor.getColumnIndex("venue"));
				event.prize[0] = cursor.getString(cursor.getColumnIndex("prize1"));
				event.prize[1] = cursor.getString(cursor.getColumnIndex("prize2"));
				event.prize[2] = cursor.getString(cursor.getColumnIndex("prize3"));
				event.color = cursor.getString(cursor.getColumnIndex("color"));
				event.thumb = cursor.getString(cursor.getColumnIndex("thumb"));
				event.cover = cursor.getString(cursor.getColumnIndex("cover"));
				
				data.add(event);
				
				
				
			}
			while(cursor.moveToNext());
		}

		return data;

	}
	
	public List<DayEventJson> getEvent(int id)
	{
		String query = "SELECT * FROM main WHERE id="+id;
		SQLiteDatabase db = this.getWritableDatabase();
		List<DayEventJson> data =new ArrayList<DayEventJson>();
		Cursor cursor = db.rawQuery(query, null);
		if(cursor.moveToFirst())
		{
			do
			{
				DayEventJson event = new DayEventJson();
				event.id = Integer.parseInt(cursor.getString(cursor.getColumnIndex("id")));
				event.day = Integer.parseInt(cursor.getString(cursor.getColumnIndex("day")));
				event.title = cursor.getString(cursor.getColumnIndex("title"));
				event.time = cursor.getString(cursor.getColumnIndex("time"));
				event.desc = cursor.getString(cursor.getColumnIndex("desp"));
				event.eventLocation = cursor.getString(cursor.getColumnIndex("venue"));
				event.prize[0] = cursor.getString(cursor.getColumnIndex("prize1"));
				event.prize[1] = cursor.getString(cursor.getColumnIndex("prize2"));
				event.prize[2] = cursor.getString(cursor.getColumnIndex("prize3"));
				event.color = cursor.getString(cursor.getColumnIndex("color"));
				event.thumb = cursor.getString(cursor.getColumnIndex("thumb"));
				event.cover = cursor.getString(cursor.getColumnIndex("cover"));
				
				data.add(event);
				
				
				
			}
			while(cursor.moveToNext());
		}

		return data;

	}
	
	public Cursor listTable()
	{
		SQLiteDatabase db = this.getWritableDatabase();
		Cursor table = db.rawQuery("SELECT name _id FROM sqlite_master WHERE type = 'table' ", null);
		if(table.moveToFirst())
		{
			Log.d("Table List", "Not Null " + table.getCount());
			do
			{
				Log.d("Table Name", table.getString(0));
			}
			while(table.moveToNext());
			
			return table;

			
		}
		return null;
	}
	
	public void deleteTable()
	{
		SQLiteDatabase db = this.getWritableDatabase();
		String query1 = "DROP TABLE IF EXISTS main";
		String query2 = "DROP TABLE IF EXISTS feed";
		db.execSQL(query1);
		db.execSQL(query2);
	}
}
