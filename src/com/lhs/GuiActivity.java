package com.lhs;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.TextView;


public class GuiActivity extends Activity implements OnClickListener{

	private int health = 100;
	private int shield = 100;

	private TextView healthText, shieldText;
	private BroadcastReceiver broadReceiver;

	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.guilayout);

		healthText = (TextView)findViewById(R.id.HealthValue);

		broadReceiver = new BroadcastReceiver  (){

			@Override
			public void onReceive(Context arg0, Intent arg1) {
				String mess = arg1.getStringExtra("com.lhs.ACTION");
				GunMessage g = GunMessage.fromString(mess);
				if (g != null){
					switch(g){
						case GOTSHOT:				
							health -= Integer.parseInt(arg1.getStringExtra("com.lhs.DAMAGE"));
							break;
						case DISCONNECT:
							finish();
							break;
						case HEALTHUPDATE:
							health = Integer.parseInt(arg1.getStringExtra("com.lhs.HEALTH"));
							break;
						case SHIELDUPDATE:
							shield = Integer.parseInt(arg1.getStringExtra("com.lhs.SHIELD"));
							break;	

					}
				}
				updateGui();
			}
		};
		IntentFilter filter = new IntentFilter("com.lhs.GUN.MESSAGE");
		registerReceiver(broadReceiver,filter);

	}

	public void updateGui(){
		healthText.setText("" + health);

	}

	//@override
	public void finish(){
		unregisterReceiver( broadReceiver);
		super.finish();
	}

	public void onClick(View arg0) {
		// TODO Auto-generated method stub

	}
}
