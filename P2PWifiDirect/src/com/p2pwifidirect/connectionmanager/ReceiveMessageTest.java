package com.p2pwifidirect.connectionmanager;

import java.io.InputStream;
import java.net.ServerSocket;
import java.net.Socket;

import android.app.IntentService;
import android.content.Intent;
import android.util.Log;

public class ReceiveMessageTest extends IntentService
{
	byte[] buffer;
    //Socket s;
    InputStream ins;
    String mys;

	public ReceiveMessageTest() {
		super("ReceiveMessage");
		//this.s = s;
		// TODO Auto-generated constructor stub
	}

	@Override
	protected void onHandleIntent(Intent arg0) {
		
		Log.w("rmservice","Started service, received intent.");
		String ip = arg0.getStringExtra("ipaddr");
		int port = arg0.getIntExtra("port", -1);

		try{
			ServerSocket ss = new ServerSocket(port);
			Socket s = ss.accept();
			ins = s.getInputStream();
			buffer = new byte[5];
			Log.w("rmservice","I am going to read from socket now.");
			ins.read(buffer,0,5);
			Log.w("rmservice","I have returned from socket read.");
			mys = new String(buffer);
			s.close();
		}catch(Exception e){
			System.out.println(e.toString());
		}
		
		Intent i = new Intent("MessageReceived");
		i.putExtra("msg", mys);
		sendBroadcast(i);
		
	}
	
}