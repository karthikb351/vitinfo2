package com.karthikb351.vitacad;

/*
 * COMMON DATA HANDLER FOR THE APP  
 * AUTHOR: SAURABH JOSHI & KARTHIK BALAKRISHNAN
 * USAGE:
 * 1)To save attendance:
 *  DataHandler dat = new DataHandler(getApplicationContext());
 *  dat.saveAttendance(String in JSON fomat with the "Valid" thingy);
 *  
 * 2)To get Attendance:
 *  DataHandler dat = new DataHandler(getApplicationContext() , Subject Number like 0 , 1 etc);
 *  to get subject name: dat.getSubjectName() use similarly for others
*/

import java.util.ArrayList;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.preference.PreferenceManager;
import android.text.format.Time;
import android.widget.Toast;

public class DataHandler {
	Context context;
	SharedPreferences preferences;
	Editor edit;
	final static int ATTEN_REQUEST=5;
	final static int CAPTCHA_REQUEST=7;
	final static int RESULT_TIMEDOUT=10;
	final static int RESULT_ERROR=9;
	
	int subnum;
	
	private ArrayList<Attendance> details;
	private int subLength;
	
	
	
	public DataHandler(Context context){
		this.context = context;
		preferences = PreferenceManager.getDefaultSharedPreferences(context);
		setSubLength(preferences.getInt("NUMBEROFSUBJECTS", 0));
	}
	
	public Subject loadSubject(int subnum){
		ArrayList <String> str = new ArrayList<String>();
		Subject sub=new Subject();
		str = getList("ATTENDANCE_LIST");
		int breaker = 0;
		for (int i = 0 ; i < str.size() ; i++){
			if (str.get(i).toString().equals("BREAK"+subnum)){
				breaker = i+1;
			}
		}
		sub.code=str.get(breaker);
		sub.title=str.get(breaker+1);
		sub.type=str.get(breaker+2);
		sub.slot=str.get(breaker+3);
		
		try{
			sub.attended=Integer.parseInt(str.get(breaker+4));
			sub.conducted=Integer.parseInt(str.get(breaker+5));
		}
		catch (Exception e){
			sub.attended=0;
			sub.conducted=0;
		}
		sub.percentage=SubjectAdapter.getPer(sub.attended,sub.conducted);
		sub.regdate=str.get(breaker+7);
		sub.classnbr=str.get(breaker+8);
		sub.putAttendanceDetails(str.get(breaker+9));
		return sub;
	}
	
	public void saveAttendance(String jsonInput){
    	if (jsonInput.contains("valid%")){
    		ArrayList<String> att = new ArrayList<String>();
    		jsonInput = jsonInput.substring(jsonInput.indexOf('%')+1);
			JSONArray j;
			
			int count = 0;
			
			try {
				j = new JSONArray(jsonInput);
				for (int i = 0 ; i < j.length() ; i++){
					JSONObject json = new JSONObject(j.getString(i));
					att.add("BREAK"+count); count+=1; 
					att.add(json.getString("code")); //0
					att.add(json.getString("title")); //1
					att.add(json.getString("type")); //2
					att.add(json.getString("slot")); //3
					att.add(json.getString("attended")); //4
					att.add(json.getString("conducted")); //5
					att.add(json.getString("percentage")); //6
					att.add(json.getString("regdate")); //7
					att.add(json.getString("classnbr")); //8
					att.add(json.getJSONArray("details").toString()); //9
				}
				
				Editor edit = preferences.edit();
				edit.putInt("NUMBEROFSUBJECTS", count);
				Time now = new Time();
				now.setToNow();
				edit.putLong("updateOn", now.toMillis(true));
				edit.commit();
				saveList("ATTENDANCE_LIST" , att);
				Toast.makeText(context, "Refreshed attendance", Toast.LENGTH_SHORT).show();
			} catch (JSONException e) {
				
				Toast.makeText(context, "Oops. Something went wrong :(", Toast.LENGTH_SHORT).show();
			}			
		}
		else{
			Toast.makeText(context, "Error fetching attendance", Toast.LENGTH_SHORT).show();
		}
	}
	
	
	public void saveMsg(String key, String msg)
	{
		Editor edit = preferences.edit();
		edit.putString("MSG_"+key, msg);
		edit.commit();
	}
	
	boolean checkIfNewMsg(String key)
	{
		if(preferences.getString("MSG_"+key, null)==null)
			return true;
		else
			return false;
	}
	public void saveList(String key , ArrayList<String> list){
		Editor edit = preferences.edit();
		
		//BREAK THE LIST ADD A NUMBER AND SAVE IT 
		for (int i = 0 ; i< list.size();i++){
			String tmp = key;
			tmp = tmp + i;
			edit.putString(tmp, list.get(i));	
		}
		
		//ADD THE SIZE ALSO 
		
		edit.putInt(key + "SIZE", list.size());
		edit.commit();
	}
	
	public ArrayList<String> getList(String key){
		ArrayList<String> temp  = new ArrayList<String>();
		
		//GET THE SIZE
		int max  = preferences.getInt(key+"SIZE", 0);
		
		for (int i = 0 ; i < max ;  i++){
			temp.add(preferences.getString(key+i, "Error"));
		}
		return temp;
		
	}

	public ArrayList<Attendance> getDetails() {
		return details;
	}

	public void setDetails(ArrayList<Attendance> details) {
		this.details = details;
	}

	public int getSubLength() {
		return subLength;
	}

	public void setSubLength(int subLength) {
		this.subLength = subLength;
	}
	

}
