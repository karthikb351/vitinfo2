package com.karthikb351.vitinfo2;


import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;

public class About extends Activity {
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.about);
		
	}
	
	public void feedback(View v)
	{
		Intent emailIntent = new Intent(android.content.Intent.ACTION_SEND);  
		String aEmailList[] = { "karthikb351@gmail.com"};
		emailIntent.putExtra(android.content.Intent.EXTRA_EMAIL, aEmailList);  
		emailIntent.putExtra(android.content.Intent.EXTRA_SUBJECT, "[VITinfo] Feedback");  
		emailIntent.setType("plain/text");  
		emailIntent.putExtra(android.content.Intent.EXTRA_TEXT, "Tell why you're :) or :(");  
		startActivity(emailIntent);  
	}
	
	public void getInTouch(View v)
	{
		Intent emailIntent = new Intent(android.content.Intent.ACTION_SEND);  
		String aEmailList[] = { "karthikb351@gmail.com"};
		emailIntent.putExtra(android.content.Intent.EXTRA_EMAIL, aEmailList);  
		emailIntent.putExtra(android.content.Intent.EXTRA_SUBJECT, "[VITinfoApp] I'm interested in joining your team!");  
		emailIntent.setType("plain/text");  
		emailIntent.putExtra(android.content.Intent.EXTRA_TEXT, "");  
		startActivity(emailIntent); 
	}

}
