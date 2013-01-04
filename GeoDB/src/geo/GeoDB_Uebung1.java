package geo;

import java.io.IOException;
import java.sql.SQLException;


public class GeoDB_Uebung1 {

	public static void main(String[] args) throws ClassNotFoundException, SQLException, IOException{

		SqlHandler sqlHandler = new SqlHandler();
		sqlHandler.connect("jdbc:mysql://localhost", "root", "");
		
		//Nur zum Testen, damit bei wiederholtem Aufruf keine Fehler auftreten
		sqlHandler.returnQuery("DROP Database testdb2");
		
		sqlHandler.returnQuery("CREATE Database testdb2");
		sqlHandler.runQuery("USE testdb2");
		sqlHandler.returnQuery("CREATE TABLE t_artikel (id INTEGER, name VARCHAR(150), preis FLOAT)");
		sqlHandler.runQuery("DROP TABLE t_artikel");
		sqlHandler.returnQuery("DROP DATABASE testdb2");
		sqlHandler.runQuery("USE testdb");
		sqlHandler.returnQuery("CREATE TABLE t_person (id INTEGER NOT NULL, vname VARCHAR(150) " +
				"NOT NULL, name VARCHAR(150) NOT NULL)");
		//sqlHandler.runFromSource("Users/Izzy/ DatenbankU1Ändern.txt");
		sqlHandler.close();
	}
}
