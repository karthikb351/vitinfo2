package com.karthikb351.vitinfo2;

import java.util.ArrayList;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

public class GetAttendance extends Activity {
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		getAtten();
	}
	

	
	void getAtten()
	{
		Intent i = new Intent(GetAttendance.this, DownloadAttendance.class);
		startActivityForResult(i, 4);
	}
	void getCaptcha()
	{
		Intent i = new Intent(GetAttendance.this,CaptchaDialog.class);
		startActivityForResult(i, 5);
	}
	
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {

    	String result=data.getStringExtra("result");
    	if (requestCode == 4)
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
	    		if(result.equals("error"))
	    		{
	    			Toast.makeText(GetAttendance.this, "Error fetching Attendance", Toast.LENGTH_SHORT).show();
	    		}
	    		else if(result.equals("timedout"))
	    		{
	    			getCaptcha();
	    		}
	    		
	    	}
    	}
    	if (requestCode == 5)
    	{
    		if(resultCode == RESULT_OK)
    		{
    			Log.i("status","Captcha Sent!");
    			getAtten();
    		}
	    	if (resultCode == RESULT_CANCELED)
	    	{
	    		if(result.equals("error"))
	    		{
	    			Toast.makeText(GetAttendance.this, "Error fetching Attendance", Toast.LENGTH_SHORT).show();
	    		}
	    		else if(result.equals("timedout"))
	    		{
	    			getCaptcha();
	    		}
	    		else if(result.equals("cancelled"))
	    		{
	    			Toast.makeText(GetAttendance.this, "Error fetching Attendance", Toast.LENGTH_SHORT).show();
	    			finish();
	    		}
	    		
	    	}
    	}
    }

}
