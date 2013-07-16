package com.sau.cardtypes;

import java.text.DecimalFormat;

import android.content.Context;
import android.graphics.Typeface;
import android.os.CountDownTimer;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;

import com.fima.cardsui.objects.Card;
import com.karthikb351.vitinfo2dev.R;

public class CurrentCard extends Card {
	Context cntx;
	View vw;
	
	private void Changefnt(TextView t , String font ){
		font = "fonts/" + font + ".ttf";
		Typeface fnt = Typeface.createFromAsset(cntx.getAssets(),font); 
		t.setTypeface(fnt);
	}
	
	@Override
	public View getCardContent(Context context) {
		cntx = context;
		vw = LayoutInflater.from(cntx).inflate(R.layout.card_current,null);
		
		
		Changefnt((TextView) vw.findViewById (R.id.txt_subject) , "Roboto-Light");
		Changefnt((TextView) vw.findViewById (R.id.txt_title) , "Roboto-Thin");
		Changefnt((TextView) vw.findViewById (R.id.txt_tmr) , "Roboto-Light");
		Changefnt((TextView) vw.findViewById (R.id.txt_venue) , "Roboto-Light");
		
		countDown();
		return vw;
	}
	
	private void countDown(){
		 new CountDownTimer(120000, 1000) {
			 TextView txt = (TextView) vw.findViewById(R.id.txt_tmr);
		     public void onTick(long millisUntilFinished) {
		    	 
		    	 double d = (double) millisUntilFinished;
		    	 
		    	 String minutes = new DecimalFormat("00").format((int) ((d / (1000*60)) % 60));
		    	 String seconds = new DecimalFormat("00").format((int) ((d % (1000*60*60)) % (1000*60)) / 1000);
		    	 
		         txt.setText( minutes +  ":" + seconds );
		     }

		     public void onFinish() {
		         txt.setText("00:00");
		     }
		  }.start();
	}
}
