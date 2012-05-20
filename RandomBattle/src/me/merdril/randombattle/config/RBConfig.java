/**
 * 
 */

package me.merdril.randombattle.config;

import java.io.File;
import java.io.IOException;

import me.merdril.randombattle.RandomBattle;

import org.bukkit.configuration.file.FileConfiguration;

/**
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
				plugin.getLogger().warning(
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
					plugin.getLogger().warning(
					        RandomBattle.prefix + "Unable to create configuration file. Shutting down...");
					plugin.getPluginLoader().disablePlugin(plugin);
				}
			}
			catch (IOException e) {
				plugin.getLogger().warning(
				        RandomBattle.prefix + "Unable to create configuration file. Shutting down...");
				plugin.getPluginLoader().disablePlugin(plugin);
			}
		}
		if (!configExisted) {
			plugin.getConfig().options().copyDefaults(true);
			plugin.getConfig().options().copyDefaults();
		}
		plugin.saveConfig();
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
		int[] dim = new int[3];
		dim[0] = config.getInt("stageheight");
		dim[1] = config.getInt("stagewidth");
		dim[2] = config.getInt("stagelength");
		return dim;
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
}
