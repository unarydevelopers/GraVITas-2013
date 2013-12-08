package com.gravitas2013.app.admin;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONArray;
import org.json.JSONObject;

import com.actionbarsherlock.app.SherlockActivity;
import com.gravitas2013.app.DatabaseHandler;
import com.gravitas2013.app.DayEventJson;
import com.gravitas2013.app.FeedAdapter;
import com.gravitas2013.app.FeedJson;
import com.gravitas2013.app.FeedList;
import com.gravitas2013.app.R;

import android.os.AsyncTask;
import android.os.Bundle;
import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.view.Window;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

public class PushActivity extends SherlockActivity {

	private EditText editTextFeed;
	private Intent intent;
	private int eventid;
	private String token;
	private String feed;
	FeedAdapter adapterFeed;
	private ListView listviewFeed;
	private ArrayList<FeedList> feedList;
	getFeedByEventId getFeed;
	
	BroadcastReceiver receiver;
	@Override
	protected void onPause() {
		this.unregisterReceiver(receiver);
		super.onPause();		
	}


	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_INDETERMINATE_PROGRESS);
		setContentView(R.layout.activity_push);
		listviewFeed = (ListView)findViewById(R.id.listViewFeed);
		feedList = new ArrayList<FeedList>();
		adapterFeed = new FeedAdapter(this, R.layout.feed_row, feedList);
		listviewFeed.setAdapter(adapterFeed);
		SharedPreferences pref = this.getSharedPreferences("MyPref", 0);
		token = pref.getString("old_token", "-1");
		intent = getIntent();
		eventid = intent.getIntExtra("eventid", -1);
		DatabaseHandler db = new DatabaseHandler(getApplicationContext());
		
		getSupportActionBar().setTitle(db.getEvent(eventid).get(0).title);
		db.close();
		editTextFeed = (EditText)findViewById(R.id.editTextFeed);
		
		getFeed = new getFeedByEventId();
		getFeed.execute();
		
	}

	@Override
	protected void onResume() {
		// TODO Auto-generated method stub
		super.onResume();
		receiver = new BroadcastReceiver(){

			@Override
			public void onReceive(Context context, Intent intent) {
				new getFeedByEventId().execute();
				//adapterFeed.notifyDataSetChanged();
				Log.d("FEED","call refresh list");
			}
		};
		this.registerReceiver(receiver, new IntentFilter("RefreshList"));

		
	}


	public void sendFeed(View v)
	{
		feed = editTextFeed.getText().toString();
		if(feed.length() == 0)
		{
			//return;
		}
		editTextFeed.setText(null);
		SendFeed feed = new SendFeed();
		feed.execute();

	}

	class getFeedByEventId extends AsyncTask<Void, Void, String>
	{

		int id;
		String data;
		String title;
		String time;
		String category;
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss z"); // the format of your date
		String color;
		String thumb;
		//String category;
		String url = "http://ieeevit.com/gravitas/dev/api.php?mode=feed&event_id="+eventid;
		InputStream is = null;
		StringBuilder sb=null;
		String result = "{\"eventid\":\"-1\",\"message\":\"login_error\"}";
		private JSONArray jArray;
		private List<DayEventJson> dayEvent;

		@Override
		protected String doInBackground(Void... params) {
			try {
				HttpClient httpclient = new DefaultHttpClient();
				HttpPost httppost = new HttpPost(url);
				ArrayList<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>();
				httppost.setEntity(new UrlEncodedFormEntity(nameValuePairs));
				HttpResponse response = httpclient.execute(httppost);
				HttpEntity entity = response.getEntity();
				is = entity.getContent();
				//String httpResponse = entity.toString();


			} catch (UnsupportedEncodingException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (ClientProtocolException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

			try{
				BufferedReader reader = new BufferedReader(new InputStreamReader(is,"iso-8859-1"),8);
				sb = new StringBuilder();
				sb.append(reader.readLine() + "\n");
				String line="0";

				while ((line = reader.readLine()) != null) 
				{
					sb.append(line + "\n");
				}

				is.close();
				result=sb.toString();

				Log.d("Response",result);
				JSONObject jObj = new JSONObject(result);
				jArray = jObj.getJSONArray("feeds");
				DatabaseHandler db = new DatabaseHandler(getApplicationContext());
				feedList.clear();
				for(int i = 0; i< jArray.length(); i++)
				{
					JSONObject temp = jArray.getJSONObject(i);
					id = temp.getInt("eventid");
					
					time  = temp.getString("timestamp");
					Date date = new Date(Long.parseLong(time)*1000L); // *1000 is to convert minutes to milliseconds
					
					String formattedDate = sdf.format(date);
					title = temp.getString("eventname");
					data = temp.getString("message");
					dayEvent = db.getEvent(id);
					DayEventJson eventObj = dayEvent.get(0);
					color = eventObj.color;
					category = eventObj.category;
					thumb = eventObj.thumb;
					FeedJson feed= new FeedJson(id, data, title, time, category, color, thumb);
					feedList.add(new FeedList(title, formattedDate, data, category , color, id, eventObj.day, db.getAlarm(id), thumb));
				}
			}
			catch(Exception e)
			{
				Log.e("log_tag", "Error converting result "+e.toString());
			}

			return result;
		}
		@Override
		protected void onPostExecute(String result)
		{
			adapterFeed.notifyDataSetChanged();
			setProgressBarIndeterminateVisibility(false);
			
			

		}
		
		@Override
		protected void onPreExecute()
		{
			setProgressBarIndeterminateVisibility(true);
			
			

		}

	}
	class SendFeed extends AsyncTask<Void, Void, String>
	{
		InputStream is = null;
		StringBuilder sb=null;
		String result = "{\"eventid\":\"-1\",\"message\":\"login_error\"}";
		@Override
		protected void onPreExecute()
		{
			setProgressBarIndeterminateVisibility(true);
			
			

		}

		@Override
		protected String doInBackground(Void... params) {
			try {
				HttpClient httpclient = new DefaultHttpClient();
				HttpPost httppost = new HttpPost("http://ieeevit.com/gravitas/dev/api.php?mode=add_feed");
				ArrayList<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>();
				nameValuePairs.add(new BasicNameValuePair("token", token));
				nameValuePairs.add(new BasicNameValuePair("message", feed));
				nameValuePairs.add(new BasicNameValuePair("event_id", String.valueOf(eventid)));
				httppost.setEntity(new UrlEncodedFormEntity(nameValuePairs));
				HttpResponse response = httpclient.execute(httppost);
				HttpEntity entity = response.getEntity();
				is = entity.getContent();
				//String httpResponse = entity.toString();


			} catch (UnsupportedEncodingException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (ClientProtocolException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

			try{
				BufferedReader reader = new BufferedReader(new InputStreamReader(is,"iso-8859-1"),8);
				sb = new StringBuilder();
				sb.append(reader.readLine() + "\n");
				String line="0";

				while ((line = reader.readLine()) != null) 
				{
					sb.append(line + "\n");
				}

				is.close();
				result=sb.toString();

				Log.d("Response",result);

			}
			catch(Exception e)
			{
				Log.e("log_tag", "Error converting result "+e.toString());
			}

			return result;

		}
		@Override
		protected void onPostExecute(String result)
		{
			Toast.makeText(getApplicationContext(), "Successfully Posted.", Toast.LENGTH_LONG).show();
			setProgressBarIndeterminateVisibility(false);
			
		}


	}

	

}
