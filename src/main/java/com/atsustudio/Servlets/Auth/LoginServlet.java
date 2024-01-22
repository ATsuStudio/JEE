package com.atsustudio.Servlets.Auth;

import java.io.IOException;
import java.util.Map;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.atsustudio.Exceptions.DatabaseException;
import com.atsustudio.Helpers.RequestXMLParser;
import com.atsustudio.Models.User;
import com.atsustudio.Services.AuthServices;

@WebServlet("/auth/login")
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

	
	
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		response.setContentType("text/xml");
		
		Map<String, Object> data = RequestXMLParser.ParseRequest(request, "data");
		
		if(!AuthServices.loginValidate(data, response)) {
			return;
		}

		User user = new User();
		user.connectToDatabase();
		
		try {
			if(AuthServices.userExist((String) data.get("login"))) {
				response.setContentType("text/xml");
				response.setStatus(409);
				response.getWriter().println("<message>This user exist. Try to choose another login name</message>");
				return;
			}
		} catch (DatabaseException e) {
			e.printStackTrace();
			response.setStatus(500);
			response.getWriter().println("<error>" + e.getMessage() + "</error>");
			return;
		}
		
		
		user.setLogin((String) data.get("login"));
		user.setPassword((String) data.get("password"));
		user.setEmail((String) data.get("email"));

		String createdResult = user.save().getLogin();
		if(createdResult != null) {
			response.setStatus(201);
			response.getWriter().println("<message>User " + createdResult + " was created successful</message>");
		}
		else {
			response.setStatus(500);
			response.getWriter().println("<error>Something went wrong</error>");
		}
	}
}
