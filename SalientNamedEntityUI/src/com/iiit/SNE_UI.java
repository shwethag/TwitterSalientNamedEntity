package com.iiit;

import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;

import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.ire.ner.NEExtractor;
import com.ire.ner.PosTagger;
import com.ire.wiki.NEWiki;

/**
 * Servlet implementation class SNE_UI
 */
@WebServlet("/SNE_UI")
public class SNE_UI extends HttpServlet {
	private static final long serialVersionUID = 1L;

	/**
	 * Default constructor.
	 */
	public SNE_UI() {
	}

	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse
	 *      response)
	 */
	protected void doGet(HttpServletRequest request,
			HttpServletResponse response) throws ServletException, IOException {
		// TODO Auto-generated method stub
	}

	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse
	 *      response)
	 */
	protected void doPost(HttpServletRequest request,
			HttpServletResponse response) throws ServletException, IOException {
		// TODO Auto-generated method stub
		System.out.println("Received");
		ServletContext cntxt = getServletContext();
		String shpath = cntxt.getRealPath("/WEB-INF/run.sh");
		File f = new File(shpath);
		if(f.exists()){
			System.out.println("exists");
			f.setExecutable(true);
		}
		System.out.println(shpath);
		PrintWriter writer = response.getWriter();
		response.setContentType("text/plain");
		String paramName = "tweet";
		String tweet = request.getParameter(paramName);
		System.out.println("We got the request :  ");
		System.out.println("This is the param value " + tweet);
		// send redirect back to page this :
		PosTagger tagger = new PosTagger(cntxt);
		String tagged = tagger.getTaggedTweet(tweet);
		System.out.println("--------"+tagged);
		NEExtractor extractor = new NEExtractor();
		String namedEntities = extractor.getNamedEntities(tagged);
		System.out.println("--------"+namedEntities);
		NEWiki newiki = new NEWiki(cntxt);
		String sne = newiki.getSNE(namedEntities);
		System.out.println("--------"+sne);
		// Process the request here :
		writer.println(sne);
	}
}
