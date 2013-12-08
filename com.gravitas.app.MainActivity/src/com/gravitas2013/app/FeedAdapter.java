package com.gravitas2013.app;

import java.util.ArrayList;
import java.util.Date;

import com.android.volley.toolbox.NetworkImageView;
import com.gravitas2013.app.R;

import android.app.Activity;
import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

public class FeedAdapter extends ArrayAdapter<FeedList> {

	Context context;
	ArrayList<FeedList> data;
	int layoutResourceId;
	private Date time_format;


	public FeedAdapter(Context context, int textViewResourceId, ArrayList<FeedList> data) {
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
			holder.icon = (NetworkImageView)row.findViewById(R.id.feedLogo);
			holder.feedTitle = (TextView)row.findViewById(R.id.feedTitle);
			holder.category = (TextView)row.findViewById(R.id.feedCategory);
			holder.feedTime = (TextView)row.findViewById(R.id.feedTime);
			holder.feedText = (TextView)row.findViewById(R.id.feedData);
			row.setTag(holder);
		}
		else
		{
			holder = (EventHolder)row.getTag();
			//holder.icon = (ImageView)row.findViewById(R.id.eLogo);
		}
		FeedList listItem = data.get(position);
		//time_format = new java.util.Date(Long.parseLong(listItem.feedTime)*1000);
		holder.feedTitle.setText(listItem.feedTitle);
		holder.feedText.setText(listItem.feedText);
		holder.feedTime.setText(listItem.feedTime);
		holder.id = listItem.id;
		holder.category.setText(listItem.category);
		holder.category.setTextColor(Color.parseColor(listItem.color));

		holder.icon.setDefaultImageResId(R.drawable.bulb72);
		holder.icon.setImageUrl("http://ieeevit.com/gravitas/"+listItem.thumb, ImageUtil.getImageLoader(context.getApplicationContext()));

		/*ImageListener listener = ImageLoader.getImageListener(holder.icon, R.drawable.bulb72, R.drawable.bulb72);
        mImageLoader.get("http://ieeevit.com/gravitas/"+listItem.thumb, listener);*/


		//holder.icon.setImageBitmap(listItem.icon);
		holder.id = listItem.id;
		holder.day = listItem.day;
/*
		if(listItem.alarmStatus == 0)
		{
			holder.feedAlarm.setImageResource(R.drawable.bell_off);
		}
		else
		{
			holder.feedAlarm.setImageResource(R.drawable.bell_on);
		}
		holder.feedAlarm.setOnClickListener(new changeImage(holder.feedAlarm, position, listItem.id, holder.day));
		//Log.d("ListView", "Executed");*/
		return row;

	}


	static class EventHolder
	{
		NetworkImageView icon;
		ImageView feedAlarm;
		TextView feedTitle;
		TextView location;
		TextView feedTime;
		TextView feedText;
		TextView category;
		int alarmStatus;
		int id;
		int day;
	}
}

