
package me.merdril.randombattle.config;

import java.io.File;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Collections;
import java.util.EnumMap;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.locks.ReentrantLock;

import javax.sql.rowset.CachedRowSet;

import me.merdril.randombattle.RBUtilities;
import me.merdril.randombattle.RandomBattle;
import me.merdril.randombattle.battle.RBElem;
import me.merdril.randombattle.battle.RBLivingEntity;
import me.merdril.randombattle.battle.RBLivingEntity.Stat;
import me.merdril.randombattle.battle.RBMagic;
import me.merdril.randombattle.battle.RBMonster;
import me.merdril.randombattle.battle.RBPlayer;
import me.merdril.randombattle.battle.RBSkill;
import me.merdril.randombattle.battle.ai.AI;

import org.bukkit.entity.EntityType;
import org.bukkit.entity.HumanEntity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.getspout.spoutapi.player.SpoutPlayer;

import com.sun.rowset.CachedRowSetImpl;

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
	private static RandomBattle	         plugin;
	private static Map<String, Integer>	 playerBaseStats;
	private static Map<String, Object[]>	playerBaseAttributes;
	private static List<String>	         activeMobs;
	private static Map<String, Object[]>	monsterStats;
	private static ReentrantLock	     playerStatsLock;
	/**
	 * <p>
	 * A {@link HashMap} mapping a player name preceded by an asterisk -
	 * <code>new String("*"+name.toLowerCase())</code> - to the stats, skills, and magicks known by
	 * the player. The Object returned by the <code>get()</code> method is:<br/>
	 * <code>new Object[] {{@link EnumMap}&lt{@link Stat}, {@link Integer}&gt,
	 * {@link LinkedList}&lt{@link RBSkill}&gt, {@link LinkedList}&lt{@link RBMagic}&gt};</code>
	 * </p>
	 */
	private static Map<String, Object[]>	playerStats;
	private static ReentrantLock	     cachedPlayersLock;
	private static Map<String, RBPlayer>	cachedPlayers;

	/**
	 * <p>
	 * Loads the values of <code>stat.db</code> and verifies its contents. That is to say, it
	 * creates <code>stat.db</code> should it not exist, the monsters table in it should it not
	 * exist or be of the expected format, and the players table in it should it not exist or be of
	 * the expected format. This is to prevent tying up the server thread as disk reads are slower
	 * than memory reads.
	 * </p>
	 * @param instance
	 *            The {@link RandomBattle} plugin this database is tied to. Used for outputting
	 *            error messages.
	 * @param playerBaseStats
	 *            The {@link Map}&lt{@link String}, {@link Integer}&gt loaded from the config file
	 *            containing the stats for a brand new player.
	 * @param expectedMobs
	 *            The {@link List}&lt{@link String}&gt loaded from the config file representing the
	 *            mobs that this instance of the plugin should load and support.
	 */
	public static void
	        initialize(RandomBattle instance, Map<String, Integer> playerBaseStats, List<String> expectedMobs)
	{
		// Save the plugin instance
		plugin = instance;
		activeMobs = expectedMobs;
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
			conn = getConnection();
		}
		catch (SQLException e) {
			e.printStackTrace();
			plugin.getLogger().severe(RandomBattle.prefix + "Unable to connect to stats.db! Shutting down...");
			plugin.getPluginLoader().disablePlugin(plugin);
		}
		// Create a Statement to use for verifying the integrity of the database
		Statement statement = null;
		try {
			statement =
			        conn.createStatement(ResultSet.TYPE_FORWARD_ONLY, ResultSet.CONCUR_READ_ONLY,
			                ResultSet.CLOSE_CURSORS_AT_COMMIT);
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
					plugin.getLogger().warning(RandomBattle.prefix + "Column ID mismatch. Remaking monsters table.");
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

		// Cache the contents of the monster and player table.
		monsterStats = loadMonsterStats(statement, RBLivingEntity.MONSTERS, activeMobs);
		playerBaseAttributes = Collections.synchronizedMap(new HashMap<String, Object[]>());
		playerStats = loadPlayerStats(statement);

		// Close the connections and call it a day folks!
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
		// Initialize anything else that needs to be initialized
		cachedPlayers = Collections.synchronizedMap(new HashMap<String, RBPlayer>());
		cachedPlayersLock = new ReentrantLock(true);
		playerStatsLock = new ReentrantLock(true);
	}

	public static AI getMonsterAI(LivingEntity monster)
	{
		// First check to see if this monster was specified in the config
		String monsterName = monster.toString();
		monsterName = monsterName.toLowerCase();
		monsterName.replaceAll("craft", "");
		boolean active = false;
		for (String name : activeMobs) {
			name.replaceAll(" ", "");
			if (name.equalsIgnoreCase(monsterName)) {
				active = true;
				break;
			}
		}
		// If the monster isn't active there's nothing more to do
		if (!active)
			return null;
		// Otherwise, return the AI from the living entity mapping
		else {
			try {
				Constructor<? extends AI> construct =
				        RBLivingEntity.MONSTERS.get(monsterName).getConstructor(Object[].class);
				AI ai = construct.newInstance((Object) monsterStats.get(monsterName));
				return ai;
			}
			catch (InstantiationException e) {
				// If an exception is thrown, return a null AI as something went very wrong.
				return null;
			}
			catch (IllegalAccessException e) {
				return null;
			}
			catch (NoSuchMethodException e) {
				return null;
			}
			catch (SecurityException e) {
				return null;
			}
			catch (IllegalArgumentException e) {
				return null;
			}
			catch (InvocationTargetException e) {
				return null;
			}
		}
	}

	/**
	 * <p>
	 * Saves the current status of an {@link RBPlayer}. Specifically, it saves all the player's
	 * {@link Stat}, their <code>{@link RBSkill}s</code>, and their <code>{@link RBMagic}ks</code>.
	 * @param name
	 *            The {@link String} of the player to save. Care should be taken to pass the player
	 *            name from the actual {@link Player}/{@link HumanEntity} object (not the display
	 *            name).
	 * @return True if the player was written to the database without error. False if the player was
	 *         not cached, does not exist, or a database write error occurred.
	 */
	public static boolean savePlayer(String name)
	{
		boolean savedSuccessfully = false;
		// A save has to be mirrored between all three objects that may contain the player:
		// cachedPlayer, playerStats, and the database
		RBPlayer player = null;
		cachedPlayersLock.lock();
		if (cachedPlayers.containsKey(name.toLowerCase()))
			player = cachedPlayers.get(name.toLowerCase());
		cachedPlayersLock.unlock();
		// There is no need to check playerStats for this player, because of the player is not
		// cached, then no changes could have occurred (that is, the player should have already been
		// saved before it was unloaded by whatever method unloaded it)
		if (player == null)
			return false;
		// Get the attributes of the player
		Map<Stat, Integer> statMap = player.getOriginalStats();
		List<RBSkill> skills = player.getSkills();
		List<RBMagic> magicks = player.getMagicks();
		// Save the player
		try {
			// Format the skills and magicks
			String skillLine = formatRBList(skills);
			String magicLine = formatRBList(magicks);
			// Write to the database
			String query = "UPDATE players SET ", stats = "";
			// Add the stats and their values
			for (Map.Entry<Stat, Integer> entry : statMap.entrySet())
				stats += entry.getKey().toString().toLowerCase() + "=" + entry.getValue() + ", ";
			// Add the other columns to the query, then add skills and magic
			query +=
			        stats + "skills='" + skillLine + "', magicks='" + magicLine + "' WHERE name ='*"
			                + name.toLowerCase() + "'";
			plugin.getLogger().info(RandomBattle.prefix + "Query: " + query);
			Connection conn = getConnection();
			Statement stat = conn.createStatement();
			stat.executeUpdate(query);
			savedSuccessfully = true;
		}
		catch (SQLException e) {
			queryFailed(e, false);
		}
		return savedSuccessfully;
	}

	/**
	 * <p>
	 * For conserving memory usage, this program removes a player from the mapping of cached
	 * players. It is up to the JVM to then get rid of the actual {@link RBPlayer} object from
	 * memory. The calling method should be the last method with a reference to the removed
	 * {@link RBPlayer} for this method to have any meaning.
	 * </p>
	 * @param name
	 *            The {@link String} of the player to unload. Care should be taken to pass the
	 *            player name from the actual {@link Player}/{@link HumanEntity} object (not the
	 *            display name).
	 * @return The {@link RBPlayer} as contained in the mapping of cached players, or null if it was
	 *         not found.
	 */
	public static RBPlayer unloadPlayer(String name)
	{
		RBPlayer player = null;
		// A player has to be cached to be unloaded
		cachedPlayersLock.lock();
		if (cachedPlayers.containsKey(name.toLowerCase()))
			player = cachedPlayers.remove(name.toLowerCase());
		cachedPlayersLock.unlock();
		return player;
	}

	/**
	 * <p>
	 * Returns an {@link RBPlayer} specified by the given name. This method first looks to see if
	 * the player has already been loaded, and returns that instance, otherwise it loads the player
	 * as last saved (the attributes specified in stats.db). If the player does not exist in either
	 * location, it is created, so care should be taken to pass the player name from the actual
	 * {@link Player}/{@link HumanEntity} object (not the display name).
	 * </p>
	 * @param name
	 *            The {@link String} of the player's name to load.
	 * @return An {@link RBPlayer} if the load was successful. Null otherwise. It is possible for a
	 *         database write to fail in this method and a player still be returned, so one should
	 *         not assume the player exists in the database if this method does not return null.
	 */
	@SuppressWarnings ("unchecked")
	public static RBPlayer loadPlayer(String name)
	{
		RBPlayer rbPlayer = null;
		// It's assumed that the player is registered
		cachedPlayersLock.lock();
		if (cachedPlayers.containsKey(name.toLowerCase())) {
			cachedPlayersLock.unlock();
			return cachedPlayers.get(name.toLowerCase());
		}
		playerStatsLock.lock();
		if (playerStats.containsKey("*" + name.toLowerCase())) {
			Object[] attributes = playerStats.get("*" + name.toLowerCase());
			rbPlayer =
			        new RBPlayer(plugin, (SpoutPlayer) plugin.getServer().getPlayer(name),
			                (EnumMap<Stat, Integer>) attributes[0], null, (List<RBSkill>) attributes[1],
			                (List<RBMagic>) attributes[2], null);
			cachedPlayers.put(name.toLowerCase(), rbPlayer);
			cachedPlayersLock.unlock();
			playerStatsLock.unlock();
		}
		// We have to make a new player
		else {
			Object[] attributes = playerBaseAttributes.get("base");
			EnumMap<Stat, Integer> statMap = (EnumMap<Stat, Integer>) attributes[0];
			List<RBSkill> skills = (List<RBSkill>) attributes[1];
			List<RBMagic> magicks = (List<RBMagic>) attributes[2];
			rbPlayer =
			        new RBPlayer(plugin, (SpoutPlayer) plugin.getServer().getPlayer(name), statMap, null, skills,
			                magicks, null);
			playerStats.put("*" + name.toLowerCase(), attributes);
			cachedPlayers.put(name.toLowerCase(), rbPlayer);
			cachedPlayersLock.unlock();
			playerStatsLock.unlock();
			// Save the player
			try {
				// Format the skills and magicks
				String skillLine = formatRBList(skills);
				String magicLine = formatRBList(magicks);
				// Write to the database
				String query = "INSERT INTO players (", statValues = "", statColumns = "";
				// Align the stats and their values
				for (Map.Entry<Stat, Integer> entry : statMap.entrySet()) {
					statColumns += entry.getKey().toString().toLowerCase() + ", ";
					statValues += entry.getValue() + ", ";
				}
				// Add the other columns to the query, then add skills and magic
				query += statColumns + "name, skills, magicks) VALUES (";
				query += statValues + "'*" + name.toLowerCase() + "', '" + skillLine + "', '" + magicLine + "')";
				plugin.getLogger().info(RandomBattle.prefix + "Query: " + query);
				Connection conn = getConnection();
				Statement stat = conn.createStatement();
				stat.executeUpdate(query);
			}
			catch (SQLException e) {
				queryFailed(e, false);
			}
		}
		return rbPlayer;
	}

	private static String formatRBList(List<?> list)
	{
		String result = "";
		StringBuilder formatted;
		for (Object object : list) {
			String name = object.toString();
			name.replaceAll("\\_", "\\ ");
			name = name.substring(0, 1).toUpperCase() + name.substring(1).toLowerCase();
			formatted = new StringBuilder(name);
			for (int i = 1; i < formatted.length(); i++) {
				if (formatted.charAt(i - 1) == ' ') {
					formatted.replace(i, i + 1, Character.toString(Character.toUpperCase(formatted.charAt(i))));
				}
			}
			result += formatted + ";";
		}
		return result;
	}

	/**
	 * <p>
	 * Loads the contents (all of them) of the players table into memory for quick access. The
	 * results are stored in a {@link HashMap}&lt{@link String}, {@link Object}&gt.
	 * </p>
	 * @param statement
	 *            The {@link Statement} to use to fetch the players.
	 * @return A {@link Map}&lt{@link String}, {@link Object}&gt that maps the lower case name of
	 *         the players as specified in the stats.db file to an <code>{@link Object}[]</code> of
	 *         the form:<br />
	 *         {{@link EnumMap}&lt{@link Stat}, {@link Integer}&gt, {@link LinkedList}&lt
	 *         {@link RBSkill}&gt and {@link LinkedList}&lt{@link RBMagic}&gt}<br />
	 *         Where the EnumMap are the stats of the {@link RBPlayer}, and the other three are
	 *         self-explanitory
	 */
	@SuppressWarnings ("resource")
	private static Map<String, Object[]> loadPlayerStats(Statement statement)
	{
		// Get the players from the database
		ResultSet set = null;
		try {
			set = statement.executeQuery("SELECT * FROM players");
		}
		catch (SQLException e) {
			plugin.getLogger().severe(
			        RandomBattle.prefix + "Unable to read the monsters from the database! Shutting down...");
			e.printStackTrace();
			plugin.getPluginLoader().disablePlugin(plugin);
		}
		// Initialize the player cache
		HashMap<String, Object[]> playerMap = new HashMap<String, Object[]>();
		// Go through the list of players
		try {
			int row = 0;
			while (set.next()) {
				row++;
				String playerName = set.getString("name");
				// // Make the attribute holder
				Object[] attributes = null;
				// Make an EnumMap to hold the stats
				EnumMap<Stat, Integer> stats = new EnumMap<RBLivingEntity.Stat, Integer>(Stat.class);
				// Make a LinkedList for the skills and magic.
				LinkedList<RBSkill> skills = new LinkedList<RBSkill>();
				LinkedList<RBMagic> magicks = new LinkedList<RBMagic>();
				if (playerName.charAt(0) == '*' || playerName.equalsIgnoreCase("base")) {
					// Get all the stats
					for (Stat stat : RBLivingEntity.Stat.values()) {
						String statName = stat.toString().toLowerCase();
						int statAmount = set.getInt(statName);
						stats.put(stat, statAmount);
						// Check to make sure the base stat in the database is up to date
						if (playerName.equalsIgnoreCase("base")) {
							// Skipping stats that aren't provided for in the config
							if (statName.equalsIgnoreCase("chp") || statName.equalsIgnoreCase("cmp")
							        || statName.equalsIgnoreCase("exp") || statName.equalsIgnoreCase("level"))
								continue;
							// If there's a stat mismatch, update the database
							if (!playerBaseStats.get(statName).equals(stats.get(stat))) {
								stats.put(stat, playerBaseStats.get(statName));
								statement.executeUpdate("UPDATE players SET " + statName + "="
								        + playerBaseStats.get(statName) + " WHERE name='" + playerName + "'");
								// The SQLite driver I'm using doesn't support set.update(...), so
								// we're doing this the longer way.
								set = statement.executeQuery("SELECT * FROM players");
								int currentRow = 0;
								while (currentRow++ < row) {
									boolean hasNext = set.next();
									if (!hasNext)
										break;
								}
							}
						}
					}
					// Get the skills of this player
					String[] skillArray = set.getString("skills").split("\\;");
					for (String skill : skillArray)
						if (!skill.isEmpty())
							skills.add(RBSkill.skillMap.get(skill.toLowerCase().replaceAll("\\ ", "\\_")));
					// Get the magicks of this player
					String[] magicksArray = set.getString("magicks").split("\\;");
					for (String magic : magicksArray)
						if (!magic.isEmpty())
							magicks.add(RBMagic.magicMap.get(magic.toLowerCase().replaceAll("\\ ", "\\_")));
				}
				// If stats is empty, then neither of the two conditions were true
				if (stats.isEmpty())
					continue;
				attributes = new Object[] {stats, skills, magicks};
				// If we're on the base player, set him aside for easier access
				if (playerName.equalsIgnoreCase("base")) {
					// Add chp, cmp, exp, and level
					@SuppressWarnings ("unchecked")
					EnumMap<Stat, Integer> statMap = (EnumMap<Stat, Integer>) attributes[0];
					statMap.put(Stat.CHP, statMap.get(Stat.HP));
					statMap.put(Stat.CMP, statMap.get(Stat.MP));
					statMap.put(Stat.LEVEL, set.getInt(Stat.LEVEL.toString().toLowerCase()));
					statMap.put(Stat.EXP, set.getInt(Stat.EXP.toString().toLowerCase()));
					playerBaseAttributes.put(playerName.toLowerCase(), attributes);
				}
				playerMap.put(playerName.toLowerCase(), attributes);
			}
		}
		catch (SQLException e) {
			queryFailed(e, true);
		}
		if (set != null)
			try {
				set.close();
			}
			catch (SQLException e) {
				queryFailed(e, true);
			}
		return playerMap;
	}

	/**
	 * <p>
	 * Loads the contents (all of them) of the monster table into memory for quick access. The
	 * results are stored in a {@link HashMap}&lt{@link String}, {@link Object}&gt.
	 * </p>
	 * @param statement
	 *            The {@link Statement} to use to fetch the monsters.
	 * @param monsters
	 *            The {@link Map}&lt{@link String}, {@link EntityType}&gt to use for monster
	 *            comparison (of the enabled monsters).
	 * @param activeMobs
	 *            The {@link List}&lt{@link String}&gt containing the enabled mobs as specified by
	 *            the configuration file.
	 * @return A {@link Map}&lt{@link String}, {@link Object}&gt that maps the lower case name of
	 *         the monster as specified in the stats.db file to an <code>{@link Object}[]</code> of
	 *         the form:<br />
	 *         {{@link EnumMap}&lt{@link Stat}, {@link Integer}&gt, {@link LinkedList}&lt
	 *         {@link RBSkill}&gt, {@link LinkedList}&lt{@link RBMagic}&gt, {@link LinkedList}&lt
	 *         {@link RBElem}&gt}<br />
	 *         Where the EnumMap are the stats of the {@link RBMonster} (excluding CHP, CMP, and
	 *         LEVEL), and the other three are self-explanitory
	 */
	private static Map<String, Object[]> loadMonsterStats(Statement statement,
	        Map<String, Class<? extends AI>> monsters, List<String> activeMobs)
	{
		// Get the monsters from the database:
		ResultSet set = null;
		try {
			set = statement.executeQuery("SELECT * FROM monsters");
		}
		catch (SQLException e) {
			plugin.getLogger().severe(
			        RandomBattle.prefix + "Unable to read the monsters from the database! Shutting down...");
			e.printStackTrace();
			plugin.getPluginLoader().disablePlugin(plugin);
		}
		// Initialize the stat holder
		Map<String, Object[]> result = new HashMap<String, Object[]>();
		// //Get the stats for the enabled monsters
		// Find the enabled monsters and add their stats.
		try {
			while (set.next()) {
				String monsterName = set.getString("name");
				// If the mob is enabled...
				if (activeMobs.contains(monsterName.toLowerCase())) {
					// Get the AIs for the enabled monsters
					try {
						// It is checked, by that ClassCastException
						@SuppressWarnings ("unchecked")
						Class<? extends AI> aiClass =
						        (Class<? extends AI>) Class.forName("me.merdril.randombattle.battle.ai."
						                + monsterName.replaceAll("\\ ", "") + "AI");
						monsters.put(monsterName.replaceAll("\\ ", "").toLowerCase(), aiClass);
					}
					catch (ClassNotFoundException e) {
						plugin.getLogger().severe(
						        RandomBattle.prefix + "Unkown class: " + "me.merdril.randombattle.battle.ai."
						                + monsterName.replaceAll("\\ ", "") + "AI");
						e.printStackTrace();
					}
					catch (ClassCastException e) {
						plugin.getLogger().severe(
						        RandomBattle.prefix + "Bad cast: " + "me.merdril.randombattle.battle.ai."
						                + monsterName.replaceAll("\\ ", "") + "AI");
						e.printStackTrace();
					}
					// Make an object array to hold the values associated with the monster (its
					// stats, skills, magicks, and weaknesses)
					Object[] attributes;
					// // Get its stats and add it to the map
					// Make an EnumMap to hold the stats
					EnumMap<Stat, Integer> stats = new EnumMap<RBLivingEntity.Stat, Integer>(Stat.class);
					// Make a LinkedList for the skills, magic, and elemental weaknesses.
					LinkedList<RBSkill> skills = new LinkedList<RBSkill>();
					LinkedList<RBMagic> magicks = new LinkedList<RBMagic>();
					LinkedList<RBElem> weakness = new LinkedList<RBElem>();
					// Populate the stats list
					for (Stat stat : RBLivingEntity.Stat.values()) {
						String statName = stat.toString().toLowerCase();
						// We don't care about certain stats (more accurately, asking for them would
						// throw an exception)
						if (statName.equalsIgnoreCase("chp") || statName.equalsIgnoreCase("cmp")
						        || statName.equals("level"))
							continue;
						// Get the stat and load it in.
						int statAmount = set.getInt(statName);
						stats.put(stat, statAmount);
					}
					// Get the skills of this monster
					String[] skillArray = set.getString("skills").split("\\;");
					for (String skill : skillArray)
						if (!skill.isEmpty())
							skills.add(RBSkill.skillMap.get(skill.toLowerCase().replaceAll("\\ ", "\\_")));
					// Get the magicks of this monster
					String[] magicksArray = set.getString("magicks").split("\\;");
					for (String magic : magicksArray)
						if (!magic.isEmpty())
							magicks.add(RBMagic.magicMap.get(magic.toLowerCase().replaceAll("\\ ", "\\_")));
					// Get the weaknesses of this monster
					String[] weaknessArray = set.getString("weak").split("\\;");
					for (String weak : weaknessArray)
						if (!weak.isEmpty())
							weakness.add(RBElem.elemMap.get(weak.toLowerCase()));
					// Add all of these lists and the map to the attributes of this monster and
					// store it in the map
					attributes = new Object[] {stats, skills, magicks, weakness};
					result.put(monsterName.toLowerCase().replaceAll("\\ ", ""), attributes);
				}
			} // Repeat
		}
		catch (SQLException e) {
			queryFailed(e, true);
		}
		return result;
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
				"'Pig Zombie'," + // name
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
				"'Kill;'," + // magicks
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
				"100," + // str
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
				"125," + // mag
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
				"50," + // mdef
				"10," + // agl
				"95," + // acc
				"10," + // eva
				"150," + // luck
				"'Smash;Fortify;'," + // skills
				"';'," + // magicks
				"50000," + // exp
				"'lightning;'" + // weak
				")");
		//@formatter:on
		return statement.executeBatch();
	}

	/**
	 * <p>
	 * Performs the provided query. Used for more complex operations than what the other methods of
	 * this class can provide.
	 * </p>
	 * @param query
	 * @return A CachedRowSet from the performed query. Null if the query fails or is a query that
	 *         does not return a ResultSet.
	 */
	public static CachedRowSet performQuery(String query)
	{
		CachedRowSet rowSet = null;
		try {
			Connection conn = getConnection();
			Statement stat = conn.createStatement();
			ResultSet set = stat.executeQuery(query);
			rowSet = new CachedRowSetImpl();
			rowSet.populate(set);
			stat.close();
			conn.close();
		}
		catch (SQLException e) {
			queryFailed(e, false);
		}
		return rowSet;
	}

	/**
	 * <p>
	 * A short helper method to shorten line length as well as to reduce programmer error. Returns a
	 * connection to the stats.db file.
	 * </p>
	 * @return A Connection to the stats.db file located in the plugin data folder of this plugin.
	 * @throws SQLException
	 *             If the Connection cannot be obtained, or whatever reasons
	 *             DriverManager.getConnection(String url) throws an SQLException.
	 */
	private static Connection getConnection() throws SQLException
	{
		return DriverManager.getConnection("jdbc:sqlite:" + new File(plugin.getDataFolder(), "stats.db").getPath());
	}

	// It's a utility class, so it need not be instantiated
	private RBDatabase() throws AssertionError
	{
		throw new AssertionError("This class cannot be instantiated!");
	}
}
