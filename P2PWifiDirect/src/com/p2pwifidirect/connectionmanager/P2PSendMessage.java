package com.p2pwifidirect.connectionmanager;

import java.io.OutputStream;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.net.SocketAddress;
import java.nio.ByteBuffer;
import java.text.SimpleDateFormat;
import java.util.Date;

import android.app.IntentService;
import android.content.Intent;
import android.util.Log;

public class P2PSendMessage extends IntentService {

	byte[] buffer;
	Date date;
    SimpleDateFormat dateFormat;

	public P2PSendMessage() {
		super("P2PSendMessage");
		dateFormat = new SimpleDateFormat("HH:mm:ss");
    	date = new Date();
	}

	protected void onHandleIntent(Intent intent) {

		Log.w("smservice",dateFormat.format(new Date()) + " Started service, received intent.");
		String ip = intent.getStringExtra("ipaddr");
		int port = intent.getIntExtra("port", -1);
		P2PMessage p2pmsg = intent.getParcelableExtra("msg");

		try{

			Log.w("smservice",dateFormat.format(new Date()) + " Trying to connect to " + ip + ":" + port);
			Socket s = new Socket();
			s.connect(new InetSocketAddress(ip,port),0);
			Log.w("smservice",dateFormat.format(new Date()) + " Connection successful ");
			OutputStream outs = s.getOutputStream();

			for(int i=0;i<P2PMessage.msgarraysize;i++){
				if(P2PMessage.lenarray[i] == -1){ // -1 means we should use the previous value as the length
					if(i==0  || P2PMessage.lenarray[i-1] == -1){
						Log.w("smessage","P2P Message len array not instantiated properly, exiting receive message task.");
						return;
					}

					//Log.w("smessage", "Trying to write string " + i);
					outs.write(p2pmsg.msgarray[i].getBytes(),0,p2pmsg.msgarray[i].length());
					//Log.w("smessage", "Wrote string " + p2pmsg.msgarray[i]);


				}else{

					//Log.w("smessage", "Trying to write int " + i + " (" + p2pmsg.msgarray[i] + ")");
					buffer = ByteBuffer.allocate(P2PMessage.lenarray[i]).putInt(Integer.parseInt(p2pmsg.msgarray[i])).array();
					outs.write(buffer,0,P2PMessage.lenarray[i]);
					//Log.w("smessage", "Wrote int " + p2pmsg.msgarray[i]);

				}
			}

			s.close();

		}catch(Exception e){
			Log.w("smessage",e.toString());
			e.printStackTrace();
		}
		
		Log.w("smservice",dateFormat.format(new Date()) + " Service ended. ");
		
		//don't need this for now
		//Intent i = new Intent("MessageSent");
		//i.putExtra("msg",p2pmsg);
		//sendBroadcast(i);

	}

}