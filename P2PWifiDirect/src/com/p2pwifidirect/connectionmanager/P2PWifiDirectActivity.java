package com.p2pwifidirect.connectionmanager;


import android.app.Activity;
import android.app.AlertDialog;
import android.app.ListActivity;
import android.app.ListFragment;
import android.content.Context;
import android.net.wifi.p2p.WifiP2pManager;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.Button;


public class P2PWifiDirectActivity extends ListActivity{
	
	P2PConnectionManager c;
	Context context;
	WifiP2pManager wfp2pmgr;
	
    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);
    	context = getApplicationContext();
    	wfp2pmgr = (WifiP2pManager) getSystemService(Context.WIFI_P2P_SERVICE);
    	c = new P2PConnectionManager(context,getMainLooper(),wfp2pmgr,new AlertDialog.Builder(this));
    	c.setScanLabel((TextView)findViewById(R.id.scanlabel));
    	setListAdapter(c.adapter);
    	c.run();

    }
    
    public void startScan(View v){
    	if(((Button)findViewById(R.id.scanbutton)).getText().equals("Start Scanning")){
    		c.startDiscovery();
    		((Button)findViewById(R.id.scanbutton)).setText("Stop Scanning");
    	}else{
    		c.stopDiscovery();
    		((Button)findViewById(R.id.scanbutton)).setText("Start Scanning");
    	}
    }
    
    protected void onListItemClick(ListView l, View v, int position, long id) {
    	c.tryConnection(position);
    }

}