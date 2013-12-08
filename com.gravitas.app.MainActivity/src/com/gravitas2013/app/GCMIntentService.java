/*
 * Copyright 2012 Google Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.gravitas2013.app;

import static com.gravitas2013.app.gcmservice.CommonUtilities.SENDER_ID;
import static com.gravitas2013.app.gcmservice.CommonUtilities.displayMessage;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.NotificationCompat;
import android.util.Log;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.ImageLoader.ImageContainer;
import com.android.volley.toolbox.ImageLoader.ImageListener;
import com.google.android.gcm.GCMBaseIntentService;
import com.gravitas2013.app.R;
import com.gravitas2013.app.gcmservice.ServerUtilities;

/**
 * IntentService responsible for handling GCM messages.
 */
public class GCMIntentService extends GCMBaseIntentService {

	@SuppressWarnings("hiding")
	private static final String TAG = "GCMIntentService";

	private String title;
	private String note;
	private int eventId;
	PendingIntent intent;
	Notification notification;

	public GCMIntentService() {
		super(SENDER_ID);
	}

	@Override
	protected void onRegistered(Context context, String registrationId) {
		Log.i(TAG, "Device registered: regId = " + registrationId);
		// displayMessage(context, getString(R.string.gcm_registered,
		// registrationId));
		// Toast.makeText(getBaseContext(), "From Service",
		// Toast.LENGTH_LONG).show();
		ServerUtilities.register(context, registrationId, "-1");
	}

	@Override
	protected void onUnregistered(Context context, String registrationId) {
		Log.i(TAG, "Device unregistered");
		// displayMessage(context, getString(R.string.gcm_unregistered));
		ServerUtilities.unregister(context, registrationId);
	}

	@Override
	protected void onMessage(Context context, Intent intent) {
		Log.i(TAG, "Received message. Extras: " + intent.getExtras());
		Bundle abc = intent.getExtras();
		// abc.getString("message");
		String message = abc.getString("message", "GraVITas2013");// .getString("price","GraVITas 2013");//getString(R.string.gcm_message);
		try {
			message = URLDecoder.decode(message, "UTF-8");
		} catch (UnsupportedEncodingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		displayMessage(context, message);
		int eventid = Integer.parseInt(abc.getString("eventid"));
		DatabaseHandler db = new DatabaseHandler(context);
		db.addFeed(eventid, message, "0");
		db.close();
		Intent refreshList = new Intent("RefreshList");

		this.sendBroadcast(refreshList);
		generateNotification(context, message,
				db.getEvent(eventid).get(0).title, eventid, db
						.getEvent(eventid).get(0).thumb, db.getAlarm(eventid));
	}

	@Override
	protected void onDeletedMessages(Context context, int total) {
		Log.i(TAG, "Received deleted messages notification");
		String message = getString(R.string.gcm_deleted, total);
		// displayMessage(context, message);
		// notifies user
		// generateNotification(context, message, "GraVITas 2013", 0,);
	}

	@Override
	public void onError(Context context, String errorId) {
		Log.i(TAG, "Received error: " + errorId);
		// displayMessage(context, getString(R.string.gcm_error, errorId));
	}

	@Override
	protected boolean onRecoverableError(Context context, String errorId) {
		// log message
		Log.i(TAG, "Received recoverable error: " + errorId);
		// displayMessage(context, getString(R.string.gcm_recoverable_error,
		// errorId));
		return super.onRecoverableError(context, errorId);
	}

	/**
	 * Issues a notification to inform the user that server has sent a message.
	 * 
	 * @param i
	 * @param thumb
	 */
	@SuppressWarnings("deprecation")
	private void generateNotification(final Context context, String message,
			String title, int id, final String thumb, final int alarmStat) {
		int icon = R.drawable.ic_static_gcm;
		long when = System.currentTimeMillis();
		this.title = title;
		this.note = message;
		this.eventId = id;
		final NotificationManager manager = (NotificationManager) context
				.getSystemService(Context.NOTIFICATION_SERVICE);
		// TODO
		/* Notification notification = new Notification(icon, message, when); */
		// String title = context.getString(R.string.app_name);
		Intent notificationIntent = new Intent(context, FeedActivity.class);

		// set intent so it does not start a new activity
		notificationIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP
				| Intent.FLAG_ACTIVITY_SINGLE_TOP);
		intent = PendingIntent.getActivity(context, 0, notificationIntent, 0);
		/*
		 * notification.setLatestEventInfo(context, title, message, intent);
		 * notification.flags |= Notification.FLAG_AUTO_CANCEL;
		 * notification.defaults |= Notification.DEFAULT_SOUND;
		 * notification.defaults |= Notification.DEFAULT_VIBRATE;
		 * notificationManager.notify(id, notification);
		 */

		new AsyncTask<Void, Void, Void>() {

			@Override
			protected void onPreExecute() {
				ImageListener listener = new ImageListener() {

					@Override
					public void onErrorResponse(VolleyError error) {
						Log.d("Image", "Failed");
						error.printStackTrace();
					}

					@Override
					public void onResponse(ImageContainer response,
							boolean isImmediate) {
						Log.d("Image", "Loaded");
						Notification notification = new NotificationCompat.Builder(
								GCMIntentService.this)
								.setContentTitle(GCMIntentService.this.title)
								.setContentText(GCMIntentService.this.note)
								.setSmallIcon(R.drawable.bulb72)
								.setLargeIcon(response.getBitmap())
								.setContentIntent(intent).build();

						notification.flags = Notification.FLAG_ONLY_ALERT_ONCE;
						notification.defaults |= Notification.DEFAULT_SOUND;

						if (alarmStat == 1) {
							manager.cancel(eventId);
							manager.notify(eventId, notification);
						}
					}
				};

				notification = new NotificationCompat.Builder(context)
						.setContentTitle(GCMIntentService.this.title)
						.setContentText(GCMIntentService.this.note)
						.setSmallIcon(R.drawable.bulb72)
						.setLargeIcon(
								BitmapFactory.decodeResource(
										context.getResources(),
										R.drawable.bulb_2x))
						.setContentIntent(intent).build();

				notification.flags = Notification.FLAG_ONLY_ALERT_ONCE;
				notification.defaults |= Notification.DEFAULT_SOUND;
				// here we set the default sound for our
				// notification

				// The PendingIntent to launch our activity if the user selects
				// this
				// notification
				 //if (alarmStat == 1) {
				manager.notify(eventId, notification);
				 //}

			}

			@Override
			protected Void doInBackground(Void... params) {
				// TODO Auto-generated method stub
				return null;
			}
		}.execute();

	}

}
