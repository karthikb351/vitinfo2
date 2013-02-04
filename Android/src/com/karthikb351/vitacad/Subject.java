package com.karthikb351.vitacad;

import java.util.ArrayList;

import org.json.JSONArray;
import org.json.JSONException;


public class Subject {

	String title, code, type, slot, regdate, attended, conducted, classnbr;
	int attendance_length;
	Attendance attendance[]=null;
	Subject()
	{
		super();
		attendance_length=0;
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
	


}
