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
import android.view.Window;
import android.view.WindowManager;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

public class MainActivity extends Activity implements OnClickListener{

	TextView statusView;
	Button startButton, stopButton;
	public static final int REQUEST_ENABLE_BT = 22030;
	public static final int REQUEST_QRCODE = 202948;

	private String btAddress = "";

	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);

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
				String cont = intent.getStringExtra("com.lhs.ACTION");

				GunMessage g = GunMessage.fromString(cont);
				if(g!=null){

					switch(g){
					case CONNECTED:
						startButton.setEnabled(false);
						stopButton.setEnabled(true);

						//try to switch to the other activity
						Intent startMainGui = new Intent( MainActivity.this, GuiActivity.class);
						startActivity(startMainGui);
						break;
					case DISCONNECT:
						startButton.setEnabled(true);
						stopButton.setEnabled(false);
						Toast.makeText(MainActivity.this, "Disconnected from gun", Toast.LENGTH_LONG).show();

					}
				}


			}      	

		};
		//register for gun.message intents
		IntentFilter filter = new IntentFilter("com.lhs.GUN.MESSAGE");
		registerReceiver(broadReceiver,filter);


		//enable BT if its not turned on.
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
			requestQR();
		}
		else if(src.getId() == R.id.StopButton){
			//start the services
			Log.v("main", " service stop");
			stopService(new Intent(this, GunService.class));
			startButton.setEnabled(true);
			stopButton.setEnabled(false);
		} 
	}

	public void requestQR(){
		Toast.makeText(MainActivity.this, "Please scan the Gun", Toast.LENGTH_LONG).show();

		Intent intent = new Intent("com.google.zxing.client.android.SCAN");
		intent.putExtra("SCAN_MODE", "QR_CODE_MODE");
		startActivityForResult(intent, REQUEST_QRCODE);

	}
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {  
		if (requestCode == REQUEST_QRCODE) {
			if (resultCode == RESULT_OK) {
				String contents = data.getStringExtra("SCAN_RESULT");
				String format = data.getStringExtra("SCAN_RESULT_FORMAT");
				// Handle successful scan
				Log.v("main", contents);
				btAddress = contents;
				Intent startIntent = new Intent(this, GunService.class);
				Bundle b = new Bundle();
				b.putString("com.lhs.btaddress", contents);
				startIntent.putExtras(b);
				startService(startIntent);

			} else if (resultCode == RESULT_CANCELED) {
				// Handle cancel
				Log.v("main", "cancelled");
				Toast.makeText(MainActivity.this, "Scan Cancelled", Toast.LENGTH_LONG).show();


			}

		}  
	}


}