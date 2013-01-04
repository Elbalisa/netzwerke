package geo;

import java.io.IOException;
import java.sql.SQLException;

public class Test {

		public static void main(String[] args) throws ClassNotFoundException, SQLException, IOException{

			SqlHandler sqlHandler = new SqlHandler();
			sqlHandler.connect("jdbc:mysql://localhost", "root", "");
			
			String sourcePath = "/Users/Izzy/Documents/Studium/GeoDBTest.txt";
			
			sqlHandler.returnQuery("Create Database blubber");
			//sqlHandler.runQuery("Source " + sourcePath);
			sqlHandler.runFromSource(sourcePath);
			
		}

}