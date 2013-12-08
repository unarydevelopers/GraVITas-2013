package com.gravitas2013.app;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.StatusLine;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import static com.gravitas2013.app.gcmservice.CommonUtilities.DISPLAY_MESSAGE_ACTION;
import static com.gravitas2013.app.gcmservice.CommonUtilities.EXTRA_MESSAGE;
import static com.gravitas2013.app.gcmservice.CommonUtilities.SENDER_ID;


import com.actionbarsherlock.app.SherlockActivity;
import com.actionbarsherlock.app.ActionBarDrawerToggle;
import com.actionbarsherlock.view.MenuItem;
import com.google.android.gcm.GCMRegistrar;
import com.gravitas2013.app.R;	
import com.gravitas2013.app.EventAdapter.EventHolder;
import com.gravitas2013.app.admin.AdminActivity;
import com.gravitas2013.app.gcmservice.ServerUtilities;
import com.gravitas2013.app.rightlist.RightItem;
import com.gravitas2013.app.rightlist.RightListAdapter;

import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.content.res.Configuration;
import android.graphics.Bitmap;

import android.support.v4.widget.DrawerLayout;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.AdapterView.OnItemClickListener;

public class MainActivity extends SherlockActivity {

	SharedPreferences pref;
	private ListAdapter adapterList = null;
	ListView listview = null;
	ListView listViewEvent = null;
	DatabaseHandler db;
	List<DayEventJson> data;

	EventAdapter adapterEvent;
	ArrayList<EventList> event;
	//TextView eventDay;
	int day;
	Bitmap icon;
	Bitmap newIcon;

