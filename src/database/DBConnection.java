package database;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class DBConnection {
	public static Connection getConnection(String connStr, String user, String pass) {
		Connection conn = null;
		try {
			Class.forName("org.h2.Driver");
		} catch(ClassNotFoundException e) {
			log("B³¹d przy ³adowaniu sterownika bazy: " + e);
			return null;
		}
		try {
			conn = DriverManager.getConnection(connStr, user, pass);
		} catch(SQLException e) {
			log("Nie mo¿na nawi¹zaæ po³¹czenia z baz¹: " + e);
			return null;
		}
		return conn;
	}
		public static void log(String msg) {
			System.out.println(msg);
		}
}
