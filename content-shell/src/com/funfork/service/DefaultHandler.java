package com.funfork.service;

import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;

import org.apache.http.HttpEntity;
import org.apache.http.HttpException;
import org.apache.http.HttpRequest;
import org.apache.http.HttpResponse;
import org.apache.http.entity.ContentProducer;
import org.apache.http.entity.EntityTemplate;
import org.apache.http.protocol.HttpContext;
import org.apache.http.protocol.HttpRequestHandler;

public class DefaultHandler implements HttpRequestHandler {

	@Override
	public void handle(HttpRequest request, HttpResponse response, final HttpContext context) throws HttpException, IOException {
		try {
			HttpEntity entity = new EntityTemplate(new ContentProducer() {
				public void writeTo(final OutputStream outstream) throws IOException {
					OutputStreamWriter writer = new OutputStreamWriter(outstream, "UTF-8");
					String resp = "Hello world! :P";

					writer.write(resp);
					writer.flush();
				}
			});

			((EntityTemplate) entity).setContentType("text/html");

			response.setEntity(entity);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
