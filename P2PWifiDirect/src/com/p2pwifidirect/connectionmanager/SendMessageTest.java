package com.p2pwifidirect.connectionmanager;

import java.io.OutputStream;
import java.net.Socket;

import android.app.IntentService;
import android.content.Intent;
import android.util.Log;

public class SendMessageTest extends IntentService
{
	byte[] buffer;
    Socket s;
    OutputStream outs;
    String mys = "HELL0";

	public SendMessageTest() {
		super("SendMessage");
		// TODO Auto-generated constructor stub
	}

	@Override
	protected void onHandleIntent(Intent arg0) {
		Log.w("smservice","Started service, received intent.");
		String ip = arg0.getStringExtra("ipaddr");
		int port = arg0.getIntExtra("port", -1);
		try{
			Socket s = new Socket(ip,port);
			outs = s.getOutputStream();
			Log.w("service","I am going to write to the socket now.");
			outs.write(mys.getBytes());
			Log.w("service","I have returned from socket write.");
			s.close();
		}catch(Exception e){
			System.out.println(e.toString());
		}
		Intent i = new Intent("MessageSent");
		i.putExtra("msg",mys);
		sendBroadcast(i);
		
		
	}
	
}