package com.karthikb351.vitacad;

//COMMON DATA HANDLER FOR THE APP  
//AUTHOR: SAURABH JOSHI & KARTHIK BALAKRISHNAN
import java.util.ArrayList;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.preference.PreferenceManager;
import android.widget.Toast;

public class DataHandler {
	
	Context context;
	SharedPreferences preferences;
	public static SubjectData subdata = new SubjectData();
	
	public DataHandler(Context context){
		this.context = context;
		preferences = PreferenceManager.getDefaultSharedPreferences(context);
	}
	
	public Subject[] loadSubjects(String JSONtext)
	{
		Subject subs[]=null;
		int j=preferences.getInt("Sub_size", 0);
		ArrayList<String> sub=new ArrayList<String>();
		return subs;
	}
	public boolean saveSubjects(String JSONtext)
	{
		boolean flag=true;
		int k=0,j=0;
		JSONArray array;
		ArrayList<String> sub=new ArrayList<String>();
		//keys=["sl_no","code","title","type","slot","regdate","attended","conducted","percentage","extra", "classnbr"]
		try
		{
			array = new JSONArray(JSONtext);
			for(int i=0;i<array.length();i++)
			{	
				JSONObject obj=new JSONObject(array.get(i).toString());
				Subject s=new Subject();
				s.code=obj.getString("code");
				s.title=obj.getString("title");
				s.slot=obj.getString("slot");
				s.attended=obj.getString("attended");
				s.conducted=obj.getString("conducted");
				s.classnbr=obj.getString("classnbr");
				JSONArray att=obj.getJSONArray("details");
				s.addAttendance(att);
				subdata.addSubject(s);
				
			}
			Editor edit = preferences.edit();
			edit.putInt("Sub_size", j);
			edit.commit();
		} 
		catch (JSONException e)
		{
			flag=false;
			e.printStackTrace();
		}
		return flag;
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
	

}
