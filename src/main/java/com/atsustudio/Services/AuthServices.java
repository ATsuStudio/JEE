package com.atsustudio.Services;

import java.io.IOException;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.atsustudio.Exceptions.DatabaseException;
import com.atsustudio.Models.User;

public class AuthServices {
	private User authorizated;
	
	public boolean authorize(HttpServletRequest request, HttpServletResponse response) throws IOException {
		String authHead = request.getHeader("Authorization");
		if(authHead == null) {
			return false;
		}	
		String token = authHead.replace("Bearer ", "");
		System.out.println("token: " + token);
		if(token.length()>0) {
			User user = new User();
			user.connectToDatabase();
			List<User> userList = user.all();
			
			String logPass;
			for(User item : userList) {
				logPass = item.getLogin()+item.getPassword();
				String hashed = hashing(logPass);
				if(hashed.equals(token)) {
					this.authorizated = item;
					return true;
				}
			}
		}	
		else {
			return false;
		}
		return false;
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
			throw new DatabaseException("Not have database connaction");
		}
		
		List<User> founded = user.where("login",Login);
		User firstUser = founded.isEmpty()? null: founded.get(0); 
		if(firstUser != null) {
			return true;
		}
		
		return false;
	}
	
	public static boolean loginValidate(Map<String, Object> data, HttpServletResponse response) throws IOException {
		String login = (String) data.get("login"), password = (String) data.get("password"),
				email = (String) data.get("email");
		if (login == null|| password == null || email == null) {
			response.setStatus(400);
			response.getWriter().println("<error>Fields is not filled</error>");
			return false;
		}
		
		if (login.isEmpty()|| password.isEmpty() || email.isEmpty()) {
			response.setStatus(400);
			response.getWriter().println("<error>Fields is not filled</error>");
			return false;
		}
		if (password.length() < 8 || password.length() > 50) {
			response.setStatus(400);
			response.getWriter().println("<error>Password length should be between 8 and 50</error>");
			return false;
		}
		
		return true;
	}
	
	
	
	public static boolean singinValidate(Map<String, Object> data, HttpServletResponse response) throws IOException {
		String login = (String) data.get("login"), password = (String) data.get("password");
		if (login == null|| password == null) {
			response.setStatus(400);
			response.getWriter().println("<error>Fields is not filled</error>");
			return false;
		}
		
		if (login.isEmpty()|| password.isEmpty()) {
			response.setStatus(400);
			response.getWriter().println("<error>Fields is not filled</error>");
			return false;
		}
		if (password.length() < 8 || password.length() > 50) {
			response.setStatus(400);
			response.getWriter().println("<error>Password length should be between 8 and 50</error>");
			return false;
		}
		
		return true;
	}
	
	
	
	public static boolean matchUserData(Map<String, Object> data) {
		User user = new User();
		user.connectToDatabase();
		
		String login  =  (String) data.get("login");
		String password  =  (String) data.get("password");

		
		User foundedUser = user.first( user.where("login",login));
		String flogin  =  foundedUser.getLogin();
		String fpassword  =  foundedUser.getPassword();
		if(	flogin.equals(login) &&
				fpassword.equals(password)) {
			return true;
		}
		return false;
	}
	
	public static String hashing(String value) {
		return "mhsh:"+String.format("0x%08X", value.hashCode()) ;
	}
	
	public User getAuthorizated() {
		return this.authorizated;
	}
	
}
