package com.karthikb351.vitacad;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import org.json.JSONArray;
import org.json.JSONException;

import android.util.Log;


public class Subject {

	String title, code, type, slot, regdate, classnbr,atten_last_status,atten_last_date;
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
				attendance[attendance_length++]=new Attendance(getDay(d),s);
				att_valid=true;
			}
		} catch (JSONException e) {
			att_valid=false;
			e.printStackTrace();
		}
		if(att_valid)
		{
			atten_last_status=attendance[0].status;
			atten_last_date=attendance[0].date;
		}
		
	}
	String getDay(String date)
	{
		String dd=date.substring(0, date.indexOf('-')),mm=date.substring(date.indexOf('-')+1, date.lastIndexOf('-')),yy=date.substring(date.lastIndexOf('-')+1, date.length()-1);
		String pattern="dd-MMM-yyyy", newPattern="EEEE, dd-MMM", result="Error";
		SimpleDateFormat df= new SimpleDateFormat(pattern);
		try {
			Date d=df.parse(date);
			df= new SimpleDateFormat(newPattern);
			result=df.format(d);
		} catch (ParseException e) {
			result="error";
			e.printStackTrace();
		}
		return result;
	}
	


}
