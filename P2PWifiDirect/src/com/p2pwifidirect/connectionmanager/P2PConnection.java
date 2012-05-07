package com.p2pwifidirect.connectionmanager;

import java.io.InputStream;
import java.io.OutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.nio.ByteBuffer;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import android.app.IntentService;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.NetworkInfo;
import android.net.wifi.p2p.*;
import android.net.wifi.p2p.WifiP2pManager.Channel;
import android.os.AsyncTask;
import android.util.Log;
import android.widget.TextView;
import android.widget.Toast;


public class P2PConnection{
	
	final int WAITING_FOR_CONNECTION = 2;
	final int CONNECTED = 3;
	
	int groupowner = 0;
	int currentState = 0;
	int childport;
	int sendport;
	int recvport;
	String peerIP;
	String peerMAC;
	String myIP;
	String myMAC;
	boolean isConnected = false;
	String displayname = "";
	String groupip = "";
	Channel chnl = null;
	Context cntxt = null;
	WifiP2pDevice dev = null;
	WifiP2pManager p2pmgr = null;
	P2PConnectionManager conmgr = null;
	Socket mysocket;
	TextView console;
	SimpleDateFormat dateFormat;
	
	public P2PConnection(Context c1, Channel c2, WifiP2pManager mgr, P2PConnectionManager cmgr, WifiP2pDevice d, TextView c3){
		
		cntxt = c1;
		chnl = c2;
		p2pmgr = mgr;
		conmgr = cmgr;
		dev = d;
		console = c3;
		displayname = (dev == null) ? "" : dev.deviceName;
		
    	dateFormat = new SimpleDateFormat("HH:mm:ss");

		
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
	
	public void setMyInfo(String mac, String ip){
		this.myMAC = mac;
		this.myIP = ip;
	}
	
	public void setPeerInfo( String mac, String ip, int cp, int go){
		this.groupowner = go;
		this.peerMAC = mac;
		this.peerIP = ip;
		
		this.childport = cp; 
		this.sendport = (this.groupowner==1) ? this.childport : this.childport+1;
		this.recvport = (this.groupowner==1) ? this.childport+1 : this.childport;
		//if(this.groupowner==1)
		//	appendToConsole("I am the group owner send port " + sendport + " recv port " + recvport);
		//else
		//	appendToConsole("I am not group owner send port " + sendport + " recv port " + recvport);
	}
	
	public void setConnected(){
		currentState = CONNECTED;
    	displayname += " - connected";
    	isConnected = true;
	}

	public void startServer(){
		
    	//appendToConsole("CON: Starting P2PRecvMessage Service");
    	Intent recvIntent = new Intent(cntxt, P2PReceiveMessage.class);
    	recvIntent.putExtra("ipaddr",myIP);
    	recvIntent.putExtra("port", this.recvport);
    	recvIntent.putExtra("peerMAC", peerMAC);
    	recvIntent.putExtra("rMAC", myMAC);
    	cntxt.startService(recvIntent);

	}

	public void sendMessage(P2PMessage msg){
		
		//appendToConsole("CON: Starting P2PSendMessage Service");
		Intent sendIntent = new Intent(cntxt, P2PSendMessage.class);
		sendIntent.putExtra("ipaddr",peerIP);
    	sendIntent.putExtra("port", this.sendport);
    	sendIntent.putExtra("msg", msg);
    	cntxt.startService(sendIntent);
		
	}
	
	public void disconnect(){
		displayname = dev.deviceName;
	}

	
	public void sendNewMessageToRoutingManager(P2PMessage msg){
		appendToConsole("CON: Received new message.");
		Intent i = new Intent("newMessage");
		i.putExtra("msg", msg);
		cntxt.sendBroadcast(i);
	}
	
	public void appendToConsole(String s){
    	
		console.append(dateFormat.format(new Date()) + " " + s + "\n");
		console.post(new Runnable()
		    {
		        public void run()
		        {
		        	 final int scrollAmount = console.getLayout().getLineTop(console.getLineCount())- console.getHeight();
		        	 if(scrollAmount>0)
		        		 console.scrollTo(0, scrollAmount);
		        	 else
		        		 console.scrollTo(0,0);
		        }
		    });
	}

}