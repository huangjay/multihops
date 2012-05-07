package com.p2pwifidirect.connectionmanager;

import java.io.InputStream;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.nio.ByteBuffer;
import java.text.SimpleDateFormat;
import java.util.Date;

import android.app.IntentService;
import android.content.Intent;
import android.util.Log;

public class P2PReceiveMessage extends IntentService
{
	byte[] buffer;
	int len;
	Date date;
    SimpleDateFormat dateFormat;

	public P2PReceiveMessage() {
		super("P2PReceiveMessage");
		dateFormat = new SimpleDateFormat("HH:mm:ss");
    	date = new Date();
	}

	@Override
	protected void onHandleIntent(Intent arg0) {
		
		Log.w("rmservice",dateFormat.format(new Date()) + " Started service, received intent.");
		String ip = arg0.getStringExtra("ipaddr");
		int port = arg0.getIntExtra("port", -1);
		String peerMAC = arg0.getStringExtra("peerMAC");
		String rMAC = arg0.getStringExtra("rMAC");
		String[] msgarray = new String[P2PMessage.msgarraysize];


		try{
			//do socket initialization
			ServerSocket ss = new ServerSocket();
			ss.setReuseAddress(true);
			ss.setSoTimeout(0);
			ss.bind(new InetSocketAddress(ip,port));
			Log.w("rmservice",dateFormat.format(new Date()) + " Creating server socket on port " + port + ", accepting connections on server socket.");
			Socket s = ss.accept();
			Log.w("rmservice",dateFormat.format(new Date()) + " Connection successful on server socket.");
			InputStream ins = s.getInputStream();
			
			for(int i=0;i<P2PMessage.msgarraysize;i++){
				
				if(P2PMessage.lenarray[i] == -1){ // -1 means we should use the previous value as the length
					if(i==0  || P2PMessage.lenarray[i-1] == -1){
						Log.w("rmessage","P2P Message len array not instantiated properly, exiting receive message task.");
						return;
					}
					
					len = Integer.parseInt(msgarray[i-1]);
					buffer = ByteBuffer.allocate(len).array();
					ins.read(buffer,0,len);
					msgarray[i] = new String(buffer);
					Log.w("rmessage", "Received string " + msgarray[i]);

					
				}else{ //this is a length that we should read in as an int
					
					len = P2PMessage.lenarray[i];
					buffer = ByteBuffer.allocate(len).array();
					ins.read(buffer,0,len);
					msgarray[i] = ByteBuffer.wrap(buffer).getInt() + "";
					Log.w("rmessage", "Received int " + msgarray[i]);
					
				}
			}
			
			s.close();
			ss.close();
						
		}catch(Exception e){
			Log.w("rmessage",e.toString());
			e.printStackTrace();
		}
		
		Log.w("rmservice",dateFormat.format(new Date()) + " Service ended. ");
		
		Intent i = new Intent("MessageReceived");
		P2PMessage msg = new P2PMessage(msgarray);
		msg.setLastHop(peerMAC);
		i.putExtra("msg", msg);
		sendBroadcast(i);
		
		Intent i2 = new Intent("RestartServer");
		i2.putExtra("rMAC", rMAC);
		sendBroadcast(i2);
		
	}
	
}
