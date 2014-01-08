package com.funfork.service;

import org.chromium.content_shell.R;

import android.app.Notification;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Intent;
import android.os.IBinder;

public class HTTPService extends Service{
	
	private Server server;
	
	@Override
	public void onCreate() {
		super.onCreate();
		
		Notification printification = new Notification(R.drawable.printer,"Printer Server",System.currentTimeMillis());
		Intent notificationIntent = new Intent(this, org.chromium.content_shell.ContentShellActivity.class);
		PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, notificationIntent, 0);
		printification.setLatestEventInfo(this, "Printer Server","Printer detection service", pendingIntent);
        startForeground(1337, printification);
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
