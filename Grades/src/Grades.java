

import java.io.IOException;
import java.io.PrintWriter;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.text.NumberFormat;
import java.util.Properties;
import java.util.Scanner;
import java.util.ArrayList;

/**
 * Servlet implementation class Grades
 */
@WebServlet("/Grades")
public class Grades extends HttpServlet {
	private static final long serialVersionUID = 1L;
	private String message; 
	private double average, total;
	private int gradeNo;
	private ResultSet result;
	private static Connection conn;
    /**
     * @see HttpServlet#HttpServlet()
     */
    public Grades() {
        super();
        message = "";
    }
	public void init() throws ServletException {
		// Do required initialization
	   	result = null;
		try {
			Class.forName("oracle.jdbc.driver.OracleDriver");
		} catch (ClassNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		String url = "jdbc:oracle:thin:testuserdb/password@localhost";
		// properties for creating connection to Oracle database
		Properties props = new Properties();
		props.setProperty("user", "testuserdb");
		props.setProperty("password", "password");
		// creating connection to Oracle database using JDBC
		try {
			conn = DriverManager.getConnection(url, props);
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}    
	}
	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
			doPost(request, response);
	}

	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		response.setContentType("text/html");
		PrintWriter out = response.getWriter();
	   	String sql = "insert into grades (assignment, grade) values (?,?)";
	   	String theGrade = request.getParameter("grade");
	   	String theAssignment = request.getParameter("assignment");
		try {
	    	PreparedStatement  st = conn.prepareStatement(sql);
			st.setString(1, theAssignment);
			st.setDouble(2, Double.parseDouble(theGrade));
	    	st.executeUpdate();			
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		String listSQL = "select * from grades";
		try {
			PreparedStatement preStatement = conn.prepareStatement(listSQL);
			result = preStatement.executeQuery();
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		String id = "";
		String assignment = "";
		String grade = "";
		total = 0.0;
		gradeNo = 0;
		try {
			while (result.next()) {
				id = Integer.toString(result.getInt("ID"));
				assignment = result.getString("assignment");
				grade = Double.toString(result.getDouble("grade"));
				total = total + result.getDouble("grade");
				gradeNo ++;
				message = message + "	<div class=\"row\"><br><div class=\"col-md-4\">"+id+"</div>" +
																"<div class=\"col-md-4\">"+assignment+"</div>" + 
																"<div class=\"col-md-4\">"+grade+"</div></div>";
			}
			average = total/gradeNo;
		} catch (Exception e) {
			e.printStackTrace();
		}
        HttpSession session = request.getSession(true);
        
        session.setAttribute("average", average);
		request.setAttribute("message", message);

    	getServletContext().getRequestDispatcher("/grades.jsp").forward(request, response);
	}
}
