import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Statement;

/**
 * The Connection Class - will connect to the database, run queries, and print the results
 * @author swapneel
 *
 */
public class DatabaseConnection {
	
	private Connection conn;
	
	/**
	 * The Constructor
	 * @param driver The Database driver to load
	 * @param connection The connection string for the database - i.e., which database to connect to
	 */
	public DatabaseConnection(String driver, String connection) {
		try {
			//this will load the driver into the class path
			Class.forName(driver);
			
			//this will establish the connection
			conn = DriverManager.getConnection(connection);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	/**
	 * This will close the connections
	 */
	public void closeConnections() {
		try {
			conn.close();
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	/**
	 * Given a query to run, it will use the connection to get the results and print out the results
	 * @param query The Query to run
	 */
	public void runQuery(String query) {
		try {
			Statement statement = conn.createStatement();
			
			ResultSet rs = statement.executeQuery(query);
			ResultSetMetaData rsmd = rs.getMetaData();
			
			int columns = rsmd.getColumnCount();
			
			//iterate over all the rows
			while (rs.next()) {
				
				//iterate over all the columns using the metadata
				for (int i=1; i<=columns; i++) {
					System.out.print(rsmd.getColumnName(i) + ": " + rs.getString(i) + ", ");
				}
				System.out.println();
				
			}
			
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

}
