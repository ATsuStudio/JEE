package com.atsustudio;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * Servlet implementation class testing
 */
@WebServlet("/testing")
public class testing extends HttpServlet {
	private static final long serialVersionUID = 1L;
       
    /**
     * @see HttpServlet#HttpServlet()
     */
    public testing() {
        super();
        // TODO Auto-generated constructor stub
    }

	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		// TODO Auto-generated method stub
		response.getWriter().append("Served at: ").append(request.getContextPath());
	}

	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		Map<String, Object> data = new HashMap<String, Object>();
		Map<String, Object> someMap = new HashMap<String, Object>();
		Map<String, Object> someSecondMap = new HashMap<String, Object>();
		//List<Map<String, Object>> subData = new HashMap<String, Object>();
		
		//List<Map<String, Object>> maps = new ArrayList<Map<String, Object>>();
		someMap.put("FirstEl", 1);
		someMap.put("SecondEl", 2);
		someMap.put("ThreedEl", 3);
		

		someSecondMap.put("FirstElSecond", 12);
		someSecondMap.put("SecondElSecond", 22);
		
		data.put("subInfo", someMap);
		data.put("subInfoSecond", someSecondMap);
		
		response.getWriter().println(data.get("subInfo"));
		response.getWriter().println(data);
		
	}

}
