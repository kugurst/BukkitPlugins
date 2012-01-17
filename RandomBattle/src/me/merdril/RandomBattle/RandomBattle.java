/**
 * 
 */

package me.merdril.RandomBattle;

import java.util.logging.Logger;

import me.merdril.RandomBattle.listeners.RandomBattleAttackCleanerListener;
import me.merdril.RandomBattle.listeners.RandomBattleAttackListener;
import me.merdril.RandomBattle.listeners.RandomBattleScreenListener;
import me.merdril.RandomBattle.listeners.RandomBattleSpoutListener;

import org.bukkit.event.Event;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;

/**
 * @author Merdril
 * 
 */
public class RandomBattle extends JavaPlugin
{
	Logger	      log	           = Logger.getLogger("Minecraft");
	protected int	numReqEntities	= 5;
	
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
		getCommand("removeblocks").setExecutor(cExec);
		
		RandomBattleSpoutListener spoutPlayerListener = new RandomBattleSpoutListener(this);
		pm.registerEvent(Event.Type.CUSTOM_EVENT, spoutPlayerListener, Event.Priority.Monitor, this);
		
		RandomBattleAttackListener attackListener = new RandomBattleAttackListener(this);
		pm.registerEvent(Event.Type.ENTITY_DAMAGE, attackListener, Event.Priority.Highest, this);
		
		RandomBattleAttackCleanerListener attackCleanerListener =
		        new RandomBattleAttackCleanerListener(this, numReqEntities);
		pm.registerEvent(Event.Type.ENTITY_DEATH, attackCleanerListener, Event.Priority.Monitor,
		        this);
		pm.registerEvent(Event.Type.ENTITY_EXPLODE, attackCleanerListener, Event.Priority.Monitor,
		        this);
		
		RandomBattleScreenListener screenListener = new RandomBattleScreenListener(this);
		pm.registerEvent(Event.Type.CUSTOM_EVENT, screenListener, Event.Priority.Monitor, this);
		
		log.info("[RandomBattle] Random Battle has started!");
	}
	
	public void onDisable()
	{
		RandomBattleCommandExecutor cExec = new RandomBattleCommandExecutor(this);
		getCommand("removeblocks").setExecutor(cExec);
		getCommand("removeblocks").execute(getServer().getConsoleSender(), "removeblocks",
		        new String[0]);
		log.info("[RandomBattle] Random Battle has shut down!");
	}
}
