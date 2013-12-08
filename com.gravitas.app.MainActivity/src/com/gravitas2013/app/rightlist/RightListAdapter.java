package com.gravitas2013.app.rightlist;
import com.gravitas2013.app.R;
import java.util.ArrayList;

import android.R.color;
import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Bitmap.Config;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

public class RightListAdapter extends ArrayAdapter<RightItem> {

	Context mContext;
	ArrayList<RightItem> data;
	int resourceId;


	public RightListAdapter(Context context, int textViewResourceId,
			ArrayList<RightItem> objects) {
		super(context, textViewResourceId, objects);
		this.mContext = context;
		this.resourceId = textViewResourceId;
		this.data = objects;
		// TODO Auto-generated constructor stub
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent)
	{
		View row = convertView;
		cHolder holder = null;
		if(row == null)
		{
			LayoutInflater inflator = ((Activity)mContext).getLayoutInflater();
			row = inflator.inflate(resourceId, parent, false);
			holder = new cHolder();
			holder.cColor = (ImageView)row.findViewById(R.id.cColor);
			holder.cName = (TextView)row.findViewById(R.id.cName);
			row.setTag(holder);
		}
		else
		{
			holder =(cHolder)row.getTag();
		}
		
		RightItem item = data.get(position);
		Bitmap bmp = Bitmap.createBitmap(100, 100, Config.ARGB_8888);
		Canvas c = new Canvas(bmp);
		Paint p = new Paint();
		p.setColor(Color.parseColor(item.color));
		c.drawCircle(50, 50, 50, p);
		holder.cName.setText(item.cName);
		holder.cColor.setImageBitmap(bmp);
		return row;
	}


	static class cHolder
	{
		int id;
		TextView cName;
		ImageView cColor;
	}

}
