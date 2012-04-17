package com.p2pwifidirect.connectionmanager;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import android.app.Activity;
import android.app.AlarmManager;
import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.app.ListActivity;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.NetworkInfo;
import android.net.wifi.p2p.*;
import android.net.wifi.p2p.WifiP2pManager.*;
import android.os.Looper;
import android.os.SystemClock;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.TextView;
import android.widget.Toast;


public class P2PConnectionManager extends BroadcastReceiver
	implements Runnable, WifiP2pManager.ActionListener, WifiP2pManager.ChannelListener,
				 WifiP2pManager.ConnectionInfoListener, WifiP2pManager.GroupInfoListener, 
				  WifiP2pManager.PeerListListener {
	
	final int CONNECTING_TO_PEER = 1;
	
	int currentState;
	WifiP2pDevice currentPeer;
	P2PConnectionAdapter adapter;
	AlarmManager alrmmgr;
	Boolean discoveryOn = false;
	Channel chnl;
	Context cntxt;
	IntentFilter intntfltr;
	Looper lpr;
	TextView sl;
	WifiP2pManager p2pmgr;
	AlertDialog.Builder adbldr;
	ArrayList<P2PConnection> connections;
    WifiP2pDeviceList curdlist;

	
	public P2PConnectionManager(Context c, Looper l, WifiP2pManager mgr, AlertDialog.Builder ad){
		cntxt = c;
		lpr = l;
		p2pmgr = mgr;
		adbldr = ad;
		connections = new ArrayList<P2PConnection>();
    	adapter = new P2PConnectionAdapter(cntxt, R.layout.listview_peer_row,connections);
    	
    	alrmmgr = (AlarmManager)cntxt.getSystemService(Context.ALARM_SERVICE);
    	Intent i=new Intent("scanAlarm");
    	PendingIntent pi=PendingIntent.getBroadcast(cntxt, 0, i, 0);
        alrmmgr.setRepeating(AlarmManager.RTC_WAKEUP, System.currentTimeMillis(), 1000 * 10, pi); // Millisec * Second * Minute

		intntfltr = new IntentFilter();
		intntfltr.addAction(WifiP2pManager.WIFI_P2P_PEERS_CHANGED_ACTION);
		intntfltr.addAction(WifiP2pManager.WIFI_P2P_CONNECTION_CHANGED_ACTION);
		intntfltr.addAction(WifiP2pManager.WIFI_P2P_STATE_CHANGED_ACTION);
		intntfltr.addAction(WifiP2pManager.WIFI_P2P_THIS_DEVICE_CHANGED_ACTION);
		intntfltr.addAction("scanAlarm");
		cntxt.registerReceiver(this, intntfltr);
	}

	public void run() {
		chnl = p2pmgr.initialize(cntxt, lpr, null);
	}
	
	public void discoverPeers(){	
		p2pmgr.discoverPeers(chnl, this);
	}
	
	public void startDiscovery(){ discoveryOn = true;}
	
	public void stopDiscovery(){ discoveryOn = false; }
	
	public void setScanLabel(TextView tv){ sl = tv;}
	
	public void onFailure(int arg0) {
		CharSequence text = "Unsuccessful discovery: ";
		if(arg0 == WifiP2pManager.P2P_UNSUPPORTED)
			text = text + " unsupported";
		if(arg0 == WifiP2pManager.ERROR)
			text = text + " error";
		if(arg0 == WifiP2pManager.BUSY)
			text = text + " busy";
		
    	int duration = Toast.LENGTH_SHORT;
    	Toast toast = Toast.makeText(cntxt, text, duration);
    	toast.show();		
	}

	public void onSuccess() {
		
	}

	public void onReceive(Context context, Intent intent) {
		CharSequence text;
    	int duration;
    	Toast toast;
    	
    	String action = intent.getAction();
		
		if(action.equals(WifiP2pManager.WIFI_P2P_PEERS_CHANGED_ACTION)){
			text = "Received peers-changed-action";
	    	duration = Toast.LENGTH_SHORT;
	    	toast = Toast.makeText(cntxt, text, duration);
	    	toast.show();
	    	
	    	p2pmgr.requestPeers(chnl,this);
	    	
	    }else if(action.equals(WifiP2pManager.WIFI_P2P_CONNECTION_CHANGED_ACTION)){
	    	text = "Received connection-changed-action";
	    	duration = Toast.LENGTH_SHORT;
	    	toast = Toast.makeText(cntxt, text, duration);
	    	toast.show();
	    
	    	NetworkInfo networkinfo = null;
	    	networkinfo = (NetworkInfo) intent.getParcelableExtra(WifiP2pManager.EXTRA_NETWORK_INFO);

            if (networkinfo.isConnected()) {
            	//this means another device has successfully connected to me
            	//still can't figure out how to get MAC of other device though
            	Toast.makeText(cntxt,networkinfo.toString(),Toast.LENGTH_LONG).show();
            	p2pmgr.requestConnectionInfo(chnl, this);
            }
	    	
	    }else if(action.equals(WifiP2pManager.WIFI_P2P_STATE_CHANGED_ACTION)){
	    	text = "Received state-changed-action";
	    	duration = Toast.LENGTH_SHORT;
	    	toast = Toast.makeText(cntxt, text, duration);
	    	toast.show();
	    	
	    	int state = intent.getIntExtra(WifiP2pManager.EXTRA_WIFI_STATE, -1);
	    	if(state == WifiP2pManager.WIFI_P2P_STATE_DISABLED){
	    		adbldr.setTitle("Wifi Direct Alert");
	    		adbldr.setMessage("Wifi Direct is currently disabled, enable Wifi Direct in Android settings.");
	    		adbldr.setIcon(android.R.drawable.ic_dialog_alert);
	    		adbldr.setPositiveButton("OK", new OnClickListener() {
	    			public void onClick(DialogInterface dialog, int which) {
	    				Intent intent = new Intent(android.provider.Settings.ACTION_WIRELESS_SETTINGS);
	    				intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
	    				cntxt.startActivity(intent);
	    			}
	    		});
	    		adbldr.show();
	    	}
	    	
	    }else if(action.equals(WifiP2pManager.WIFI_P2P_THIS_DEVICE_CHANGED_ACTION)){
	    	text = "Received device-changed-action";
	    	duration = Toast.LENGTH_SHORT;
	    	toast = Toast.makeText(cntxt, text, duration);
	    	toast.show();
	    	
	    }else if(action.equals("scanAlarm")){
	    	
	    	if(!discoveryOn)
	    		return;
	    	
	    	discoverPeers();
	    	if(sl.getText().toString().equals("scan on"))
	    		sl.setText("scan off");
	    	else
	    		sl.setText("scan on");
	    	
	    }else{
	    
	    	text = "Received unknown intent " + intent.toString();
	    	duration = Toast.LENGTH_SHORT;
	    	toast = Toast.makeText(cntxt, text, duration);
	    	toast.show();
	    	
	    }
	}

	public void onPeersAvailable(WifiP2pDeviceList dlist) {
		WifiP2pDevice dev = null;
		WifiP2pConfig cnfg = null;
		P2PConnection tempcon = new P2PConnection(cntxt,chnl,p2pmgr,dev);
				
		curdlist = dlist;
		Collection<WifiP2pDevice> dcol = dlist.getDeviceList();
		Iterator<WifiP2pDevice> it = dcol.iterator();

		while(it.hasNext()){
			
			dev = it.next();
			tempcon.setDevice(dev);
			if(discoveryOn && !connections.contains(tempcon)){
				cnfg = new WifiP2pConfig();
				cnfg.deviceAddress = dev.deviceAddress;
				P2PConnection c = new P2PConnection(cntxt,chnl,p2pmgr,dev);
				c.waitForConnection();
				p2pmgr.connect(chnl, cnfg, c);
				connections.add(c);
				CharSequence text = "Trying to connect to peer: " + dev.toString();
		    	int duration = Toast.LENGTH_SHORT;
		    	Toast toast = Toast.makeText(cntxt, text, duration);
		    	toast.show();	
			}
				
		}
		
		adapter.notifyDataSetChanged();	
	}
	
	public void tryConnection(int indx){
		
		WifiP2pConfig cnfg = null;
		P2PConnection connection = null;
		
		if(connections.get(indx) == null)
			return;
		
		if(connections.get(indx).displayname.contains("connected")){
			//try disconnection here
		}else{
			connection = connections.get(indx);
			cnfg = new WifiP2pConfig();
			cnfg.deviceAddress = connection.dev.deviceAddress;
			p2pmgr.connect(chnl, cnfg, connection);
		}
		
		
	}

	@Override
	public void onGroupInfoAvailable(WifiP2pGroup arg0) {
		CharSequence text = "Group info available: " + arg0.toString();
    	int duration = Toast.LENGTH_LONG;
    	Toast toast = Toast.makeText(cntxt, text, duration);
    	toast.show();	
		
	}

	public void onChannelDisconnected() {
		CharSequence text = "Channel disconnected ";
    	int duration = Toast.LENGTH_SHORT;
    	Toast toast = Toast.makeText(cntxt, text, duration);
    	toast.show();	
		
	}

	@Override
	public void onConnectionInfoAvailable(WifiP2pInfo info) {
		// TODO Auto-generated method stub
		
	}

}
