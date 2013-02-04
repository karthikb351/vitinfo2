package com.karthikb351.vitacad;

import java.util.ArrayList;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.telephony.TelephonyManager;
import android.text.format.DateUtils;
import android.text.format.Time;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.actionbarsherlock.app.SherlockActivity;
import com.crittercism.app.Crittercism;
import com.google.analytics.tracking.android.GAServiceManager;
import com.google.analytics.tracking.android.GoogleAnalytics;
import com.google.analytics.tracking.android.Tracker;
import com.helpshift.Helpshift;
import com.karthikb351.vitinfo2dev.R;

public class MainActivity extends SherlockActivity {
	
	final Helpshift hs = new Helpshift();
	static Tracker mTracker;
	static GoogleAnalytics mInstance;
	SharedPreferences settings;
	SharedPreferences.Editor editor;
	boolean newuser;
	String DOB, REGNO;
	ListView listViewSub;
	TextView tv;
	void extrasInit()
	{
    	JSONObject crittercismConfig = new JSONObject();
    	mInstance = GoogleAnalytics.getInstance(this);
    	mTracker = mInstance.getTracker("UA-38195928-1");
    	GAServiceManager.getInstance().setDispatchPeriod(30);
    	try
    	{
    	    crittercismConfig.put("shouldCollectLogcat", true); // send logcat data for devices with API Level 16 and higher
    	    crittercismConfig.put("customVersionName", this.getApplicationInfo().packageName);
    	}
    	catch (JSONException je){}

    	//Crittercism.init(getApplicationContext(), "50e22966f71696783c000012", crittercismConfig);
    	
    	hs.install(MainActivity.this,
    			"91ff50eded9d62de7020a839c1e2292e",
    			"vitinfo-android.helpshift.com",
    			"vitinfo-android_platform_20130101001453620-203e6cbb7463f6f");
	}
    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
    	extrasInit();
    	settings = getSharedPreferences("vitacad", 0);
    	TelephonyManager tManager = (TelephonyManager)MainActivity.this.getSystemService(Context.TELEPHONY_SERVICE);
    	String uid = tManager.getDeviceId();
    	Crittercism.setUsername(settings.getString("regno", "USERNAME"));
    	hs.setDeviceIdentifier (settings.getString("regno", uid));
    	hs.setUsername (settings.getString("regno", "NEWUSER"));
    	super.onCreate(savedInstanceState);
    	setContentView(R.layout.main);
    	mTracker.sendView("/MainActivity");
    	tv=(TextView)findViewById(R.id.updateOn);
    	listViewSub=(ListView)findViewById(R.id.list);
    	loadAtten();
    }
    
    @Override
    public boolean onCreateOptionsMenu(com.actionbarsherlock.view.Menu menu) {
        getSupportMenuInflater().inflate(R.menu.menu, menu);
        return true;
    }
    
    @Override
    public boolean onOptionsItemSelected(com.actionbarsherlock.view.MenuItem item) {
    	
        switch (item.getItemId()) {
        	case R.id.about:
        		mTracker.sendEvent("ui_action", "button_press", "about_button", 0l);
            	hs.showSupport(MainActivity.this);
        		//startActivity(new Intent(this, About.class));
        		return true;
        	
        	case R.id.refreshAtt:
        		mTracker.sendEvent("ui_action", "button_press", "refresh_attendance_button", 0l);
        		if(!settings.getBoolean("credentials", false))
					loginDialog();
				else
				{
					Intent i = new Intent(getApplicationContext(),GetStuff.class);
					i.putExtra("what", "atten");
				    startActivityForResult(i,1);
				}
        		return true;
        	/*case R.id.refreshMarks:
        		if(!settings.getBoolean("credentials", false))
					loginDialog();
				else
				{
					Intent i = new Intent(getApplicationContext(),GetStuff.class);
					i.putExtra("what", "marks");
				    startActivityForResult(i,2);
				}
        		return true;*/
        	case R.id.details:
        		loginDialog();
				return true;
        	case R.id.share:
        		Intent sharingIntent = new Intent(android.content.Intent.ACTION_SEND);
        		sharingIntent.setType("text/plain");
        		String shareBody = "Check your VIT Attendance on your android phone! http://play.google.com/store/apps/details?id=com.karthikb351.vitinfo2";
        		sharingIntent.putExtra(android.content.Intent.EXTRA_TEXT, shareBody);
        		startActivity(Intent.createChooser(sharingIntent, "Make us famous via"));
        		mTracker.sendEvent("ui_action", "button_press", "share_button", 0l);
        		return true;
        	default:
        		return super.onOptionsItemSelected(item);
        }
    }
 
    void saveAtteToSharedPrefs(String source)
    {
    	String tags[]={"sl_no","code","title","type","slot","regdate","attended","conducted","percentage","extra", "classnbr"};
		JSONArray array;
		ArrayList<String> list=new ArrayList();
		try {
			array = new JSONArray(source);
		
		int k=0;
		for(int i=0;i<array.length();i++)
		{	
			if(i%10==0&&i!=0)
			{
				k=0;
			}
			editor.putString("Subject_"+k+"_"+tags[k],array.get(i).toString());
			k++;
		}
		} catch (JSONException e) {
			Toast.makeText(MainActivity.this, "Error saving attendance", Toast.LENGTH_SHORT).show();
			e.printStackTrace();
		}
    }
    void initListview()
    {
    	String tag[]={"sl_no","code","title","type","slot","regdate","att","con","per","extra"};
		int size=settings.getInt("Subject_size", 0);
    	for(int i=0;i<size;i++)
    	{
    		
    	}
    }
    void saveAttenToSharedPrefs(String source)
    {
    }
    
    void saveMarksToSharedPrefs(String source)
    {
    	Toast.makeText(MainActivity.this, "Got Marks!", Toast.LENGTH_SHORT).show();
    	
    }
    void loadAtten()
    {
    	if(settings.getBoolean("newuser", true))
    	{
    		if(settings.getBoolean("credentials", false))
    		{
    			Intent i = new Intent(MainActivity.this,GetStuff.class);
    			i.putExtra("what", "atten");
    		}
    		else
    		{
    			Toast.makeText(MainActivity.this, "Please enter your credentials", Toast.LENGTH_SHORT).show();
    			loginDialog();
    		}
    	}
    	else
    	{
    		String title,code,type,slot,nbr;
    		int max, atten,size;
            size=settings.getInt("Sub_size",0);
            List listSub= new ArrayList();
            for(int i=1;i<size;i++)
    		{
            	nbr=String.valueOf(i);
            	code=settings.getString("Sub_"+nbr+"code","DB Error");
    			title=settings.getString("Sub_"+nbr+"title","DB Error"); 
    			type=settings.getString("Sub_"+nbr+"type","DB Error"); 
    			slot=settings.getString("Sub_"+nbr+"slot","DB Error");
    			atten=settings.getInt("Sub_"+nbr+"atten",0); 
    			max=settings.getInt("Sub_"+nbr+"max",0);
    			Log.i("loaded:",title+" "+code+" "+slot);
    		}
            listViewSub.setOnItemClickListener(otcl);
            Time t=new Time(Time.getCurrentTimezone());
            t.setToNow();
            long now=t.toMillis(false);
            long then=settings.getLong("updateTime",0);
            String update;
            update="Last refreshed: "+(String)DateUtils.getRelativeTimeSpanString(then,now,DateUtils.MINUTE_IN_MILLIS);
            if(update==null||then==0)
            	update="Not updated. Go to Menu->Refresh";
        	tv.setText(update);
            listViewSub.setAdapter( new SubjectAdapter(MainActivity.this, R.layout.single_item_sub, listSub ) );
    		
    	}
    	
    	
    }
     OnItemClickListener otcl=new OnItemClickListener() {
	   
		public void onItemClick(AdapterView<?> parent, View view, int position, long id)
		{
			Intent a=new Intent(MainActivity.this, SubjectDetails.class);
			a.putExtra("index", position+1);
			startActivity(a);
		}
	};
    
	
	void loginDialog()
    {
		Log.i("status", "inside logindialog()");
    	LayoutInflater inflator=getLayoutInflater();
        View popup=inflator.inflate(R.layout.details_popup, null);
        editor=settings.edit();
        final String loadregno=settings.getString("regno", " ");
        final int loaddiagyy=settings.getInt("dobyy", 1993);
        final int loaddiagmm=settings.getInt("dobmm", 2);
        final int loaddiagdd=settings.getInt("dobdd", 14);
        final EditText edit=(EditText)popup.findViewById(R.id.diagRegno);
    	final DatePicker dp=(DatePicker)popup.findViewById(R.id.datePicker);
    	if(!loadregno.equals(" "))
    		edit.setText(loadregno);
    	dp.updateDate(loaddiagyy, loaddiagmm, loaddiagdd);
    	AlertDialog.Builder builder;
        builder=new AlertDialog.Builder(this);
        builder.setCancelable(false);
        builder.setTitle("Login").setView(popup);
        builder.setPositiveButton("Login", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int whichButton) {
            	
            	int d,m,y;
            	REGNO=edit.getText().toString();
            	d=dp.getDayOfMonth();
            	m=dp.getMonth()+1;
            	y=dp.getYear();
            	DOB=dateFormat(d, m, y);
            	editor.putInt("dobdd", d).putInt("dobmm", m-1).putInt("dobyy", y);
            	if(loaddiagdd!=d||loaddiagmm!=m-1||loaddiagyy!=y||!loadregno.equals(REGNO))
            	{
            		editor.putBoolean("newuser", true);
            		mTracker.sendEvent("user_changed", loadregno, REGNO, 0l);
            	}
            	editor.commit();
            	diagLogin();
            }
        })
        .setNegativeButton("Cancel",null).show();
        
    }
	boolean checkcredentials()
    {
    	boolean flag=false;
    	if(REGNO.length()==9)
    		if(REGNO.matches("(\\d)+(\\d)+([A-Z])+([A-Z])+([A-Z])+(\\d)+(\\d)+(\\d)")||REGNO.matches("(\\d)+(\\d)+([A-Z])+([A-Z])+([A-Z])+(\\d)+(\\d)+(\\d)+(\\d)"))
    			flag=true;
    	return flag;
    	
    }
	void diagLogin()
    {
    	if(!checkcredentials())
    	{
    		Toast.makeText(this, "Invalid Registration Number!", Toast.LENGTH_SHORT).show();
    	}
    	else
    	{
    		editor.putString("regno", REGNO);
    		editor.putString("dob", DOB);
    		editor.putBoolean("credentials", true);
    		editor.commit();
    		if(settings.getBoolean("newuser", true))
    				loadAtten();
    	}
    }
	
    String dateFormat(int d, int m, int y)
    {
    	String dd="",mm="",yy="";
    	if(d<10)
    		dd="0";
    	if(m<10)
    		mm="0";
    	dd+=String.valueOf(d);
    	mm+=String.valueOf(m);
    	yy=String.valueOf(y);
    	return dd+mm+yy;
    }
	
	
	
	
	
    
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {

    	String result=data.getStringExtra("result");
    	if (requestCode == 1)
    	{
    		if(resultCode == RESULT_OK)
    		{
    			saveAttenToSharedPrefs(result);
        		Toast.makeText(MainActivity.this, "Success!", Toast.LENGTH_SHORT).show();
    			
    		}
	    	if (resultCode == RESULT_CANCELED)
	    	{
	    		if(result.equals("error"))
	    			Toast.makeText(MainActivity.this, "Something went wrong", Toast.LENGTH_SHORT).show();
	    		else if(result.equals("redirect"))
	    			Toast.makeText(MainActivity.this, "Incorrect Captcha", Toast.LENGTH_SHORT).show();
	    		else
	    			Toast.makeText(MainActivity.this, "Cancelled", Toast.LENGTH_SHORT).show();
	    		
	    	}
    	}
    	else if(requestCode == 2)
    	{
    		if(resultCode == RESULT_OK)
    		{
    			Bundle b=new Bundle();
    			saveMarksToSharedPrefs(result);
        		Toast.makeText(MainActivity.this, "Success! Booyeah!", Toast.LENGTH_SHORT).show();
    			
    		}
	    	if (resultCode == RESULT_CANCELED)
	    	{
	    		if(result.equals("error"))
	    			Toast.makeText(MainActivity.this, "Error fetching Attendance", Toast.LENGTH_SHORT).show();
	    		else if(result.equals("redirect"))
	    			Toast.makeText(MainActivity.this, "Incorrect Captcha", Toast.LENGTH_SHORT).show();
	    		else
	    			Toast.makeText(MainActivity.this, "Cancelled", Toast.LENGTH_SHORT).show();
	    		
	    	}
    	}
    }
    
}