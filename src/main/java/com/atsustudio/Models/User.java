package com.atsustudio.Models;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class User extends Model<User> {

	private int id;
	private String login;
	private String password;
	private String email;
	
	@Override
	protected String getTableName() {
		return "user";
	}

	@Override
	protected List<User> createFromResultSet(ResultSet result) throws SQLException {
		this.tCollection = new ArrayList<>();
		while(result.next()) {
			User tmpUser = new User();
			tmpUser.id = Integer.parseInt(result.getString("id"));
			tmpUser.login = result.getString("login");
			tmpUser.password = result.getString("password");
			tmpUser.email = result.getString("email");
			this.tCollection.add(tmpUser);
		}
		return this.tCollection;
	}

	@Override
	protected Map<String, Object> initFields() {
		Map<String, Object> myFileds = new HashMap<String, Object>();
		myFileds.put("id", id);
		myFileds.put("login", login);
		myFileds.put("password", password);
		myFileds.put("email", email);
		return myFileds;
	}
	
	

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public String getLogin() {
		return login;
	}

	public void setLogin(String login) {
		this.login = login;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}
	
}
