/**
 * 
 */

package me.merdril.randombattle.config;

import java.io.File;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.HashSet;

import me.merdril.randombattle.RBUtilities;
import me.merdril.randombattle.RandomBattle;

/**
 * <p>
 * A facade that handles reading player information from the player database.
 * </p>
 * <p>
 * Supports simple operations such as reading a stat for a player or monster, saving stats, making
 * players, and so on. Also allows complicated queries to the database.
 * </p>
 * @author Merdril
 */
public final class RBDatabase
{
	private static RandomBattle	plugin;
	
	/**
	 * <p>
	 * Loads the player database and verifies its integrity.
	 * </p>
	 */
	public static void initialize(RandomBattle instance)
	{
		// Save the plugin instance
		plugin = instance;
		try {
			Class.forName("org.sqlite.JDBC");
		}
		catch (ClassNotFoundException e) {
			plugin.getLogger().severe(RandomBattle.prefix + "Unable to load the SQLite driver! Shutting down...");
			plugin.getPluginLoader().disablePlugin(plugin);
		}
		// ///////////////////////////////////////////////////// Verify the contents of the database
		// // Exceptions in this area are fatal.
		// Create a connection to the database (and thus creating the database if it does not exist)
		Connection conn = null;
		try {
			conn = DriverManager.getConnection("jdbc:sqlite:" + new File(plugin.getDataFolder(), "stats.db").getPath());
		}
		catch (SQLException e) {
			e.printStackTrace();
			plugin.getLogger().severe(RandomBattle.prefix + "Unable to connect to stats.db! Shutting down...");
			plugin.getPluginLoader().disablePlugin(plugin);
		}
		// Create a Statement to use for verifying the integrity of the database
		Statement statement = null;
		try {
			statement = conn.createStatement();
		}
		catch (SQLException e) {
			queryFailed(e, true);
		}
		// Execute a series of dependent statements
		try {
			// Check to make sure the table monsters exists
			ResultSet set =
			        statement.executeQuery("SELECT name FROM sqlite_master WHERE type='table' AND name='monsters'");
			boolean made = false;
			// If not, make it
			try {
				if (set.isClosed()) {
					createTable("monsters", statement);
					made = true;
				}
			}
			catch (AbstractMethodError e) {
				statement.executeUpdate("DROP TABLE IF EXISTS monsters");
				createTable("monsters", statement);
			}
			// If so, make sure it has the right column headers
			if (!made) {
				ResultSetMetaData data = set.getMetaData();
				// We do not store chp or cmp for monsters. If there's a size mismatch, drop the
				// table and make a new one
				if (data.getColumnCount() != RBUtilities.statNames.size() - 2) {
					statement.executeUpdate("DROP TABLE IF EXISTS monsters");
					createTable("monsters", statement);
				}
				// Otherwise, check the column information
				else {
					int currentColumn = 1;
					// Make sure the column names match expected stat names
					HashSet<String> columnNames = new HashSet<String>();
					while (currentColumn <= data.getColumnCount())
						columnNames.add(data.getColumnName(currentColumn++));
					// Remove chp and cmp from the set
					@SuppressWarnings ("unchecked")
					HashSet<String> statNames = (HashSet<String>) RBUtilities.statNames.clone();
					statNames.remove("chp");
					statNames.remove("cmp");
					// If the two sets are equal, then the table is good as far as we are concerned.
					// Otherwise, drop it and make a new one.
					if (!statNames.equals(columnNames)) {
						statement.executeUpdate("DROP TABLE IF EXISTS monsters");
						createTable("monsters", statement);
					}
				}
			}
		}
		catch (SQLException e) {
			queryFailed(e, true);
		}
		// ///////////////////////////////////////////////////End verification of the table contents
	}
	
	/**
	 * <p>
	 * Performs a routine set of actions when a query fails.
	 * </p>
	 * @param e
	 *            The SQLException that triggered this method call.
	 * @param shutdownOnFail
	 *            A boolean that indicates to shutdown this plugin if the query failed. True means
	 *            to shutdown.
	 */
	private static void queryFailed(SQLException e, boolean shutdownOnFail)
	{
		plugin.getLogger().warning(RandomBattle.prefix + "A query failed! SQL State: " + e.getSQLState());
		while (e != null) {
			e.printStackTrace();
			e = e.getNextException();
		}
		if (shutdownOnFail) {
			plugin.getLogger().severe("Shutting down as a result of the failed query...");
			plugin.getPluginLoader().disablePlugin(plugin);
		}
	}
	
	private static int createTable(String type, Statement statement) throws SQLException
	{
		int result = 0;
		if (type.equals("monsters")) {
			//@formatter:off
			result = statement.executeUpdate("CREATE TABLE monsters (" +
				"name TEXT NOT NULL," +
				"hp INT NOT NULL," +
				"mp INT NOT NULL," +
				"str INT NOT NULL," +
				"mag INT NOT NULL," +
				"def INT NOT NULL," +
				"mdef INT NOT NULL," +
				"agl INT NOT NULL," +
				"acc INT NOT NULL," +
				"eva INT NOT NULL," +
				"luck INT NOT NULL" +
				")");
			//@formatter:on
		}
		else if (type.equals("players")) {
			
		}
		return result;
	}
}
