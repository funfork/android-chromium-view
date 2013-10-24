package com.funfork.print;

import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.URLDecoder;
import java.util.HashMap;
import java.util.Iterator;

import org.apache.http.HttpEntity;
import org.apache.http.HttpEntityEnclosingRequest;
import org.apache.http.HttpException;
import org.apache.http.HttpRequest;
import org.apache.http.HttpResponse;
import org.apache.http.entity.ContentProducer;
import org.apache.http.entity.EntityTemplate;
import org.apache.http.protocol.HttpContext;
import org.apache.http.protocol.HttpRequestHandler;
import org.apache.http.util.EntityUtils;

import android.content.Context;

public class PrintHandler implements HttpRequestHandler {

	private Context context;
	private String message;
	private PrintPlugin printPlugin;
	private HashMap<String, String> printers;

	public PrintHandler(Context context) {
		this.context = context;
		printPlugin = new PrintPlugin(this.context);
	}

	@Override
	public void handle(final HttpRequest request, HttpResponse response,
			final HttpContext context) throws HttpException, IOException {
		try {
			message = null;

			if (request instanceof HttpEntityEnclosingRequest) {
				HttpEntity req = ((HttpEntityEnclosingRequest) request)
						.getEntity();
				message = EntityUtils.toString(req, "UTF-8");
				message = URLDecoder.decode(message, "UTF-8");
			}

			HttpEntity entity = new EntityTemplate(new ContentProducer() {
				public void writeTo(final OutputStream outstream)
						throws IOException {
					OutputStreamWriter writer = new OutputStreamWriter(
							outstream, "UTF-8");

					HashMap<String, String> data = new HashMap<String, String>();
					synchronized (this) {
						if (message != null) {
							String[] post = message.split("&|=");

							for (int i = 0, len = post.length; i < len; i++) {
								data.put(post[i], post[++i]);
							}
						}
					}

					if(message != null){
						if (data.get("action").equalsIgnoreCase("print")) {
							message = printPlugin.print(data.get("text"));
						} else if (data.get("action").equalsIgnoreCase("connect")) {
							if (data.get("mac") != null) {
								String ip = printers.get(data.get("mac"));
								message = printPlugin.connect(ip, 5000);
							} else if (data.get("ip") != null) {
								message = printPlugin.connect(data.get("ip"), 5000);
							}
						} else if (data.get("action").equalsIgnoreCase("find")) {
							String[] printers = printPlugin.getPrinters(5000);
							message = "{";
							for (int i = 0, len = printers.length; i < len; i++) {
								message = message + "\"" + printers[i] + "\",";
							}
							message = message.substring(0, message.length() - 1);
							message = message + "}";
						} else if (data.get("action").equalsIgnoreCase("map")) {
							printers = printPlugin.mapPrinters(5000);
							if (printers != null) {
								Iterator<String> i = printers.keySet().iterator();
								message = "{";
								while (i.hasNext()) {
									String key = (String) i.next();
									String value = (String) printers.get(key);
									message = message + "\"" + key + "\":\"" + value + "\",";
								}
								message = message.substring(0, message.length() - 1);
								message = message + "}";
							} else {
								message = "No printers found";
							}
						}
					} else {
						message = "<!DOCTYPE html>\n"
								+ "<html>\n"
								+ "<body>\n"
								+ "<p>Available Operations</p>"
								+ "<form method='post' action=''>"
								+ "<input type='submit' name='action' value='find'>"
								+ "<input type='submit' name='action' value='map'>"
								+ "</form>"
								+ "<form method='post' action=''>"
								+ "Connect by IP: <input type='text' name='ip'>"
								+ "<input type='submit' name='action' value='connect'>"
								+ "</form>"
								+ "<form method='post' action=''>"
								+ "Connect by MAC: <input type='text' name='mac'>"
								+ "<input type='submit' name='action' value='connect'>"
								+ "</form>"
								+ "<form method='post' action=''>"
								+ "<textarea name='text'></textarea><br>"
								+ "<input type='submit' name='action' value='print'>"
								+ "</form>"
								+ "</body>\n"
								+ "</html>";
					}

					writer.write(message);
					writer.flush();
				}
			});

			((EntityTemplate) entity).setContentType("text/html");

			response.addHeader("Access-Control-Allow-Origin", "*");
			response.addHeader("Access-Control-Allow-Headers", "Origin, X-Requested-With, Content-Type, Accept");
			response.setEntity(entity);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
