package com.gravitas2013.app;

import java.util.ArrayList;
import java.util.List;

import com.android.volley.VolleyError;
import com.android.volley.toolbox.ImageLoader.ImageContainer;
import com.android.volley.toolbox.ImageLoader.ImageListener;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;

public class AlarmReceiver extends BroadcastReceiver {

	Bundle extras;
	String title;
	int id;
	int day;
	Context context;
	PendingIntent contentIntent;
	String note;
	int alarmStat;
	NotificationManager manager;

	@SuppressWarnings("deprecation")
	@Override
	public void onReceive(Context context, Intent intent) {
		this.context = context;

		manager = (NotificationManager) context
				.getSystemService(Context.NOTIFICATION_SERVICE);

		Intent notificationIntent = new Intent(context, EventActivity.class);
		extras = intent.getExtras();
		title = extras.getString("title");
		id = extras.getInt("id");
		day = extras.getInt("day");

		DatabaseHandler db = new DatabaseHandler(context);
		List<DayEventJson> dayEvent = new ArrayList<DayEventJson>();
		dayEvent = db.getEvent(day, id);
		DayEventJson newEvent = dayEvent.get(0);
		notificationIntent.putExtra("TITLE", newEvent.title);
		notificationIntent.putExtra("DESC", newEvent.desc);
		notificationIntent.putExtra("TIME", newEvent.time);
		notificationIntent.putExtra("VENUE", newEvent.eventLocation);
		notificationIntent.putExtra("PRIZE1", newEvent.prize[0]);
		notificationIntent.putExtra("PRIZE2", newEvent.prize[1]);
		notificationIntent.putExtra("PRIZE3", newEvent.prize[2]);
		notificationIntent.putExtra("DAY", newEvent.day);
		notificationIntent.putExtra("COLOR", newEvent.color);
		notificationIntent.putExtra("THUMB", newEvent.thumb);
		notificationIntent.putExtra("COVER", newEvent.cover);
		Log.d("Alarm reciever", newEvent.title);
		alarmStat = db.getAlarm(id);
		db.close();
		contentIntent = PendingIntent.getActivity(context, id,
				notificationIntent, 0);

		// here we get the title and description of our Notification
		//
		// notificationIntent.putExtra("THUMB", "thumb/45.jpg");
		// notificationIntent.putExtra("COVER", "cover/45-cover.jpg");
		Notification notification;

		ImageListener listener = new ImageListener() {

			@Override
			public void onErrorResponse(VolleyError error) {
				Log.d("Image", "Failed");
				error.printStackTrace();
			}

			@Override
			public void onResponse(ImageContainer response, boolean isImmediate) {
				Log.d("Image", "Loaded");
				Notification notification = new Notification.Builder(
						AlarmReceiver.this.context)
						.setContentTitle(title)
						.setContentText(note)
						.setSmallIcon(R.drawable.bulb72)
						.setLargeIcon(response.getBitmap())
						.setContentIntent(contentIntent).build();

				
				notification.flags = Notification.FLAG_ONLY_ALERT_ONCE;
				notification.defaults |= Notification.DEFAULT_SOUND;

				if (alarmStat == 1) {
					manager.cancel(id);
					manager.notify(id, notification);
				}
			}
		};
		note = extras.getString("note");
		notification = new Notification.Builder(context)
				.setContentTitle(title)
				.setContentText(note)
				.setSmallIcon(R.drawable.bulb72)
				.setLargeIcon(
						ImageUtil
								.getImageLoader(context.getApplicationContext())
								.get("http://ieeevit.com/gravitas/"
										+ newEvent.thumb, listener).getBitmap())
				.setContentIntent(contentIntent).build();

		Log.d("Image", "http://ieeevit.com/gravitas/" + newEvent.thumb);

		notification.flags = Notification.FLAG_ONLY_ALERT_ONCE;
		notification.defaults |= Notification.DEFAULT_SOUND;
		// here we set the default sound for our
		// notification

		// The PendingIntent to launch our activity if the user selects this
		// notification
		if (alarmStat == 1) {
			manager.notify(id, notification);
		}

	}
};