/**
 * 
 */

package me.merdril.RandomBattle;

import java.util.logging.Logger;

import org.bukkit.plugin.java.JavaPlugin;

/**
 * @author Merdril
 * 
 */
public class RandomBattle extends JavaPlugin
{
	Logger	log	= Logger.getLogger("Minecraft");
	
	/**
	 * @param args
	 */
	public void onEnable()
	{
		log.info("Random Battle has started!");
	}
	
	public void onDisable()
	{
		log.info("Random Battle has shut down!");
	}
	
}
