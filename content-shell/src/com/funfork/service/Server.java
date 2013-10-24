package com.funfork.service;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

import org.apache.http.HttpException;
import org.apache.http.impl.DefaultConnectionReuseStrategy;
import org.apache.http.impl.DefaultHttpResponseFactory;
import org.apache.http.impl.DefaultHttpServerConnection;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.protocol.BasicHttpContext;
import org.apache.http.protocol.BasicHttpProcessor;
import org.apache.http.protocol.HttpRequestHandlerRegistry;
import org.apache.http.protocol.HttpService;

import android.content.Context;

import com.funfork.print.PrintHandler;

public class Server extends Thread{
	
	private int serverPort = 8080;
	
	private BasicHttpProcessor httpProcessor;
	private BasicHttpContext httpContext;
	private HttpService httpService;
	private HttpRequestHandlerRegistry httpHandler;
	
	// Initialize all necessary
	public Server(Context context){
		httpProcessor = new BasicHttpProcessor();
		httpContext = new BasicHttpContext();
		
		/*
		httpProcessor.addInterceptor(new ResponseDate());
		httpProcessor.addInterceptor(new ResponseServer());
		httpProcessor.addInterceptor(new ResponseContent());
		httpProcessor.addInterceptor(new ResponseConnControl());
		*/

        httpService = new HttpService(httpProcessor, new DefaultConnectionReuseStrategy(), new DefaultHttpResponseFactory());
        httpHandler = new HttpRequestHandlerRegistry();
        
        // Different handlers for different operations
        httpHandler.register("*", new DefaultHandler());
        httpHandler.register("/print*", new PrintHandler(context));
        
        httpService.setHandlerResolver(httpHandler);
	}
	
	// Build the connection and response
	@Override
	public void run() {
		try {
			ServerSocket serverSocket = new ServerSocket(serverPort);
			
			serverSocket.setReuseAddress(true);
            
			while(true){
				try {
					Socket socket = serverSocket.accept();
					DefaultHttpServerConnection serverConnection = new DefaultHttpServerConnection();
					serverConnection.bind(socket, new BasicHttpParams());
					httpService.handleRequest(serverConnection, httpContext);
					serverConnection.shutdown();
				} catch (IOException e) {
					e.printStackTrace();
				} catch (HttpException e) {
					e.printStackTrace();
				}
			}
		} 
		catch (IOException e) {
			e.printStackTrace();
		}
	}
}