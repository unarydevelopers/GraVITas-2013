package com.gravitas2013.app;

import java.sql.Date;
import java.sql.Time;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;

import com.android.volley.RequestQueue;
import com.android.volley.toolbox.ImageLoader;
import com.android.volley.toolbox.ImageLoader.ImageListener;
import com.android.volley.toolbox.NetworkImageView;
import com.android.volley.toolbox.Volley;
import com.gravitas2013.app.R;

import android.app.Activity;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

public class EventAdapter extends ArrayAdapter<EventList> {

	Context context;
	ArrayList<EventList> data;
	int layoutResourceId;
	DateFormat formatter = new SimpleDateFormat("hh:mm:ss");
	
	

	public EventAdapter(Context context, int textViewResourceId, ArrayList<EventList> data) {
		super(context, textViewResourceId, data);
		this.context = context;
		this.data = data;
		this.layoutResourceId = textViewResourceId;
		
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent)
	{
		View row = convertView;
		EventHolder holder =  null;
		if(row == null)
		{
			LayoutInflater inflator = ((Activity)context).getLayoutInflater();
			row = inflator.inflate(layoutResourceId, parent, false);
			holder = new EventHolder();
			holder.icon = (NetworkImageView)row.findViewById(R.id.eLogo);
			holder.eventName = (TextView)row.findViewById(R.id.eName);
			holder.category = (TextView)row.findViewById(R.id.eCategory);
			holder.alarm = (ImageView)row.findViewById(R.id.eBell);
			holder.eview = (View)row.findViewById(R.id.eView);
			row.setTag(holder);

		}
		else
		{
			holder = (EventHolder)row.getTag();
			//holder.icon = (ImageView)row.findViewById(R.id.eLogo);
		}
		
		EventList listItem = data.get(position);
		holder.eventName.setText(listItem.eventName);
		holder.category.setText(listItem.category);
		holder.category.setTextColor(Color.parseColor(listItem.color));
		holder.eview.setBackgroundColor(Color.parseColor(listItem.color));	
		
		holder.icon.setDefaultImageResId(R.drawable.bulb72);
		holder.icon.setImageUrl("http://ieeevit.com/gravitas/"+listItem.thumb, ImageUtil.getImageLoader(context.getApplicationContext()));
		
		/*ImageListener listener = ImageLoader.getImageListener(holder.icon, R.drawable.bulb72, R.drawable.bulb72);
        mImageLoader.get("http://ieeevit.com/gravitas/"+listItem.thumb, listener);*/
		
		
		//holder.icon.setImageBitmap(listItem.icon);
		holder.id = listItem.id;
		holder.day = listItem.day;
		
		if(listItem.alarmStatus == 0)
		{
			holder.alarm.setImageResource(R.drawable.ic_action_rating_not_important);
		}
		else
		{
			holder.alarm.setImageResource(R.drawable.ic_action_rating_important);
		}
		holder.alarm.setOnClickListener(new changeImage(holder.alarm, position, listItem.id, holder.day));
		//Log.d("ListView", "Executed");
		return row;

	}

	class changeImage implements OnClickListener
	{
		int id;
		int status;
		ImageView image;
		int position;
		int day;


		public changeImage(ImageView image, int position,int id, int day)
		{
			this.position = position;
			this.image = image;
			this.id = id;
			this.day = day;
		}

		@Override
		public void onClick(View v) {
			EventList listItem = data.get(position);
			//Log.d("Click Event", "Clicked: " + id);
			DatabaseHandler db = new DatabaseHandler(context);
			
			if(listItem.alarmStatus ==1)
			{
				//Log.d("OnClick", "Setting status: 0");
				listItem.alarmStatus = 0;
				db.setAlarm(id, 0);
				image.setImageResource(R.drawable.ic_action_rating_not_important);
			}
			else
			{
				//Log.d("OnClick", "Setting status: 1");
				//Toast.makeText(context, "You will get a reminder 15 minutes prior to the event and will get notification of all the feeds for the event.", Toast.LENGTH_LONG).show();
				listItem.alarmStatus = 1 ;
				db.setAlarm(id,1);
				
				image.setImageResource(R.drawable.ic_action_rating_important);
				Log.d("TIME", listItem.time);
				Calendar cal = Calendar.getInstance();
				cal.set(Calendar.MONTH, 9);
				cal.set(Calendar.YEAR, 2013);				
				cal.set(Calendar.DAY_OF_MONTH, 26+listItem.day);
				if(Integer.parseInt(listItem.time.split(":")[0])>1 && Integer.parseInt(listItem.time.split(":")[0])<7){
					cal.set(Calendar.HOUR_OF_DAY, Integer.parseInt(listItem.time.split(":")[0])+12);
				}
				else{
					cal.set(Calendar.HOUR_OF_DAY, Integer.parseInt(listItem.time.split(":")[0]));
				}
				
				cal.set(Calendar.MINUTE, Integer.parseInt(listItem.time.split(":")[1]));
				Log.d("HOUR",""+Integer.parseInt(listItem.time.split(":")[0])+":"+Integer.parseInt(listItem.time.split(":")[0]));
				cal.add(Calendar.MINUTE, -15);
				
				//cal.set will set the alarm to trigger exactly at: 21:43, 5 May 2011
				//if you want to trigger the alarm after let's say 5 minutes after is activated you need to put
				//cal.add(Calendar.MINUTE, 5);
				Intent alarmintent = new Intent(context.getApplicationContext(), AlarmReceiver.class);
				alarmintent.putExtra("day",listItem.day);
				alarmintent.putExtra("id",listItem.id);
				alarmintent.putExtra("title",listItem.eventName);
				alarmintent.putExtra("note","Event starting in 15 minutes.");
				//HELLO_ID is a static variable that must be initialised at the BEGINNING OF CLASS with 1;
			 
				//example:protected static int HELLO_ID =1;
				PendingIntent sender = PendingIntent.getBroadcast(context.getApplicationContext(),1,
				alarmintent,PendingIntent.FLAG_UPDATE_CURRENT|  Intent.FILL_IN_DATA);
				//VERY IMPORTANT TO SET FLAG_UPDATE_CURRENT... this will send correct extra's informations to 
				//AlarmReceiver Class
								// Get the AlarmManager service
				 
				AlarmManager am = (AlarmManager) context.getSystemService(context.ALARM_SERVICE);
				am.set(AlarmManager.RTC_WAKEUP, cal.getTimeInMillis(), sender);
			}
			
			db.close();
		}
	}

	static class EventHolder
	{
		NetworkImageView icon;
		ImageView alarm;
		TextView eventName;
		TextView location;
		TextView time;
		TextView category;
		View eview;
		int alarmStatus;
		int id;
		int day;
	}
}

