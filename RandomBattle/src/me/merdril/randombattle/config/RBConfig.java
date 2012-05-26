/**
 * 
 */

package me.merdril.randombattle.config;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.PrintWriter;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Scanner;

import me.merdril.randombattle.RandomBattle;

import org.bukkit.configuration.file.FileConfiguration;

/**
 * <p>
 * This class is responsible for supplying the plugin with the appropriate values specified in the
 * configuration.
 * </p>
 * <p>
 * In case the user supplies bad values for configuration, the user is notified, and the class uses
 * values provided in the default configuration.
 * </p>
 * @author Merdril
 */
public class RBConfig
{
	private RandomBattle	  plugin;
	private FileConfiguration	config;
	
	/**
	 * <p>
	 * Initializes this class's configuration wrapper to read in the values contained in the
	 * configuration.
	 * </p>
	 * <p>
	 * This constructor simply checks for the existence of the configuration and configuration
	 * folder, and creates whatever is necessary.
	 * </p>
	 */
	public RBConfig(RandomBattle instance)
	{
		// Retain the instance for future use
		plugin = instance;
		// Check to make sure the configuration directory and file exists.
		File folder = plugin.getDataFolder();
		if (!folder.exists()) {
			if (!folder.mkdirs()) {
				plugin.getLogger().severe(
				        RandomBattle.prefix + "Unable to create configuration folder. Shutting down...");
				plugin.getPluginLoader().disablePlugin(plugin);
			}
		}
		File file = new File(folder, "config.yml");
		boolean configExisted = true;
		if (!file.exists()) {
			configExisted = false;
			try {
				if (!file.createNewFile()) {
					plugin.getLogger().severe(
					        RandomBattle.prefix + "Unable to create configuration file. Shutting down...");
					plugin.getPluginLoader().disablePlugin(plugin);
				}
			}
			catch (IOException e) {
				plugin.getLogger()
				        .severe(RandomBattle.prefix + "Unable to create configuration file. Shutting down...");
				plugin.getPluginLoader().disablePlugin(plugin);
			}
		}
		if (!configExisted) {
			// Copy the bytes of the provided config.yml into the newly created config.yml
			InputStream in = plugin.getResource("config.yml");
			try {
				Scanner reader = new Scanner(in);
				PrintWriter out = new PrintWriter(file);
				while (reader.hasNextLine()) {
					out.print(reader.nextLine());
					if (reader.hasNextLine())
						out.println();
					out.flush();
				}
			}
			catch (FileNotFoundException e) {
				plugin.getLogger().severe(
				        RandomBattle.prefix + "Unable to configure configuration file. Shutting down...");
				plugin.getPluginLoader().disablePlugin(plugin);
			}
		}
		plugin.reloadConfig();
		config = plugin.getConfig();
	}
	
	/**
	 * <p>
	 * Returns the dimension and position for the battle stage as specified in the configuration.
	 * </p>
	 * <p>
	 * Returned values may not be used if they invalidate the limit set by BattleSetter.
	 * </p>
	 * @param config
	 *            The FileConfiguration to scan for the appriopriate keys.
	 * @return An int[3] containing stageheight, stagewidth, and stagelength, respectively.
	 */
	public int[] getDimensions()
	{
		return new int[] {config.getInt("stageheight"), config.getInt("stagewidth"), config.getInt("stagelength")};
	}
	
	/**
	 * <p>
	 * Returns the user-defined chance for RandomBattles to occur.
	 * </p>
	 * @param config
	 *            The FileConfiguration to scan for the appriopriate keys.
	 * @return A user defined int. Not guaranteed to be within 0 and 100 (inclusive).
	 */
	public int getChance()
	{
		return config.getInt("randomchance");
	}
	
	/**
	 * <p>
	 * Attempts to load the user specified starting stats in the configuration. Falls back on the
	 * default configuration values when needed.
	 * </p>
	 * <p>
	 * This method can still fail if the user modifies the config.yml included with this plugin.
	 * However, we will not concern ourselves with such malicious users. The rational is that the
	 * plugin will fail to load if this method does not succeed (that is, this method is called in
	 * the onEnable() method of RandomBattle.java).
	 * </p>
	 * @return
	 */
	public Map<String, Integer> getStartStats()
	{
		// Load up the various maps for getting the stats. Formatted is the proper form for stats
		// (and will later be used for database access).
		Map<String, Integer> formatted = new HashMap<String, Integer>();
		// The stat mapping provided in the config.yml of the plugin data folder.
		Map<String, Object> userProvided = config.getConfigurationSection("startstats").getValues(false);
		// The stat mapping provided in the config.yml contained inside this jar.
		Map<String, Object> defualt =
		        plugin.getConfig().getDefaults().getConfigurationSection("startstats").getValues(false);
		// Check to make sure they have the same keys. If not, use the default config.
		if (!defualt.keySet().equals(userProvided.keySet())) {
			plugin.getLogger().warning(
			        RandomBattle.prefix + "Invalid keys for the stats descriptor! Falling back on defaults.");
			for (Map.Entry<String, Object> entry : defualt.entrySet())
				formatted.put(entry.getKey(), (Integer) entry.getValue());
			return formatted;
		}
		// Add the user provided mappings to the formatted entries. Correct any bad entries by
		// reverting to defaults.
		for (Map.Entry<String, Object> entry : config.getConfigurationSection("startstats").getValues(false).entrySet()) {
			try {
				formatted.put(entry.getKey(), (Integer) entry.getValue());
			}
			// If it failed to cast, then use the default value for that mapping.
			catch (ClassCastException e) {
				plugin.getLogger().warning(
				        RandomBattle.prefix + "Stat: " + entry.getKey()
				                + " is not mapped to an integer! Falling back on the default value for this stat.");
				formatted.put(entry.getKey(), (Integer) defualt.get(entry.getKey()));
			}
		}
		return formatted;
	}
	
	/**
	 * <p>
	 * Returns the user defined number for expected mobs. It should not be greater than the number
	 * given in the default configuration (which is the most that this plugin can service).
	 * </p>
	 * @return An int representing the maximum number of mobs to expect. The actual mobs to service
	 *         are determined by the values contained within stats.db.
	 */
	public List<String> getExpectedMobs()
	{
		return config.getStringList("expectedmobs");
	}
}
