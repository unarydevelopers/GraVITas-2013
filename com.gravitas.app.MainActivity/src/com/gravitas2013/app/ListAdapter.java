package com.gravitas2013.app;

import java.util.ArrayList;

import com.gravitas2013.app.R;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

public class ListAdapter extends ArrayAdapter<ListItem>{

	Context context;
	int layoutResourceId;
	ArrayList<ListItem> item;
	public ListAdapter(Context context, int layoutResourceId,	ArrayList<ListItem> item) {
		super(context, layoutResourceId, item);
		// TODO Auto-generated constructor stub
		this.context = context;
		this.item = item;
		this.layoutResourceId = layoutResourceId;
	}
	@Override
	public View getView(int position, View convertView, ViewGroup parent)
	{
		View row = convertView;
		ItemHolder holder =  null;
		if(row == null)
		{
			LayoutInflater inflator = ((Activity)context).getLayoutInflater();
			row = inflator.inflate(layoutResourceId, parent, false);
			holder = new ItemHolder();
			holder.pic = (ImageView)row.findViewById(R.id.list_pic);
			holder.item = (TextView)row.findViewById(R.id.eventName);
			row.setTag(holder);
			
		}
		else
		{
			holder = (ItemHolder)row.getTag();
		}
		ListItem listItem = item.get(position);
		holder.item.setText(listItem.item);
		holder.pic.setImageResource(listItem.pic);
		//Log.d("ListView", "Executed");
		return row;
	}
	
	static class ItemHolder
	{
		ImageView pic;
		TextView item;
		int id;
	}
}
