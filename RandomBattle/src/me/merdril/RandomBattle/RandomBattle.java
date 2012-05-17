/**
 * 
 */

package me.merdril.RandomBattle;

import java.util.logging.Logger;

import me.merdril.RandomBattle.listeners.RBScreenListener;
import me.merdril.RandomBattle.listeners.RandomBattleAttackCleanerListener;
import me.merdril.RandomBattle.listeners.RandomBattleAttackListener;

import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;

/**
 * @author Merdril
 * 
 */
public class RandomBattle extends JavaPlugin
{
	Logger			log				= Logger.getLogger("Minecraft");
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

		RandomBattleAttackListener attackListener = new RandomBattleAttackListener(this);
		pm.registerEvents(attackListener, this);

		RandomBattleAttackCleanerListener attackCleanerListener = new RandomBattleAttackCleanerListener(this, numReqEntities);
		pm.registerEvents(attackCleanerListener, this);

		RBScreenListener screenListener = new RBScreenListener(this);
		pm.registerEvents(screenListener, this);

		log.info("[RandomBattle] Random Battle has started!");
	}

	public void onDisable()
	{
		RandomBattleCommandExecutor cExec = new RandomBattleCommandExecutor(this);
		getCommand("removeblocks").setExecutor(cExec);
		getCommand("removeblocks").execute(getServer().getConsoleSender(), "removeblocks", new String[0]);
		log.info("[RandomBattle] Random Battle has shut down!");
	}
}
