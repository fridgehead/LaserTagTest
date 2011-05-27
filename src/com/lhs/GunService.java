package com.lhs;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.util.Log;
import android.widget.Toast;

/*
 * Creates a bluetooth listening thread, when receiving data forwards it out as broadcast intents
 * for the UI or other apps to listen for
 */
public class GunService extends Service{

	public static final String TAG = "GunService";
	BluetoothThread btThread;

	@Override
	public IBinder onBind(Intent intent) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void onCreate() {
		Toast.makeText(this, "GunService Created", Toast.LENGTH_LONG).show();
		Log.d(TAG, "onCreate");
		btThread = new BluetoothThread(this);


	}

	@Override
	public void onDestroy() {
		Toast.makeText(this, "GunService Stopped", Toast.LENGTH_LONG).show();
		Log.d(TAG, "onDestroy");
		btThread.halt();
	}

	@Override
	public void onStart(Intent intent, int startid) {
		Toast.makeText(this, "GunService Started", Toast.LENGTH_LONG).show();
		Log.d(TAG, "onStart");
		String addr = intent.getExtras().getString("com.lhs.btaddress");
		//btThread.connectAddress("00:1E:C2:8A:BF:36");
		btThread.connectAddress(addr);
		btThread.start();

	}

	public void received(String buf){
		//Toast.makeText(this, "GunService:" + buf, Toast.LENGTH_LONG).show();
		//chuck an intent
		Intent i = new Intent("com.lhs.GUN.MESSAGE");
		String[] msgComponents = buf.split(",");
		if(msgComponents.length > 0){
			GunMessage g = GunMessage.fromString(msgComponents[0]);
			if(g!=null){
				i.putExtra("com.lhs.ACTION", msgComponents[0]);

				switch(g){
				case GOTSHOT:
					//we were shot
					i.putExtra("com.lhs.DAMAGE", msgComponents[1]);
					i.putExtra("com.lhs.PLAYERID", msgComponents[2]);
					break;
				case HEALTHUPDATE:
					i.putExtra("com.lhs.HEALTH", msgComponents[1]);
					break;
				
				case SHIELDUPDATE:
					i.putExtra("com.lhs.SHIELD", msgComponents[1]);
					break;
				}


				sendBroadcast(i);
			}
			Log.d(TAG, "Received : " + buf);

		}
	}
}
