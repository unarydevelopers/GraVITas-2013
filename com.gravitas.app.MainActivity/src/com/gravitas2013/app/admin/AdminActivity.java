package com.gravitas2013.app.admin;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONException;
import org.json.JSONObject;

import com.actionbarsherlock.app.SherlockActivity;
import com.gravitas2013.app.R;

import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.widget.EditText;
import android.widget.Toast;
import android.support.v4.app.NavUtils;

public class AdminActivity extends SherlockActivity {

	private EditText username;
	private EditText password;
	private String user;
	private String pass;
	private String token;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_INDETERMINATE_PROGRESS);
		getSupportActionBar().setTitle("Coordinator Login");
		setContentView(R.layout.activity_admin);
		SharedPreferences pref = this.getSharedPreferences("MyPref", 0);
		token = pref.getString("old_token", "-1");

	}

	public void login(View v)
	{
		username = (EditText)findViewById(R.id.username);
		password = (EditText)findViewById(R.id.password);
		if(username.getText().length() == 0 || password.getText().length() == 0)
		{
			return;
		}
		user = username.getText().toString();
		pass = password.getText().toString();
		
		ConnectivityManager connectivityManager = (ConnectivityManager) getSystemService(getBaseContext().CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
  		if(activeNetworkInfo != null)
  		{
  			Log.d("Admin","going to execute loginTask");
  			new loginTask().execute();
  		}
  		else
  		{
  			Toast.makeText(getApplicationContext(), "No internet connection.", Toast.LENGTH_LONG).show();
  		}
	} 

	class loginTask extends AsyncTask<Void, Void, String>
	{
		InputStream is = null;
		StringBuilder sb=null;
		String result = "{\"eventid\":\"-1\",\"message\":\"login_error\"}";
		@Override
		protected String doInBackground(Void... params) {
			
			try {
				HttpClient httpclient = new DefaultHttpClient();
				HttpPost httppost = new HttpPost("http://ieeevit.com/gravitas/dev/api.php?mode=login");
				ArrayList<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>();
				nameValuePairs.add(new BasicNameValuePair("username", user));
				nameValuePairs.add(new BasicNameValuePair("password", pass));
				nameValuePairs.add(new BasicNameValuePair("token",token));
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
		protected void onPostExecute(String result) {
			// TODO Auto-generated method stub
			super.onPostExecute(result);
			try {
				JSONObject jobj = new JSONObject(result);
				 String eventID = jobj.getString("eventid");
				 String message = jobj.getString("message");
				 if(message.equals("success"))
				 {
					 Intent intent = new Intent(getApplicationContext(), PushActivity.class);
					 intent.putExtra("eventid", Integer.parseInt(eventID));
					 startActivity(intent);
				 }
				 else{
					 Toast.makeText(getApplicationContext(), "Login Error: Please try after sometime.", Toast.LENGTH_LONG).show();
				 }
			} catch (JSONException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			setProgressBarIndeterminateVisibility(false);
		}
		@Override
		protected void onPreExecute()
		{
			setProgressBarIndeterminateVisibility(true);
		}@Override
		protected void onProgressUpdate(Void... values) {
			// TODO Auto-generated method stub
			super.onProgressUpdate(values);
		}

	}


}
