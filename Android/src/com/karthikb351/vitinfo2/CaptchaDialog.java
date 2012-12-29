package com.karthikb351.vitinfo2;

import java.io.InputStream;
import java.util.ArrayList;

import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.Display;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.karthikb351.vitinfotest.R;

public class CaptchaDialog extends Activity {
	
	SharedPreferences settings;
	SharedPreferences.Editor editor;
	String regno="",dob="",captcha="",result="";
	boolean newuser;
	Button refresh;
	ImageView imCaptcha;
	AlertDialog.Builder builder;
	DownloadImageTask currentLCTask;
	SubmitCaptchaTask currentSCTask;
	EditText captcha_edittext;
	View view;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		Intent i =getIntent();
		newuser=i.getBooleanExtra("newuser", true);
		settings=getSharedPreferences("vitinfotest", 0);
		regno=settings.getString("regno", " ");
		dob=settings.getString("dob", " ");
		Log.i("status", "OnCreate");
		view= getLayoutInflater().inflate(R.layout.captcha_dialog, null);
		builder= new AlertDialog.Builder(CaptchaDialog.this);
		imCaptcha=(ImageView)view.findViewById(R.id.captcha_img);
		refresh = (Button)view.findViewById(R.id.captcha_refresh);
    	refresh.setOnClickListener(ocl);
		builder.setView(view).setCancelable(false).setPositiveButton("Enter", diagocl).setNegativeButton("Cancel", diagocl).setTitle("Enter Captcha");
		AlertDialog dialog = builder.create();
    	dialog.show();
    	
