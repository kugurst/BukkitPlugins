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
	 * @param expectedMobs
	 */
	public static void initialize(RandomBattle instance, Map<String, Integer> playerBaseStats, int expectedMobs)
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
				// chp and cmp, including weak), drop the table and make a new one
				if (rowCount != RBUtilities.statNames.size() - 1) {
					statement.executeUpdate("DROP TABLE monsters");
					createTable("monsters", statement);
				}
				// If the column names and the stat names aren't the same (except chp and cmp and
				// including weak), drop the table and make a new one
				else {
					@SuppressWarnings ("unchecked")
					HashSet<String> statNames = (HashSet<String>) RBUtilities.statNames.clone();
					statNames.remove("chp");
					statNames.remove("cmp");
					statNames.add("weak");
					if (!statNames.equals(columnNames)) {
						statement.executeUpdate("DROP TABLE monsters");
						createTable("monsters", statement);
					}
				}
			}
			// At this point, we have a working monster table. Let's make sure that we have the
			// expected number of listed mobs.
			set = statement.executeQuery("SELECT name FROM monsters");
			int rowCount = 0;
			while (set.next()) {
				plugin.getLogger().info(RandomBattle.prefix + set.getString(1));
				rowCount++;
			}
			if (rowCount < expectedMobs) {
				statement.executeUpdate("DELETE FROM monsters");
				generateMonsters(statement);
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
				// addition of level), drop the table and make a new one
				plugin.getLogger().info(RandomBattle.prefix + RBUtilities.statNames);
				plugin.getLogger().info(RandomBattle.prefix + columnNames);
				if (rowCount != RBUtilities.statNames.size() + 1) {
					plugin.getLogger().warning(RandomBattle.prefix + "Column ID mismatch. Remaking players table.");
					statement.executeUpdate("DROP TABLE players");
					createTable("players", statement);
				}
				// If the column names and the stat names aren't the same (with the addition of
				// level), drop the table and make a new one
				else {
					@SuppressWarnings ("unchecked")
					HashSet<String> statNames = (HashSet<String>) RBUtilities.statNames.clone();
					statNames.add("level");
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
				"exp INT NOT NULL," +
				"weak TEXT NOT NULL" +
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
	 * @return An int[] containing the results of the "INSERT INTOs" this method performs on the
	 *         table monsters in stats.db
	 */
	private static int[] generateMonsters(Statement statement) throws SQLException
	{
		//@formatter:off
		statement.addBatch("INSERT INTO monsters VALUES (" +
				"'Enderman'," + // name
				"40000," + // hp
				"400," + // mp
				"175," + // str
				"75," + // mag
				"50," + // def
				"100," + // mdef
				"175," + // agl
				"80," + // acc
				"80," + // eva
				"100," + // luck
				"'Head Smasher;'," + // skills
				"'Teleport;'," + // magicks
				"2000," + // exp
				"'ice;'" + // weak
				")");
		statement.addBatch("INSERT INTO monsters VALUES (" +
				"'Zombie Pigman'," + // name
				"20000," + // hp
				"100," + // mp
				"150," + // str
				"20," + // mag
				"150," + // def
				"20," + // mdef
				"50," + // agl
				"70," + // acc
				"15," + // eva
				"20," + // luck
				"'Call Friends;Sword Slash;'," + // skills
				"';'," + // magicks
				"750," + // exp
				"'holy;'" + // weak
				")");
		statement.addBatch("INSERT INTO monsters VALUES (" +
				"'Blaze'," + // name
				"20000," + // hp
				"600," + // mp
				"100," + // str
				"175," + // mag
				"100," + // def
				"175," + // mdef
				"80," + // agl
				"90," + // acc
				"60," + // eva
				"50," + // luck
				"'Pillar;'," + // skills
				"'Fireball;'," + // magicks
				"10000," + // exp
				"'ice;'" + // weak
				")");
		statement.addBatch("INSERT INTO monsters VALUES (" +
				"'Creeper'," + // name (a.k.a tonberry)
				"50000," + // hp
				"999," + // mp
				"0," + // str
				"255," + // mag
				"125," + // def
				"175," + // mdef
				"25," + // agl
				"100," + // acc
				"15," + // eva
				"150," + // luck
				"'Explode;'," + // skills
				"'/kill;'," + // magicks
				"100000," + // exp
				"'ice;'" + // weak
				")");
		statement.addBatch("INSERT INTO monsters VALUES (" +
				"'Skeleton'," + // name
				"20000," + // hp
				"200," + // mp
				"125," + // str
				"100," + // mag
				"75," + // def
				"75," + // mdef
				"150," + // agl
				"90," + // acc
				"50," + // eva
				"50," + // luck
				"'Aim;Triple Shot;'," + // skills
				"';'," + // magicks
				"500," + // exp
				"'fire;holy;'" + // weak
				")");
		statement.addBatch("INSERT INTO monsters VALUES (" +
				"'Spider'," + // name
				"16000," + // hp
				"200," + // mp
				"125," + // str
				"100," + // mag
				"75," + // def
				"125," + // mdef
				"150," + // agl
				"80," + // acc
				"70," + // eva
				"125," + // luck
				"'Sticky Web;'," + // skills
				"'Bio;'," + // magicks
				"500," + // exp
				"'fire;ice;'" + // weak
				")");
		statement.addBatch("INSERT INTO monsters VALUES (" +
				"'Spider Jockey'," + // name
				"36000," + // hp
				"400," + // mp
				"175," + // str
				"100," + // mag
				"125," + // def
				"100," + // mdef
				"160," + // agl
				"85," + // acc
				"60," + // eva
				"75," + // luck
				"'Sticky Web;Triple Shot;Poison Arrow;'," + // skills
				"'Bio;'," + // magicks
				"1000," + // exp
				"'fire;ice;holy;'" + // weak
				")");
		statement.addBatch("INSERT INTO monsters VALUES (" +
				"'Zombie'," + // name
				"20000," + // hp
				"100," + // mp
				"125," + // str
				"10," + // mag
				"150," + // def
				"10," + // mdef
				"40," + // agl
				"60," + // acc
				"20," + // eva
				"20," + // luck
				"'Groan;Maul;'," + // skills
				"'Foul Breath;'," + // magicks
				"500," + // exp
				"'fire;holy;'" + // weak
				")");
		statement.addBatch("INSERT INTO monsters VALUES (" +
				"'Iron Golem'," + // name
				"100000," + // hp
				"100," + // mp
				"225," + // str
				"10," + // mag
				"200," + // def
				"10," + // mdef
				"10," + // agl
				"95," + // acc
				"10," + // eva
				"150," + // luck
				"'Smash;Fortify;'," + // skills
				"';'," + // magicks
				"50000," + // exp
				"'thunder;'" + // weak
				")");
		//@formatter:on
		return statement.executeBatch();
	}
}
