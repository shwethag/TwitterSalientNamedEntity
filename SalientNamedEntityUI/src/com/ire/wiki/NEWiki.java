package com.ire.wiki;

import java.io.ByteArrayInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;

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
	HashSet<String> URLs = new HashSet<String>();
	List<String> results = new ArrayList<String>();
	private float TitleWeight;
	private float nGramWeight;
	private float NNPWeight;
	private float BodyWeight;
	private float titleCount;
	private Map<String,Integer> sneMap;
	public NEWiki() {
		sneMap = new HashMap<String, Integer>();
		HttpHost proxy = new HttpHost("proxy.iiit.ac.in", 8080);
		client = new DefaultHttpClient();
		client.getParams().setParameter(ConnRoutePNames.DEFAULT_PROXY, proxy);
	}

	private List<String> getTopURLs(String query, int n) {
		if(sneMap.containsKey(query)){
			return results;
		}
		String urlQuery = url + "api.php?action=query&format=xml";
		String xmlOutput;
		int i = 0;
		int count;
		String urlOffset = "&generator=search&gsrlimit=50&gsroffset=";
		String offset = "";
		try {
			
			String qry = URLEncoder.encode(query, "UTF-8");
			urlQuery += "&gsrsearch=" + qry
					+ "&gsrprop=titlesnippet&format=xml&continue=";
			for (i = 0; i < n; i += 50) {
				offset = urlQuery + urlOffset + i;
				xmlOutput = HttpQueries.sendGetQuery(offset, client);
				count = extractURLs(xmlOutput, query);
			}

		} catch (HttpException e) {
			e.printStackTrace();
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		} finally {

		}
		return results;
	}

	private float rank(String ne, float tcount, int hits, int flag, int ngram) {
		// TCount is no. of times NE is appearing in titles
		float score = 1;
		if (hits != 0) {
			TitleWeight = (tcount / hits) * 10;
			BodyWeight = (1 - TitleWeight / 10) * 2;

		} else
			TitleWeight = BodyWeight = 0;
		nGramWeight = 0;
		if (ngram > 1)
			nGramWeight = (ngram - 1) * 10;
		NNPWeight = flag * 5;
		score = score + TitleWeight + BodyWeight + nGramWeight + NNPWeight;
		return score;

	}

	private int extractURLs(String xmlOutput, String query) {
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

				String result = "";

				propNode = properties.getNamedItem("title");
				result += propNode.getTextContent() + " ";
				String words[] = query.split(" ");
				for (String word : words) {
					if (result.toLowerCase().contains(word.toLowerCase())) {
						titleCount += 1 / words.length;
					}
				}
				results.add(result);
			}
			Integer cnt;
			if((cnt = sneMap.get(query))==null)
				sneMap.put(query, count);
			else{
				sneMap.put(query, count+cnt);
			}
		} catch (SAXException e) {
			e.printStackTrace();
		} catch (ParserConfigurationException e) {
			e.printStackTrace();
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return count;
	}

	public String getSNE(String neList) throws FileNotFoundException {

		String words[] = neList.split("\\|");
		String sneList="";
		List<Float> scores = new ArrayList<Float>();
		List<String> SNEs = new ArrayList<String>();
		for (String i : words) {
			int flag = 0;
			if (i.endsWith("~")) {
				// ~ is used for assigning extra score for NNPs
				flag = 1;
				i = i.replace("~", "");
			}
			getTopURLs(i, 700);
			int hits = sneMap.get(i);//results.size();
			int ngram = i.split(" ").length;

			float tcount = titleCount;
			float score = rank(i, tcount, hits, flag, ngram);
			scores.add(score);

			results.clear();
			titleCount = 0;
		}
			for (int j = 0; j < 3; j++) {
				if (j >= scores.size())
					break;
				int maxindex = 0;
				float max = 0;
				for (int sc = 0; sc < scores.size(); sc++) {
					if (scores.get(sc) > max) {
						max = scores.get(sc);
						maxindex = sc;
					}
				}
				SNEs.add(words[maxindex]);
				scores.set(maxindex, -1.0f);

			}
			StringBuilder builder = new StringBuilder();
			for (String sne : SNEs) {
				if (sne.endsWith("~"))
					sne = sne.substring(0, sne.lastIndexOf('~'));
				builder.append(sne + ",");

			}
			sneList = builder.substring(0, builder.lastIndexOf(","));

		
		client.getConnectionManager().shutdown();
		return sneList;
	}

}
