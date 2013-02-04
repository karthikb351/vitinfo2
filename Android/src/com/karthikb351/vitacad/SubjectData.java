package com.karthikb351.vitacad;

public class SubjectData {
	
	public Subject subjects[];
	public int length;
	SubjectData()
	{
		length=0;
		subjects=null;
	}
	public void addSubject(Subject s)
	{
		subjects[length++]=s;
	}
	public void clear()
	{
		subjects=null;
	}
	

}
