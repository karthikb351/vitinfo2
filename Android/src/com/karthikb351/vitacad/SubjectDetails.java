package com.karthikb351.vitacad;

import java.util.ArrayList;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONException;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.Rect;
import android.graphics.drawable.ClipDrawable;
import android.graphics.drawable.ShapeDrawable;
import android.graphics.drawable.shapes.RoundRectShape;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.actionbarsherlock.app.SherlockActivity;
import com.karthikb351.vitinfo2dev.*;


public class SubjectDetails extends SherlockActivity {
	
	
	int cl_red=Color.parseColor("#FF0000"),
		cl_green=Color.parseColor("#00AF33"),
		cl_amber=Color.parseColor("#FF8300");
	SharedPreferences settings;
	String title,code,type,slot,nbr;
	boolean progFlag=false;
	int max, atten,count=1,size,pos,globe_makeup,globe_bunk,prev_bunk,prev_makeup,class_offset;
	TextView tv_title,tv_slot,tv_type,tv_code,tv_atten,tv_net_per,bunk_val,makeup_val;
	ListView list;
	ProgressBar progBar;
	Button bunk_add,bunk_sub, makeup_add, makeup_sub;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.subject_details);
		tv_title=(TextView)findViewById(R.id.title_detailed);
		tv_slot=(TextView)findViewById(R.id.slot_detailed);
		tv_code=(TextView)findViewById(R.id.code_detailed);
		tv_type=(TextView)findViewById(R.id.type_detailed);
		tv_atten=(TextView)findViewById(R.id.atten_detailed);
		tv_net_per=(TextView)findViewById(R.id.net_per);
		bunk_val=(TextView)findViewById(R.id.bunk_val);
		makeup_val=(TextView)findViewById(R.id.makeup_val);
		progBar=(ProgressBar)findViewById(R.id.progressBar_detailed);
		bunk_add=(Button)findViewById(R.id.bunk_add);
		bunk_sub=(Button)findViewById(R.id.bunk_sub);
		makeup_add=(Button)findViewById(R.id.makeup_add);
		makeup_sub=(Button)findViewById(R.id.makeup_sub);
		bunk_add.setOnClickListener(ocl);
		bunk_sub.setOnClickListener(ocl);
		makeup_add.setOnClickListener(ocl);
		makeup_sub.setOnClickListener(ocl);

		
		settings = getSharedPreferences("vitacad", 0);
		Intent i=getIntent();
		pos=i.getIntExtra("index", -1);
		load();
		globe_makeup=0;
		globe_bunk=0;
		setContent();
		
	}
	
	OnClickListener ocl = new OnClickListener() {
		
		@Override
		public void onClick(View v) {
			// TODO Auto-generated method stub
			
			switch (v.getId())
			{
				case R.id.bunk_add:
					onBunk(true);
					break;
				case R.id.bunk_sub:
					if(globe_bunk!=0)
						onBunk(false);
					break;
				case R.id.makeup_add:
					onMakeup(true);
					break;
				case R.id.makeup_sub:
					if(globe_makeup!=0)
						onMakeup(false);
					break;
			}
			
		}
	};
	
	void onBunk(boolean f)
	{
		if(f)
			globe_bunk+=class_offset;
		else
			globe_bunk-=class_offset;
		if(globe_bunk<0)
		{
			globe_bunk=0;
		}
		if(globe_bunk==0)
			bunk_sub.setClickable(false);
		else
			bunk_sub.setClickable(true);
		bunk_val.setText("If you miss "+globe_bunk+" more class(s)");
		update();
	}
	void onMakeup(boolean f)
	{
		if(f)
			globe_makeup+=class_offset;
		else
			globe_makeup-=class_offset;
		if(globe_makeup<0)
			globe_makeup=0;
		if(globe_makeup==0)
			makeup_sub.setClickable(false);
		else
			makeup_sub.setClickable(true);
		makeup_val.setText("If you attend "+globe_makeup+" more class(s)");
		update();
	}
	
	void update()
	{
		int t_atten=atten+globe_makeup,t_max=max+globe_makeup+globe_bunk;
		float per=getPer(t_atten, t_max);
		tv_net_per.setText(String.valueOf(per)+"%");
		tv_net_per.setTextColor(getColor(t_atten, t_max));
		Log.i("color", String.valueOf(getColor(t_atten, t_max)));
		Rect bounds = progBar.getProgressDrawable().getBounds();
		if(per<80&&per>=75)
			progBar.setProgressDrawable(SubjectDetails.this.getResources().getDrawable(R.drawable.progress_amber));//Amber
       	else if(per<75)
       		progBar.setProgressDrawable(SubjectDetails.this.getResources().getDrawable(R.drawable.progress_red));//Red
       	else
       		progBar.setProgressDrawable(SubjectDetails.this.getResources().getDrawable(R.drawable.progress_green));//Green

		progBar.getProgressDrawable().setBounds(bounds);
       	progBar.setMax(t_max);
        progBar.setProgress(t_atten);
        int a=progBar.getProgress();
        progBar.setProgress(a+1);
        progBar.setProgress(a-1);
        progBar.setProgress(a+0);
		
		if(globe_makeup==0&&globe_bunk==0)
		{
			tv_atten.setText("You have attended "+t_atten+" out of "+t_max+" classes");
		}

		else
		{
			tv_atten.setText("You would have attended "+t_atten+" out of "+t_max+" classes");
		}
	}
	void load()
	{
		nbr=String.valueOf(pos);
		code=settings.getString("Sub_"+nbr+"code","DB Error");
		title=settings.getString("Sub_"+nbr+"title","DB Error"); 
		type=settings.getString("Sub_"+nbr+"type","DB Error"); 
		slot=settings.getString("Sub_"+nbr+"slot","DB Error");
		atten=settings.getInt("Sub_"+nbr+"atten",0); 
		max=settings.getInt("Sub_"+nbr+"max",0);
		class_offset = 1;
		if(type.contains("Lab"))
		for( int i=0; i<slot.length(); i++ ) {
		    if( slot.charAt(i) == '+' ) {
		        class_offset++;
		    } 
		}
		Log.i("details:",code+title+slot);
	}
	
	float getPer(int num, int div)
	{
		return (float)((int)(((float)num/div)*1000))/10;
	}
	int getColor(int num, int div)
	{
		float a=getPer(num,div);
		int c=cl_green;
       	if(a<80&&a>=75)
       		c=cl_amber;
       	else if(a<75)
       		c=cl_red;
       	else
       		c=cl_green;
       	return c;
		
	}
	void setContent()
	{
		tv_title.setText(title);
		tv_slot.setText(slot);
		tv_type.setText(type);
		tv_code.setText(code);
		bunk_val.setText("If you miss 0 more class(s)");
		makeup_val.setText("If you attend 0 more class(s)");
		float per=getPer(atten,max);

		tv_net_per.setText(String.valueOf(per)+"%");
		tv_net_per.setTextColor(getColor(atten, max));
		float x[]={5,5,5,5,5,5,5,5};
       	ShapeDrawable pgDrawable = new ShapeDrawable(new RoundRectShape(x, null,null));
       	pgDrawable.getPaint().setColor(getColor(atten,max));
       	ClipDrawable progress = new ClipDrawable(pgDrawable, Gravity.LEFT, ClipDrawable.HORIZONTAL);
       	progBar.setProgressDrawable(progress);
		if(per<80&&per>=75)
			progBar.setBackgroundDrawable(SubjectDetails.this.getResources().getDrawable(R.drawable.progress_amber));//Amber
       	else if(per<75)
       		progBar.setBackgroundDrawable(SubjectDetails.this.getResources().getDrawable(R.drawable.progress_red));//Red
       	else
       		progBar.setBackgroundDrawable(SubjectDetails.this.getResources().getDrawable(R.drawable.progress_green));//Green
       	progBar.setMax(max);
        progBar.setProgress(0);
        progBar.invalidate();
        progBar.setProgress(atten);
        tv_atten.setText("You have attended "+per+"% ("+atten+" out of "+max+") of your classes");
        update();
		
	}
	
}
