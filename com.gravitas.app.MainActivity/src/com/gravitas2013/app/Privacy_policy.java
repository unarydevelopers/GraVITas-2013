package com.gravitas2013.app;

import com.actionbarsherlock.app.SherlockActivity;
import com.gravitas2013.app.R;

import android.os.Bundle;
import android.app.Activity;
import android.view.View;

public class Privacy_policy extends SherlockActivity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_privacy_policy);
		
		getSupportActionBar().setTitle("Privacy Policy");
		//WebView privacy = (WebView)findViewById(R.id.webView1);
		//privacy.loadUrl("https://docs.google.com/viewer?embedded=true&url=http://ieeevit.com/gravitas/privacy_policy_1.pdf");
	}

	

	public void backButton(View view)
	{
		this.onBackPressed();
	}

}
