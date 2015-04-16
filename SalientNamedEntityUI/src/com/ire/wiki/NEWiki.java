package com.ire.wiki;

import java.io.BufferedWriter;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Scanner;
import java.util.Set;

import javax.servlet.ServletContext;
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
	private String scoreFile = "/WEB-INF/score.txt";
	private String stopWords = "/WEB-INF/stopwords.txt";
	private Map<String, Float> neMap;
	private BufferedWriter scoreWriter;
	private Set<String> stopwordSet;
	private ServletContext context;

	public NEWiki(ServletContext cntxt) throws IOException {
		context = cntxt;
		neMap = new HashMap<String, Float>();
		HttpHost proxy = new HttpHost("proxy.iiit.ac.in", 8080);
		client = new DefaultHttpClient();
		client.getParams().setParameter(ConnRoutePNames.DEFAULT_PROXY, proxy);
		System.out.println(context.getContextPath());
		String path = context.getRealPath(scoreFile);
		stopwordSet = new HashSet<String>();
		loadScores();
		loadStopWords();
		scoreWriter = new BufferedWriter(new FileWriter(path, true));
		
		
	}

	private void loadStopWords() {
		Scanner sc = null;
		try {
			String path = context.getRealPath(stopWords);
			
			sc = new Scanner(new File(path));
			String line;
			while (sc.hasNext()) {
				line = sc.nextLine();
				stopwordSet.add(line);

			}
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			if (sc != null)
				sc.close();
		}

	}

	private void loadScores() {
		Scanner sc = null;
		try {
			String path = context.getRealPath(scoreFile);
			
			sc = new Scanner(new File(path));
			String line, content[];
			while (sc.hasNext()) {
				line = sc.nextLine();
				content = line.split("=");
				neMap.put(content[0].toLowerCase(),
						Float.parseFloat(content[1]));
			}
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			if (sc != null)
				sc.close();
		}
	}

	public boolean getTopURLs(String query, int n) {
		if (neMap.containsKey(query.toLowerCase())) {
			System.out.println("exits");
			return true;
		}
		String urlQuery = url + "api.php?action=query&format=xml";
		String xmlOutput;
		int i = 0;
		String urlOffset = "&generator=search&gsrlimit=50&gsroffset=";
		String offset = "";
		try {

			String qry = URLEncoder.encode(query, "UTF-8");
			urlQuery += "&gsrsearch=" + qry
					+ "&gsrprop=titlesnippet&format=xml&continue=";
			for (i = 0; i < n; i += 50) {
				offset = urlQuery + urlOffset + i;
				xmlOutput = HttpQueries.sendGetQuery(offset, client);
				extractURLs(xmlOutput, query);
			}

		} catch (HttpException e) {
			e.printStackTrace();
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		} finally {

		}
		return false;
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

	public String getSNE(String neList) throws IOException {

		String words[] = neList.split("\\|");
		String sneList = "";
		List<Float> scores = new ArrayList<Float>();
		Set<String> SNEs = new LinkedHashSet<String>();
		for (String i : words) {
			int flag = 0;
			if (i.endsWith("~")) {
				// ~ is used for assigning extra score for NNPs
				flag = 1;
				i = i.replace("~", "");
			}
			if (stopwordSet.contains(i.toLowerCase())) {
				scores.add(0.0f);
				continue;
			}

			boolean res = getTopURLs(i, 700);
			if (res) {
				scores.add(neMap.get(i.toLowerCase()));
			} else {
				int hits = results.size();
				int ngram = i.split(" ").length;

				float tcount = titleCount;
				float score = rank(i, tcount, hits, flag, ngram);
				neMap.put(i.toLowerCase(), score);
				System.out.println("word: " + i + " score : " + score);
				scores.add(score);
				scoreWriter.write(i + "=" + neMap.get(i.toLowerCase()) + "\n");
				scoreWriter.flush();

			}

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
			if (scores.get(maxindex) > 0.0f) {
				SNEs.add(words[maxindex]);
			}
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
		scoreWriter.close();
		return sneList;
	}

}
