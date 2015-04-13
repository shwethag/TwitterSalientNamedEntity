package com.ire.wiki;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.io.Reader;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.HashSet;
import java.util.List;
import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.Scanner;
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

	public NEWiki() {
		HttpHost proxy = new HttpHost("proxy.iiit.ac.in", 8080);
		client = new DefaultHttpClient();
		client.getParams().setParameter(ConnRoutePNames.DEFAULT_PROXY, proxy);
	}

	public List<String> getNEs(String fileName) {
		List<String> list = new ArrayList<String>();
		try {

			File file = new File(fileName);
			Scanner scanner = new Scanner(file);
			scanner.useDelimiter("\r\n");
			String tweet;
			while (scanner.hasNext()) {
				tweet = scanner.nextLine();
				list.add(tweet);
			}
			scanner.close();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
		return list;
	}

	public List<String> getTopURLs(String query, int n) {

		String urlQuery = url + "api.php?action=query&format=xml";
		String xmlOutput;
		int i = 0;
		int count;
		String urlOffset = "&generator=search&gsrlimit=50&gsroffset=";
		String offset = "";
		try {

			query = URLEncoder.encode(query, "UTF-8");
			System.out.println(query);// ////////////////////////////////////////////
			urlQuery += "&gsrsearch=" + query
					+ "&srprop=titlesnippet&format=xml&continue=";
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
			BodyWeight = (1 - TitleWeight/10) * 2;
			
		} else
			TitleWeight = BodyWeight = 0;
		nGramWeight=0;
		if(ngram>1)
			nGramWeight = (ngram - 1) * 10;
		NNPWeight = flag * 5;
		score = score + TitleWeight + BodyWeight + nGramWeight + NNPWeight; 
		//System.out.print(score);
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
		} catch (SAXException e) {
			e.printStackTrace();
		} catch (ParserConfigurationException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (UnsupportedEncodingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return count;
	}

	public static void main(String[] args) throws FileNotFoundException {
		NEWiki eg = new NEWiki();

		List<String> nerlist = eg.getNEs("custom_ner_output.txt");
		PrintWriter writer = new PrintWriter(new File(
				"custom_ner_count_output.txt"));
		for (String s : nerlist) {
			String words[] = s.split("\\|");
			List<Float> scores = new ArrayList<Float>();
			List<String> SNEs = new ArrayList<String>();
			for (String i : words) {
				int flag = 0;
				if (i.endsWith("~")) {
					// ~ is used for assigning extra score for NNPs
					flag = 1;
					i.replace("~", "");
				}
				// System.out.println(i);
				eg.getTopURLs(i, 2100);
				// String count = (eg.results.size()) + "|";
				int hits = eg.results.size();
				int ngram = i.split(" ").length;

				float tcount = eg.titleCount;
				float score = eg.rank(i, tcount, hits, flag, ngram);
				scores.add(score);
				
				
				// for (String res : eg.results)
				// System.out.println(res);
				//writer.print(score + ",");
				eg.results.clear();
				eg.titleCount = 0;
			}
			for(int j=0;j<3;j++)
			{
				if(j>scores.size())
					break;
				int maxindex=0;
				float max = 0;
				for(int sc=0;sc<scores.size();sc++)
				{
					if(scores.get(sc)>max){
						System.out.println(scores.get(sc));
						max = scores.get(sc);
						maxindex=sc;
					}
				}
				SNEs.add(words[maxindex]);
				scores.set(maxindex, -1.0f);
			
			}
			for(String kk : SNEs)
				System.out.println(kk);
			
			///writer.print("\n");
			// System.out.print("\n");
		}
		writer.close();
		eg.client.getConnectionManager().shutdown();
	}

}
