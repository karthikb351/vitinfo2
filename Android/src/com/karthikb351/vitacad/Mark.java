package com.karthikb351.vitacad;
/*
 * THIS CLASS CAN CONTAIN MARKS OF ALL KIND OF SUBJECTS (CBL/PBL/LAB) 
 * USE THE iscbl and isLab booleans to get the types
 * All the Quiz and Cat status and marks are in an array for easier use
 * PBL Class contains all information about the PBL subjects
 * 
 * ENJOY! ;)
 * 
 */
public class Mark {
	
	//CHECK IF CBL AND LAB
	boolean iscbl , islab;
	
	//GET AND SET CLASSNBR
	String classnbr;
	
	//ALL THE TEST MARKS FOR CBL AND LAB
	Test[] cat = new Test[2] ;
	Test[] quiz = new Test[3];
	
	PBLTest[] pbl= new PBLTest[5];
	
	//LAB AND ASSIGNMENTS
	Test asgn = new Test();
	Test labcam = new Test();
	
	Mark(){
		super();
		for (int i = 0 ; i<5 ; i++){
			pbl[i] = new PBLTest();
			if (i<2)
				cat[i] = new Test();
			if (i<3)
				quiz[i] = new Test();
		}
		
	}
	
	//FOR PBL
	
	//PBL CLASS
	public class PBLTest{
		String title;
		String max;
		String wgt;
		String conOn;
		String status;               //REMOVE THE ONES WHICH ARE NOT REQUIRED!
		String scdMarks;
		String scdPercnt;
	}
	
	//CBL AND LAB MARKS CLASS
	public class Test{
		String name;
		String status ;
		String marks;
	}
}
