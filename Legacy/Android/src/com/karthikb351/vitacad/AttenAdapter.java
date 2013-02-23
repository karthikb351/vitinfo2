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

public class AttenAdapter extends ArrayAdapter {
	
      private int resource;
      private LayoutInflater inflater;
      private Context context;
      private int startsHere;

      public AttenAdapter ( Context ctx, int resourceId, List objects, int start) {
            super( ctx, resourceId, objects );
            startsHere=start;
            context=ctx;
            resource = resourceId;
            inflater = LayoutInflater.from( ctx );
      }
      
      
      @Override
      public View getView ( int position, View view, ViewGroup parent ) {
		            /* Extract the Subject's object to show */
		            String s,d;
		            Attendance at = (Attendance) getItem( position );
		            s=at.getStatus();
		            d=at.getDate();
		            /* create a new view of my layout and inflate it in the row */
		            view = ( RelativeLayout ) inflater.inflate( resource, null );
		            TextView date = (TextView) view.findViewById(R.id.atten_detail_date);
			       	date.setText(d);
			       	TextView status = (TextView) view.findViewById(R.id.atten_detail_status);
			       	status.setText(s);
			       	if(s.equals("Absent"))
			       		status.setTextColor(Color.parseColor("#FF0000"));
            return view;
      }
}
