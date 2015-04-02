package com.ire.wiki;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.apache.http.HttpException;
import org.apache.http.HttpHost;
import org.apache.http.client.HttpClient;
import org.apache.http.conn.params.ConnRoutePNames;
import org.apache.http.impl.client.DefaultHttpClient;
import org.w3c.dom.Document;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;


public class NEWiki {
	String url = "https://en.wikipedia.org/w/";
	HttpClient client;
	HashSet<String> URLs = new HashSet<>();
	List<String> results = new ArrayList<>();

	public NEWiki() {
		HttpHost proxy = new HttpHost("proxy.iiit.ac.in", 8080);
		client = new DefaultHttpClient();
		client.getParams().setParameter(ConnRoutePNames.DEFAULT_PROXY, proxy);
	}

	public List<String> getTopURLs(String query, int n) {
		String urlQuery = url + "api.php?action=query&format=xml";
		String xmlOutput;
		int i = 0;
		int count;
		String urlOffset = "&gsrlimit=50&gsroffset=";
		String offset = "";
		try {
			query = URLEncoder.encode(query, "UTF-8");
			urlQuery += "&generator=search&gsrsearch=" + query
					+ "&gsrprop=snippet&prop=info&inprop=url";
			for (i = 0; i < n; i += 50) {
				offset = urlQuery + urlOffset + i;
				xmlOutput = HttpQueries.sendGetQuery(offset, client);
				count = extractURLs(xmlOutput);
				if (count <= 50)
					break;
			}
			
		} catch (HttpException e) {
			e.printStackTrace();
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		} finally {
			client.getConnectionManager().shutdown();
		}
		return results;
	}

	private int extractURLs(String xmlOutput) {
		int i = 0;
		NamedNodeMap properties;
		Node propNode, page;
		int count = 0;
		try {
			DocumentBuilderFactory docFactory = DocumentBuilderFactory
					.newInstance();
			DocumentBuilder docBuilder = docFactory.newDocumentBuilder();
			InputStream in = new ByteArrayInputStream(xmlOutput.getBytes());
			Reader reader = new InputStreamReader(in, "UTF-8");
			InputSource is = new InputSource(reader);
			is.setEncoding("UTF-8");
			Document doc = docBuilder.parse(is);
			NodeList pageList = doc.getElementsByTagName("page");
			count = pageList.getLength();
			for (i = 0; i < count; i++) {
				page = pageList.item(i);
				properties = page.getAttributes();
				propNode = properties.getNamedItem("fullurl");
				String result = "";
				result += propNode.getTextContent()+ " ";
				propNode = properties.getNamedItem("title");
				result += propNode.getTextContent() + " ";
				results.add(result);
				if (i >= 20)
					break;
			}
		} catch (SAXException | IOException | ParserConfigurationException e) {
			e.printStackTrace();
		}
		return count;
	}
	
	public static void main(String[] args) {
		NEWiki eg = new NEWiki();
		eg.getTopURLs("sachin", 3);
		for(String res : eg.results){
			System.out.println(res);
		}
	}
}

