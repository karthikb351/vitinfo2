package com.karthikb351.vitinfo2;

import com.karthikb351.vitinfotest.R;

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
import android.widget.NumberPicker;
import android.widget.NumberPicker.OnValueChangeListener;
import android.widget.ProgressBar;
import android.widget.TextView;

public class SubjectDetails extends Activity {
	
	
	int cl_red=Color.parseColor("#FF0000"),
		cl_green=Color.parseColor("#00AF33"),
		cl_orange=Color.parseColor("#FF8300");
	SharedPreferences settings;
	String title,code,type,slot,nbr;
	
	int max, atten,count=1,size,pos,globe_makeup,globe_bunk;
	TextView tv_title,tv_slot,tv_type,tv_code,tv_atten,tv_bunk_info,tv_net_per,tv_makeup_info;
	ProgressBar progBar;
	NumberPicker np_bunk, np_makeup;
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
		progBar=(ProgressBar)findViewById(R.id.progressBar_detailed);
		np_bunk=(NumberPicker)findViewById(R.id.numberPicker_bunk);
		np_makeup=(NumberPicker)findViewById(R.id.numberPicker_makeup);
		np_bunk.setMinValue(0);
		np_bunk.setMaxValue(30);
		np_makeup.setMinValue(0);
		np_makeup.setMaxValue(30);
		np_bunk.setOnValueChangedListener(ovcl);
		np_makeup.setOnValueChangedListener(ovcl);
		np_bunk.setDescendantFocusability(NumberPicker.FOCUS_BLOCK_DESCENDANTS);
		np_makeup.setDescendantFocusability(NumberPicker.FOCUS_BLOCK_DESCENDANTS);
		settings = getSharedPreferences("vitinfotest", 0);
		Intent i=getIntent();
		pos=i.getIntExtra("index", -1);
		load();
		globe_makeup=0;
		globe_bunk=0;
		setContent();
		
	}
	
	OnValueChangeListener ovcl = new OnValueChangeListener() {
		
		@Override
		public void onValueChange(NumberPicker picker, int oldVal, int newVal) {
			
			switch(picker.getId())
			{
				case R.id.numberPicker_bunk:
					onBunk(newVal);
					break;
				case R.id.numberPicker_makeup:
					onMakeup(newVal);
					break;
			}
			
		}
	};
	
	void onBunk(int a)
	{
		globe_bunk=a;
		tv_bunk_info.setText("If you miss "+a+" more class(s)");
		update();
	}
	void onMakeup(int a)
	{
		globe_makeup=a;
		tv_makeup_info.setText("If you attend "+a+" more class(s)");
		update();
	}
	
	void update()
	{
		int t_atten=atten+globe_makeup,t_max=max+globe_makeup+globe_bunk;
		float per=getPer(t_atten, t_max);
		tv_net_per.setText(String.valueOf(per)+"%");
		tv_net_per.setTextColor(getColor(t_atten, t_max));
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
		int c;
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
