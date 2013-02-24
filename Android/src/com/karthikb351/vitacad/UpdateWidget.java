package com.karthikb351.vitacad;
import com.karthikb351.vitinfo2dev.R;

import android.app.PendingIntent;
import android.app.Service;
import android.appwidget.AppWidgetManager;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.IBinder;
import android.preference.PreferenceManager;
import android.widget.RemoteViews;

public class UpdateWidget extends Service {

	@Override
	public IBinder onBind(Intent arg0) {
		// TODO Auto-generated method stub
		return null;
	}
	
	  @SuppressWarnings({ "deprecation" })
	@Override
	  public void onStart(Intent intent, int startId) {
		  AppWidgetManager appWidgetManager = AppWidgetManager.getInstance(this
			        .getApplicationContext());
		  int[] allWidgetIds = intent.getIntArrayExtra(AppWidgetManager.EXTRA_APPWIDGET_IDS);
		  
		  //CHECK WHICH BUTTON WAS PRESSED IF NON RETURN 2
		  int back = intent.getIntExtra("back", 2);
		  
		  //GET DATA
		  DataHandler dat = new DataHandler(getApplicationContext());
		  SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
		  SharedPreferences.Editor editor = preferences.edit();
		  
		  
		  //CURRENT LOCATION IN WIDGET
		  int cur = 0;
		  
		  //GO FRONT OR BACK
		  if (back == 2)
			  cur = preferences.getInt("CURRENT", 0);
		  else if (back == 0)
			  cur = preferences.getInt("CURRENT", 0) +1 ;
		  else if (back == 1)
			  cur = preferences.getInt("CURRENT", 0) - 1;
		  
		 //CHECK IF REACHED LIMITS 
		  if (cur<0)
			  cur = dat.getSubLength()-1;
		  else if (cur >= dat.getSubLength()){
			  cur = 0;
		  }
		  
		  //SAVE CURRENT POSITION FOR FUTURE USE
		  editor.putInt("CURRENT", cur);
		  editor.commit();
		  
		
		      
		  
		  //WIDGET UPDATE CODE
		  for (int widgetId : allWidgetIds) {
			  RemoteViews remoteViews = new RemoteViews(this.getApplicationContext().getPackageName(), R.layout.widget_layout);
			  
			  Subject sub;
			  
			  //LOAD THE DATA
			  sub = dat.loadSubject(cur);
			  
			  //SET DATA OF THE SUBJECT
			  remoteViews.setTextViewText(R.id.lbl_wid_sub , sub.title);
			  remoteViews.setProgressBar(R.id.prg_wid_per, sub.conducted, sub.attended , false);
			  remoteViews.setTextViewText(R.id.lbl_wid_slt, sub.slot+" ");
			  remoteViews.setTextViewText(R.id.lbl_wid_per, String.valueOf(sub.percentage)+"%");
			  
			  
			  //ONCLICK LISTENERS DONT CHANGE THESE
			  Intent clickIntent = new Intent(this.getApplicationContext(),ProviderWidget.class);
			  Intent clickIntent2 = new Intent(this.getApplicationContext(),ProviderWidget.class);
			 
			  clickIntent.setAction(AppWidgetManager.ACTION_APPWIDGET_UPDATE);
			  clickIntent.putExtra(AppWidgetManager.EXTRA_APPWIDGET_IDS,allWidgetIds);
			  clickIntent.putExtra("back",0);	
			  
			  clickIntent2.setAction(AppWidgetManager.ACTION_APPWIDGET_UPDATE);
			  clickIntent2.putExtra(AppWidgetManager.EXTRA_APPWIDGET_IDS,allWidgetIds);
			  clickIntent2.putExtra("back",1);	
			  
			  
			  PendingIntent pendingIntent = PendingIntent.getBroadcast(getApplicationContext(), 0, clickIntent,PendingIntent.FLAG_UPDATE_CURRENT);
			  PendingIntent pendingIntent2 = PendingIntent.getBroadcast(getApplicationContext(), 1, clickIntent2,PendingIntent.FLAG_UPDATE_CURRENT);
			  remoteViews.setOnClickPendingIntent(R.id.img_wid_nxt , pendingIntent);
			  remoteViews.setOnClickPendingIntent(R.id.img_wid_bck , pendingIntent2);
			  
			  
			  appWidgetManager.updateAppWidget(widgetId, remoteViews);

		  }
		  stopSelf();
		  super.onStart(intent, startId);
		  
	  }
	
	
	

}
