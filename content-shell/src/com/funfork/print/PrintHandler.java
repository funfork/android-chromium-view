package com.funfork.print;

import java.io.IOException;
import java.net.URLDecoder;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import org.apache.http.HttpEntity;
import org.apache.http.HttpEntityEnclosingRequest;
import org.apache.http.HttpException;
import org.apache.http.HttpRequest;
import org.apache.http.HttpResponse;
import org.apache.http.entity.StringEntity;
import org.apache.http.protocol.HttpContext;
import org.apache.http.protocol.HttpRequestHandler;
import org.apache.http.util.EntityUtils;

import android.content.Context;
import android.util.Log;

public class PrintHandler implements HttpRequestHandler {

	private Context context;
	private PrintPlugin printPlugin;
	private Map<String, String> printers = Collections.emptyMap();

	public PrintHandler(Context context) {
		this.context = context;
		printPlugin = new PrintPlugin(this.context);
	}

	@Override
	public void handle(final HttpRequest request, HttpResponse response,
			final HttpContext context) throws HttpException, IOException {
		try {
			String message = null;
			String result = null;

			if (request instanceof HttpEntityEnclosingRequest) {
				HttpEntity req = ((HttpEntityEnclosingRequest) request)
						.getEntity();
				message = EntityUtils.toString(req, "UTF-8");
			}

			Map<String, String> data = new HashMap<String, String>();
			if (message != null) {
				String[] post = message.split("&|=");

				for (int i = 0, len = post.length; i < len; i++) {
					String name = post[i];
					String value = URLDecoder.decode(post[++i], "UTF-8");
					data.put(name, value);
				}
			}

			if (message != null){
				if (data.get("action").equalsIgnoreCase("print")) {
					result = processPrint(data);
				} else if (data.get("action").equalsIgnoreCase("connect")) {
					result = processConnect(data);
				} else if (data.get("action").equalsIgnoreCase("find")) {
					result = processFind();
				} else if (data.get("action").equalsIgnoreCase("map")) {
					result = processMap();
				}
			} 
			
			if (result == null) {
				result = "<!DOCTYPE html>\n"
						+ "<html>\n"
						+ "<body>\n"
						+ "<p>Available Operations</p>"
						+ "<form method='post' action=''>"
						+ "<input type='submit' name='action' value='find'>"
						+ "<input type='submit' name='action' value='map'>"
						+ "</form>\n"
						+ "<form method='post' action=''>"
						+ "Connect by IP: <input type='text' name='ip'>"
						+ "<input type='submit' name='action' value='connect'>"
						+ "</form>\n"
						+ "<form method='post' action=''>"
						+ "Connect by MAC: <input type='text' name='mac'>"
						+ "<input type='submit' name='action' value='connect'>"
						+ "</form>\n"
						+ "<form method='post' action=''>"
						+ "<textarea name='text'></textarea><br>"
						+ "<input type='submit' name='action' value='print'>"
						+ "</form>"
						+ "</body>\n"
						+ "</html>";
			}
			
			response.addHeader("Access-Control-Allow-Origin", "*");
			response.addHeader("Access-Control-Allow-Headers", "Origin, X-Requested-With, Content-Type, Accept");
			response.setEntity(new StringEntity(result));
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private String processMap() {
		String result;
		printers = printPlugin.mapPrinters(5000);
		if (printers != null) {
			Iterator<String> i = printers.keySet().iterator();
			StringBuilder builder = new StringBuilder();
			builder.append("{");
			while (i.hasNext()) {
				String key = (String) i.next();
				String value = (String) printers.get(key);
				builder.append("\"" + key + "\":\"" + value + "\"");
				if (i.hasNext()) builder.append(",");
			}
			builder.append("}");
			result = builder.toString();
		} else {
			result = "No printers found";
		}
		return result;
	}

	private String processFind() {
		String result;
		String[] printers = printPlugin.getPrinters(5000);
		StringBuilder builder = new StringBuilder();
		builder.append("{");
		for (int i = 0, len = printers.length; i < len; i++) {
			builder.append("\"" + printers[i] + "\"");
			if (i < len-1) builder.append(",");
		}
		builder.append("}");
		result = builder.toString();
		return result;
	}

	private String processConnect(Map<String, String> data) {
		String result;
		String ip = null;
		if (data.get("mac") != null) ip = printers.get(data.get("mac"));
		else if (data.get("ip") != null) ip = data.get("ip");
		
		if (ip == null) result = "Printer not found";
		else result = printPlugin.connect(ip, 5000);
		return result;
	}

	private String processPrint(Map<String, String> data) {
		String result;
		result = printPlugin.print(data.get("text"));
		return result;
	}
}