    	currentLCTask=new DownloadImageTask(imCaptcha);
    	currentLCTask.execute(regno);
		
	}
	
	android.content.DialogInterface.OnClickListener diagocl = new android.content.DialogInterface.OnClickListener() {
		
		@Override
		public void onClick(DialogInterface dialog, int which) {
			
			
			switch(which)
			{
				case DialogInterface.BUTTON_POSITIVE:
					captcha_edittext=(EditText)(view.findViewById(R.id.captcha_edittext));
					captcha=captcha_edittext.getText().toString();
					Toast.makeText(CaptchaDialog.this, captcha, Toast.LENGTH_SHORT).show();
					submitCaptcha();
					break;
				case DialogInterface.BUTTON_NEGATIVE:
					cancelled();
					break;
			
			}
			
			
		}
	};
	OnClickListener ocl=new OnClickListener() {
		
		@Override
		public void onClick(View v) {
		
			if(currentLCTask.getStatus()==AsyncTask.Status.RUNNING)
				Toast.makeText(CaptchaDialog.this, "Still Loading", Toast.LENGTH_SHORT).show();
			else
			{
				currentLCTask=new DownloadImageTask(imCaptcha);
				currentLCTask.execute(regno);
			}
		}
	};
	
	@SuppressWarnings("unchecked")
	void submitCaptcha()
	{
		ArrayList<String> details = new ArrayList<String>();
		details.add(0,regno);
		details.add(1,dob);
		details.add(2,captcha);
		currentSCTask = new SubmitCaptchaTask();
		currentSCTask.execute(details);
		
	}
	
	void cancelled()
	{
		Intent returnIntent = new Intent();
		returnIntent.putExtra("result", "cancelled");
		setResult(RESULT_CANCELED, returnIntent);        
		finish();
	}
	
	
	private class SubmitCaptchaTask extends AsyncTask<ArrayList <String>, Void, String>
	{
		ProgressDialog pdia;
		
		protected void onPreExecute() {
			
		  	pdia = new ProgressDialog(CaptchaDialog.this);
	        pdia.setMessage("Fetching Attendance");
	        pdia.setCancelable(true);
	        pdia.setOnCancelListener(new DialogInterface.OnCancelListener() {
				
				@Override
				public void onCancel(DialogInterface dialog) {
					cancel(true);
					
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
			String urldisplay = "http://vitattx.appspot.com/att/"+details.get(0)+"/"+details.get(1)+"/"+details.get(2);
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
			if(isCancelled())
				return "cancelled";
			else
			return res;
		}
		
		protected void onPostExecute(String result) {

			if(!result.equals("cancelled"))	{
				
				Intent returnIntent = new Intent();
				if(result.equals("error")){
					returnIntent.putExtra("result", "error");
					setResult(RESULT_CANCELED, returnIntent);
				}
				else if(result.contains("redirect")){
					returnIntent.putExtra("result", "redirect");
					setResult(RESULT_CANCELED, returnIntent);
				}
				else {

				 	returnIntent.putExtra("result",result);
				 	setResult(RESULT_OK,returnIntent);
					
				}
				pdia.dismiss();
				finish();
			}
			else
				cancelled();
				
		}
	}
	
	private class DownloadImageTask extends AsyncTask<String, Void, Bitmap> {
		  ImageView bmImage;
		  ProgressDialog pdia;

		  public DownloadImageTask(ImageView bmImage) {
		      this.bmImage = bmImage;
		  }

		  protected void onPreExecute() {
			
			  	pdia = new ProgressDialog(CaptchaDialog.this);
		        pdia.setMessage("Fetching Captcha");
		        pdia.setCancelable(true);
		        pdia.setOnCancelListener(new DialogInterface.OnCancelListener() {
					
					@Override
					public void onCancel(DialogInterface dialog) {
						Display display = getWindowManager().getDefaultDisplay();
						@SuppressWarnings("deprecation")
						int width=(int)(display.getWidth()*0.6);
						int height=(int)(width*25/130);
						LinearLayout.LayoutParams parms = new LinearLayout.LayoutParams(width,height);
					    parms.setMargins(10, 10, 10, 10);
					    bmImage.setLayoutParams(parms);
						bmImage.setImageResource(R.drawable.ic_captcha_error);
						cancel(true);
					}
				});
		        pdia.setProgressStyle(ProgressDialog.STYLE_SPINNER);
		        pdia.show();
		  }
		  protected Bitmap doInBackground(String... urls) {
		      String urldisplay = "http://vitattx.appspot.com/captcha/"+urls[0];
		      Bitmap mIcon11 = null;
		      try {
		    	  /*
		    	  HttpParams params=new BasicHttpParams();
		    	  HttpConnectionParams.setConnectionTimeout(params, 15000);
		    	  HttpConnectionParams.setSoTimeout(params, 15000);
		    	  //HttpClient client = new DefaultHttpClient(params);
		    	  HttpClient client = new DefaultHttpClient();
		    	  HttpGet request = new HttpGet(urldisplay);
		    	  HttpResponse response;
		    	  response = client.execute(request);
		        //InputStream in = new java.net.URL(urldisplay).openStream();
		    	  HttpEntity entity=response.getEntity();
		    	  mIcon11 = BitmapFactory.decodeStream(entity.getContent());
		    	  */
		    	  
		    	  InputStream in = new java.net.URL(urldisplay).openStream();
		    	  mIcon11 = BitmapFactory.decodeStream(in);
		    	  
		    	  
		      } catch (Exception e) {
		    	  mIcon11=null;
		          Log.e("Error", e.getMessage());
		          e.printStackTrace();
		      }
		      if(isCancelled())
		    	  return null;
		      else
		    	  return mIcon11;
		  }
		  

		  protected void onPostExecute(Bitmap result) {
			  Display display = getWindowManager().getDefaultDisplay();
			  @SuppressWarnings("deprecation")
			  int width=(int)(display.getWidth()*0.6);
			  int height=(int)(width*25/130);
			  LinearLayout.LayoutParams parms = new LinearLayout.LayoutParams(width,height);
			  parms.setMargins(10, 10, 10, 10);
			  bmImage.setLayoutParams(parms);
			  if(result==null)
			  {
				  Toast.makeText(CaptchaDialog.this, "Error fetching Captcha. Try again.", Toast.LENGTH_SHORT).show();
				  bmImage.setImageResource(R.drawable.ic_captcha_error);
			  }
			  else
			  {
			      bmImage.setImageBitmap(result);
			      
			  }
			  pdia.dismiss();
		  }
		}
	
}