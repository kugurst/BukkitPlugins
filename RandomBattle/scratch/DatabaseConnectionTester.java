/**
 * Tester class to connect to the database and get results for queries
 * @author swapneel
 *
 */
public class DatabaseConnectionTester {

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		// TODO Auto-generated method stub
		
		DatabaseConnection db = new DatabaseConnection("org.sqlite.JDBC", "jdbc:sqlite:foursquare.sqlite");
		
		db.runQuery("select tu.name as Person_Name, fb.name as Badge from twitter_user tu, twitteruser_foursquarebadge t, foursquare_badge fb where tu.name like '%aaron%' and tu.id = t.twitter_user_id and t.foursquare_badge_id = fb.id order by Person_Name");
		db.closeConnections();

	}

}
