package com.atsustudio.Services;

import java.io.IOException;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.atsustudio.Exceptions.DatabaseException;
import com.atsustudio.Models.User;

public class AuthServices {

	public static boolean Varify(HttpServletRequest request, HttpServletResponse response) throws IOException {
		if(request.getHeader("Authorization") == null) {
			response.setStatus(401);
			response.getWriter().println("<message>You unauthorized</message>");
			return false;
		}		
		return true;
	}
	public static String getLoginExample() {
		return "Request example info\r\n"
				+ "<data>\r\n"
				+ "    <login>yourLogin</login>\r\n"
				+ "    <password>yourPassword</password>\r\n"
				+ "    <email>yourEmail</email>\r\n"
				+ "</data>";
	}
	
	public static boolean userExist(String Login) throws DatabaseException  {
		User user = new User();
		if(!user.connectToDatabase()) {
			throw new DatabaseException("Not have connaction with database");
		}
		
		User founded = (user.where("login",Login)).get(0);
		
		
		
		return false;
	}
	
}
