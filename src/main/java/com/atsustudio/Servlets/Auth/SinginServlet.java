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

/**
 * Servlet implementation class SinginServlet
 */
@WebServlet("/auth/singin")
public class SinginServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;
       
    /**
     * @see HttpServlet#HttpServlet()
     */
    public SinginServlet() {
        super();
        // TODO Auto-generated constructor stub
    }

	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		// TODO Auto-generated method stub
		response.getWriter().println(AuthServices.getLoginExample());
	}

	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		response.setContentType("text/xml");
		
		Map<String, Object> data = RequestXMLParser.ParseRequest(request, "data");
		
		if(!AuthServices.singinValidate(data, response)) {
			return;
		}

		User user = new User();
		user.connectToDatabase();
		
		try {
			if(!AuthServices.userExist((String) data.get("login"))) {
				response.setContentType("text/xml");
				response.setStatus(404);
				response.getWriter().println("<message>This user not exist</message>");
				return;
			}
		} catch (DatabaseException e) {
			e.printStackTrace();
			response.setStatus(500);
			response.getWriter().println("<error>" + e.getMessage() + "</error>");
			return;
		}
		
		
		if(!AuthServices.matchUserData(data)) {
			response.setContentType("text/xml");
			response.setStatus(404);
			response.getWriter().println("<message>Incorrect login or password</message>");
			return;
		}
		
		String userpass = (String) data.get("login")+ (String) data.get("password");
		response.getWriter().println("<message>You singed successful</message>");
		response.getWriter().println("<message>Bearer token : "+ AuthServices.hashing(userpass) +" </message> ");
	}

}
