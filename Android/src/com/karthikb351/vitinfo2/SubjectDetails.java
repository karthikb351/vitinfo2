package com.karthikb351.vitinfo2;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.drawable.ClipDrawable;
import android.graphics.drawable.ShapeDrawable;
import android.graphics.drawable.shapes.RoundRectShape;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;


public class SubjectDetails extends Activity {
	
	
	int cl_red=Color.parseColor("#FF0000"),
		cl_green=Color.parseColor("#00AF33"),
		cl_orange=Color.parseColor("#FF8300");
	SharedPreferences settings;
	String title,code,type,slot,nbr;
	boolean progFlag=false;
	int max, atten,count=1,size,pos,globe_makeup,globe_bunk,prev_bunk,prev_makeup;
	TextView tv_title,tv_slot,tv_type,tv_code,tv_atten,tv_bunk_info,tv_net_per,tv_makeup_info,bunk_val,makeup_val;
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
		tv_bunk_info=(TextView)findViewById(R.id.bunk_detailed);
		tv_net_per=(TextView)findViewById(R.id.net_per);
		tv_makeup_info=(TextView)findViewById(R.id.makeup_detailed);
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

		
		settings = getSharedPreferences("vitinfotest", 0);
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
			globe_bunk++;
		else
			globe_bunk--;
		if(globe_bunk<0)
		{
			globe_bunk=0;
		}
		if(globe_bunk==10)
			Toast.makeText(SubjectDetails.this, "DISCLAIMER:We hold no responsibility if you get debarred!", Toast.LENGTH_SHORT).show();
		tv_bunk_info.setText("If you miss "+globe_bunk+" more class(s)");
		bunk_val.setText(String.valueOf(globe_bunk));
		update();
	}
	void onMakeup(boolean f)
	{
		if(f)
			globe_makeup++;
		else
			globe_makeup--;
		if(globe_makeup<0)
			globe_makeup=0;
		tv_makeup_info.setText("If you attend "+globe_makeup+" more class(s)");
		makeup_val.setText(String.valueOf(globe_makeup));
		update();
	}
	
	void update()
	{
		int t_atten=atten+globe_makeup,t_max=max+globe_makeup+globe_bunk;
		float per=getPer(t_atten, t_max);
		tv_net_per.setText(String.valueOf(per)+"%");
		tv_net_per.setTextColor(getColor(t_atten, t_max));
		Log.i("color", String.valueOf(getColor(t_atten, t_max)));
       	float x[]={5,5,5,5,5,5,5,5};
       	ShapeDrawable pgDrawable = new ShapeDrawable(new RoundRectShape(x, null,null));
       	pgDrawable.getPaint().setColor(getColor(t_atten, t_max));
       	ClipDrawable progress = new ClipDrawable(pgDrawable, Gravity.LEFT, ClipDrawable.HORIZONTAL);
       	
       	progBar.setProgressDrawable(progress);
       	progBar.setMax(t_max);
       	progBar.setProgress(t_atten);
		
		if(globe_makeup==0&&globe_bunk==0)
		{
			tv_atten.setText("You have attended "+per+"% ("+t_atten+" out of "+t_max+") of your classes");
		}

		else
		{
			tv_atten.setText("You would have attended "+per+"% ("+t_atten+" out of "+t_max+") of your classes");
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
       		c=cl_orange;
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
		tv_bunk_info.setText("If you miss 0 more class(s)");
		tv_makeup_info.setText("If you attend 0 more class(s)");
		bunk_val.setText("0");
		makeup_val.setText("0");
		float per=getPer(atten,max);

		tv_net_per.setText(String.valueOf(per)+"%");
		tv_net_per.setTextColor(getColor(atten, max));
       	
		float x[]={5,5,5,5,5,5,5,5};
       	ShapeDrawable pgDrawable = new ShapeDrawable(new RoundRectShape(x, null,null));
       	pgDrawable.getPaint().setColor(getColor(atten, max));
       	ClipDrawable progress = new ClipDrawable(pgDrawable, Gravity.LEFT, ClipDrawable.HORIZONTAL);
       	
       	progBar.setProgressDrawable(progress);
       	progBar.setBackgroundDrawable(SubjectDetails.this.getResources().getDrawable(R.drawable.progress_green));
       	progBar.setMax(max);
       	progBar.setProgress(atten);
		
		tv_atten.setText("You have attended "+per+"% ("+atten+" out of "+max+") of your classes");
		
	}
	
}
