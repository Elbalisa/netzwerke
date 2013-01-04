package geo;

import java.io.IOException;
import java.sql.SQLException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * Servlet implementation class GeoDBU2
 */
public class GoeDBStud extends HttpServlet {
	private static final long serialVersionUID = 1L;

	/**
	 * @see HttpServlet#HttpServlet()
	 */
	public GoeDBStud() {
		super();
	}

	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		SqlHandler sqlHandler = new SqlHandler();

		response.setContentType("text/html; charset=UTF-8");
		String flipper = request.getParameter("flipper");
		String id = request.getParameter("id");
		String ersatzteil = request.getParameter("ersatzteil");
		String kunde = request.getParameter("kunde");
		try {
			sqlHandler.runQuery(buildStringInsert(flipper, id, ersatzteil, kunde));
		} catch (SQLException e1) {
			e1.printStackTrace();
		}

		try {
			sqlHandler.connect("jdbc:mysql://localhost", "root", "");
			sqlHandler.returnQuery(buildStringInsert(flipper, id, ersatzteil, kunde));
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		} catch (SQLException e) {
			e.printStackTrace();
		}

	}

	public String buildStringInsert(String flipper, String id, String ersatzteil, String Kunde){

		String comand = "INSERT INTO Automaten (" + flipper + ", " + id + ", " + ersatzteil + ", " + Kunde + ")";

		return comand;
	}

	public String buildStringSelect(String flipper, String id, String ersatzteil, String Kunde){

		String comand = flipper;

		return comand;
	}
}
