package com.karthikb351.vitacad;

/*
 * COMMON DATA HANDLER FOR THE APP  
 * AUTHOR: SAURABH JOSHI
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
	
	private String subjectName;
	private String slot;
	private String type;
	private String percentage;
	private String subjectCode;
	private String regDate;
	private String classnbr;
	private int Attended;
	private int subLength;
	private int Conducted;
	
	
	
	public DataHandler(Context context){
		this.context = context;
		preferences = PreferenceManager.getDefaultSharedPreferences(context);
		setSubLength(preferences.getInt("NUMBEROFSUBJECTS", 0));
	}
	
	public DataHandler(Context context , int subnum){
		this.subnum = subnum;
		preferences = PreferenceManager.getDefaultSharedPreferences(context);
		setSubLength(preferences.getInt("NUMBEROFSUBJECTS", 0));
		loadAttendance();
	}
	
	private void loadAttendance(){
		ArrayList <String> str = new ArrayList<String>();
		str = getList("ATTENDANCE_LIST");
		int breaker = 0;
		for (int i = 0 ; i < str.size() ; i++){
			if (str.get(i).toString().equals("BREAK"+subnum)){
				breaker = i+1;
			}
		}
		setSubjectCode(str.get(breaker));
		setSubjectName(str.get(breaker+1));
		setType(str.get(breaker+2));
		setSlot(str.get(breaker+3));
		
		try{
			setAttended(Integer.parseInt(str.get(breaker+4)));
			setConducted(Integer.parseInt(str.get(breaker+5)));
			setPercentage(str.get(breaker+6));
		}
		catch (Exception e){
			e.printStackTrace();
			setAttended(0);
			setConducted(0);
			setPercentage("Not Uploaded");
		}
		setRegDate(str.get(breaker+7));
		setClassNbr(str.get(breaker+8));
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
					JSONArray d = json.getJSONArray("details");
					for (int k = 0 ; k<d.length();k++){
						att.add(d.getString(0));						}
				}
				
				Editor edit = preferences.edit();
				edit.putInt("NUMBEROFSUBJECTS", count);
				edit.commit();
				
				saveList("ATTENDANCE_LIST" , att);
			} catch (JSONException e) {
			}			
		}
		else{
			
		}
	}
/*   public void saveAttendance(ArrayList<String> list){
		ArrayList <String> str = new ArrayList<String>();
		int count = 0;
		for (int i = 0 ; i<list.size(); i++){
			if (i != 0 && i != 1){
				if ((i-2)%10 == 0){
					str.add("BREAK"+count); count+=1;}
				str.add(list.get(i).toString());				
			}
		}
		Editor edit = preferences.edit();
		
		edit.putInt("NUMBEROFSUBJECTS", count-1);
		edit.commit();
		
		saveList("ATTENDANCE_LIST" , str);
	}*/
	
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

	public String getSubjectName() {
		return subjectName;
	}

	public void setSubjectName(String subjectName) {
		this.subjectName = subjectName;
	}

	public String getSlot() {
		return slot;
	}

	public void setSlot(String slot) {
		this.slot = slot;
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public String getPercentage() {
		return percentage;
	}

	public void setPercentage(String percentage) {
		this.percentage = percentage;
	}

	public int getAttended() {
		return Attended;
	}

	public void setAttended(int attended) {
		Attended = attended;
	}

	public int getConducted() {
		return Conducted;
	}

	public void setConducted(int conducted) {
		Conducted = conducted;
	}

	public String getSubjectCode() {
		return subjectCode;
	}

	public void setSubjectCode(String subjectCode) {
		this.subjectCode = subjectCode;
	}

	public String getRegDate() {
		return regDate;
	}

	public void setRegDate(String regDate) {
		this.regDate = regDate;
	}
	public String getClassNbr() {
		return classnbr;
	}

	public void setClassNbr(String classnbr) {
		this.classnbr = classnbr;
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
