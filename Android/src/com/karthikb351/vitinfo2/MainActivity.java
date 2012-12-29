package com.karthikb351.vitinfo2;

import java.util.ArrayList;
import java.util.List;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.Toast;

import com.karthikb351.vitinfotest.R;

public class MainActivity extends Activity {
	
	SharedPreferences settings;
	SharedPreferences.Editor editor;
	boolean newuser;
	String DOB, REGNO;
	ListView listViewSub;
	ImageView foot;
    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
    	super.onCreate(savedInstanceState);
    	setContentView(R.layout.main);
    	settings = getSharedPreferences("vitinfotest", 0);
    	listViewSub=(ListView)findViewById(R.id.list);
    	Button a=(Button)findViewById(R.id.button_atten);
    	Button l=(Button)findViewById(R.id.button_login);
    	
    	a.setOnClickListener(ocl);
    	l.setOnClickListener(ocl);
    	loadAtten();
    }
    
    OnClickListener ocl = new OnClickListener() {
		
		@Override
		public void onClick(View v) {
			
			switch(v.getId())
			{
				case R.id.button_atten:
			
					if(!settings.getBoolean("credentials", false))
						loginDialog();
					else
					{
						Intent i = new Intent(getApplicationContext(),CaptchaDialog.class);
						i.putExtra("newuser", false);
					    startActivityForResult(i,1);
					}
					break;
				
				case R.id.button_login:
					
					loginDialog();
					break;
			}
			 
		}
	};
    void savedToSharedPrefs(String source)
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
        editor.commit();
        listViewSub.setOnItemClickListener(otcl);
        listViewSub.setAdapter( new SubjectAdapter(MainActivity.this, R.layout.single_item, listSub ) );
		
    }
    
    void loadAtten()
    {
    	if(settings.getBoolean("newuser", true))
    	{
    		loginDialog();
    		if(settings.getBoolean("credentials", false))
    		{
        		Intent i = new Intent(getApplicationContext(),CaptchaDialog.class);
        		i.putExtra("newuser", true);
    		    startActivityForResult(i,1);
    		}
    	}
    	else
    	{
    		String title,code,type,slot,nbr;
    		int max, atten,count=1,size;
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
        int loaddiagyy=settings.getInt("dobyy", 2012);
        int loaddiagmm=settings.getInt("dobmm", 12);
        int loaddiagdd=settings.getInt("dobdd", 21);
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
    			savedToSharedPrefs(result);
        		Toast.makeText(MainActivity.this, "Success! Booyeah!", Toast.LENGTH_SHORT).show();
    			
    		}
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