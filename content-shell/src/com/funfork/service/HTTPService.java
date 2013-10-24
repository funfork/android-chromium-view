package com.funfork.service;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;

public class HTTPService extends Service{
	
	private Server server;
	
	@Override
	public void onCreate() {
		super.onCreate();
		
		server = new Server(this.getApplicationContext());
	}
	
	@Override
	public int onStartCommand(Intent intent, int flags, int startId) {
		server.start();
		return START_STICKY;
	}
	
	@Override
	public IBinder onBind(Intent intent) {
		return null;
	}
}
