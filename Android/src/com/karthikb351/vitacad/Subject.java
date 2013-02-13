package com.karthikb351.vitacad;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.GregorianCalendar;

import org.json.JSONArray;
import org.json.JSONException;

import android.util.Log;


public class Subject {

	String title, code, type, slot, regdate, classnbr;
	int attended, conducted;
	float percentage;
	int attendance_length;
	boolean att_valid;
	Attendance attendance[]=null;
	Subject()
	{
		super();
		attendance_length=0;
		att_valid=false;
	}
	void addAttendance(JSONArray array)
	{
		try
		{
			for(int i=array.length()-1;i>=0;i--)
			{
				String s,d;
				s=array.getString(i--);
				d=array.getString(i);
				attendance[attendance_length++]=new Attendance(s,d);
			}
		}
		catch (JSONException e) {
			e.printStackTrace();
		}
	}
	public void putAttendanceDetails(String json) {
		try {
			JSONArray j=new JSONArray(json);
			int l=j.length();
			attendance=new Attendance[l/2];
			int c=l-1;
			for(int i=(l/2)-1;i>=0;i--)
			{
				String s,d;
				s=j.getString(c--);
				d=j.getString(c--);
				attendance[attendance_length++]=new Attendance(d,s);
				att_valid=true;
			}
		} catch (JSONException e) {
			att_valid=false;
			e.printStackTrace();
		}
		
	}
	
	int Month(String m)
	{
		int k=-1;
		String mon[]={"Jan","Feb","Mar","Apr","May","Jun","Jul","Aug","Sep","Oct","Nov","Dec"};
		for(int i=0;i<12;i++)
			if(m.equalsIgnoreCase(mon[i]))
				k=i;
		return k;
	}
	String getDay(String date)
	{
		String dd=date.substring(0, date.indexOf('-')),mm=date.substring(date.indexOf('-')+1, date.lastIndexOf('-')),yy=date.substring(date.lastIndexOf('-')+1, date.length()-1);
		Calendar calendar = Calendar.getInstance();
		calendar.set(Integer.parseInt(yy), Month(mm), Integer.parseInt(dd));
		String days[]={" ","Sun","Mon","Tue","Wed","Thu","Fri","Sat"};
		return days[calendar.get(Calendar.DAY_OF_WEEK)];
	}
	


}
