package com.iiit;

import java.io.IOException;
import java.util.ArrayList;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

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
        // TODO Auto-generated constructor stub
    }

	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		// TODO Auto-generated method stub
	}

	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		// TODO Auto-generated method stub
		response.setContentType("text/plain");
		
		        String paramName = "textbox";
		        String namedEntityValue = request.getParameter(paramName);
		        
		        System.out.println("We got the request :  ");
		        System.out.println("This is the param value "+ namedEntityValue);
		        //send redirect back to page this :
		        
		        //Process the request here :
		        
		        response.sendRedirect("forwaded.jsp");
	}	
	
		public ArrayList<String> getListElements(){
			
			ArrayList<String> arr=new ArrayList<String>();
			arr.add("hello");
	        arr.add("there");
	        arr.add("sampleTesting");
			
			return arr;
		}
	}
