package com.funfork.print;

import java.util.HashMap;
import java.util.Set;

import com.bixolon.printer.BixolonPrinter;

import android.annotation.SuppressLint;
import android.content.Context;
import android.os.Handler;
import android.os.Message;

public class PrintPlugin {

	private String message;
	private String[] arrMessage;
	
	private Context context;
	private Handler handler;
	private BixolonPrinter printer;
	
	@SuppressLint("HandlerLeak")
	public PrintPlugin(Context context){
		this.context = context;
		
		this.handler = new Handler(){
	        @Override
	        public void handleMessage(Message msg) {
	        	switch( msg.what ){
	        	case BixolonPrinter.MESSAGE_NETWORK_DEVICE_SET:
	        		if(msg.obj == null){
	        			arrMessage = new String[]{"No printers found"};
	        		}else{
	        			@SuppressWarnings("unchecked")
						Set<String> ipAddressSet = (Set<String>) msg.obj;
	        			arrMessage = ipAddressSet.toArray(new String[ipAddressSet.size()]);
	        		}
		        	synchronized (PrintPlugin.this) {
		        		PrintPlugin.this.notifyAll();
		        	}
		        	break;
	        	case BixolonPrinter.MESSAGE_STATE_CHANGE:
	        		switch (msg.arg1){
	        		case BixolonPrinter.STATE_NONE:
	        			message = "Disconnected";
	        			synchronized (PrintPlugin.this) {
			        		PrintPlugin.this.notifyAll();
			        	}
	        			break;
	        		case BixolonPrinter.STATE_CONNECTING:
	        			// Just wait until state connected
	        			break;
	        		case BixolonPrinter.STATE_CONNECTED:
	        			message = "Connected";
	        			synchronized (PrintPlugin.this) {
			        		PrintPlugin.this.notifyAll();
			        	}
	        			break;
	        		}
	        		break;
	        	case BixolonPrinter.MESSAGE_PRINT_COMPLETE:
	        		message = "You've printed successfully";
	        		synchronized (PrintPlugin.this) {
		        		PrintPlugin.this.notifyAll();
		        	}
	        		break;
	        	}
	        }
		};
	    
	    printer = new BixolonPrinter(this.context,handler,null);
	}
	
	public String[] getPrinters(Integer timeout){
		arrMessage = null;
		printer.findNetworkPrinters(timeout);
	    synchronized (this) {
			try {
				this.wait();
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
	    }
		return arrMessage;
	}
	
	public String connect(String ip, Integer timeout){
		message = null;
		printer.connect(ip,9100,timeout);
		synchronized (this) {
			try {
				this.wait();
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
		return message;
	}
	
	public String print(String text){
		message = null;
		
		printer.printText(text+"\n\n", BixolonPrinter.ALIGNMENT_CENTER, BixolonPrinter.TEXT_ATTRIBUTE_FONT_A, BixolonPrinter.TEXT_SIZE_HORIZONTAL1, true);
		printer.lineFeed(3, false);
		printer.cutPaper(true);
		synchronized (this) {
			try {
				this.wait();
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
		return message;
	}
	
	public HashMap<String, String> mapPrinters(Integer timeout){
		HashMap<String, String> printers = new HashMap<String, String>();
		
		arrMessage = this.getPrinters(timeout);
		
		if(arrMessage[0].contentEquals("No printers found")){
			return null;
		}else{
			for(String ip : arrMessage){
				message = null;
				printer.connect(ip,9100,timeout);
				synchronized (this) {
					try {
						this.wait();
					} catch (InterruptedException e) {
						e.printStackTrace();
					}
				}
				printers.put(printer.getMacAddress(), ip);
			}
			return printers;
		}
	}
}