	AsyncTask<Void, Void, Void> mRegisterTask;
	private DrawerLayout mDrawerLayout;
	private ActionBarDrawerToggle mDrawerToggle;
	private CharSequence mDrawerTitle;
	private CharSequence mTitle;
	private RelativeLayout mDrawerList;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_INDETERMINATE_PROGRESS);
		setContentView(R.layout.activity_main); 
		mDrawerList = (RelativeLayout) findViewById(R.id.left_drawer);

		mTitle = mDrawerTitle = getTitle();
		mDrawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
		mDrawerToggle = new ActionBarDrawerToggle(
				this,                  /* host Activity */
				getSupportActionBar(),
				mDrawerLayout,         /* DrawerLayout object */
				R.drawable.ic_action_menu_icon,  /* nav drawer icon to replace 'Up' caret */
				R.string.admin_panel,  /* "open drawer" description */
				R.string.by  /* "close drawer" description */
				) {

			/** Called when a drawer has settled in a completely closed state. */
			public void onDrawerClosed(View view) {
				supportInvalidateOptionsMenu();
			}

			/** Called when a drawer has settled in a completely open state. */
			public void onDrawerOpened(View drawerView) {
				//getSupportActionBar().setTitle(mDrawerTitle);
				
				supportInvalidateOptionsMenu();
			}
		};

		// Set the drawer toggle as the DrawerListener
		mDrawerLayout.setDrawerListener(mDrawerToggle);
		getSupportActionBar().setDisplayHomeAsUpEnabled(true);
		getSupportActionBar().setHomeButtonEnabled(true);
		db = new DatabaseHandler(this);

		//Setting Version
		pref = getApplicationContext().getSharedPreferences("MyPref", 0);
		final Editor editor = pref.edit();
		//editor.putString("old_token", "-1");
		//editor.putInt("vers", -1);
		//editor.putInt("DAY", 0);
		//editor.commit();
		if(pref.getInt("vers", -1) == -1)
		{
			db.createTable();



			editor.putInt("vers", 0);
			editor.putInt("DAY", 0);
			editor.commit();
		}
		if(pref.getInt("vers", -1) > 0)
		{
			day = 1;
			getSupportActionBar().setTitle("Day 1");
			data =new ArrayList<DayEventJson>();
			data = db.getEvents(day);
			event = new ArrayList<EventList>();
			for(int i =0; i < data.size(); i++)
			{
				DayEventJson newEvent = data.get(i);

				event.add(new EventList(newEvent.title, newEvent.time, newEvent.eventLocation, newEvent.category, newEvent.id, day, db.getAlarm(newEvent.id), newEvent.thumb, newEvent.color));
			}
			adapterEvent = new EventAdapter(this, R.layout.test, event);
			listViewEvent = (ListView)findViewById(R.id.eventList);
			Collections.sort(event);
			listViewEvent.setAdapter(adapterEvent);

			if(isNetworkConnected())
			{
				//updater.setVisibility(View.VISIBLE);
				Fetch fetch = new Fetch(this, new Handler(), adapterEvent, event);

				fetch.execute();
			}

		}
		else
		{
			//updater.setVisibility(View.VISIBLE);
			//Toast.makeText(this, "Version: " + pref.getInt("vers", -1), //Toast.LENGTH_SHORT).show();
			event = new ArrayList<EventList>();
			adapterEvent = new EventAdapter(this, R.layout.test, event);
			listViewEvent = (ListView)findViewById(R.id.eventList);
			listViewEvent.setAdapter(adapterEvent);
			if(!isNetworkConnected())
			{
				setContentView(R.layout.no_network);
				return;
			}
			Fetch fetch = new Fetch(this, new Handler(), adapterEvent, event);
			fetch.execute();
		}
		//Adding items to Right ListView
		ArrayList<RightItem> rItem = new ArrayList<RightItem>();
		rItem.add(new RightItem(0, "Applied Engineering", "#fa6900"));
		rItem.add(new RightItem(1, "Bioxyn", "#60a917"));
		rItem.add(new RightItem(2, "Builtrix", "#6a01fe"));
		rItem.add(new RightItem(3, "Informals", "#a20025"));
		rItem.add(new RightItem(4, "Online", "#008a00"));
		rItem.add(new RightItem(5, "Electrical", "#a6a6a6"));
		rItem.add(new RightItem(6, "Robotix", "#647687"));
		rItem.add(new RightItem(7, "Management", "#f1a30b"));
		rItem.add(new RightItem(8, "Computer", "#258dcc"));
		rItem.add(new RightItem(9, "Premium", "#d8c100"));
		rItem.add(new RightItem(10, "Quizzes", "#0050ef"));
		rItem.add(new RightItem(11, "Paper Presentation", "#00aba9"));
		ListView rightList = (ListView)findViewById(R.id.right_drawer);
		RightListAdapter rightAdapter = new RightListAdapter(this, R.layout.right, rItem);
		rightList.setAdapter(rightAdapter);	
		rightList.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> arg0, View v, int position,
					long arg3) {
				toggleDrawerRight();
				Log.d("Position", "Position: " + position);
				loadCatEvents(position);
			}
		});

		ArrayList<ListItem> item = new ArrayList<ListItem>();
		item.add(new ListItem(R.drawable.ic_action_collections_go_to_today, "Day 1"));
		item.add(new ListItem(R.drawable.ic_action_collections_go_to_today, "Day 2"));
		item.add(new ListItem(R.drawable.ic_action_collections_go_to_today, "Day 3"));
		item.add(new ListItem(R.drawable.bulb72, "Live Feeds"));
		item.add(new ListItem(R.drawable.ic_action_device_access_not_secure, "Coordinator Login"));
		adapterList = new ListAdapter(this, R.layout.side_list_row, item);
		listview = (ListView)findViewById(R.id.listViewFeed);
		listview.setAdapter(adapterList);
		listview.setOnItemClickListener(new OnItemClickListener() {
			
			@Override
			public void onItemClick(AdapterView<?> parent, View view, int position,
					long id) {
				mDrawerLayout.closeDrawers();
				if(position == 3)
				{
					Intent intent = new Intent(getApplicationContext(), FeedActivity.class);
					startActivity(intent);
					return;
				}
				if(position == 4)
				{
					Intent intent = new Intent(getApplicationContext(), AdminActivity.class);
					startActivity(intent);
					return;
				}
				if(day != position +1)
				{
					getSupportActionBar().setTitle("Day "+Integer.toString(position +1));
					day = loadDayEvents(position +1);
				}
			}
		});

		listViewEvent.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> arg0, View v, int position,
					long arg3) {
				EventHolder holder = (EventHolder)v.getTag();
				List<DayEventJson> dayEvent =new ArrayList<DayEventJson>();
				dayEvent = db.getEvent(holder.day, holder.id);
				Log.d("Item",holder.day+" "+holder.eventName+" "+holder.id);
				DayEventJson newEvent = dayEvent.get(0);
				Intent intent = new Intent(getApplicationContext(), EventActivity.class);
				intent.putExtra("TITLE", newEvent.title);
				intent.putExtra("DESC", newEvent.desc);
				intent.putExtra("TIME", newEvent.time);
				intent.putExtra("VENUE", newEvent.eventLocation);
				intent.putExtra("PRIZE1", newEvent.prize[0]);
				intent.putExtra("PRIZE2", newEvent.prize[1]);
				intent.putExtra("PRIZE3", newEvent.prize[2]);
				intent.putExtra("DAY", newEvent.day);
				intent.putExtra("COLOR", newEvent.color);
				intent.putExtra("THUMB", newEvent.thumb);
				intent.putExtra("COVER", newEvent.cover);
				startActivity(intent);

			}
		});
		//GCM Implementation
		final String oldId="-1";
		registerReceiver(mHandleMessageReceiver,
				new IntentFilter(DISPLAY_MESSAGE_ACTION));
		final String regId = GCMRegistrar.getRegistrationId(this);
		if (regId.equals("")) {
			// Automatically registers application on startup.
			GCMRegistrar.register(this, SENDER_ID);
		} else {
			// Device is already registered on GCM, check server.
			if (GCMRegistrar.isRegisteredOnServer(this)) {
				// Skips registration.
				//mDisplay.append(getString(R.string.already_registered) + "\n");

				//Toast.makeText(getBaseContext(),"Already "+ regId, Toast.LENGTH_LONG).show();
			} else {
				// Try to register again, but not in the UI thread.
				// It's also necessary to cancel the thread onDestroy(),
				// hence the use of AsyncTask instead of a raw thread.
				//Toast.makeText(getBaseContext(),"Now "+ regId, Toast.LENGTH_LONG).show();
				final Context context = this;
				mRegisterTask = new AsyncTask<Void, Void, Void>() {

					@Override
					protected void onPreExecute() {
						setProgressBarIndeterminateVisibility(true);
					}
					@Override
					protected Void doInBackground(Void... params) {
						ServerUtilities.register(context, regId,pref.getString("old_token", "-1"));
						return null;
					}

					@Override
					protected void onPostExecute(Void result) {
						editor.putString("old_token", regId);
						editor.commit();
						mRegisterTask = null;
						setProgressBarIndeterminateVisibility(false);
					}

				};
				mRegisterTask.execute(null, null, null);
			}
		}
	}

	@Override
	protected void onPostCreate(Bundle savedInstanceState) {
		super.onPostCreate(savedInstanceState);
		// Sync the toggle state after onRestoreInstanceState has occurred.
		mDrawerToggle.syncState();
	}

	@Override
	public void onConfigurationChanged(Configuration newConfig) {
		super.onConfigurationChanged(newConfig);
		mDrawerToggle.onConfigurationChanged(newConfig);
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// Pass the event to ActionBarDrawerToggle, if it returns
		// true, then it has handled the app icon touch event
		if (mDrawerToggle.onOptionsItemSelected(item)) {
			return true;
		}
		// Handle your other action bar items...

		return super.onOptionsItemSelected(item);
	}



	/*public void toggleDrawer(View view)
	{
		DrawerLayout mDrawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
		ActionBarDrawerToggle mDrawerToggle = new ActionBarDrawerToggle(this, mDrawerLayout, R.drawable.back_button, R.string.Drawer, R.string.Drawer);
		mDrawerLayout.setDrawerListener(mDrawerToggle);
		mDrawerLayout.openDrawer(Gravity.LEFT);
	}

	public void toggleDrawer()
	{
		DrawerLayout mDrawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
		ActionBarDrawerToggle mDrawerToggle = new ActionBarDrawerToggle(this, mDrawerLayout, R.drawable.back_button, R.string.Drawer, R.string.Drawer);
		mDrawerLayout.setDrawerListener(mDrawerToggle);
		mDrawerLayout.closeDrawer(Gravity.LEFT);
	}*/
	public void toggleDrawerRight(View view)
	{
		/*DrawerLayout mDrawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
		ActionBarDrawerToggle mDrawerToggle = new ActionBarDrawerToggle(this, mDrawerLayout, R.drawable.back_button, R.string.Drawer, R.string.Drawer);
		mDrawerLayout.setDrawerListener(mDrawerToggle);
		mDrawerLayout.closeDrawer(Gravity.RIGHT);*/
	}

	public void toggleDrawerRight()
	{
		/*DrawerLayout mDrawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
		ActionBarDrawerToggle mDrawerToggle = new ActionBarDrawerToggle(this, mDrawerLayout, R.drawable.back_button, R.string.Drawer, R.string.Drawer);
		mDrawerLayout.setDrawerListener(mDrawerToggle);
		mDrawerLayout.closeDrawer(Gravity.RIGHT);*/

	}
	public void loadPrivacyPolicy(View view)
	{
		
		Intent intent = new Intent(getApplicationContext(), Privacy_policy.class);
		startActivity(intent);
	}

	public int loadDayEvents(int day)
	{
		db = new DatabaseHandler(this);
		data =new ArrayList<DayEventJson>();
		data = db.getEvents(day);
		event.clear();
		for(int i =0; i < data.size(); i++)
		{
			DayEventJson newEvent = data.get(i);
			event.add(new EventList(newEvent.title, newEvent.time, newEvent.eventLocation, newEvent.category, newEvent.id, day, db.getAlarm(newEvent.id), newEvent.thumb, newEvent.color));
		}
		Collections.sort(event);
		adapterEvent.notifyDataSetChanged();
		listViewEvent.setSelectionAfterHeaderView();
		return day;
	}

	private boolean isNetworkConnected() 
	{
		ConnectivityManager cm = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
		NetworkInfo ni = cm.getActiveNetworkInfo();
		if (ni == null) {
			return false;
		} else
			return true;
	}

	private void checkNotNull(Object reference, String name) {
		if (reference == null) {
			throw new NullPointerException(
					getString(R.string.error_config, name));
		}
	}

	@Override
	protected void onDestroy() {
		//stopService(intentService);
		if (mRegisterTask != null) {
			mRegisterTask.cancel(true);
		}
		unregisterReceiver(mHandleMessageReceiver);
		GCMRegistrar.onDestroy(this);
		super.onDestroy();
	}
	private final BroadcastReceiver mHandleMessageReceiver =
			new BroadcastReceiver() {
		@Override
		public void onReceive(Context context, Intent intent) {
			String newMessage = intent.getExtras().getString(EXTRA_MESSAGE);
		}
	};
	public int loadCatEvents(int id)
	{
		db = new DatabaseHandler(this);
		data =new ArrayList<DayEventJson>();
		data = db.getEventsCate(id);
		event.clear();


		for(int i =0; i < data.size(); i++)
		{
			//Same handle database here too for alarm status
			DayEventJson newEvent = data.get(i);
			event.add(new EventList(newEvent.title, newEvent.time, newEvent.eventLocation, newEvent.category, newEvent.id, day, db.getAlarm(newEvent.id), newEvent.thumb, newEvent.color));
		}
		//adapterEvent = new EventAdapter(this, R.layout.event_row, event);
		Collections.sort(event);
		adapterEvent.notifyDataSetChanged();
		listViewEvent.setSelectionAfterHeaderView();
		return day;
	}

	public class Fetch extends AsyncTask<Void, Void, Void> {

		Context context;
		Handler handler;
		Runnable r;
		String url;
		String data;
		SharedPreferences pref;
		ListView listview = null;
		ListView listViewEvent = null;
		DatabaseHandler db;
		List<DayEventJson> listData;
		EventAdapter adapterEvent;
		ArrayList<EventList> event;
		boolean updated;
		ImageHelper imageHelper;
		Bitmap icon;
		Bitmap newIcon;


		public Fetch(Context context, Handler handler, EventAdapter adapterEvent, ArrayList<EventList> event)
		{
			this.event = event;
			this.adapterEvent = adapterEvent;
			this.context = context;
			this.handler = handler;
		}

		@Override
		protected Void doInBackground(Void... arg0) 
		{
			updated = false;
			pref = context.getSharedPreferences("MyPref", 0);
			url = "http://ieeevit.com/gravitas/api.php";
			data = readData(url);
			//Log.d("data", data);
			int currentVersion = pref.getInt("vers", -1);
			int newVersion;
			try
			{
				newVersion = Integer.parseInt(data);
			}
			catch(Exception e)
			{
				newVersion = 0;
			}

			if(currentVersion < newVersion)
			{
				DatabaseHandler db = new DatabaseHandler(context);
				db.deleteTable();
				db.createTable();
				//Log.d("data", "Need to update"); 
				url = "http://ieeevit.com/gravitas/dev/api.php?update=99";
				data = readData(url);
				Log.d("data", data); 
				JSONObject jObj;
				try {
					//data = URLDecoder.decode(data, "UTF-8");
					jObj = new JSONObject(data);
					JSONObject jObject = jObj.getJSONObject("day1");
					JSONArray jArray = jObject.getJSONArray("events");
					for(int i = 0; i < jArray.length(); i++)
					{
						DayEventJson dayEvent = new DayEventJson(jArray.getJSONObject(i), 1);
						db.addEvent(dayEvent);
					}
					jObject = jObj.getJSONObject("day2");
					jArray = jObject.getJSONArray("events");
					for(int i = 0; i < jArray.length(); i++)
					{
						DayEventJson dayEvent = new DayEventJson(jArray.getJSONObject(i), 2);
						db.addEvent(dayEvent);
					}
					jObject = jObj.getJSONObject("day3");
					jArray = jObject.getJSONArray("events");
					for(int i = 0; i < jArray.length(); i++)
					{
						DayEventJson dayEvent = new DayEventJson(jArray.getJSONObject(i), 3);
						db.addEvent(dayEvent);
					}
					Editor editor = pref.edit();
					editor.putInt("vers", newVersion);
					editor.commit();
					updated = true;
					
					data = readData("http://ieeevit.com/gravitas/dev/api.php?mode=feed&last_id=-1");
					
					jObj = new JSONObject(data);
					jArray = jObj.getJSONArray("feeds");

					for(int i = 0; i< jArray.length(); i++)
					{
						JSONObject temp = jArray.getJSONObject(i);
						db.addFeed(temp.getInt("eventid"), temp.getString("message"), temp.getString("timestamp"));
					}

				}
					//http://ieeevit.com/gravitas/dev/api.php?mode=feed&last_id=-1

					catch (JSONException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
					db.close();	
					
				}
			return null;
			}


			@Override
			protected void onPreExecute()
			{
				setProgressBarIndeterminateVisibility(true);
			}


			@Override
			protected void onPostExecute(Void result)
			{
				int day = 1;
				if(updated)
				{
					//icon = BitmapFactory.decodeResource(context.getResources(), R.drawable.bulb72);
					//newIcon = imageHelper.getRoundedCornerBitmap(icon, 200);
					db = new DatabaseHandler(context);
					listData =new ArrayList<DayEventJson>();
					listData = db.getEvents(day);
					event.clear();
					for(int i =0; i < listData.size(); i++)
					{
						DayEventJson newEvent = listData.get(i);
						event.add(new EventList(newEvent.title, newEvent.time, newEvent.eventLocation, newEvent.category, newEvent.id, day, db.getAlarm(newEvent.id), newEvent.thumb, newEvent.color));
					}
					Collections.sort(event);
					adapterEvent.notifyDataSetChanged();	


				}
				setProgressBarIndeterminateVisibility(false);


			}

			public String readData(String path) {
				StringBuilder builder = new StringBuilder();
				HttpClient client = new DefaultHttpClient();
				HttpGet httpGet = new HttpGet(path);
				try {
					HttpResponse response = client.execute(httpGet);
					StatusLine statusLine = response.getStatusLine();
					int statusCode = statusLine.getStatusCode();
					if (statusCode == 200) {
						HttpEntity entity = response.getEntity();
						InputStream content = entity.getContent();
						BufferedReader reader = new BufferedReader(new InputStreamReader(content));
						String line;
						while ((line = reader.readLine()) != null) {
							builder.append(line);
						}
					} else {
						//Log.d("Error", "Failed to download file");
					}

				} catch (ClientProtocolException e) {
					e.printStackTrace();
				} catch (IOException e) {
					e.printStackTrace();
				}
				return builder.toString();
			}

		}

	}
