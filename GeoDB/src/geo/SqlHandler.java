package geo;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Statement;

public class SqlHandler {

	Connection con;
	PreparedStatement ps;

	public void connect(String url, String userName, String password) throws ClassNotFoundException, SQLException{
		Class.forName("com.mysql.jdbc.Driver");
		con = DriverManager.getConnection(url, userName, password);
	}

	public void close() throws SQLException{
		con.close();
	}

	public  java.sql.ResultSet returnQuery(String sql) throws SQLException, IOException{
		Statement st = con.createStatement();
		//ps = con.prepareStatement(sql);
		return st.executeQuery(sql);
	}

	public void runQuery(String sql) throws SQLException{
		ps = con.prepareStatement(sql);
		ps.execute(sql);
	}

	public void runFromSource(String sourcePath) throws SQLException, FileNotFoundException{
		try {
			BufferedReader reader = new BufferedReader(new FileReader(sourcePath));
			String line;
			String statement = "";

			line = reader.readLine();
			statement = line;
			while(line != null){
				while(!line.contains(";")){
					line = reader.readLine();
					statement += line;
				}
				java.sql.Statement st = con.createStatement(); 
				st.execute(statement);

				line = reader.readLine();
				statement = line;
			}
		}catch (IOException e) {
			e.printStackTrace();
		}

	}

}