package com.ire.wiki;
import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.InetSocketAddress;
import java.net.Proxy;
import java.net.URL;
import java.util.zip.GZIPInputStream;

import org.apache.http.HttpException;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.ResponseHandler;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.BasicResponseHandler;

public class HttpQueries {
	public static String sendGetQuery(String query, HttpClient client)
			throws HttpException {
		// Send the query
		HttpGet queryRequest = new HttpGet(query);
		ResponseHandler<String> responseHandler = new BasicResponseHandler();
		String responseBody;
		try {
			responseBody = client.execute(queryRequest, responseHandler);
		} catch (ClientProtocolException e) {
			throw new HttpException("An HTTP protocol error occurred.");
		} catch (IOException e) {
			e.printStackTrace();
			throw new HttpException("The connection was aborted.");
		}
		return responseBody.toString();
	}

	public static String getQuery(String query) throws HttpException {
		String url = query;
		URL obj;
		StringBuffer response = new StringBuffer();
		try {
			obj = new URL(url);
			Proxy proxy = new Proxy(Proxy.Type.HTTP, new InetSocketAddress(
					"proxy.iiit.ac.in", 8080));
			HttpURLConnection con = (HttpURLConnection) obj
					.openConnection(proxy);
			con.setRequestMethod("GET");
			int responseCode = con.getResponseCode();
			System.out.println("\nSending 'GET' request to URL : " + url);
			System.out.println("Response Code : " + responseCode);
			System.out.println(con.getContentEncoding());
			GZIPInputStream in = new GZIPInputStream(con.getInputStream());
			int c;
			while (true) {
				c = in.read();
				if (c == -1) {
					break;
				}
				response.append((char) c);
			}
		} catch (IOException e) {
			throw new HttpException("An HTTP protocol error occurred.");
		}
		return response.toString();
	}
}
