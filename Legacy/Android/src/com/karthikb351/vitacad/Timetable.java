package com.karthikb351.vitacad;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class Timetable {
	
	String nonInstructionDays[]={"14-Jan-2013","15-Jan-2013","26-Jan-2013","6-Feb-2013","7-Feb-2013","8-Feb-2013","9-Feb-2013","16-Feb-2013","17-Feb-2013","18-Feb-2013","19-Feb-2013","20-Feb-2013","21-Feb-2013","22-Feb-2013","23-Feb-2013","24-Feb-2013","27-Mar-2013","29-Mar-2013","30-Mar-2013","31-Mar-2013","1-Apr-2013","2-Apr-2013","3-Apr-2013","4-Apr-2013","5-Apr-2013","6-Apr-2013","7-Apr-2013","11-Apr-2013","14-Apr-2013","1-May-2013"};
	
	void build(String date)
	{
	String pattern="dd-MMM-yyyy", newPattern="EEE, dd-MMM", result="Error";
	SimpleDateFormat df= new SimpleDateFormat(pattern);
	try {
		Date d=df.parse(date);
		df= new SimpleDateFormat(newPattern);
		result=df.format(d);
	} catch (ParseException e) {
		result="error";
		e.printStackTrace();
	}
	}

}
