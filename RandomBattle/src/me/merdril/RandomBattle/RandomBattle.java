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
	Logger	log	= Logger.getLogger("Minecraft");
	
	/**
	 * @param args
	 */
	public void onEnable()
	{
		PluginManager pm = this.getServer().getPluginManager();
		RandomBattleCommandExecutor cExec = new RandomBattleCommandExecutor(this);
		getCommand("regbattle").setExecutor(cExec);
		getCommand("stopbattles").setExecutor(cExec);
		getCommand("unregbattle").setExecutor(cExec);
		getCommand("resumebattles").setExecutor(cExec);
		getCommand("showregplayers").setExecutor(cExec);
		getCommand("showspoutplayers").setExecutor(cExec);
		
		RandomBattleSpoutListener spoutPlayerListener = new RandomBattleSpoutListener(this);
		pm.registerEvent(Event.Type.CUSTOM_EVENT, spoutPlayerListener, Event.Priority.Monitor, this);
		
		RandomBattleAttackListener attackListener = new RandomBattleAttackListener(this);
		pm.registerEvent(Event.Type.ENTITY_DAMAGE, attackListener, Event.Priority.Highest, this);
		log.info("[RandomBattle] Random Battle has started!");
	}
	
	public void onDisable()
	{
		log.info("[RandomBattle] Random Battle has shut down!");
	}
}
