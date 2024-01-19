package com.atsustudio.Models;

import java.io.InputStream;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathFactory;

import org.w3c.dom.Document;
import org.xml.sax.InputSource;

import com.atsustudio.Interfaces.CRUDInterface;


public abstract class Model<T> implements CRUDInterface<T> {
	

    List<T> tCollection = new ArrayList<>();
    Map<String, Object> modelFields;
    
    
	// connection properties
	protected boolean hasConfig = false;
	protected String db_vendor;
	protected String db_name;
	protected String db_port;
	protected String db_driver;
	protected String db_url;
	protected String db_login;
	protected String db_password;
	
	protected Connection conn;
	protected Statement stmt;
	protected ResultSet result;
	
	
	public Model() {
		this.hasConfig = getConfig();
		
	}
	
	
	
	protected boolean getConfig() {
		try {
	        InputStream input = Thread.currentThread().getContextClassLoader().getResourceAsStream("META-INF/database-conf.xml");
	        Document document = DocumentBuilderFactory.newInstance().newDocumentBuilder().parse(new InputSource(input));
	        XPath xpath = XPathFactory.newInstance().newXPath();
	        db_url = (String) xpath.compile("//database//url").evaluate(document, XPathConstants.STRING);
	        db_name = (String) xpath.compile("//database//name").evaluate(document, XPathConstants.STRING);
	        db_port = (String) xpath.compile("//database//port").evaluate(document, XPathConstants.STRING);
	        db_login = (String) xpath.compile("//database//login").evaluate(document, XPathConstants.STRING);
	        db_driver = (String) xpath.compile("//database//driver").evaluate(document, XPathConstants.STRING);
	        db_vendor = (String) xpath.compile("//database//vendor").evaluate(document, XPathConstants.STRING);
	        db_password = (String) xpath.compile("//database//password").evaluate(document, XPathConstants.STRING);
	        return true;
	    } catch (Exception e) {
	        e.printStackTrace();
	        System.out.println("Exception with getting db config: " + e.getMessage());
	        return false;
	    }
	}
	
	
	
	public boolean connectToDatabase() {
		if (!hasConfig) {
	        return false;
		}
	    try {
	        Class.forName(db_driver);
	        String username = db_login;
	        String password = db_password;
	        String url = db_url + ":" + db_port + "/" + db_name;
	        
	        conn = DriverManager.getConnection(url, username, password);
	        return true;
	    } catch (ClassNotFoundException e) {
	        e.printStackTrace();
	        System.out.println("Driver not found: " + e.getMessage());
	        return false;
	    } catch (SQLException e) {
	        e.printStackTrace();
	        System.out.println("SQL Exception: " + e.getMessage());
	        return false;
	    }
	}
		
		
		
		
		
    @Override
    public List<T> all() {
        try {
            stmt = conn.createStatement();
            result = stmt.executeQuery("SELECT * FROM " + getTableName() + ";");

            while(result.next()) {
            	tCollection = createFromResultSet(result);
            }
            stmt.close();
        } catch(SQLException e) {
            e.printStackTrace();
            System.out.println("Exception with ALL operation: " + e.getMessage());
        }
        return tCollection;
    }

    



	@Override
	public List<T> where(String column, String value) {
		try {
            stmt = conn.createStatement();
            System.out.println("SELECT * FROM " + getTableName() + " WHERE " + column + " = " + value + ";");
            result = stmt.executeQuery("SELECT * FROM " + getTableName() + " WHERE " + column + " = " + value + ";");
            tCollection = createFromResultSet(result);
            stmt.close();
            return tCollection;
        } catch(SQLException e) {
            e.printStackTrace();
            System.out.println("Exception with WHERE operation: " + e.getMessage());
            return null;
        }
	}


	@Override
	public boolean delete(int id) {
		try {
            stmt = conn.createStatement();
            stmt.executeUpdate("DELETE FROM " + getTableName() + " WHERE id =" + id + ";");
            return true;
        } catch(SQLException e) {
            e.printStackTrace();
            System.out.println("Exception with DELETE operation: " + e.getMessage());
            return false;
        }
	}
	
	

