package com.ire.ner;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;

import javax.servlet.ServletContext;

public class PosTagger {
	private ServletContext context;
	
	public PosTagger(ServletContext cntxt) {
		context = cntxt;
	}
	
	
		
	public String getTaggedTweet(String tweet) throws IOException {
		String line,tagged="";
		String path = context.getRealPath("/WEB-INF/run.sh");
		String catHome = System.getProperty("catalina.home");
		System.out.println("Cat home"+catHome);
		String cmd[] = { path, tweet,catHome };
		
		Process p = Runtime.getRuntime().exec("chmod 777 " + path);
		p = Runtime.getRuntime().exec(cmd);
		BufferedReader in = new BufferedReader(new InputStreamReader(
				p.getInputStream()));
		
		while ((line = in.readLine()) != null) {
			tagged+=line;
		}
		in.close();
		
		return tagged;
	}
}
