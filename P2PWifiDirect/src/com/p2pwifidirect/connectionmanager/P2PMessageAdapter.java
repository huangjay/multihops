package com.p2pwifidirect.connectionmanager;

import java.util.ArrayList;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

public class P2PMessageAdapter extends ArrayAdapter<P2PMessage> {
	
	Context cntxt = null;
	ArrayList<P2PMessage> messagelist = null;

	public P2PMessageAdapter(Context c, ArrayList<P2PMessage> ml) {
		super(c, R.layout.listview_message_row ,ml);
		cntxt = c;
		messagelist = ml;
	}
	
	public View getView(int position, View convertView, ViewGroup parent) {
		
		View v = convertView;
        if (v == null) {
            LayoutInflater vi = (LayoutInflater)cntxt.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            v = vi.inflate(R.layout.listview_message_row, null);
        }
        P2PMessage msg = messagelist.get(position);
        if (msg != null) {
                TextView tv = (TextView) v.findViewById(R.id.messageID);
                if (tv != null) {
                      tv.setText(msg.uid);                            
                }
                tv = (TextView) v.findViewById(R.id.messageSource);
                if (tv != null) {
                      tv.setText(msg.source);                            
                }
                tv = (TextView) v.findViewById(R.id.messageDest);
                if (tv != null) {
                      tv.setText(msg.destination);                            
                }
                tv = (TextView) v.findViewById(R.id.messageBody);
                if (tv != null) {
                      tv.setText(msg.body);                            
                }
        }
        return v;
		
	}

}