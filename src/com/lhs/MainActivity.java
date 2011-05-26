package com.lhs;

import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

public class MainActivity extends Activity implements OnClickListener{
	
	TextView statusView;
	Button startButton, stopButton;
	public static final int REQUEST_ENABLE_BT = 22030;
	
    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);
        statusView = (TextView)findViewById(R.id.StatusText);
        statusView.setText("start");
        
        startButton = (Button)findViewById(R.id.StartButton);
        startButton.setOnClickListener(this);
        stopButton = (Button)findViewById(R.id.StopButton);
        stopButton.setOnClickListener(this);
        stopButton.setEnabled(false);
        
        BroadcastReceiver broadReceiver = new BroadcastReceiver  (){

			@Override
			public void onReceive(Context context, Intent intent) {
				String cont = intent.getStringExtra("CONTENTS");
				statusView.setText(cont);
				if(cont.equals("CONNECTED")){
					startButton.setEnabled(false);
					stopButton.setEnabled(true);
				} else if (cont.equals("FAILEDCONNECT")){
					startButton.setEnabled(true);
					stopButton.setEnabled(false);

				}
				
			}      	
        	
        };
        IntentFilter filter = new IntentFilter("GUN.MESSAGE");
        registerReceiver(broadReceiver,filter);
        BluetoothAdapter btDev = BluetoothAdapter.getDefaultAdapter();		
		if (!btDev.isEnabled()) {
		    Intent enableBtIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
		    startActivityForResult(enableBtIntent, REQUEST_ENABLE_BT);
		}
        
    }

	public void onClick(View src) {
		if(src.getId() == R.id.StartButton){
			//start the services
			Log.v("main", " service start");
		      startService(new Intent(this, GunService.class));
		}
		else if(src.getId() == R.id.StopButton){
			//start the services
			Log.v("main", " service stop");
		      stopService(new Intent(this, GunService.class));
		      startButton.setEnabled(true);
				stopButton.setEnabled(false);
		}
	}
    
    
}