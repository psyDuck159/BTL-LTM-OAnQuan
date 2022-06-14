package jdbc.dao;

import java.sql.Connection;
import java.sql.DriverManager;

public class DAO {
	public static Connection conn;
	
	public DAO() {
		if(conn == null) {
			String dbUrl = "jdbc:mysql://localhost:3306/oanquan?autoReconnect=true&useSSL=false";
	           //String dbClass = "com.mysql.jdbc.Driver";
	 
	           try {
	               //Class.forName(dbClass);
	               conn = DriverManager.getConnection (dbUrl, "root", "CNPM@2021");
	           }catch(Exception e) {
	               e.printStackTrace();
	           }
		}
	}
}
