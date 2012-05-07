package com.p2pwifidirect.connectionmanager;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.text.Layout;
import android.widget.TextView;

public class P2PRoutingManager extends BroadcastReceiver {
	
	TextView console;
	Context cntxt;
	P2PConnectionManager connectionmanager;
	ArrayList<P2PMessage> messagelist;
	P2PMessageAdapter adapter;
	SimpleDateFormat dateformat;

	
	public P2PRoutingManager(Context c, P2PConnectionManager conmgr, TextView con){
		cntxt = c;
		connectionmanager = conmgr;
		console = con;
		messagelist = new ArrayList<P2PMessage>();
		adapter = new P2PMessageAdapter(cntxt,messagelist);
		dateformat = new SimpleDateFormat("HH:mm:ss");
		
		
		//P2PMessage msg = new P2PMessage(connectionmanager.myMAC,"ff:ff:ff:ff:ff:ff","hello world");
		//messagelist.add(msg);
		
		IntentFilter i = new IntentFilter();
		i.addAction("newConnection");
		i.addAction("MessageSent");
		i.addAction("MessageReceived");
		i.addAction("OutgoingApplicationMessage");
		cntxt.registerReceiver(this, i);
				
	}
	
	//public void setConnectionManager(P2PConnectionManager cm){ connectionmanager = cm;}
	

	@Override
	public void onReceive(Context arg0, Intent arg1) {
		
		String action = arg1.getAction();
		
		if(action.equals("newConnection")){
			
			try{
	    		Thread.sleep(3000);
	    	}catch (Exception e){
	    			System.out.println(e.toString());
	    	}
			
			String peerMAC = arg1.getStringExtra("peerMAC");
			appendToConsole("RMGR: New connection available - forwarding messages.");
			forwardMesagesToPeer(peerMAC);	
			
		}else if(action.equals("MessageReceived")){
			
			
			P2PMessage msg = arg1.getParcelableExtra("msg");
			//String receivermac = arg1.getParcelableExtra("rMAC");

			appendToConsole("RMGR: received message with uid " + msg.uid);
			messagelist.add(msg);
			adapter.notifyDataSetChanged();
			
			if(msg.destination.equals("ff:ff:ff:ff:ff:ff") || msg.destination.equals(connectionmanager.myMAC)){
				Intent i = new Intent("IncomingApplicationMessage");
				i.putExtra("msg", msg);
				cntxt.sendBroadcast(i);
				if(msg.destination.equals(connectionmanager.myMAC))
						return;
			}
			
			Iterator<P2PConnection> conit = connectionmanager.connections.iterator();
			while(conit.hasNext()){
				P2PConnection con = conit.next();
				//if(con.myMAC.equals(receivermac))
				//	con.startServer();
				if(con.isConnected && !con.peerMAC.equals(msg.lasthop))
					con.sendMessage(msg);
			}

		}else if(action.equals("OutgoingApplicationMessage")){
			
			appendToConsole("Received outgoing application message, forwarding to all peers.");
			
			String destination = arg1.getStringExtra("destination");
			String body = arg1.getStringExtra("body");
			P2PMessage msg = new P2PMessage(connectionmanager.myMAC,destination,body);
			messagelist.add(msg);
			adapter.notifyDataSetChanged();
			
			Iterator<P2PConnection> conit = connectionmanager.connections.iterator();
			while(conit.hasNext()){
				P2PConnection con = conit.next();
				if(con.isConnected)
					con.sendMessage(msg);
			}
		}
		
	}
	
	public void forwardMesagesToPeer(String peerMAC){
		Iterator<P2PMessage> it = messagelist.iterator();
		while(it.hasNext()){
			P2PMessage msg = it.next();
			if(msg.lasthop.equals(peerMAC))
				continue;
			appendToConsole("RMGR: Trying to forward message with uid " + msg.uid + " to "  + peerMAC);
			connectionmanager.sendMessageToPeer(peerMAC,msg);
		}
		
	}
	
	public void appendToConsole(String s){
    	
		console.append(dateformat.format(new Date()) + " " + s + "\n");
		console.post(new Runnable()
		    {
		        public void run()
		        {
		        	 Layout l = console.getLayout();
		        	 if(l == null)
		        		 return;
		        	 final int scrollAmount = l.getLineTop(console.getLineCount())- console.getHeight();
		        	 if(scrollAmount>0)
		        		 console.scrollTo(0, scrollAmount);
		        	 else
		        		 console.scrollTo(0,0);
		        }
		    });
	}

}
