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
import android.util.Log;
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
	public void saveMarks(String jArray){
		ArrayList<String> mrk = new ArrayList<String>();
		try{
			JSONArray j = new JSONArray(jArray);
			
			JSONArray CBL = j.getJSONArray(0);
			JSONArray PBL = j.getJSONArray(1);
			
			//SAVE PBL MARKS
			int count = 0;
			for (int i = 0 ; i < PBL.length() ; i++){
				mrk.add("BREAK" + count);count+=1;
				JSONArray temp = PBL.getJSONArray(i);
				for (int k = 0 ; k < temp.length() ; k++){
					mrk.add(temp.getString(k));
				}
			}
			mrk.add("BREAK"+count);
			saveList("PBLMARKS" , mrk);
			
			//SAVE CBL MARKS
			count = 0;
			mrk.clear();
			for (int i = 0 ; i < CBL.length() ; i++){
				mrk.add("BREAK" + count);count+=1;
				JSONArray temp = CBL.getJSONArray(i);
				for (int k = 0 ; k < temp.length() ; k++){
					mrk.add(temp.getString(k));
				}
			}
			mrk.add("BREAK"+count);
			saveList("CBLMARKS" , mrk);
			
		}catch(JSONException e){Toast.makeText(context, "Oops. Something went wrong :(", Toast.LENGTH_SHORT).show();}
	}
	
	
	public Mark loadMarks(String clsnbr){
		Mark mr = new Mark();
		
		mr.classnbr = clsnbr;
		
		ArrayList<String> mrk = new ArrayList<String>();
		
		mrk = getList("PBLMARKS");
		
		boolean isPBL = checkIfPBL(mrk,clsnbr);
		
		if (isPBL){
			mrk = gtSubMarks(mrk ,clsnbr);
			mr.iscbl = false;
			
			                        //!!!!!!! REWRITE THIS PART THERE IS ONLY DIFFERENCE OF 4 IN EACH !!!!!!!!!!!//
			
			//ADD THE TEST TITLES
			int count = 0;
			Log.v("TESTER", mrk.get(5));
			for (int i = 5 ; i <= 9; i++){
				mr.pbl[count].title = mrk.get(i).toString(); count += 1;
				
			}
			
			//ADD MAX MARKS
			count = 0;
			for (int i = 10 ; i <= 14 ; i++){
				mr.pbl[count].max = mrk.get(i); count +=1 ;
			}
			
			
			//ADD Weightage %
			count = 0;
			for (int i = 15 ; i <= 19 ; i++){
				mr.pbl[count].wgt = mrk.get(i); count += 1;
			}
			
			//ADD Conducted On
			count = 0;
			for (int i = 20 ; i <= 24 ; i++){
				mr.pbl[count].conOn = mrk.get(i); count += 1;
			}
			
			//ADD Status
			count = 0;
			for (int i = 25 ; i <= 29 ; i++){
				mr.pbl[count].status = mrk.get(i); count += 1;
			}
			
			//ADD Scored marks
			
			count = 0;
			for (int i = 30 ; i <= 34 ; i++){
				mr.pbl[count].scdMarks = mrk.get(i); count += 1;
			}
			

			//ADD Scored %
			count = 0;
			for (int i = 35 ; i <= 39 ; i++){
				mr.pbl[count].scdPercnt = mrk.get(i); count += 1;
			}	
		}
		else{
			mrk = getList("CBLMARKS");
			mrk = gtSubMarks(mrk ,clsnbr);
			mr.iscbl = true;
			if (mrk.size() == 18){
				
				mr.islab = false;
				
				//CAT - 1
				mr.cat[0].name = "CAT-I";
				mr.cat[0].status = mrk.get(5);
				mr.cat[0].marks = mrk.get(6);
				
				//CAT - 2
				mr.cat[1].name = "CAT-II";
				mr.cat[1].status = mrk.get(7);
				mr.cat[1].marks = mrk.get(8);
				
				//FEED QUIZ MARKS STATUS
				int count = 0;
				for (int i = 9 ; i <= 13 ; i += 2){
					mr.quiz[count].status = mrk.get(i);
					count += 1;
				}
				
				//FEED QUIZ MARKS 
				count = 0;
				for (int i = 10 ; i <= 14 ; i += 2){
					mr.quiz[count].marks = mrk.get(i);
					count += 1;
				}
				
				mr.asgn.status = mrk.get(15);
				mr.asgn.marks = mrk.get(16);
				
			}
			
			else {
				mr.islab = true;
				mr.labcam.status = mrk.get(6);
				mr.labcam.status = mrk.get(7);
			}
			
		}
		
		return mr;
	}
	
	private ArrayList<String> gtSubMarks(ArrayList<String> mk,String nbr){
		ArrayList<String> tmp = new ArrayList<String>();
		int count = 0;
		for (int i = 0 ; i < mk.size() ; i++){
				if(mk.get(i).toString().equals("BREAK" + count)){
					count += 1;
					if (mk.get(i+2).toString().equals(nbr)){
						for (int l = i+1 ; ;l++){
							if (mk.get(l).toString().equals("BREAK" + (count)))
								return tmp;
							else
								tmp.add(mk.get(l).toString());
						}
					}}
				
				
		}
		return tmp;
	}
	
	private boolean checkIfPBL(ArrayList<String> mk , String nbr){
		int count = 0;
		try{
		for (int i = 0 ; i < mk.size() ; i++){
			if(mk.get(i).toString().equals("BREAK" + count)){
				count += 1;
				if (mk.get(i+2).toString().equals(nbr)){
					return true;}}
			
			
		}}catch(Exception e){}
		return false;
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
