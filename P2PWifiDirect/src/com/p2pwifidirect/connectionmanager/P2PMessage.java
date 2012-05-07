package com.p2pwifidirect.connectionmanager;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

import android.os.Parcel;
import android.os.Parcelable;
import android.util.Log;

public class P2PMessage implements Parcelable{
	
	String uid;
	String source;
	String destination;
	String body;
	SimpleDateFormat dateformat = new SimpleDateFormat("HH:mm:ss");
	ArrayList<String> sentlist;
	String lasthop;
	
	static final int msgarraysize = 8;
	static final int[] lenarray = {4,-1,4,-1,4,-1,4,-1}; 
								 //this list should be used to describe the data in the
	      						 //message, get used by P2PSendMessage/P2PReceiveMessage
	                             //the purpose of these is so that the Send/Receive message classes
								 //don't need to be changed to support new message fields, rather
								 //this list just needs to be added to
								 //use the length of the field if it is known, or -1 if its a variable len string
								 //when -1 is encountered, the data in the i-1 field is assumed to be the length of the string
								 //also, update msgarraysize
	String[] msgarray = {"1","1","1","1","1","1","1","1"};
	
	
	//this constructor is used when we're the source
	public P2PMessage(String s, String d, String b){
		
		this.uid = dateformat.format(new Date()) + s;
		this.source = s;
		this.destination = d;
		this.body = b;
		this.lasthop = "";
		
		msgarray[0] = uid.length() + "";
		msgarray[1] = uid;
		msgarray[2] = source.length() + "";
		msgarray[3] = source;
		msgarray[4] = destination.length() + "";
		msgarray[5] = destination;
		msgarray[6] = body.length() + "";
		msgarray[7] = body;
		
	}
	
	//this constructor is used for incomming messages
	public P2PMessage(String[] inarray){
		
		for(int i=0;i<P2PMessage.msgarraysize;i++)
			this.msgarray[i] = inarray[i];
		
		this.uid = inarray[1];
		this.source = inarray[3];
		this.destination = inarray[5];
		this.body = inarray[7];
		this.lasthop = "";

	}
	
	//this constructor is used by the parcelable creator
	public P2PMessage(Parcel source){
		
		for(int i=0;i<P2PMessage.msgarraysize;i++){
			if(P2PMessage.lenarray[i] != -1)
				msgarray[i] = source.readInt() + "";
			else
				msgarray[i] = source.readString();
			Log.w("p2pmsg","read " + msgarray[i] + " from the parcel");
		}
		
		this.lasthop = source.readString();
		
		this.uid = msgarray[1];
		this.source = msgarray[3];
		this.destination = msgarray[5];
		this.body = msgarray[7];
  }
	
	public void addToSentList(String dest){ sentlist.add(dest); }
	
	public void setLastHop(String lasthop){
		this.lasthop = lasthop;
	}

	@Override
	public int describeContents() {
		return 0;
	}

	//this is called when a P2PMessage object is added to an Intent as an Extra
	public void writeToParcel(Parcel arg0, int arg1) {
		for(int i=0;i<P2PMessage.msgarraysize;i++){
			if(P2PMessage.lenarray[i] != -1)
				arg0.writeInt(Integer.parseInt(msgarray[i]));
			else
				arg0.writeString(msgarray[i]);
			//Log.w("p2pmsg","wrote " + msgarray[i] + " to the parcel");
		}
		arg0.writeString(this.lasthop);
	}
	
	//this is the creator which un-parcels messages when an intent is received and getPareclableExtra is called
    public static final Parcelable.Creator<P2PMessage> CREATOR = new Parcelable.Creator<P2PMessage>() {
	      public P2PMessage createFromParcel(Parcel source) {
	            return new P2PMessage(source);
	      }
	      public P2PMessage[] newArray(int size) {
	            return new P2PMessage[size];
	      }
	};

}
