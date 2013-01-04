package com.karthikb351.vitinfo2;

import java.util.ArrayList;
import java.util.List;

import org.json.JSONException;
import org.json.JSONObject;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.format.Time;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.crittercism.app.Crittercism;
import com.helpshift.Helpshift;
import com.karthikb351.vitinfo2.R;

public class MainActivity extends Activity {
	
	final Helpshift hs = new Helpshift();
	SharedPreferences settings;
	SharedPreferences.Editor editor;
	boolean newuser;
	String DOB, REGNO;
	ListView listViewSub;
	TextView tv;
	
	void extrasInit()
	{
    	JSONObject crittercismConfig = new JSONObject();
    	try
    	{
    	    crittercismConfig.put("shouldCollectLogcat", true); // send logcat data for devices with API Level 16 and higher
    	    crittercismConfig.put("customVersionName", this.getApplicationInfo().packageName);
    	}
    	catch (JSONException je){}

    	Crittercism.init(getApplicationContext(), "50e22966f71696783c000012", crittercismConfig);
    	
    	hs.install(MainActivity.this,
    			"91ff50eded9d62de7020a839c1e2292e",
    			"vitinfo-android.helpshift.com",
    			"vitinfo-android_platform_20130101001453620-203e6cbb7463f6f");
	}
    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
    	extrasInit();
    	settings = getSharedPreferences("vitinfotest", 0);
    	Crittercism.setUsername(settings.getString("regno", "NEWUSER"));
    	hs.setDeviceIdentifier (settings.getString("regno", "NEWUSER"));
    	hs.setUsername (settings.getString("regno", "NEWUSER"));
    	super.onCreate(savedInstanceState);
    	setContentView(R.layout.main);
    	tv=(TextView)findViewById(R.id.updateOn);
    	tv.setText(settings.getString("updateOn", "Last Update not saved. Go to Menu->Refresh"));
    	listViewSub=(ListView)findViewById(R.id.list);
    	loadAtten();
    }
    
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu, menu);
        return true;
    }
    
    public boolean onOptionsItemSelected(MenuItem item) {
    	
        switch (item.getItemId()) {
        	case R.id.about:
            	hs.showSupport(MainActivity.this);
        		//startActivity(new Intent(this, About.class));
        		return true;
        	
        	case R.id.refresh:
        		if(!settings.getBoolean("credentials", false))
					loginDialog();
				else
				{
					Intent i = new Intent(getApplicationContext(),GetAttendance.class);
				    startActivityForResult(i,1);
				}
        		return true;
        	case R.id.details:
        		loginDialog();
				return true;
        	case R.id.share:
        		Intent sharingIntent = new Intent(android.content.Intent.ACTION_SEND);
        		sharingIntent.setType("text/plain");
        		String shareBody = "Check your VIT Attendance on your android phone! http://play.google.com/store/apps/details?id=com.karthikb351.vitinfo2";
        		sharingIntent.putExtra(android.content.Intent.EXTRA_TEXT, shareBody);
        		startActivity(Intent.createChooser(sharingIntent, "Spread the bunking via"));
        		return true;
        	default:
        		return super.onOptionsItemSelected(item);
        }
    }
 
    void saveAttenToSharedPrefs(String source)
    {
    	editor=settings.edit();
    	String title,code,type,slot,nbr,regdate;
		int max, atten,count=1;
        List listSub= new ArrayList();
        Document doc=Jsoup.parse(source);
        Elements elements = doc.getElementsByTag("tr");
        editor.putInt("Sub_size",elements.size()/10);
        for(int i=1;i<elements.size();)
		{
        	nbr=String.valueOf(count);
			elements.get(i++).ownText();
			code=elements.get(i++).ownText();
			editor.putString("Sub_"+nbr+"code",code); 
			title=elements.get(i++).ownText();
			editor.putString("Sub_"+nbr+"title",title); 
			type=elements.get(i++).ownText();
			editor.putString("Sub_"+nbr+"type",type); 
			slot=elements.get(i++).ownText();
			editor.putString("Sub_"+nbr+"slot",slot);
			regdate=elements.get(i++).ownText();
			editor.putString("Sub_"+nbr+"regdate",regdate);
			atten=Integer.parseInt(elements.get(i++).ownText());
			editor.putInt("Sub_"+nbr+"atten",atten); 
			max=Integer.parseInt(elements.get(i++).ownText());
			editor.putInt("Sub_"+nbr+"max",max); 
			elements.get(i++).ownText();
			elements.get(i++).ownText();
			
			Log.i("stored:",title+" "+code+" "+slot);
			listSub.add(new Subject(title, code, type, slot, max, atten));
			count++;
		}
        editor.putBoolean("newuser", false);
        Time today = new Time(Time.getCurrentTimezone());
        today.setToNow();
        String update="Last refreshed on "+today.monthDay+"/"+today.month+1+"/"+today.year+" at "+today.format("%k:%M:%S");
        editor.putString("updateOn", update);
        editor.commit();
        listViewSub.setOnItemClickListener(otcl);
    	tv.setText(settings.getString("updateOn", "Last Update not saved. Go to Menu->Refresh"));
        listViewSub.setAdapter( new SubjectAdapter(MainActivity.this, R.layout.single_item, listSub ) );
        
        
        
		
    }
    
    void saveMarksToSharedPrefs(String source)
    {
    	editor=settings.edit();
    	String title,code,type,slot,nbr,regdate;
		int max, atten,count=1;
        List listSub= new ArrayList();
        Document doc=Jsoup.parse(source);
        Elements elements = doc.getElementsByTag("tr");
        editor.putInt("Sub_size",elements.size()/10);
        for(int i=1;i<elements.size();)
		{
        	nbr=String.valueOf(count);
			elements.get(i++).ownText();
			code=elements.get(i++).ownText();
			editor.putString("Sub_"+nbr+"code",code); 
			title=elements.get(i++).ownText();
			editor.putString("Sub_"+nbr+"title",title); 
			type=elements.get(i++).ownText();
			editor.putString("Sub_"+nbr+"type",type); 
			slot=elements.get(i++).ownText();
			editor.putString("Sub_"+nbr+"slot",slot);
			regdate=elements.get(i++).ownText();
			editor.putString("Sub_"+nbr+"regdate",regdate);
			atten=Integer.parseInt(elements.get(i++).ownText());
			editor.putInt("Sub_"+nbr+"atten",atten); 
			max=Integer.parseInt(elements.get(i++).ownText());
			editor.putInt("Sub_"+nbr+"max",max); 
			elements.get(i++).ownText();
			elements.get(i++).ownText();
			
			Log.i("stored:",title+" "+code+" "+slot);
			listSub.add(new Subject(title, code, type, slot, max, atten));
			count++;
		}
        editor.putBoolean("newuser", false);
        Time today = new Time(Time.getCurrentTimezone());
        today.setToNow();
        String update="Last refreshed on "+today.monthDay+"/"+today.month+1+"/"+today.year+" at "+today.format("%k:%M:%S");
        editor.putString("updateOn", update);
        editor.commit();
        listViewSub.setOnItemClickListener(otcl);
    	tv.setText(settings.getString("updateOn", "Last Update not saved. Go to Menu->Refresh"));
        listViewSub.setAdapter( new SubjectAdapter(MainActivity.this, R.layout.single_item, listSub ) );
        
        
        
		
    }
    	
    
    void loadAtten()
    {
    	if(settings.getBoolean("newuser", true))
    	{
    		if(settings.getBoolean("credentials", false))
    		{
    			Intent i = new Intent(getApplicationContext(),GetAttendance.class);
    			i.putExtra("newuser", true);
    		    startActivityForResult(i,1);
    		}
    		else
    		{
    			Toast.makeText(MainActivity.this, "Please enter your credentials first", Toast.LENGTH_SHORT).show();
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
    			listSub.add(new Subject(title, code, type, slot, max, atten));
    			Log.i("loaded:",title+" "+code+" "+slot);
    		}
            listViewSub.setOnItemClickListener(otcl);
        	tv.setText(settings.getString("updateOn", "Last Update not saved. Go to Menu->Refresh"));
            listViewSub.setAdapter( new SubjectAdapter(MainActivity.this, R.layout.single_item, listSub ) );
    		
    	}
    	
    	
    }
    
    
   OnItemClickListener otcl=new OnItemClickListener() {
	   
		public void onItemClick(AdapterView<?> parent, View view, int position, long id)
		{
			Intent a=new Intent(MainActivity.this, SubjectDetails.class);
			a.putExtra("index", position+1);
			startActivity(a);
		    Toast.makeText(MainActivity.this,settings.getString("Sub_"+String.valueOf(position+1)+"title","DB Error"), Toast.LENGTH_SHORT).show();
		}
	};
    
	
	void loginDialog()
    {
		Log.i("status", "inside logindialog()");
    	LayoutInflater inflator=getLayoutInflater();
        View popup=inflator.inflate(R.layout.details_popup, null);
        editor=settings.edit();
        String loadregno=settings.getString("regno", null);
        int loaddiagyy=settings.getInt("dobyy", 1993);
        int loaddiagmm=settings.getInt("dobmm", 2);
        int loaddiagdd=settings.getInt("dobdd", 14);
        final EditText edit=(EditText)popup.findViewById(R.id.diagRegno);
    	final DatePicker dp=(DatePicker)popup.findViewById(R.id.datePicker);
    	if(loadregno!=null)
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
    	else if(requestCode == 2)
    	{
    		if(resultCode == RESULT_OK)
    		{
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