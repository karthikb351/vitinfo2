package com.karthikb351.vitacad;

import java.util.ArrayList;

import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;

import com.actionbarsherlock.app.SherlockActivity;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

public class DownloadAttendance extends SherlockActivity{
	

	
	SharedPreferences settings;
	SharedPreferences.Editor editor;
	String regno,dob,captcha;
	LoadAttendanceTask currentTask;
	boolean haveCap=false;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		start();
		Intent returnIntent = new Intent();
		returnIntent.putExtra("result", "cancelled");
		setResult(RESULT_CANCELED, returnIntent);  
	}
	
	void startLoadAttendance()
	{
		if(settings.getBoolean("newuser", true))
		{
			loginDiag();
		}
		else
		{
			settings=getSharedPreferences("vitacad", 0);
			regno=settings.getString("regno", " ");
			dob=settings.getString("dob", " ");
			currentTask=new LoadAttendanceTask();
			ArrayList<String> details = new ArrayList<String>();
			details.add(0,regno);
			details.add(1,dob);
			currentTask.execute(details);
		}
	}
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		
		if(requestCode==DataHandler.CAPTCHA_REQUEST)
		{
			String result=data.getStringExtra("result");
			if(resultCode==DataHandler.RESULT_ERROR)
			{
				
			}
		}
		
	}
	
	private class LoadAttendanceTask extends AsyncTask<ArrayList <String>, Void, String>
	{
		ProgressDialog pdia;
		boolean cancelled=false;
		String url;
		protected void onPreExecute() {
			
		  	pdia = new ProgressDialog(DownloadAttendance.this);
	        pdia.setMessage("Loading Attendance");
	        pdia.setCancelable(false);
	        pdia.setOnCancelListener(new DialogInterface.OnCancelListener() {
				
				@Override
				public void onCancel(DialogInterface dialog) {
					cancelled=true;
					currentTask.onCancelled("cancelled");
					
				}
			});
	        pdia.setProgressStyle(ProgressDialog.STYLE_SPINNER);
	        pdia.show();
	  }
		@Override
		protected String doInBackground(ArrayList <String>... params) {
			// TODO Auto-generated method stub
			String res="";
			ArrayList <String> details=params[0];
			String urldisplay = "http://vitacademicsdev.appspot.com/att/"+details.get(0)+"/"+details.get(1);
			url=urldisplay;
			try {
				HttpClient client = new DefaultHttpClient();
				HttpGet request = new HttpGet(urldisplay);
				HttpResponse response;
				response = client.execute(request);
				res=EntityUtils.toString(response.getEntity());
				}
			
			catch (Exception e) {
				res="error";
				Log.e("Error", e.getMessage());
				e.printStackTrace();
				}
			if(cancelled)
				return "cancelled";
			else
				return res;
		}
		@Override
		protected void onCancelled(String result) {
			Intent returnIntent = new Intent();
			Log.i("cancelled",result);
			returnIntent.putExtra("result", "cancelled");
		 	setResult(RESULT_CANCELED,returnIntent);
		 	finish();
		}
		@Override
		protected void onPostExecute(String result) {

			Toast.makeText(DownloadAttendance.this, "got:"+result+" due to url :"+url, Toast.LENGTH_LONG).show();
			if(!result.equals("cancelled"))
			{
				
				Intent returnIntent = new Intent();
				if(result.contains("timedout"))
				{
					Log.i("timedout",result);
					returnIntent.putExtra("result", result);
				 	setResult(DataHandler.RESULT_TIMEDOUT,returnIntent);
				}
				else if(result.contains("valid"))
				{
					returnIntent.putExtra("result", result);
					Log.i("json", result);
				 	setResult(RESULT_OK,returnIntent);
				}
				else
				{
					Log.e("error",result);
					returnIntent.putExtra("result", "error");
				 	setResult(DataHandler.RESULT_ERROR,returnIntent);
				}
				
			}
			if (pdia.isShowing()) {
				   pdia.dismiss();
				}

			finish();
				
		}
	}
}
