package com.p2pwifidirect.connectionmanager;

import java.util.ArrayList;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

public class P2PFileRequestAdapter extends ArrayAdapter<P2PFileRequest> {
	
	Context cntxt = null;
	ArrayList<P2PFileRequest> reqlist = null;

	public P2PFileRequestAdapter(Context c, ArrayList<P2PFileRequest> rl) {
		super(c, R.layout.listview_request_row,rl);
		cntxt = c;
		reqlist = rl;
	}
	
	public View getView(int position, View convertView, ViewGroup parent) {
		
		View v = convertView;
        if (v == null) {
            LayoutInflater vi = (LayoutInflater)cntxt.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            v = vi.inflate(R.layout.listview_request_row, null);
        }
        P2PFileRequest req = reqlist.get(position);
        if (req != null) {
                TextView pn = (TextView) v.findViewById(R.id.requestFile);
                if (pn != null) {
                      pn.setText(req.requestFile);                            
                }
        }
        return v;
		
	}
}