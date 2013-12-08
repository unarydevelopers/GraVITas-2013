package com.gravitas2013.app;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;

import com.actionbarsherlock.app.SherlockActivity;
import com.android.volley.toolbox.NetworkImageView;
import com.gravitas2013.app.R;
import android.os.Bundle;
import android.app.Activity;
import android.content.Intent;
import android.content.res.ColorStateList;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;


public class EventActivity extends SherlockActivity {
	
	View view;
	TextView text;
	


	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		setContentView(R.layout.activity_event);
		Intent intent = getIntent();
		String title = intent.getStringExtra("TITLE");
		getSupportActionBar().setTitle(title);
		String time = intent.getStringExtra("TIME");
		String venue = intent.getStringExtra("VENUE");
		String prize = "";
		String url = intent.getStringExtra("THUMB");
		String url2 = intent.getStringExtra("COVER");
		//Toast.makeText(getBaseContext(), "a"+intent.getStringExtra("PRIZE1")+"a", Toast.LENGTH_LONG).show();
		
		
		if(!intent.getStringExtra("PRIZE1").equalsIgnoreCase("") && !intent.getStringExtra("PRIZE1").equalsIgnoreCase("null"))
		{
			prize="Rs " + intent.getStringExtra("PRIZE1");
		}
		if(intent.getStringExtra("PRIZE2")!=null && !intent.getStringExtra("PRIZE1").equalsIgnoreCase("null"))
		{
			prize=prize.concat(", Rs " + intent.getStringExtra("PRIZE2"));
		}
		if(intent.getStringExtra("PRIZE3")!=null && !intent.getStringExtra("PRIZE1").equalsIgnoreCase("null"))
		{
			prize=prize.concat(", Rs " + intent.getStringExtra("PRIZE3"));
		}
		String desc = intent.getStringExtra("DESC");
		TextView eventDate = (TextView)findViewById(R.id.eventDate);
		TextView mEventTime = (TextView)findViewById(R.id.mEventTime);
		TextView mEventVenue = (TextView)findViewById(R.id.mEventVenue);
		if(venue!=null)
		{
			mEventVenue.setText(venue);	
		}
		
		//TextView eventTitle = (TextView)findViewById(R.id.title);
		TextView eventDesc = (TextView)findViewById(R.id.mEventInfo);
		TextView cashPrize = (TextView)findViewById(R.id.mEventCashPrize);
		//eventTitle.setText(title);
		int day = 26 + intent.getIntExtra("DAY", 0);
		eventDate.setText(day + "th September 2013");
		if(time.equalsIgnoreCase("00:00:00"))
		{
			//timeLayout.setVisibility(View.GONE);
			mEventTime.setText("To be announced");
		}
		else
		{
			mEventTime.setText(time);
		}
		try {
			eventDesc.setText(URLDecoder.decode(desc, "ISO-8859-1"));
		} catch (UnsupportedEncodingException e) {
			// TODO Auto-generated catch block
			//Toast.makeText(getBaseContext(), "decode error", Toast.LENGTH_LONG).show();
			e.printStackTrace();
		}
		if(prize.equalsIgnoreCase(""))
		{
			cashPrize.setText("To be announced");
		}
		else
		{
			if(title.equals("Game A Thon"))
				prize = intent.getStringExtra("PRIZE1") ;
			cashPrize.setText(prize);
		}
		
		ImageHelper imageHelper = new ImageHelper();
		
		NetworkImageView mImage = (NetworkImageView)findViewById(R.id.titleImage);
		mImage.setDefaultImageResId(R.drawable.bulb72); 
		mImage.setErrorImageResId(R.drawable.bulb72);
		mImage.setImageUrl("http://ieeevit.com/gravitas/"+url,ImageUtil.getImageLoader(getApplicationContext()));
		
		NetworkImageView mCoverImage = (NetworkImageView)findViewById(R.id.spash);
		mCoverImage.setDefaultImageResId(R.drawable.vit2xlight); 
		mCoverImage.setErrorImageResId(R.drawable.vit2x);
		mCoverImage.setImageUrl("http://ieeevit.com/gravitas/"+url2,ImageUtil.getImageLoader(getApplicationContext()));
		
	}

	
	
	public void backButton(View view)
	{
		this.onBackPressed();
	}

}
