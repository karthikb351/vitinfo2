package com.karthikb351.vitacad;

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
import com.karthikb351.vitinfo2dev.R;

public class SubjectAdapter extends ArrayAdapter {
	
      private int resource;
      private LayoutInflater inflater;
      private Context context;

      public SubjectAdapter ( Context ctx, int resourceId, List objects) {

            super( ctx, resourceId, objects );
            context=ctx;
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
			       	title.setText(sub.title);
			       	TextView slot = (TextView) view.findViewById(R.id.slot);
			       	slot.setText(sub.slot);
			       	TextView type = (TextView) view.findViewById(R.id.type);
			       	type.setText(sub.type);
			        TextView atten = (TextView) view.findViewById(R.id.atten);
			       	int con,att;
			       	con=sub.conducted;
			       	att=sub.attended;
			       	float per=sub.percentage;
			       	atten.setText(att+"/"+con+"\n"+per+"%");
			       	ProgressBar pg= (ProgressBar) view.findViewById(R.id.progress);
			       	int color;
			       	if(per<80&&per>=75)
			       		color=Color.parseColor("#FF8300");//Amber
			       	else if(per<75)
			       		color=Color.parseColor("#FF0000");//Red
			       	else
			       		color=Color.parseColor("#00AF33");//Green
			       	 
			       	float x[]={5,5,5,5,5,5,5,5};
			       	ShapeDrawable pgDrawable = new ShapeDrawable(new RoundRectShape(x, null,null));
			       	pgDrawable.getPaint().setColor(color);
			       	ClipDrawable progress = new ClipDrawable(pgDrawable, Gravity.LEFT, ClipDrawable.HORIZONTAL);
			       	pg.setProgressDrawable(progress);
			       	pg.setBackgroundDrawable(this.getContext().getResources().getDrawable(R.drawable.progress_green));
					pg.setMax(con);
			        pg.setProgress(att);
			        pg.invalidate();
            return view;
      }
     static float getPer(int num, int div)
  	{
  		return (float)((int)(((float)num/div)*1000))/10;
  	}
}