	@Override
	public T find(int id) {
		try {
            stmt = conn.createStatement();
            result = stmt.executeQuery("SELECT * FROM " + getTableName() + " WHERE id = " + id + ";");
            tCollection = (List<T>) createFromResultSet(result);
            stmt.close();
            return tCollection.get(0);
        } catch(SQLException e) {
            e.printStackTrace();
            System.out.println("Exception with FIND operation: " + e.getMessage());
            return null;
        }
	}

	
	
	@Override
	public T update(Map<String, Object> object){		
		this.modelFields = object;
		
		try {
			String query = this.GenUpdateQuery(this.modelFields);
			stmt = conn.createStatement();
			int recordId = stmt.executeUpdate(query, Statement.RETURN_GENERATED_KEYS);
            stmt.close();
			return this.find(recordId);
		} catch(SQLException e) {
			e.printStackTrace();
            System.out.println("Exception with SAVE operation: " + e.getMessage());
			return null;
		}
	}

	@Override
	public T save() {
		this.modelFields = initFields();
		
		try {
			String myQuery = this.GenInsertQuery(this.modelFields);
			stmt = conn.createStatement();
			stmt.executeUpdate(myQuery, Statement.RETURN_GENERATED_KEYS);
			ResultSet rs = stmt.getGeneratedKeys();
			int recordId = 0;
			if (rs.next()) {
			    recordId = rs.getInt(1);
			}
			System.out.println("Created record: "+recordId);
            stmt.close();
			return this.find(recordId);
		} catch(SQLException e) {
			e.printStackTrace();
            System.out.println("Exception with SAVE operation: " + e.getMessage());
			return null;
		}
	}
	
	
	
	
	
	
	private String GenInsertQuery( Map<String, Object> object ) {
		String query= "";
		String tbFields = "(";
		String tbValues = "(";
		int size = object.size();
		int i = 0;
		for (Map.Entry<String, Object> entry : object.entrySet()) {
			if(entry.getKey() != "id") {
				tbFields = tbFields + entry.getKey();
				tbValues =  tbValues + '"' + entry.getValue() + '"';
				if (i < size-1) {
					tbFields = tbFields + ",";
					tbValues = tbValues + ",";
				}
			}
			
			i++;
		}
		tbFields = tbFields + ")";
		tbValues = tbValues + ")";
		query = "INSERT INTO " + getTableName() + tbFields + " VALUES " + tbValues + ";";
		return query;
	}
	
	private String GenUpdateQuery( Map<String, Object> object ) {
		String query= "";
		String tbValues = "";
		int size = object.size();
		int i = 0;
		for (Map.Entry<String, Object> entry : object.entrySet()) {
			if(entry.getKey() != "id") {
				tbValues = tbValues + entry.getKey() + "=\""+  entry.getValue() + "\"";
				if (i < size-1) {
					tbValues = tbValues + ",";
				}
			}
			
			i++;
		}
		query = "UPDATE " + getTableName() + " SET " +  tbValues + " WHERE id=" + this.modelFields.get("id")  + ";";
		return query;
	}

	
	/**
	 * This method need for init your table name
	 * @return String tableName
	 */
    protected abstract String getTableName();
    

    
	/**
	 * In this method you need implement construct your entity
	 * Example
		<code>
		this.tCollection = new ArrayList<>();
		while(result.next()) {
			User tmpUser = new User();
			tmpUser.id = Integer.parseInt(result.getString("id"));
			tmpUser.name = result.getString("name");
			tmpUser.email = result.getString("email");
			tmpUser.age = Integer.parseInt(result.getString("age"));
			tmpUser.phone = Integer.parseInt(result.getString("phone"));
			this.tCollection.add(tmpUser);
		}
		return this.tCollection;
		</code>
	 * @return List<T> tCollection
	 */
    protected abstract List<T> createFromResultSet(ResultSet result) throws SQLException;
    
    
    /**
     *In this method 
     * 	
		<code>
		Map<String, Object> myFileds = new HashMap<String, Object>();
		myFileds.put("id", id);
		myFileds.put("name", name);
		myFileds.put("email", email);
		myFileds.put("age", age);
		myFileds.put("phone", phone);
		return myFileds;
		</code>
     * @return Map<String, Object>
     */
    protected abstract Map<String, Object> initFields();
}

