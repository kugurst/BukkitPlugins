/**
 * 
 */

package me.merdril.randombattle.config;

import java.io.File;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;

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
			plugin.getLogger().info(RandomBattle.prefix + "Unable to load the SQLite driver! Shutting down...");
			plugin.getPluginLoader().disablePlugin(plugin);
		}
		try {
			Connection conn =
			        DriverManager.getConnection("jdbc:sqlite:" + new File(plugin.getDataFolder(), "stats.db"));
			Statement statement = conn.createStatement();
			statement.execute("CREATE TABLE monsters" + "(" + "P_Id int," + "LastName varchar(255)" + ")");
		}
		catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
}
