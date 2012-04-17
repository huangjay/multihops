package com.p2pwifidirect.connectionmanager;

import java.util.ArrayList;
import java.util.List;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.NetworkInfo;
import android.net.wifi.p2p.*;
import android.net.wifi.p2p.WifiP2pManager.Channel;
import android.widget.Toast;


public class P2PConnection extends BroadcastReceiver 
		implements WifiP2pManager.ActionListener, WifiP2pManager.ConnectionInfoListener{
	
	final int WAITING_FOR_CONNECTION = 2;
	final int CONNECTED = 3;
	
	int currentState = 0;
	String displayname = "";
	Channel chnl = null;
	Context cntxt = null;
	WifiP2pDevice dev = null;
	WifiP2pManager p2pmgr = null;
	IntentFilter intntfltr = null;
	
	public P2PConnection(Context c1, Channel c2, WifiP2pManager mgr, WifiP2pDevice d){
		
		cntxt = c1;
		chnl = c2;
		p2pmgr = mgr;
		dev = d;
		displayname = (dev == null) ? "" : dev.deviceName;
		
	}
	
	public void setDevice(WifiP2pDevice d){ dev = d; }
	
	@Override
	public boolean equals(Object other){
	    if (other == null) return false;
	    if (other == this) return true;
	    if (!(other instanceof P2PConnection))return false;
	    P2PConnection othercon = (P2PConnection)other;
	    if(othercon.dev.equals(this.dev))
	    	return true;
	    return false;
	}

	
	public void waitForConnection(){
		intntfltr = new IntentFilter();
		intntfltr.addAction(WifiP2pManager.WIFI_P2P_CONNECTION_CHANGED_ACTION);
		cntxt.registerReceiver(this, intntfltr);
		currentState = WAITING_FOR_CONNECTION;
	}

	@Override
	public void onFailure(int arg0) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onSuccess() {
		// TODO Auto-generated method stub
		switch(this.currentState){
		case WAITING_FOR_CONNECTION:
			currentState = CONNECTED;
			break;
		}
		
	}

	@Override
	public void onReceive(Context arg0, Intent intnt) {
		
		CharSequence text;
    	int duration;
    	Toast toast;
    	String action = intnt.getAction();
		
		if(action.equals(WifiP2pManager.WIFI_P2P_CONNECTION_CHANGED_ACTION)){
	    	text = "CONNECTION: Received connection-changed-action";
	    	duration = Toast.LENGTH_SHORT;
	    	toast = Toast.makeText(cntxt, text, duration);
	    	toast.show();
	    
	    	NetworkInfo networkinfo = null;
	    	networkinfo = (NetworkInfo) intnt.getParcelableExtra(WifiP2pManager.EXTRA_NETWORK_INFO);

            if (networkinfo.isConnected()) {
            	displayname += " - connected";
            	p2pmgr.requestConnectionInfo(chnl, this);
            }

	    	
	    }
		
	}

	@Override
	public void onConnectionInfoAvailable(WifiP2pInfo info) {
		// TODO Auto-generated method stub
		CharSequence text = "CONNECTION: Connection info available: " + info.toString();
    	int duration = Toast.LENGTH_LONG;
    	Toast toast = Toast.makeText(cntxt, text, duration);
    	toast.show();
    	
    	//here check whether we're the group owner, then create server socket if so
		
		
	}

}