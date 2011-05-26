package com.lhs;

import java.io.IOException;
import java.util.UUID;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.Intent;
import android.util.Log;

public class BluetoothThread extends Thread  {
	
	GunService parent;
	private boolean running = false;
	BluetoothAdapter btDev;
	BluetoothDevice gunDevice;
	BluetoothSocket socket;
	private boolean ready = false;
	String buffer = "";
	
	long timeSinceLastDiscovery = 0;
	
	public BluetoothThread(GunService parent){
		this.parent = parent;
		
		btDev = BluetoothAdapter.getDefaultAdapter();		
		
	}
	
	/*
	 * Connect to a BT address
	 */
	public void connectAddress(String in){
		try{
			gunDevice = btDev.getRemoteDevice(in);
			//connect to the SPP UUID
			socket = gunDevice.createRfcommSocketToServiceRecord(UUID.fromString("00001101-0000-1000-8000-00805F9B34FB"));
			socket.connect();
			socket.getOutputStream().write("connected\r\n".getBytes());
			parent.received("CONNECTED");
			ready = true;
		} catch (IllegalArgumentException e){
			Log.v("BluetoothThread", "illegal address format");
			ready = false;
			parent.received("FAILEDCONNECT");

		} catch (IOException e) {
			Log.v("BluetoothThread", "IO Exception on connect");
			ready = false;
			parent.received("FAILEDCONNECT");
		}
		
	}
	public void start(){
		super.start();
		
	}
	
	public void halt(){
		try{
			socket.close();
		} catch (IOException e) {
			//dont care
			
		} finally {
			ready = false;
		}		
	}
	
	public void run(){
		while(ready == true){

			int s;
			try {
				s = socket.getInputStream().read();
				
				while(s != -1){
					if(s == 13){
						Log.v("BluetoothThread", "received message: " + buffer);
						synchronized(parent){
							parent.received(buffer);
						}
						buffer = "";
						s = (char)socket.getInputStream().read();
					} else {
						buffer += (char)s;
						Log.v("BluetoothThread", "received : " + s);					
						s = (char)socket.getInputStream().read();
					}
				}
			} catch (IOException e) {
				Log.v("BluetoothThread", "IO Exception on receive");
				//alert the parent class
				//wait for reconnect
				ready = false;

			}

			if(timeSinceLastDiscovery + 12000 < System.currentTimeMillis()){
				timeSinceLastDiscovery = System.currentTimeMillis();
				
			}
		}

	}

}
