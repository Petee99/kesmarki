package hu.kesmarki.project;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

public class JDBC {
	
	private static String url = "jdbc:sqlserver://localhost;databaseName=kesmarki";
	private static String user = "sa";
	private static String password = "FingerBoard101499";	
	private static Connection conn = null;
	
	public List<String> query(String query, int columnNum){
		return this.query(false, query, columnNum, new int[0]);
	}
	
	public List<String> query(boolean isMeta, String query, int columnNum, int breakPoints[]) {
		List<String> returnList = new ArrayList<String>();
		
		try {
			connect();
			Statement statement = conn.createStatement();
			ResultSet result = statement.executeQuery(query);
			ResultSetMetaData rsmd = result.getMetaData();
			
			while(result.next() || isMeta) {
				String resultRow = "";
				for (int cIndex = 1; cIndex <= columnNum; cIndex++) {
					
					if(isMeta) {
						resultRow += rsmd.getColumnName(cIndex)+":"+rsmd.getColumnTypeName(cIndex);
					} else {
						resultRow += result.getString(cIndex);
					}
					
					for (int bIndex = 0; bIndex < breakPoints.length; bIndex++) {
						if (cIndex == breakPoints[bIndex]) {
							resultRow += ";";
						}
					}
					if (cIndex != columnNum && resultRow.charAt(resultRow.length()-1) != ';') {
						resultRow += "<>";
					}
				}
				returnList.add(resultRow);
				isMeta = false;
			}
			
			disconnect();
		} catch (SQLException e) {
			error(e);
		}
		return(returnList);
	}
	
	public boolean update(String updateString) {
			
			try {
				connect();
				Statement statement = conn.createStatement();
				statement.executeUpdate(updateString);
				System.out.println("Succesful modification!");
				disconnect();
				return true;
			} catch (SQLException e) {
				error(e);
				return false;
			}
	}
	
	private static void connect(){
		try {
			conn = DriverManager.getConnection(url, user, password);
		}
		catch (SQLException e) {
			System.out.println(">> Can't connect to database <<");
			error(e);
			System.exit(0);
		}
	}
	
	private static void disconnect() {	
		try {
	        if (conn != null) {
	            conn.close();
	        }
	      } catch (SQLException e) {
	          error(e);
	      }
	}
	
	private static void error(SQLException error) {
		System.out.println("(x) Database error:\n"
				+ "-> Check if database is offline\n"
				+ "-> Check if credentials are wrong\n"
				+ "-> Check if there's a syntax error\n"
				+ "-> For more information type: details");
		if(Tools.askInput("String", "").equals("details")) {
			error.printStackTrace();
		}
	}
}
