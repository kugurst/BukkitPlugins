/**
 * 
 */

package me.merdril.randombattle.config;

import java.io.File;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.HashSet;
import java.util.Map;

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
	private static RandomBattle	       plugin;
	public static Map<String, Integer>	playerBaseStats;
	
	/**
	 * <p>
	 * Loads the player database and verifies its integrity.
	 * </p>
	 * @param instance
	 * @param playerBaseStats
	 */
	public static void initialize(RandomBattle instance, Map<String, Integer> playerBaseStats)
	{
		// Save the plugin instance
		plugin = instance;
		RBDatabase.playerBaseStats = playerBaseStats;
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
		// Execute a series of dependent statements to check that the table monsters exists and is
		// populated with expectant entries
		try {
			// Check to make sure the table monsters exists
			ResultSet set =
			        statement.executeQuery("SELECT name FROM sqlite_master WHERE type='table' AND name='monsters'");
			boolean made = false;
			// If not, make it
			try {
				// This will throw an exception on a closed set, which the ResultSet above will be
				// if a table called monsters is not found (set.isClosed() with my version of SQLite
				// JDBC throws an AbstractMethodError exception regardless of ResultSet state).
				set.getString(1);
			}
			catch (SQLException e) {
				createTable("monsters", statement);
				made = true;
			}
			// If so, make sure it has the right column headers
			if (!made) {
				set = statement.executeQuery("PRAGMA table_info(monsters)");
				// We do not store chp or cmp for monsters. If there's a size mismatch, drop the
				// table and make a new one
				int rowCount = 0;
				HashSet<String> columnNames = new HashSet<String>();
				while (set.next()) {
					columnNames.add(set.getString("name"));
					rowCount++;
				}
				// If the rowCount is not the same as the number of stats in RBUtilities (except
				// chp, cmp, and name - which is in rowCount, and including magicks, exp, and
				// skills),
				// drop the table and make a new one
				if (rowCount != RBUtilities.statNames.size() + 2) {
					statement.executeUpdate("DROP TABLE monsters");
					createTable("monsters", statement);
				}
				// If the column names and the stat names aren't the same (except chp and cmp), drop
				// the table and make a new one
				else {
					@SuppressWarnings ("unchecked")
					HashSet<String> statNames = (HashSet<String>) RBUtilities.statNames.clone();
					statNames.remove("chp");
					statNames.remove("cmp");
					statNames.add("name");
					statNames.add("magicks");
					statNames.add("skills");
					statNames.add("exp");
					if (!statNames.equals(columnNames)) {
						statement.executeUpdate("DROP TABLE monsters");
						createTable("monsters", statement);
					}
				}
			}
		}
		catch (SQLException e) {
			queryFailed(e, true);
		}
		// Execute a series of similarly dependent statements to check that the table players exists
		// and is
		// populated with expectant entries
		try {
			// Check to make sure the table players exists
			ResultSet set =
			        statement.executeQuery("SELECT name FROM sqlite_master WHERE type='table' AND name='players'");
			boolean made = false;
			// If not, make it
			try {
				set.getString(1);
			}
			catch (SQLException e) {
				createTable("players", statement);
				made = true;
			}
			// If so, make sure it has the right column headers
			if (!made) {
				set = statement.executeQuery("PRAGMA table_info(players)");
				// We do not store chp or cmp for monsters. If there's a size mismatch, drop the
				// table and make a new one
				int rowCount = 0;
				HashSet<String> columnNames = new HashSet<String>();
				while (set.next()) {
					columnNames.add(set.getString("name"));
					rowCount++;
				}
				// If the rowCount is not the same as the number of stats in RBUtilities (with the
				// addition of level, skills, name , and magicks), drop the table and make a new one
				if (rowCount != RBUtilities.statNames.size() + 5) {
					statement.executeUpdate("DROP TABLE players");
					createTable("players", statement);
				}
				// If the column names and the stat names aren't the same (with the addition of
				// level, skills, exp, name, and magicks), drop the table and make a new one
				else {
					@SuppressWarnings ("unchecked")
					HashSet<String> statNames = (HashSet<String>) RBUtilities.statNames.clone();
					statNames.add("level");
					statNames.add("skills");
					statNames.add("magicks");
					statNames.add("name");
					statNames.add("exp");
					if (!statNames.equals(columnNames)) {
						statement.executeUpdate("DROP TABLE players");
						createTable("players", statement);
					}
				}
			}
			// At this point, we know that the players table and monsters table have the correct
			// headings. Let us verify their contents.
			set = statement.executeQuery("SELECT name FROM players WHERE name='base'");
			// Check to see if the result set is closed
			try {
				set.getString(1);
			}
			catch (SQLException e) {
				// The set was empty, so we need to make the base player
				//@formatter:off
				statement.executeUpdate("INSERT INTO players VALUES (" +
					"'base'," +
					"1," +
					playerBaseStats.get("hp") + "," +
					playerBaseStats.get("mp") + "," +
					playerBaseStats.get("hp") + "," +
					playerBaseStats.get("mp") + "," +
					playerBaseStats.get("str") + "," +
					playerBaseStats.get("mag") + "," +
					playerBaseStats.get("def") + "," +
					playerBaseStats.get("mdef") + "," +
					playerBaseStats.get("agl") + "," +
					playerBaseStats.get("acc") + "," +
					playerBaseStats.get("eva") + "," +
					playerBaseStats.get("luck") + "," +
					"''," +
					"'Fire;Blizzard;Thunder;'," +
					"0" +
					")");
				//@formatter:on
			}
		}
		catch (SQLException e) {
			queryFailed(e, true);
		}
		// ///////////////////////////////////////////////////End verification of the table contents
		try {
			statement.close();
		}
		catch (SQLException e) {
			plugin.getLogger().warning(
			        RandomBattle.prefix + "Failed to close the verification statement! Is everything all right?");
			e.printStackTrace();
		}
		try {
			conn.close();
		}
		catch (SQLException e) {
			plugin.getLogger().warning(
			        RandomBattle.prefix + "Failed to close the database statement! Is everything all right?");
			e.printStackTrace();
		}
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
			plugin.getLogger().severe("Shutting down as a result of a failed query...");
			plugin.getPluginLoader().disablePlugin(plugin);
		}
	}
	
	/**
	 * <p>
	 * Creates a table in the SQLite database referenced by the given statement of a specified type.
	 * </p>
	 * @param type
	 *            The type of table to make. Currently only supports a "monsters" table and a
	 *            "players" table.
	 * @param statement
	 *            The Statement to execute the update on.
	 * @return An int returned by the executeUpdate method of the given Statement
	 * @throws SQLException
	 *             If they table already existed, or NOT NULL is not permitted by the schema.
	 */
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
				"luck INT NOT NULL," +
				"skills TEXT NOT NULL," +
				"magicks TEXT NOT NULL," +
				"exp INT NOT NULL" +
				")");
			//@formatter:on
			generateMonsters(statement);
		}
		else if (type.equals("players")) {
			//@formatter:off
			statement.executeUpdate("CREATE TABLE players (" +
				"name TEXT NOT NULL," +
				"level INT NOT NULL," +
				"hp INT NOT NULL," +
				"mp INT NOT NULL," +
				"chp INT NOT NULL," +
				"cmp INT NOT NULL," +
				"str INT NOT NULL," +
				"mag INT NOT NULL," +
				"def INT NOT NULL," +
				"mdef INT NOT NULL," +
				"agl INT NOT NULL," +
				"acc INT NOT NULL," +
				"eva INT NOT NULL," +
				"luck INT NOT NULL," +
				"skills TEXT NOT NULL," +
				"magicks TEXT NOT NULL," +
				"exp INT NOT NULL" +
				")");
			result = statement.executeUpdate("INSERT INTO players VALUES (" +
				"'base'," +
				"1," +
				playerBaseStats.get("hp") + "," +
				playerBaseStats.get("mp") + "," +
				playerBaseStats.get("hp") + "," +
				playerBaseStats.get("mp") + "," +
				playerBaseStats.get("str") + "," +
				playerBaseStats.get("mag") + "," +
				playerBaseStats.get("def") + "," +
				playerBaseStats.get("mdef") + "," +
				playerBaseStats.get("agl") + "," +
				playerBaseStats.get("acc") + "," +
				playerBaseStats.get("eva") + "," +
				playerBaseStats.get("luck") + "," +
				"''," +
				"'Fire;Blizzard;Thunder;'," +
				"0" +
				")");
			//@formatter:on
		}
		return result;
	}
	
	/**
	 * <p>
	 * Creates monster entries in the database for all recognized monsters. Some level factor should
	 * be used when translating the stats of these monsters to battle.
	 * </p>
	 * @param statement
	 *            The Statement to perform the queries on.
	 */
	private static void generateMonsters(Statement statement) throws SQLException
	{
		//@formatter:off
		statement.executeUpdate("INSERT INTO monsters VALUES (" +
			"'Enderman'," +
			"40000," +
			"4000," +
			"200," +
			"150," +
			"100," +
			"50," +
			"175," +
			"80," +
			"80," +
			"100," +
			"'Head Smasher;'," +
			"'Teleport;'," +
			"500" +
			")");
		//@formatter:on
		plugin.getLogger().info(RandomBattle.prefix + "Executed");
	}
}
