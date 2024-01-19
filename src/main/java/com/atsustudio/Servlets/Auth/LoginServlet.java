package com.atsustudio.Servlets.Auth;

import java.io.IOException;
import java.util.List;
import java.util.Map;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.atsustudio.Helpers.RequestXMLParser;
import com.atsustudio.Models.User;
import com.atsustudio.Services.AuthServices;

@WebServlet("/auth/LoginServlet")
public class LoginServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;

	/**
	 * @see HttpServlet#HttpServlet()
	 */
	public LoginServlet() {
		super();
		// TODO Auto-generated constructor stub
	}

	protected void doGet(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		// TODO Auto-generated method stub
		response.getWriter().println(AuthServices.getLoginExample());
	}

	protected void doPost(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		//	response.setContentType("text/xml");
		Map<String, Object> data = RequestXMLParser.ParseRequest(request, "data");
		String login = (String) data.get("login"), password = (String) data.get("password"),
				email = (String) data.get("email");

		if (login == null && password == null && email == null) {
			response.setContentType("text/xml");
			response.setStatus(400);
			response.getWriter().println("<error>Incorrect input values</error>");
			return;
		}

		User user = new User();
		
		if(!user.connectToDatabase()) {
			response.setContentType("text/xml");
			response.setStatus(500);
			response.getWriter().println("<error>No connection to database</error>");
			
		}
		
		
		
		user.setLogin((String) data.get("login"));
		user.setPassword((String) data.get("password"));
		user.setEmail((String) data.get("email"));

		response.setStatus(201);
		response.getWriter().println(user.save().getLogin());
		
	}

}
