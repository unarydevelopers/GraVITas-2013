package com.gravitas2013.app;

import java.util.ArrayList;

import com.actionbarsherlock.app.SherlockActivity;
import com.actionbarsherlock.view.Menu;
import android.os.AsyncTask;
import android.os.Bundle;
import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.database.Cursor;
import android.util.Log;

import android.view.Window;
import android.widget.ListView;

public class FeedActivity extends SherlockActivity {

	private ListView listviewFeed;
	private ArrayList<FeedList> feedList;
	BroadcastReceiver receiver;
	@Override
	protected void onPause() {
		
		this.unregisterReceiver(receiver);
		super.onPause();
		
	}
	
	
	private FeedAdapter adapterFeed;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_INDETERMINATE_PROGRESS);
		getSupportActionBar().setTitle("Live Feeds");
		setContentView(R.layout.activity_feed);
		listviewFeed = (ListView)findViewById(R.id.listAllFeed);
		feedList = new ArrayList<FeedList>();
		adapterFeed = new FeedAdapter(this, R.layout.feed_row, feedList);
		adapterFeed.notifyDataSetChanged();
		  
		 
		new getFeeds().execute();
	}
	@Override
	protected void onResume() {
		// TODO Auto-generated method stub
		super.onResume();
		receiver = new BroadcastReceiver(){

			@Override
			public void onReceive(Context context, Intent intent) {
				new getFeeds().execute();
				//adapterFeed.notifyDataSetChanged();
				Log.d("FEED","call refresh list");
			}
		};
		this.registerReceiver(receiver, new IntentFilter("RefreshList"));

		
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getSupportMenuInflater().inflate(R.menu.feed, menu);
		return true;
	}
	
	class getFeeds extends AsyncTask<Void, Void, Void>
	{
		String msg;
		int id;
		String title;
		String time;
		
		
		@Override
		protected void onPreExecute() {
			// TODO Auto-generated method stub
			super.onPreExecute();
			setProgressBarIndeterminateVisibility(true);
		}

		@Override
		protected Void doInBackground(Void... params) {
			DatabaseHandler db  = new DatabaseHandler(getApplicationContext());
			Log.d("FEED","Fetching from databse");
			Cursor cursor = db.getFeeds();
			if(cursor.moveToFirst())
			{
				do
				{
					msg = cursor.getString(cursor.getColumnIndex("msg"));
					id = cursor.getInt(cursor.getColumnIndex("eventid"));
					title = db.getEvent(id).get(0).title;
					time  = cursor.getString(cursor.getColumnIndex("time"));
					feedList.add(new FeedList(title, time, msg, db.getEvent(id).get(0).category, db.getEvent(id).get(0).color, id, db.getEvent(id).get(0).day, 0, db.getEvent(id).get(0).thumb));
				}
				while(cursor.moveToNext());
				
			}
			db.close();
			return null;
		}

		@Override
		protected void onPostExecute(Void result) {
			// TODO Auto-generated method stub
			listviewFeed.setAdapter(adapterFeed);
			adapterFeed.notifyDataSetChanged();
			setProgressBarIndeterminateVisibility(false);
			super.onPostExecute(result);
		}
		 
		
	}

}
