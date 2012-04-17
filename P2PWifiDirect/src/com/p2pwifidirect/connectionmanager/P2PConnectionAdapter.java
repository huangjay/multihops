package com.p2pwifidirect.connectionmanager;

import java.util.ArrayList;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

public class P2PConnectionAdapter extends ArrayAdapter<P2PConnection> {
	
	Context cntxt = null;
	int tvid;
	ArrayList<P2PConnection> peerlist = null;
	PeerHolder holder = null;

	public P2PConnectionAdapter(Context c, int textViewResourceId, ArrayList<P2PConnection> pl) {
		super(c, textViewResourceId,pl);
		cntxt = c;
		tvid = textViewResourceId;
		peerlist = pl;
	}
	
	public View getView(int position, View convertView, ViewGroup parent) {
		
		View v = convertView;
        if (v == null) {
            LayoutInflater vi = (LayoutInflater)cntxt.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            v = vi.inflate(R.layout.listview_peer_row, null);
        }
        P2PConnection connection = peerlist.get(position);
        if (connection != null) {
                TextView pn = (TextView) v.findViewById(R.id.peerName);
                if (pn != null) {
                      pn.setText(connection.displayname);                            
                }
        }
        return v;
		
	}
	
	static class PeerHolder
    {
        TextView peerName;
    }
	

}
