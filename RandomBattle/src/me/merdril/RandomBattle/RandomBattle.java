/**
 * 
 */

package me.merdril.RandomBattle;

import java.util.logging.Logger;

import org.bukkit.event.Event;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;

/**
 * @author Merdril
 * 
 */
public class RandomBattle extends JavaPlugin
{
	Logger										log				= Logger.getLogger("Minecraft");
	private final RandomBattlePlayerListener	playerListener	= new RandomBattlePlayerListener(
																		this);
	
	/**
	 * @param args
	 */
	public void onEnable()
	{
		PluginManager pm = this.getServer().getPluginManager();
		pm.registerEvent(Event.Type.PLAYER_LOGIN, playerListener, Event.Priority.Monitor, this);
		log.info("Random Battle has started!");
	}
	
	public void onDisable()
	{
		log.info("Random Battle has shut down!");
	}
	
}
