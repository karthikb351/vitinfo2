package com.karthikb351.vitinfo2;

import java.util.List;

import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ClipDrawable;
import android.graphics.drawable.ShapeDrawable;
import android.graphics.drawable.shapes.RoundRectShape;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

public class SubjectAdapter extends ArrayAdapter {
	
      private int resource;
      private LayoutInflater inflater;

      public SubjectAdapter ( Context ctx, int resourceId, List objects) {

            super( ctx, resourceId, objects );
            resource = resourceId;
            inflater = LayoutInflater.from( ctx );
      }
      
      
      @Override
      public View getView ( int position, View view, ViewGroup parent ) {

            /* create a new view of my layout and inflate it in the row */
            view = ( RelativeLayout ) inflater.inflate( resource, null );

            /* Extract the Subject's object to show */
            Subject sub = (Subject) getItem( position );
            TextView title = (TextView) view.findViewById(R.id.title);
	       	title.setText(sub.getTitle());
	       	TextView slot = (TextView) view.findViewById(R.id.slot);
	       	slot.setText(sub.getSlot());
	       	TextView type = (TextView) view.findViewById(R.id.type);
	       	type.setText(sub.getType());
	        TextView atten = (TextView) view.findViewById(R.id.atten);
	       	int a,b;
	       	a=sub.getMax();
	       	b=sub.getAtten();
	       	float per=getPer(b, a);
	       	atten.setText(b+"/"+a+"\n"+per+"%");
	       	ProgressBar pg= (ProgressBar) view.findViewById(R.id.progress);
	       	 
	       		int c=Color.parseColor("#00AF33");//Green
	       	if(per<80&&per>=75)
	       		c=Color.parseColor("#FF8300");//Orange
	       	else if(per<75)
	       		c=Color.parseColor("#FF0000");//Red
	       	else
	       		c=Color.parseColor("#00AF33");//Green
	       	 
	       	 
	       	float x[]={5,5,5,5,5,5,5,5};
	       	ShapeDrawable pgDrawable = new ShapeDrawable(new RoundRectShape(x, null,null));
	       	pgDrawable.getPaint().setColor(c);
	       	ClipDrawable progress = new ClipDrawable(pgDrawable, Gravity.LEFT, ClipDrawable.HORIZONTAL);
	       	pg.setProgressDrawable(progress);
	       	pg.setBackgroundDrawable(this.getContext().getResources().getDrawable(R.drawable.progress_green));
	       	pg.setMax(a);
	       	pg.setProgress(b);

            return view;
      }
      float getPer(int num, int div)
  	{
  		return (float)((int)(((float)num/div)*1000))/10;
  	}
}
