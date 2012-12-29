package com.karthikb351.vitinfo2;


public class Subject {

	String title,code,type,slot;
	int max, atten;
	public Subject(String tit, String cd, String ty, String sl, int m, int at) {
		super();
		title=tit;
		code=cd;
		type=ty;
		slot=sl;
		max=m;
		atten=at;
	}

	public String getTitle() {
		// TODO Auto-generated method stub
		return title;
	}
	
	public String getSlot() {
		return slot;
	}
	
	public String getCode() {
		return code;
	}
	
	public String getType() {
		return type;
	}
	public int getAtten() {
		// TODO Auto-generated method stub
		return atten;
	}

	public int getMax() {
		// TODO Auto-generated method stub
		return max;
	}


}
