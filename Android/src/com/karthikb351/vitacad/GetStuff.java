package com.karthikb351.vitacad;

import com.actionbarsherlock.app.SherlockActivity;


import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

public class GetStuff extends SherlockActivity {
	final static int CAPTCHA=11,ATTEN=22,DETAILS=33;
	String s;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		Intent returnIntent=new Intent();
		returnIntent.putExtra("result", "error");
	 	setResult(RESULT_CANCELED,returnIntent);
		Intent i=getIntent();
		s=i.getStringExtra("what");
		if(s.equals("marks"))
			getMarks();
		else if(s.equals("atten"))
			getAtten();
		else if(s.equals("details"))
			getDetails(i.getStringExtra("sub"));
		else
		{
			finish();
		}
	}
	void getMarks()
	{
		Intent i = new Intent(GetStuff.this, DownloadMarks.class);
		i.putExtra("what", s);
		startActivityForResult(i, 3);
	}

	void getAtten()
	{
		Intent i = new Intent(GetStuff.this, DownloadAttendance.class);
		i.putExtra("what", s);
		startActivityForResult(i, ATTEN);
	}
	void getDetails(String s)
	{
		Intent i =new Intent(GetStuff.this, DownloadAttenDetails.class);
		i.putExtra("sub", s);
		Toast.makeText(GetStuff.this, "Subject code is: "+s, Toast.LENGTH_SHORT).show();
		startActivityForResult(i, DETAILS);
	}
	void getCaptcha()
	{
		Intent i = new Intent(GetStuff.this,CaptchaDialog.class);
		i.putExtra("what", s);
		startActivityForResult(i, CAPTCHA);
	}
	
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {

    	String result=data.getStringExtra("result");
    	if (requestCode == ATTEN)
    	{
    		if(resultCode == RESULT_OK)
    		{
    			Log.i("status","Got attendance!");
    			Intent returnIntent=new Intent(); 
				returnIntent.putExtra("result", result);
			 	setResult(RESULT_OK,returnIntent);
			 	finish();
    		}
	    	if (resultCode == RESULT_CANCELED)
	    	{
	    		if(result.equals("timedout"))
	    		{
	    			getCaptcha();
	    		}
	    		else if(result.equals("cancelled"))
	    		{
	    			Toast.makeText(GetStuff.this, "Cancelled", Toast.LENGTH_SHORT).show();
	    			finish();
	    		}
	    		else
	    		{
	    			Toast.makeText(GetStuff.this, "Error fetching Attendance", Toast.LENGTH_SHORT).show();
	    			finish();
	    		}
	    		
	    	}
    	}
    	if (requestCode == CAPTCHA)
    	{
    		if(resultCode == RESULT_OK)
    		{
    			Log.i("status","Captcha Sent!");
    			if(s.equals("marks"))
    				getMarks();
    			else if(s.equals("atten"))
    				getAtten();
    			else
    				finish();
    		}
	    	if (resultCode == RESULT_CANCELED)
	    	{
	    		if(result.equals("error"))
	    		{
	    			Toast.makeText(GetStuff.this, "Error", Toast.LENGTH_SHORT).show();
	    			finish();
	    		}
	    		else if(result.equals("timedout"))
	    		{
	    			getCaptcha();
	    		}
	    		else if(result.equals("cancelled"))
	    		{
	    			Toast.makeText(GetStuff.this, "Cancelled", Toast.LENGTH_SHORT).show();
	    			finish();
	    		}
	    	}
    	}
    	if (requestCode == DETAILS)
		{
    		if(resultCode == RESULT_OK)
    		{
    			Log.i("status","Got details!");
    			Intent returnIntent=new Intent(); 
				returnIntent.putExtra("result", result);
			 	setResult(RESULT_OK,returnIntent);
			 	finish();
    		}
	    	if (resultCode == RESULT_CANCELED)
	    	{
	    		if(result.equals("timedout"))
	    		{
	    			getCaptcha();
	    		}
	    		else if(result.equals("cancelled"))
	    		{
	    			Toast.makeText(GetStuff.this, "Cancelled", Toast.LENGTH_SHORT).show();
	    			finish();
	    		}
	    		else
	    		{
	    			Toast.makeText(GetStuff.this, "Error fetching Details", Toast.LENGTH_SHORT).show();
	    			finish();
	    		}
	    		
	    	}
        }
    }

}
